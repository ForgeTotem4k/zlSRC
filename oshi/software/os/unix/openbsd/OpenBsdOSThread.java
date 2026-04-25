package oshi.software.os.unix.openbsd;

import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class OpenBsdOSThread extends AbstractOSThread {
  private int threadId;
  
  private String name = "";
  
  private OSProcess.State state = OSProcess.State.INVALID;
  
  private long minorFaults;
  
  private long majorFaults;
  
  private long startMemoryAddress;
  
  private long contextSwitches;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private int priority;
  
  public OpenBsdOSThread(int paramInt, Map<OpenBsdOSProcess.PsThreadColumns, String> paramMap) {
    super(paramInt);
    updateAttributes(paramMap);
  }
  
  public OpenBsdOSThread(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.threadId = paramInt2;
    updateAttributes();
  }
  
  public int getThreadId() {
    return this.threadId;
  }
  
  public String getName() {
    return this.name;
  }
  
  public OSProcess.State getState() {
    return this.state;
  }
  
  public long getStartMemoryAddress() {
    return this.startMemoryAddress;
  }
  
  public long getContextSwitches() {
    return this.contextSwitches;
  }
  
  public long getMinorFaults() {
    return this.minorFaults;
  }
  
  public long getMajorFaults() {
    return this.majorFaults;
  }
  
  public long getKernelTime() {
    return this.kernelTime;
  }
  
  public long getUserTime() {
    return this.userTime;
  }
  
  public long getUpTime() {
    return this.upTime;
  }
  
  public long getStartTime() {
    return this.startTime;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean updateAttributes() {
    String str1 = "ps -aHwwxo " + OpenBsdOSProcess.PS_THREAD_COLUMNS + " -p " + getOwningProcessId();
    List list = ExecutingCommand.runNative(str1);
    String str2 = Integer.toString(this.threadId);
    for (String str : list) {
      Map<OpenBsdOSProcess.PsThreadColumns, String> map = ParseUtil.stringToEnumMap(OpenBsdOSProcess.PsThreadColumns.class, str.trim(), ' ');
      if (map.containsKey(OpenBsdOSProcess.PsThreadColumns.ARGS) && str2.equals(map.get(OpenBsdOSProcess.PsThreadColumns.TID)))
        return updateAttributes(map); 
    } 
    this.state = OSProcess.State.INVALID;
    return false;
  }
  
  private boolean updateAttributes(Map<OpenBsdOSProcess.PsThreadColumns, String> paramMap) {
    this.threadId = ParseUtil.parseIntOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.TID), 0);
    switch (((String)paramMap.get(OpenBsdOSProcess.PsThreadColumns.STATE)).charAt(0)) {
      case 'R':
        this.state = OSProcess.State.RUNNING;
        break;
      case 'I':
      case 'S':
        this.state = OSProcess.State.SLEEPING;
        break;
      case 'D':
      case 'L':
      case 'U':
        this.state = OSProcess.State.WAITING;
        break;
      case 'Z':
        this.state = OSProcess.State.ZOMBIE;
        break;
      case 'T':
        this.state = OSProcess.State.STOPPED;
        break;
      default:
        this.state = OSProcess.State.OTHER;
        break;
    } 
    long l1 = ParseUtil.parseDHMSOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.ETIME), 0L);
    this.upTime = (l1 < 1L) ? 1L : l1;
    long l2 = System.currentTimeMillis();
    this.startTime = l2 - this.upTime;
    this.kernelTime = 0L;
    this.userTime = ParseUtil.parseDHMSOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.CPUTIME), 0L);
    this.startMemoryAddress = 0L;
    long l3 = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.NIVCSW), 0L);
    long l4 = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.NVCSW), 0L);
    this.contextSwitches = l4 + l3;
    this.majorFaults = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.MAJFLT), 0L);
    this.minorFaults = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.MINFLT), 0L);
    this.priority = ParseUtil.parseIntOrDefault(paramMap.get(OpenBsdOSProcess.PsThreadColumns.PRI), 0);
    this.name = paramMap.get(OpenBsdOSProcess.PsThreadColumns.ARGS);
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\openbsd\OpenBsdOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */