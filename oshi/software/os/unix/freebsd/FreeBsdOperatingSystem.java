package oshi.software.os.unix.freebsd;

import com.sun.jna.Structure;
import com.sun.jna.ptr.NativeLongByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.freebsd.Who;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class FreeBsdOperatingSystem extends AbstractOperatingSystem {
  private static final Logger LOG = LoggerFactory.getLogger(FreeBsdOperatingSystem.class);
  
  private static final long BOOTTIME = querySystemBootTime();
  
  static final String PS_COMMAND_ARGS;
  
  public String queryManufacturer() {
    return "Unix/BSD";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    String str1 = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
    String str2 = BsdSysctlUtil.sysctl("kern.osrelease", "");
    String str3 = BsdSysctlUtil.sysctl("kern.version", "");
    String str4 = str3.split(":")[0].replace(str1, "").replace(str2, "").trim();
    return new Pair(str1, new OperatingSystem.OSVersionInfo(str2, null, str4));
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1) ? paramInt : 64;
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new FreeBsdFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new FreeBsdInternetProtocolStats();
  }
  
  public List<OSSession> getSessions() {
    return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
  }
  
  public List<OSProcess> queryAllProcesses() {
    return getProcessListFromPS(-1);
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
  
  public OSProcess getProcess(int paramInt) {
    List<OSProcess> list = getProcessListFromPS(paramInt);
    return list.isEmpty() ? null : list.get(0);
  }
  
  private List<OSProcess> getProcessListFromPS(int paramInt) {
    String str = "ps -awwxo " + PS_COMMAND_ARGS;
    if (paramInt >= 0)
      str = str + " -p " + paramInt; 
    Predicate predicate = paramMap -> paramMap.containsKey(PsKeywords.ARGS);
    return (List<OSProcess>)ExecutingCommand.runNative(str).stream().skip(1L).parallel().map(paramString -> ParseUtil.stringToEnumMap(PsKeywords.class, paramString.trim(), ' ')).filter(predicate).map(paramMap -> new FreeBsdOSProcess((paramInt < 0) ? ParseUtil.parseIntOrDefault((String)paramMap.get(PsKeywords.PID), 0) : paramInt, paramMap, this)).filter(OperatingSystem.ProcessFiltering.VALID_PROCESS).collect(Collectors.toList());
  }
  
  public int getProcessId() {
    return FreeBsdLibc.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    List list = ExecutingCommand.runNative("ps -axo pid");
    return !list.isEmpty() ? (list.size() - 1) : 0;
  }
  
  public int getThreadId() {
    NativeLongByReference nativeLongByReference = new NativeLongByReference();
    return (FreeBsdLibc.INSTANCE.thr_self(nativeLongByReference) < 0) ? 0 : nativeLongByReference.getValue().intValue();
  }
  
  public OSThread getCurrentThread() {
    OSProcess oSProcess = getCurrentProcess();
    int i = getThreadId();
    return (OSThread)oSProcess.getThreadDetails().stream().filter(paramOSThread -> (paramOSThread.getThreadId() == paramInt)).findFirst().orElse(new FreeBsdOSThread(oSProcess.getProcessID(), i));
  }
  
  public int getThreadCount() {
    int i = 0;
    for (String str : ExecutingCommand.runNative("ps -axo nlwp"))
      i += ParseUtil.parseIntOrDefault(str.trim(), 0); 
    return i;
  }
  
  public long getSystemUptime() {
    return System.currentTimeMillis() / 1000L - BOOTTIME;
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  private static long querySystemBootTime() {
    FreeBsdLibc.Timeval timeval = new FreeBsdLibc.Timeval();
    return (!BsdSysctlUtil.sysctl("kern.boottime", (Structure)timeval) || timeval.tv_sec == 0L) ? ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L) : timeval.tv_sec;
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new FreeBsdNetworkParams();
  }
  
  public List<OSService> getServices() {
    ArrayList<OSService> arrayList = new ArrayList();
    HashSet<String> hashSet = new HashSet();
    for (OSProcess oSProcess : getChildProcesses(1, OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.PID_ASC, 0)) {
      OSService oSService = new OSService(oSProcess.getName(), oSProcess.getProcessID(), OSService.State.RUNNING);
      arrayList.add(oSService);
      hashSet.add(oSProcess.getName());
    } 
    File file = new File("/etc/rc.d");
    File[] arrayOfFile;
    if (file.exists() && file.isDirectory() && (arrayOfFile = file.listFiles()) != null) {
      for (File file1 : arrayOfFile) {
        String str = file1.getName();
        if (!hashSet.contains(str)) {
          OSService oSService = new OSService(str, 0, OSService.State.STOPPED);
          arrayList.add(oSService);
        } 
      } 
    } else {
      LOG.error("Directory: /etc/init does not exist");
    } 
    return arrayList;
  }
  
  static {
    PS_COMMAND_ARGS = Arrays.<PsKeywords>stream(PsKeywords.values()).map(Enum::name).map(paramString -> paramString.toLowerCase(Locale.ROOT)).collect(Collectors.joining(","));
  }
  
  enum PsKeywords {
    STATE, PID, PPID, USER, UID, GROUP, GID, NLWP, PRI, VSZ, RSS, ETIMES, SYSTIME, TIME, COMM, MAJFLT, MINFLT, NVCSW, NIVCSW, ARGS;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */