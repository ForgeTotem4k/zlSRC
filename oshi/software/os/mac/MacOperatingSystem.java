package oshi.software.os.mac;

import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.Who;
import oshi.driver.mac.WindowInfo;
import oshi.jna.Struct;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.ApplicationInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSDesktopWindow;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class MacOperatingSystem extends AbstractOperatingSystem {
  private static final Logger LOG = LoggerFactory.getLogger(MacOperatingSystem.class);
  
  public static final String MACOS_VERSIONS_PROPERTIES = "oshi.macos.versions.properties";
  
  private static final String SYSTEM_LIBRARY_LAUNCH_AGENTS = "/System/Library/LaunchAgents";
  
  private static final String SYSTEM_LIBRARY_LAUNCH_DAEMONS = "/System/Library/LaunchDaemons";
  
  private int maxProc = 1024;
  
  private final String osXVersion;
  
  private final int major;
  
  private final int minor;
  
  private final Supplier<List<ApplicationInfo>> installedAppsSupplier = Memoizer.memoize(MacInstalledApps::queryInstalledApps, Memoizer.installedAppsExpiration());
  
  private static final long BOOTTIME;
  
  public MacOperatingSystem() {
    String str = System.getProperty("os.version");
    int i = ParseUtil.getFirstIntValue(str);
    int j = ParseUtil.getNthIntValue(str, 2);
    if (i == 10 && j > 15) {
      String str1 = ExecutingCommand.getFirstAnswer("sw_vers -productVersion");
      if (!str1.isEmpty())
        str = str1; 
      i = ParseUtil.getFirstIntValue(str);
      j = ParseUtil.getNthIntValue(str, 2);
    } 
    this.osXVersion = str;
    this.major = i;
    this.minor = j;
    this.maxProc = SysctlUtil.sysctl("kern.maxproc", 4096);
  }
  
  public String queryManufacturer() {
    return "Apple";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    String str1 = (this.major > 10 || (this.major == 10 && this.minor >= 12)) ? "macOS" : System.getProperty("os.name");
    String str2 = parseCodeName();
    String str3 = SysctlUtil.sysctl("kern.osversion", "");
    return new Pair(str1, new OperatingSystem.OSVersionInfo(this.osXVersion, str2, str3));
  }
  
  private String parseCodeName() {
    Properties properties = FileUtil.readPropertiesFromFilename("oshi.macos.versions.properties");
    String str = null;
    if (this.major > 10) {
      str = properties.getProperty(Integer.toString(this.major));
    } else if (this.major == 10) {
      str = properties.getProperty(this.major + "." + this.minor);
    } 
    if (Util.isBlank(str))
      LOG.warn("Unable to parse version {}.{} to a codename.", Integer.valueOf(this.major), Integer.valueOf(this.minor)); 
    return str;
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt == 64 || (this.major == 10 && this.minor > 6)) ? 64 : ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32);
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new MacFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new MacInternetProtocolStats(isElevated());
  }
  
  public List<OSSession> getSessions() {
    return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
  }
  
  public List<OSProcess> queryAllProcesses() {
    ArrayList<OSProcess> arrayList = new ArrayList();
    int[] arrayOfInt = new int[this.maxProc];
    Arrays.fill(arrayOfInt, -1);
    int i = SystemB.INSTANCE.proc_listpids(1, 0, arrayOfInt, arrayOfInt.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;
    for (byte b = 0; b < i; b++) {
      if (arrayOfInt[b] >= 0) {
        OSProcess oSProcess = getProcess(arrayOfInt[b]);
        if (oSProcess != null)
          arrayList.add(oSProcess); 
      } 
    } 
    return arrayList;
  }
  
  public OSProcess getProcess(int paramInt) {
    MacOSProcess macOSProcess = new MacOSProcess(paramInt, this.major, this.minor, this);
    return macOSProcess.getState().equals(OSProcess.State.INVALID) ? null : (OSProcess)macOSProcess;
  }
  
  public List<OSProcess> queryChildProcesses(int paramInt) {
    List<OSProcess> list = queryAllProcesses();
    Set set = getChildrenOrDescendants(list, paramInt, false);
    return (List<OSProcess>)list.stream().filter(paramOSProcess -> paramSet.contains(Integer.valueOf(paramOSProcess.getProcessID()))).collect(Collectors.toList());
  }
  
  public List<OSProcess> queryDescendantProcesses(int paramInt) {
    List<OSProcess> list = queryAllProcesses();
    Set set = getChildrenOrDescendants(list, paramInt, true);
    return (List<OSProcess>)list.stream().filter(paramOSProcess -> paramSet.contains(Integer.valueOf(paramOSProcess.getProcessID()))).collect(Collectors.toList());
  }
  
  public int getProcessId() {
    return SystemB.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    return SystemB.INSTANCE.proc_listpids(1, 0, null, 0) / SystemB.INT_SIZE;
  }
  
  public int getThreadId() {
    OSThread oSThread = getCurrentThread();
    return (oSThread == null) ? 0 : oSThread.getThreadId();
  }
  
  public OSThread getCurrentThread() {
    return (OSThread)getCurrentProcess().getThreadDetails().stream().sorted(Comparator.comparingLong(OSThread::getStartTime)).findFirst().orElse(new MacOSThread(getProcessId()));
  }
  
  public int getThreadCount() {
    int[] arrayOfInt = new int[getProcessCount() + 10];
    int i = SystemB.INSTANCE.proc_listpids(1, 0, arrayOfInt, arrayOfInt.length) / SystemB.INT_SIZE;
    int j = 0;
    Struct.CloseableProcTaskInfo closeableProcTaskInfo = new Struct.CloseableProcTaskInfo();
    try {
      for (byte b = 0; b < i; b++) {
        int k = SystemB.INSTANCE.proc_pidinfo(arrayOfInt[b], 4, 0L, (Structure)closeableProcTaskInfo, closeableProcTaskInfo.size());
        if (k != -1)
          j += closeableProcTaskInfo.pti_threadnum; 
      } 
      closeableProcTaskInfo.close();
    } catch (Throwable throwable) {
      try {
        closeableProcTaskInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return j;
  }
  
  public long getSystemUptime() {
    return System.currentTimeMillis() / 1000L - BOOTTIME;
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new MacNetworkParams();
  }
  
  public List<OSService> getServices() {
    ArrayList<OSService> arrayList = new ArrayList();
    HashSet<String> hashSet = new HashSet();
    for (OSProcess oSProcess : getChildProcesses(1, OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.PID_ASC, 0)) {
      OSService oSService = new OSService(oSProcess.getName(), oSProcess.getProcessID(), OSService.State.RUNNING);
      arrayList.add(oSService);
      hashSet.add(oSProcess.getName());
    } 
    ArrayList arrayList1 = new ArrayList();
    File file = new File("/System/Library/LaunchAgents");
    if (file.exists() && file.isDirectory()) {
      arrayList1.addAll(Arrays.asList(file.listFiles((paramFile, paramString) -> paramString.toLowerCase(Locale.ROOT).endsWith(".plist"))));
    } else {
      LOG.error("Directory: /System/Library/LaunchAgents does not exist");
    } 
    file = new File("/System/Library/LaunchDaemons");
    if (file.exists() && file.isDirectory()) {
      arrayList1.addAll(Arrays.asList(file.listFiles((paramFile, paramString) -> paramString.toLowerCase(Locale.ROOT).endsWith(".plist"))));
    } else {
      LOG.error("Directory: /System/Library/LaunchDaemons does not exist");
    } 
    for (File file1 : arrayList1) {
      String str1 = file1.getName().substring(0, file1.getName().length() - 6);
      int i = str1.lastIndexOf('.');
      String str2 = (i < 0 || i > str1.length() - 2) ? str1 : str1.substring(i + 1);
      if (!hashSet.contains(str1) && !hashSet.contains(str2)) {
        OSService oSService = new OSService(str1, 0, OSService.State.STOPPED);
        arrayList.add(oSService);
      } 
    } 
    return arrayList;
  }
  
  public List<OSDesktopWindow> getDesktopWindows(boolean paramBoolean) {
    return WindowInfo.queryDesktopWindows(paramBoolean);
  }
  
  public List<ApplicationInfo> getInstalledApplications() {
    return this.installedAppsSupplier.get();
  }
  
  static {
    Struct.CloseableTimeval closeableTimeval = new Struct.CloseableTimeval();
    try {
      if (!SysctlUtil.sysctl("kern.boottime", (Structure)closeableTimeval) || closeableTimeval.tv_sec.longValue() == 0L) {
        BOOTTIME = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L);
      } else {
        BOOTTIME = closeableTimeval.tv_sec.longValue();
      } 
      closeableTimeval.close();
    } catch (Throwable throwable) {
      try {
        closeableTimeval.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */