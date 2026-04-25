package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.Kstat2;
import com.sun.jna.platform.unix.solaris.LibKstat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.unix.solaris.Who;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.Constants;
import oshi.util.ExecutingCommand;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class SolarisOperatingSystem extends AbstractOperatingSystem {
  private static final String VERSION;
  
  private static final String BUILD_NUMBER;
  
  private static final boolean ALLOW_KSTAT2 = GlobalConfig.get("oshi.os.solaris.allowKstat2", true);
  
  public static final boolean HAS_KSTAT2;
  
  private static final Supplier<Pair<Long, Long>> BOOT_UPTIME = Memoizer.memoize(SolarisOperatingSystem::queryBootAndUptime, Memoizer.defaultExpiration());
  
  private static final long BOOTTIME = querySystemBootTime();
  
  public String queryManufacturer() {
    return "Oracle";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    return new Pair("SunOS", new OperatingSystem.OSVersionInfo(VERSION, "Solaris", BUILD_NUMBER));
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt == 64) ? 64 : ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("isainfo -b"), 32);
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new SolarisFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new SolarisInternetProtocolStats();
  }
  
  public List<OSSession> getSessions() {
    return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
  }
  
  public OSProcess getProcess(int paramInt) {
    List<OSProcess> list = getProcessListFromProcfs(paramInt);
    return list.isEmpty() ? null : list.get(0);
  }
  
  public List<OSProcess> queryAllProcesses() {
    return queryAllProcessesFromPrStat();
  }
  
  public List<OSProcess> queryChildProcesses(int paramInt) {
    List<OSProcess> list = queryAllProcessesFromPrStat();
    Set set = getChildrenOrDescendants(list, paramInt, false);
    return (List<OSProcess>)list.stream().filter(paramOSProcess -> paramSet.contains(Integer.valueOf(paramOSProcess.getProcessID()))).collect(Collectors.toList());
  }
  
  public List<OSProcess> queryDescendantProcesses(int paramInt) {
    List<OSProcess> list = queryAllProcessesFromPrStat();
    Set set = getChildrenOrDescendants(list, paramInt, true);
    return (List<OSProcess>)list.stream().filter(paramOSProcess -> paramSet.contains(Integer.valueOf(paramOSProcess.getProcessID()))).collect(Collectors.toList());
  }
  
  private List<OSProcess> queryAllProcessesFromPrStat() {
    return getProcessListFromProcfs(-1);
  }
  
  private List<OSProcess> getProcessListFromProcfs(int paramInt) {
    ArrayList<OSProcess> arrayList = new ArrayList();
    File[] arrayOfFile = null;
    if (paramInt < 0) {
      File file = new File("/proc");
      arrayOfFile = file.listFiles(paramFile -> Constants.DIGITS.matcher(paramFile.getName()).matches());
    } else {
      File file = new File("/proc/" + paramInt);
      if (file.exists()) {
        arrayOfFile = new File[1];
        arrayOfFile[0] = file;
      } 
    } 
    if (arrayOfFile == null)
      return arrayList; 
    for (File file : arrayOfFile) {
      int i = ParseUtil.parseIntOrDefault(file.getName(), 0);
      SolarisOSProcess solarisOSProcess = new SolarisOSProcess(i, this);
      if (solarisOSProcess.getState() != OSProcess.State.INVALID)
        arrayList.add(solarisOSProcess); 
    } 
    return arrayList;
  }
  
  public int getProcessId() {
    return SolarisLibc.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    return (ProcessStat.getPidFiles()).length;
  }
  
  public int getThreadId() {
    return SolarisLibc.INSTANCE.thr_self();
  }
  
  public OSThread getCurrentThread() {
    return (OSThread)new SolarisOSThread(getProcessId(), getThreadId());
  }
  
  public int getThreadCount() {
    List list = ExecutingCommand.runNative("ps -eLo pid");
    return !list.isEmpty() ? (list.size() - 1) : getProcessCount();
  }
  
  public long getSystemUptime() {
    return querySystemUptime();
  }
  
  private static long querySystemUptime() {
    if (HAS_KSTAT2)
      return ((Long)((Pair)BOOT_UPTIME.get()).getB()).longValue(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup("unix", 0, "system_misc");
      if (kstat != null && kstatChain.read(kstat)) {
        long l = kstat.ks_snaptime / 1000000000L;
        if (kstatChain != null)
          kstatChain.close(); 
        return l;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return 0L;
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  private static long querySystemBootTime() {
    if (HAS_KSTAT2)
      return ((Long)((Pair)BOOT_UPTIME.get()).getA()).longValue(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup("unix", 0, "system_misc");
      if (kstat != null && kstatChain.read(kstat)) {
        long l = KstatUtil.dataLookupLong(kstat, "boot_time");
        if (kstatChain != null)
          kstatChain.close(); 
        return l;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return System.currentTimeMillis() / 1000L - querySystemUptime();
  }
  
  private static Pair<Long, Long> queryBootAndUptime() {
    Object[] arrayOfObject = KstatUtil.queryKstat2("/misc/unix/system_misc", new String[] { "boot_time", "snaptime" });
    long l1 = (arrayOfObject[0] == null) ? System.currentTimeMillis() : ((Long)arrayOfObject[0]).longValue();
    long l2 = (arrayOfObject[1] == null) ? 0L : (((Long)arrayOfObject[1]).longValue() / 1000000000L);
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new SolarisNetworkParams();
  }
  
  public List<OSService> getServices() {
    ArrayList<OSService> arrayList = new ArrayList();
    ArrayList<String> arrayList1 = new ArrayList();
    File file = new File("/etc/init.d");
    File[] arrayOfFile;
    if (file.exists() && file.isDirectory() && (arrayOfFile = file.listFiles()) != null)
      for (File file1 : arrayOfFile)
        arrayList1.add(file1.getName());  
    List list = ExecutingCommand.runNative("svcs -p");
    for (String str : list) {
      if (str.startsWith("online")) {
        int i = str.lastIndexOf(":/");
        if (i > 0) {
          String str1 = str.substring(i + 1);
          if (str1.endsWith(":default"))
            str1 = str1.substring(0, str1.length() - 8); 
          arrayList.add(new OSService(str1, 0, OSService.State.STOPPED));
        } 
        continue;
      } 
      if (str.startsWith(" ")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
        if (arrayOfString.length == 3)
          arrayList.add(new OSService(arrayOfString[2], ParseUtil.parseIntOrDefault(arrayOfString[1], 0), OSService.State.RUNNING)); 
        continue;
      } 
      if (str.startsWith("legacy_run"))
        for (String str1 : arrayList1) {
          if (str.endsWith(str1))
            arrayList.add(new OSService(str1, 0, OSService.State.STOPPED)); 
        }  
    } 
    return arrayList;
  }
  
  static {
    Kstat2 kstat2;
    String[] arrayOfString = ParseUtil.whitespaces.split(ExecutingCommand.getFirstAnswer("uname -rv"));
    VERSION = arrayOfString[0];
    BUILD_NUMBER = (arrayOfString.length > 1) ? arrayOfString[1] : "";
  }
  
  static {
    arrayOfString = null;
    try {
      if (ALLOW_KSTAT2)
        kstat2 = Kstat2.INSTANCE; 
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
    HAS_KSTAT2 = (kstat2 != null);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */