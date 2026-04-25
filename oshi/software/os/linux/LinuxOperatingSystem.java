package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.Who;
import oshi.driver.linux.proc.Auxv;
import oshi.driver.linux.proc.CpuStat;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.linux.proc.UpTime;
import oshi.jna.Struct;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.ApplicationInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class LinuxOperatingSystem extends AbstractOperatingSystem {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxOperatingSystem.class);
  
  private static final String OS_RELEASE_LOG = "os-release: {}";
  
  private static final String LSB_RELEASE_A_LOG = "lsb_release -a: {}";
  
  private static final String LSB_RELEASE_LOG = "lsb-release: {}";
  
  private static final String RELEASE_DELIM = " release ";
  
  private static final String DOUBLE_QUOTES = "(?:^\")|(?:\"$)";
  
  private static final String FILENAME_PROPERTIES = "oshi.linux.filename.properties";
  
  public static final boolean HAS_UDEV;
  
  public static final boolean HAS_GETTID;
  
  public static final boolean HAS_SYSCALL_GETTID;
  
  private final Supplier<List<ApplicationInfo>> installedAppsSupplier = Memoizer.memoize(LinuxInstalledApps::queryInstalledApps, Memoizer.installedAppsExpiration());
  
  private static final long USER_HZ;
  
  private static final long PAGE_SIZE;
  
  private static final String OS_NAME = ExecutingCommand.getFirstAnswer("uname -o");
  
  static final long BOOTTIME;
  
  private static final int[] PPID_INDEX = new int[] { 3 };
  
  public LinuxOperatingSystem() {
    getVersionInfo();
  }
  
  public String queryManufacturer() {
    return OS_NAME;
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    Triplet<String, String, String> triplet = queryFamilyVersionCodenameFromReleaseFiles();
    String str = null;
    List<CharSequence> list = FileUtil.readFile(ProcPath.VERSION);
    if (!list.isEmpty()) {
      String[] arrayOfString = ParseUtil.whitespaces.split(list.get(0));
      for (String str1 : arrayOfString) {
        if (!"Linux".equals(str1) && !"version".equals(str1)) {
          str = str1;
          break;
        } 
      } 
    } 
    OperatingSystem.OSVersionInfo oSVersionInfo = new OperatingSystem.OSVersionInfo((String)triplet.getB(), (String)triplet.getC(), str);
    return new Pair(triplet.getA(), oSVersionInfo);
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt < 64 && !ExecutingCommand.getFirstAnswer("uname -m").contains("64")) ? paramInt : 64;
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new LinuxFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new LinuxInternetProtocolStats();
  }
  
  public List<OSSession> getSessions() {
    return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
  }
  
  public OSProcess getProcess(int paramInt) {
    LinuxOSProcess linuxOSProcess = new LinuxOSProcess(paramInt, this);
    return (OSProcess)(!linuxOSProcess.getState().equals(OSProcess.State.INVALID) ? linuxOSProcess : null);
  }
  
  public List<OSProcess> queryAllProcesses() {
    return queryChildProcesses(-1);
  }
  
  public List<OSProcess> queryChildProcesses(int paramInt) {
    File[] arrayOfFile = ProcessStat.getPidFiles();
    if (paramInt >= 0)
      return queryProcessList(getChildrenOrDescendants(getParentPidsFromProcFiles(arrayOfFile), paramInt, false)); 
    HashSet<Integer> hashSet = new HashSet();
    for (File file : arrayOfFile) {
      int i = ParseUtil.parseIntOrDefault(file.getName(), -2);
      if (i != -2)
        hashSet.add(Integer.valueOf(i)); 
    } 
    return queryProcessList(hashSet);
  }
  
  public List<OSProcess> queryDescendantProcesses(int paramInt) {
    File[] arrayOfFile = ProcessStat.getPidFiles();
    return queryProcessList(getChildrenOrDescendants(getParentPidsFromProcFiles(arrayOfFile), paramInt, true));
  }
  
  private List<OSProcess> queryProcessList(Set<Integer> paramSet) {
    ArrayList<LinuxOSProcess> arrayList = new ArrayList();
    Iterator<Integer> iterator = paramSet.iterator();
    while (iterator.hasNext()) {
      int i = ((Integer)iterator.next()).intValue();
      LinuxOSProcess linuxOSProcess = new LinuxOSProcess(i, this);
      if (!linuxOSProcess.getState().equals(OSProcess.State.INVALID))
        arrayList.add(linuxOSProcess); 
    } 
    return (List)arrayList;
  }
  
  private static Map<Integer, Integer> getParentPidsFromProcFiles(File[] paramArrayOfFile) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (File file : paramArrayOfFile) {
      int i = ParseUtil.parseIntOrDefault(file.getName(), 0);
      hashMap.put(Integer.valueOf(i), Integer.valueOf(getParentPidFromProcFile(i)));
    } 
    return (Map)hashMap;
  }
  
  private static int getParentPidFromProcFile(int paramInt) {
    String str = FileUtil.getStringFromFile(String.format(Locale.ROOT, "/proc/%d/stat", new Object[] { Integer.valueOf(paramInt) }));
    if (str.isEmpty())
      return 0; 
    long[] arrayOfLong = ParseUtil.parseStringToLongArray(str, PPID_INDEX, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
    return (int)arrayOfLong[0];
  }
  
  public int getProcessId() {
    return LinuxLibc.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    return (ProcessStat.getPidFiles()).length;
  }
  
  public int getThreadId() {
    if (HAS_SYSCALL_GETTID)
      return HAS_GETTID ? LinuxLibc.INSTANCE.gettid() : LinuxLibc.INSTANCE.syscall(LinuxLibc.SYS_GETTID, new Object[0]).intValue(); 
    try {
      return ParseUtil.parseIntOrDefault(Files.readSymbolicLink((new File(ProcPath.THREAD_SELF)).toPath()).getFileName().toString(), 0);
    } catch (IOException iOException) {
      return 0;
    } 
  }
  
  public OSThread getCurrentThread() {
    return (OSThread)new LinuxOSThread(getProcessId(), getThreadId());
  }
  
  public int getThreadCount() {
    try {
      Struct.CloseableSysinfo closeableSysinfo = new Struct.CloseableSysinfo();
      try {
        if (0 != LibC.INSTANCE.sysinfo((LibC.Sysinfo)closeableSysinfo)) {
          LOG.error("Failed to get process thread count. Error code: {}", Integer.valueOf(Native.getLastError()));
          boolean bool = false;
          closeableSysinfo.close();
          return bool;
        } 
        short s = closeableSysinfo.procs;
        closeableSysinfo.close();
        return s;
      } catch (Throwable throwable) {
        try {
          closeableSysinfo.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (UnsatisfiedLinkError|NoClassDefFoundError unsatisfiedLinkError) {
      LOG.error("Failed to get procs from sysinfo. {}", unsatisfiedLinkError.getMessage());
      return 0;
    } 
  }
  
  public long getSystemUptime() {
    return (long)UpTime.getSystemUptimeSeconds();
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new LinuxNetworkParams();
  }
  
  public List<ApplicationInfo> getInstalledApplications() {
    return this.installedAppsSupplier.get();
  }
  
  private static Triplet<String, String, String> queryFamilyVersionCodenameFromReleaseFiles() {
    Triplet<String, String, String> triplet;
    if ((triplet = readDistribRelease("/etc/system-release")) != null)
      return triplet; 
    if ((triplet = readOsRelease()) != null)
      return triplet; 
    if ((triplet = execLsbRelease()) != null)
      return triplet; 
    if ((triplet = readLsbRelease()) != null)
      return triplet; 
    String str1 = getReleaseFilename();
    if ((triplet = readDistribRelease(str1)) != null)
      return triplet; 
    String str2 = filenameToFamily(str1.replace("/etc/", "").replace("release", "").replace("version", "").replace("-", "").replace("_", ""));
    return new Triplet(str2, "unknown", "unknown");
  }
  
  private static Triplet<String, String, String> readOsRelease() {
    String str1 = null;
    String str2 = "unknown";
    String str3 = "unknown";
    List list = FileUtil.readFile("/etc/os-release");
    for (String str : list) {
      if (str.startsWith("VERSION=")) {
        LOG.debug("os-release: {}", str);
        str = str.replace("VERSION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
        String[] arrayOfString = str.split("[()]");
        if (arrayOfString.length <= 1)
          arrayOfString = str.split(", "); 
        if (arrayOfString.length > 0)
          str2 = arrayOfString[0].trim(); 
        if (arrayOfString.length > 1)
          str3 = arrayOfString[1].trim(); 
        continue;
      } 
      if (str.startsWith("NAME=") && str1 == null) {
        LOG.debug("os-release: {}", str);
        str1 = str.replace("NAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
        continue;
      } 
      if (str.startsWith("VERSION_ID=") && str2.equals("unknown")) {
        LOG.debug("os-release: {}", str);
        str2 = str.replace("VERSION_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
      } 
    } 
    return (str1 == null) ? null : new Triplet(str1, str2, str3);
  }
  
  private static Triplet<String, String, String> execLsbRelease() {
    String str1 = null;
    String str2 = "unknown";
    String str3 = "unknown";
    for (String str : ExecutingCommand.runNative("lsb_release -a")) {
      if (str.startsWith("Description:")) {
        LOG.debug("lsb_release -a: {}", str);
        str = str.replace("Description:", "").trim();
        if (str.contains(" release ")) {
          Triplet<String, String, String> triplet = parseRelease(str, " release ");
          str1 = (String)triplet.getA();
          if (str2.equals("unknown"))
            str2 = (String)triplet.getB(); 
          if (str3.equals("unknown"))
            str3 = (String)triplet.getC(); 
        } 
        continue;
      } 
      if (str.startsWith("Distributor ID:") && str1 == null) {
        LOG.debug("lsb_release -a: {}", str);
        str1 = str.replace("Distributor ID:", "").trim();
        continue;
      } 
      if (str.startsWith("Release:") && str2.equals("unknown")) {
        LOG.debug("lsb_release -a: {}", str);
        str2 = str.replace("Release:", "").trim();
        continue;
      } 
      if (str.startsWith("Codename:") && str3.equals("unknown")) {
        LOG.debug("lsb_release -a: {}", str);
        str3 = str.replace("Codename:", "").trim();
      } 
    } 
    return (str1 == null) ? null : new Triplet(str1, str2, str3);
  }
  
  private static Triplet<String, String, String> readLsbRelease() {
    String str1 = null;
    String str2 = "unknown";
    String str3 = "unknown";
    List list = FileUtil.readFile("/etc/lsb-release");
    for (String str : list) {
      if (str.startsWith("DISTRIB_DESCRIPTION=")) {
        LOG.debug("lsb-release: {}", str);
        str = str.replace("DISTRIB_DESCRIPTION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
        if (str.contains(" release ")) {
          Triplet<String, String, String> triplet = parseRelease(str, " release ");
          str1 = (String)triplet.getA();
          if (str2.equals("unknown"))
            str2 = (String)triplet.getB(); 
          if (str3.equals("unknown"))
            str3 = (String)triplet.getC(); 
        } 
        continue;
      } 
      if (str.startsWith("DISTRIB_ID=") && str1 == null) {
        LOG.debug("lsb-release: {}", str);
        str1 = str.replace("DISTRIB_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
        continue;
      } 
      if (str.startsWith("DISTRIB_RELEASE=") && str2.equals("unknown")) {
        LOG.debug("lsb-release: {}", str);
        str2 = str.replace("DISTRIB_RELEASE=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
        continue;
      } 
      if (str.startsWith("DISTRIB_CODENAME=") && str3.equals("unknown")) {
        LOG.debug("lsb-release: {}", str);
        str3 = str.replace("DISTRIB_CODENAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
      } 
    } 
    return (str1 == null) ? null : new Triplet(str1, str2, str3);
  }
  
  private static Triplet<String, String, String> readDistribRelease(String paramString) {
    if ((new File(paramString)).exists()) {
      List list = FileUtil.readFile(paramString);
      for (String str : list) {
        LOG.debug("{}: {}", paramString, str);
        if (str.contains(" release "))
          return parseRelease(str, " release "); 
        if (str.contains(" VERSION "))
          return parseRelease(str, " VERSION "); 
      } 
    } 
    return null;
  }
  
  private static Triplet<String, String, String> parseRelease(String paramString1, String paramString2) {
    String[] arrayOfString = paramString1.split(paramString2);
    String str1 = arrayOfString[0].trim();
    String str2 = "unknown";
    String str3 = "unknown";
    if (arrayOfString.length > 1) {
      arrayOfString = arrayOfString[1].split("[()]");
      if (arrayOfString.length > 0)
        str2 = arrayOfString[0].trim(); 
      if (arrayOfString.length > 1)
        str3 = arrayOfString[1].trim(); 
    } 
    return new Triplet(str1, str2, str3);
  }
  
  protected static String getReleaseFilename() {
    File file = new File("/etc");
    File[] arrayOfFile = file.listFiles(paramFile -> ((paramFile.getName().endsWith("-release") || paramFile.getName().endsWith("-version") || paramFile.getName().endsWith("_release") || paramFile.getName().endsWith("_version")) && !paramFile.getName().endsWith("os-release") && !paramFile.getName().endsWith("lsb-release") && !paramFile.getName().endsWith("system-release")));
    return (arrayOfFile != null && arrayOfFile.length > 0) ? arrayOfFile[0].getPath() : ((new File("/etc/release")).exists() ? "/etc/release" : "/etc/issue");
  }
  
  private static String filenameToFamily(String paramString) {
    if (paramString.isEmpty())
      return "Solaris"; 
    if ("issue".equalsIgnoreCase(paramString))
      return "Unknown"; 
    Properties properties = FileUtil.readPropertiesFromFilename("oshi.linux.filename.properties");
    String str = properties.getProperty(paramString.toLowerCase(Locale.ROOT));
    return (str != null) ? str : (paramString.substring(0, 1).toUpperCase(Locale.ROOT) + paramString.substring(1));
  }
  
  public List<OSService> getServices() {
    ArrayList<OSService> arrayList = new ArrayList();
    HashSet<String> hashSet = new HashSet();
    for (OSProcess oSProcess : getChildProcesses(1, OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.PID_ASC, 0)) {
      OSService oSService = new OSService(oSProcess.getName(), oSProcess.getProcessID(), OSService.State.RUNNING);
      arrayList.add(oSService);
      hashSet.add(oSProcess.getName());
    } 
    boolean bool = false;
    List list = ExecutingCommand.runNative("systemctl list-unit-files");
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length >= 2 && arrayOfString[0].endsWith(".service") && "enabled".equals(arrayOfString[1])) {
        String str1 = arrayOfString[0].substring(0, arrayOfString[0].length() - 8);
        int i = str1.lastIndexOf('.');
        String str2 = (i < 0 || i > str1.length() - 2) ? str1 : str1.substring(i + 1);
        if (!hashSet.contains(str1) && !hashSet.contains(str2)) {
          OSService oSService = new OSService(str1, 0, OSService.State.STOPPED);
          arrayList.add(oSService);
          bool = true;
        } 
      } 
    } 
    if (!bool) {
      File file = new File("/etc/init");
      if (file.exists() && file.isDirectory()) {
        for (File file1 : file.listFiles((paramFile, paramString) -> paramString.toLowerCase(Locale.ROOT).endsWith(".conf"))) {
          String str1 = file1.getName().substring(0, file1.getName().length() - 5);
          int i = str1.lastIndexOf('.');
          String str2 = (i < 0 || i > str1.length() - 2) ? str1 : str1.substring(i + 1);
          if (!hashSet.contains(str1) && !hashSet.contains(str2)) {
            OSService oSService = new OSService(str1, 0, OSService.State.STOPPED);
            arrayList.add(oSService);
          } 
        } 
      } else {
        LOG.error("Directory: /etc/init does not exist");
      } 
    } 
    return arrayList;
  }
  
  public static long getHz() {
    return USER_HZ;
  }
  
  public static long getPageSize() {
    return PAGE_SIZE;
  }
  
  static {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    try {
      if (GlobalConfig.get("oshi.os.linux.allowudev", true)) {
        try {
          Udev udev = Udev.INSTANCE;
          bool1 = true;
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
          LOG.warn("Did not find udev library in operating system. Some features may not work.");
        } 
      } else {
        LOG.info("Loading of udev not allowed by configuration. Some features may not work.");
      } 
      try {
        LinuxLibc.INSTANCE.gettid();
        bool2 = true;
      } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
        LOG.debug("Did not find gettid function in operating system. Using fallbacks.");
      } 
      bool3 = bool2;
      if (!bool2)
        try {
          bool3 = (LinuxLibc.INSTANCE.syscall(LinuxLibc.SYS_GETTID, new Object[0]).intValue() > 0) ? true : false;
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
          LOG.debug("Did not find working syscall gettid function in operating system. Using procfs");
        }  
    } catch (NoClassDefFoundError noClassDefFoundError) {
      LOG.error("Did not JNA classes. Investigate incompatible version or missing native dll.");
    } 
    HAS_UDEV = bool1;
    HAS_GETTID = bool2;
    HAS_SYSCALL_GETTID = bool3;
    Map map = Auxv.queryAuxv();
    long l2 = ((Long)map.getOrDefault(Integer.valueOf(17), Long.valueOf(0L))).longValue();
    if (l2 > 0L) {
      USER_HZ = l2;
    } else {
      USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
    } 
    long l3 = ((Long)Auxv.queryAuxv().getOrDefault(Integer.valueOf(6), Long.valueOf(0L))).longValue();
    if (l3 > 0L) {
      PAGE_SIZE = l3;
    } else {
      PAGE_SIZE = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf PAGE_SIZE"), 4096L);
    } 
  }
  
  static {
    long l1 = CpuStat.getBootTime();
    if (l1 == 0L)
      l1 = System.currentTimeMillis() / 1000L - (long)UpTime.getSystemUptimeSeconds(); 
    BOOTTIME = l1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */