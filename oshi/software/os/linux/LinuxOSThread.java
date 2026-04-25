package oshi.software.os.linux;

import java.util.Locale;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxOSThread extends AbstractOSThread {
  private static final int[] PROC_TASK_STAT_ORDERS = new int[(ThreadPidStat.values()).length];
  
  private final int threadId;
  
  private String name;
  
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
  
  public LinuxOSThread(int paramInt1, int paramInt2) {
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
  
  public long getStartTime() {
    return this.startTime;
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
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean updateAttributes() {
    this.name = FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.TASK_COMM, new Object[] { Integer.valueOf(getOwningProcessId()), Integer.valueOf(this.threadId) }));
    Map map = FileUtil.getKeyValueMapFromFile(String.format(Locale.ROOT, ProcPath.TASK_STATUS, new Object[] { Integer.valueOf(getOwningProcessId()), Integer.valueOf(this.threadId) }), ":");
    String str = FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.TASK_STAT, new Object[] { Integer.valueOf(getOwningProcessId()), Integer.valueOf(this.threadId) }));
    if (str.isEmpty()) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    long l1 = System.currentTimeMillis();
    long[] arrayOfLong = ParseUtil.parseStringToLongArray(str, PROC_TASK_STAT_ORDERS, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
    this.startTime = (LinuxOperatingSystem.BOOTTIME * LinuxOperatingSystem.getHz() + arrayOfLong[ThreadPidStat.START_TIME.ordinal()]) * 1000L / LinuxOperatingSystem.getHz();
    if (this.startTime >= l1)
      this.startTime = l1 - 1L; 
    this.minorFaults = arrayOfLong[ThreadPidStat.MINOR_FAULTS.ordinal()];
    this.majorFaults = arrayOfLong[ThreadPidStat.MAJOR_FAULT.ordinal()];
    this.startMemoryAddress = arrayOfLong[ThreadPidStat.START_CODE.ordinal()];
    long l2 = ParseUtil.parseLongOrDefault((String)map.get("voluntary_ctxt_switches"), 0L);
    long l3 = ParseUtil.parseLongOrDefault((String)map.get("nonvoluntary_ctxt_switches"), 0L);
    this.contextSwitches = l2 + l3;
    this.state = ProcessStat.getState(((String)map.getOrDefault("State", "U")).charAt(0));
    this.kernelTime = arrayOfLong[ThreadPidStat.KERNEL_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
    this.userTime = arrayOfLong[ThreadPidStat.USER_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
    this.upTime = l1 - this.startTime;
    this.priority = (int)arrayOfLong[ThreadPidStat.PRIORITY.ordinal()];
    return true;
  }
  
  static {
    for (ThreadPidStat threadPidStat : ThreadPidStat.values())
      PROC_TASK_STAT_ORDERS[threadPidStat.ordinal()] = threadPidStat.getOrder() - 1; 
  }
  
  private enum ThreadPidStat {
    PPID(4),
    MINOR_FAULTS(10),
    MAJOR_FAULT(12),
    USER_TIME(14),
    KERNEL_TIME(15),
    PRIORITY(18),
    THREAD_COUNT(20),
    START_TIME(22),
    VSZ(23),
    RSS(24),
    START_CODE(26);
    
    private final int order;
    
    ThreadPidStat(int param1Int1) {
      this.order = param1Int1;
    }
    
    public int getOrder() {
      return this.order;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */