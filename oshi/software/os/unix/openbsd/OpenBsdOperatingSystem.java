package oshi.software.os.unix.openbsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class OpenBsdOperatingSystem extends AbstractOperatingSystem {
  private static final Logger LOG = LoggerFactory.getLogger(OpenBsdOperatingSystem.class);
  
  private static final long BOOTTIME = querySystemBootTime();
  
  static final String PS_COMMAND_ARGS;
  
  public String queryManufacturer() {
    return "Unix/BSD";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 1;
    String str1 = OpenBsdSysctlUtil.sysctl(arrayOfInt, "OpenBSD");
    arrayOfInt[1] = 2;
    String str2 = OpenBsdSysctlUtil.sysctl(arrayOfInt, "");
    arrayOfInt[1] = 4;
    String str3 = OpenBsdSysctlUtil.sysctl(arrayOfInt, "");
    String str4 = str3.split(":")[0].replace(str1, "").replace(str2, "").trim();
    return new Pair(str1, new OperatingSystem.OSVersionInfo(str2, null, str4));
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1) ? paramInt : 64;
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new OpenBsdFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new OpenBsdInternetProtocolStats();
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
    ArrayList<OSProcess> arrayList = new ArrayList();
    String str = "ps -awwxo " + PS_COMMAND_ARGS;
    if (paramInt >= 0)
      str = str + " -p " + paramInt; 
    List list = ExecutingCommand.runNative(str);
    if (list.isEmpty() || list.size() < 2)
      return arrayList; 
    list.remove(0);
    for (String str1 : list) {
      Map<PsKeywords, String> map = ParseUtil.stringToEnumMap(PsKeywords.class, str1.trim(), ' ');
      if (map.containsKey(PsKeywords.ARGS))
        arrayList.add(new OpenBsdOSProcess((paramInt < 0) ? ParseUtil.parseIntOrDefault((String)map.get(PsKeywords.PID), 0) : paramInt, map, this)); 
    } 
    return arrayList;
  }
  
  public int getProcessId() {
    return OpenBsdLibc.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    List list = ExecutingCommand.runNative("ps -axo pid");
    return !list.isEmpty() ? (list.size() - 1) : 0;
  }
  
  public int getThreadId() {
    return OpenBsdLibc.INSTANCE.getthrid();
  }
  
  public OSThread getCurrentThread() {
    OSProcess oSProcess = getCurrentProcess();
    int i = getThreadId();
    return (OSThread)oSProcess.getThreadDetails().stream().filter(paramOSThread -> (paramOSThread.getThreadId() == paramInt)).findFirst().orElse(new OpenBsdOSThread(oSProcess.getProcessID(), i));
  }
  
  public int getThreadCount() {
    List list = ExecutingCommand.runNative("ps -axHo tid");
    return !list.isEmpty() ? (list.size() - 1) : 0;
  }
  
  public long getSystemUptime() {
    return System.currentTimeMillis() / 1000L - BOOTTIME;
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  private static long querySystemBootTime() {
    return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L);
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new OpenBsdNetworkParams();
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
      LOG.error("Directory: /etc/rc.d does not exist");
    } 
    return arrayList;
  }
  
  static {
    PS_COMMAND_ARGS = Arrays.<PsKeywords>stream(PsKeywords.values()).map(Enum::name).map(paramString -> paramString.toLowerCase(Locale.ROOT)).collect(Collectors.joining(","));
  }
  
  enum PsKeywords {
    STATE, PID, PPID, USER, UID, GROUP, GID, PRI, VSZ, RSS, ETIME, CPUTIME, COMM, MAJFLT, MINFLT, NVCSW, NIVCSW, ARGS;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\openbsd\OpenBsdOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */