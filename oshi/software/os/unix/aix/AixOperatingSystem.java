package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Uptime;
import oshi.driver.unix.aix.Who;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatProcess;
import oshi.jna.platform.unix.AixLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Pair;

@ThreadSafe
public class AixOperatingSystem extends AbstractOperatingSystem {
  private final Supplier<Perfstat.perfstat_partition_config_t> config = Memoizer.memoize(PerfstatConfig::queryConfig);
  
  private final Supplier<Perfstat.perfstat_process_t[]> procCpu = Memoizer.memoize(PerfstatProcess::queryProcesses, Memoizer.defaultExpiration());
  
  private static final long BOOTTIME = querySystemBootTimeMillis() / 1000L;
  
  public String queryManufacturer() {
    return "IBM";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    Perfstat.perfstat_partition_config_t perfstat_partition_config_t = this.config.get();
    String str1 = System.getProperty("os.name");
    String str2 = System.getProperty("os.arch");
    String str3 = System.getProperty("os.version");
    if (Util.isBlank(str3))
      str3 = ExecutingCommand.getFirstAnswer("oslevel"); 
    String str4 = Native.toString(perfstat_partition_config_t.OSBuild);
    if (Util.isBlank(str4)) {
      str4 = ExecutingCommand.getFirstAnswer("oslevel -s");
    } else {
      int i = str4.lastIndexOf(' ');
      if (i > 0 && i < str4.length())
        str4 = str4.substring(i + 1); 
    } 
    return new Pair(str1, new OperatingSystem.OSVersionInfo(str3, str2, str4));
  }
  
  protected int queryBitness(int paramInt) {
    return (paramInt == 64) ? 64 : (((((Perfstat.perfstat_partition_config_t)this.config.get()).conf & 0x800000) > 0) ? 64 : 32);
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new AixFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new AixInternetProtocolStats();
  }
  
  public List<OSProcess> queryAllProcesses() {
    return getProcessListFromProcfs(-1);
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
    List<OSProcess> list = getProcessListFromProcfs(paramInt);
    return list.isEmpty() ? null : list.get(0);
  }
  
  private List<OSProcess> getProcessListFromProcfs(int paramInt) {
    ArrayList<AixOSProcess> arrayList = new ArrayList();
    Perfstat.perfstat_process_t[] arrayOfPerfstat_process_t = this.procCpu.get();
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Perfstat.perfstat_process_t perfstat_process_t : arrayOfPerfstat_process_t) {
      int i = (int)perfstat_process_t.pid;
      if (paramInt < 0 || i == paramInt)
        hashMap.put(Integer.valueOf(i), new Pair(Long.valueOf((long)perfstat_process_t.ucpu_time), Long.valueOf((long)perfstat_process_t.scpu_time))); 
    } 
    for (Map.Entry<Object, Object> entry : hashMap.entrySet()) {
      AixOSProcess aixOSProcess = new AixOSProcess(((Integer)entry.getKey()).intValue(), (Pair<Long, Long>)entry.getValue(), this.procCpu, this);
      if (aixOSProcess.getState() != OSProcess.State.INVALID)
        arrayList.add(aixOSProcess); 
    } 
    return (List)arrayList;
  }
  
  public int getProcessId() {
    return AixLibc.INSTANCE.getpid();
  }
  
  public int getProcessCount() {
    return ((Perfstat.perfstat_process_t[])this.procCpu.get()).length;
  }
  
  public int getThreadId() {
    return AixLibc.INSTANCE.thread_self();
  }
  
  public OSThread getCurrentThread() {
    OSProcess oSProcess = getCurrentProcess();
    int i = getThreadId();
    return (OSThread)oSProcess.getThreadDetails().stream().filter(paramOSThread -> (paramOSThread.getThreadId() == paramInt)).findFirst().orElse(new AixOSThread(oSProcess.getProcessID(), i));
  }
  
  public int getThreadCount() {
    long l = 0L;
    for (Perfstat.perfstat_process_t perfstat_process_t : (Perfstat.perfstat_process_t[])this.procCpu.get())
      l += perfstat_process_t.num_threads; 
    return (int)l;
  }
  
  public long getSystemUptime() {
    return System.currentTimeMillis() / 1000L - BOOTTIME;
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  private static long querySystemBootTimeMillis() {
    long l = Who.queryBootTime();
    return (l >= 1000L) ? l : (System.currentTimeMillis() - Uptime.queryUpTime());
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new AixNetworkParams();
  }
  
  public List<OSService> getServices() {
    ArrayList<OSService> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("lssrc -a");
    if (list.size() > 1) {
      list.remove(0);
      for (String str : list) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
        if (str.contains("active")) {
          if (arrayOfString.length == 4) {
            arrayList.add(new OSService(arrayOfString[0], ParseUtil.parseIntOrDefault(arrayOfString[2], 0), OSService.State.RUNNING));
            continue;
          } 
          if (arrayOfString.length == 3)
            arrayList.add(new OSService(arrayOfString[0], ParseUtil.parseIntOrDefault(arrayOfString[1], 0), OSService.State.RUNNING)); 
          continue;
        } 
        if (str.contains("inoperative"))
          arrayList.add(new OSService(arrayOfString[0], 0, OSService.State.STOPPED)); 
      } 
    } 
    File file = new File("/etc/rc.d/init.d");
    File[] arrayOfFile;
    if (file.exists() && file.isDirectory() && (arrayOfFile = file.listFiles()) != null)
      for (File file1 : arrayOfFile) {
        String str = ExecutingCommand.getFirstAnswer(file1.getAbsolutePath() + " status");
        if (str.contains("running")) {
          arrayList.add(new OSService(file1.getName(), ParseUtil.parseLastInt(str, 0), OSService.State.RUNNING));
        } else {
          arrayList.add(new OSService(file1.getName(), 0, OSService.State.STOPPED));
        } 
      }  
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */