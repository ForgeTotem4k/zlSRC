package oshi.software.os.unix.freebsd;

import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class FreeBsdOSThread extends AbstractOSThread {
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
  
  public FreeBsdOSThread(int paramInt, Map<FreeBsdOSProcess.PsThreadColumns, String> paramMap) {
    super(paramInt);
    updateAttributes(paramMap);
  }
  
  public FreeBsdOSThread(int paramInt1, int paramInt2) {
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
    List list = ExecutingCommand.runNative("ps -awwxo " + FreeBsdOSProcess.PS_THREAD_COLUMNS + " -H -p " + getOwningProcessId());
    String str = Integer.toString(this.threadId);
    for (String str1 : list) {
      Map<FreeBsdOSProcess.PsThreadColumns, String> map = ParseUtil.stringToEnumMap(FreeBsdOSProcess.PsThreadColumns.class, str1.trim(), ' ');
      if (map.containsKey(FreeBsdOSProcess.PsThreadColumns.PRI) && str.equals(map.get(FreeBsdOSProcess.PsThreadColumns.LWP)))
        return updateAttributes(map); 
    } 
    this.state = OSProcess.State.INVALID;
    return false;
  }
  
  private boolean updateAttributes(Map<FreeBsdOSProcess.PsThreadColumns, String> paramMap) {
    this.name = paramMap.get(FreeBsdOSProcess.PsThreadColumns.TDNAME);
    this.threadId = ParseUtil.parseIntOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.LWP), 0);
    switch (((String)paramMap.get(FreeBsdOSProcess.PsThreadColumns.STATE)).charAt(0)) {
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
    long l1 = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.ETIMES), 0L);
    this.upTime = (l1 < 1L) ? 1L : l1;
    long l2 = System.currentTimeMillis();
    this.startTime = l2 - this.upTime;
    this.kernelTime = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.SYSTIME), 0L);
    this.userTime = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.TIME), 0L) - this.kernelTime;
    this.startMemoryAddress = ParseUtil.hexStringToLong(paramMap.get(FreeBsdOSProcess.PsThreadColumns.TDADDR), 0L);
    long l3 = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.NIVCSW), 0L);
    long l4 = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.NVCSW), 0L);
    this.contextSwitches = l4 + l3;
    this.majorFaults = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.MAJFLT), 0L);
    this.minorFaults = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.MINFLT), 0L);
    this.priority = ParseUtil.parseIntOrDefault(paramMap.get(FreeBsdOSProcess.PsThreadColumns.PRI), 0);
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */