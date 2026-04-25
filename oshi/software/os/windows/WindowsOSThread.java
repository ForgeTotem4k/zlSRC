package oshi.software.os.windows;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;

@ThreadSafe
public class WindowsOSThread extends AbstractOSThread {
  private final int threadId;
  
  private String name;
  
  private OSProcess.State state;
  
  private long startMemoryAddress;
  
  private long contextSwitches;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private int priority;
  
  public WindowsOSThread(int paramInt1, int paramInt2, String paramString, ThreadPerformanceData.PerfCounterBlock paramPerfCounterBlock) {
    super(paramInt1);
    this.threadId = paramInt2;
    updateAttributes(paramString, paramPerfCounterBlock);
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
  
  public long getKernelTime() {
    return this.kernelTime;
  }
  
  public long getUserTime() {
    return this.userTime;
  }
  
  public long getStartTime() {
    return this.startTime;
  }
  
  public long getUpTime() {
    return this.upTime;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean updateAttributes() {
    Set<Integer> set = Collections.singleton(Integer.valueOf(getOwningProcessId()));
    String str = this.name.split("/")[0];
    Map map = ThreadPerformanceData.buildThreadMapFromPerfCounters(set, str, getThreadId());
    return updateAttributes(str, (ThreadPerformanceData.PerfCounterBlock)map.get(Integer.valueOf(getThreadId())));
  }
  
  private boolean updateAttributes(String paramString, ThreadPerformanceData.PerfCounterBlock paramPerfCounterBlock) {
    if (paramPerfCounterBlock == null) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    if (paramPerfCounterBlock.getName().contains("/") || paramString.isEmpty()) {
      this.name = paramPerfCounterBlock.getName();
    } else {
      this.name = paramString + "/" + paramPerfCounterBlock.getName();
    } 
    if (paramPerfCounterBlock.getThreadWaitReason() == 5) {
      this.state = OSProcess.State.SUSPENDED;
    } else {
      switch (paramPerfCounterBlock.getThreadState()) {
        case 0:
          this.state = OSProcess.State.NEW;
          this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
          this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
          this.kernelTime = paramPerfCounterBlock.getKernelTime();
          this.userTime = paramPerfCounterBlock.getUserTime();
          this.startTime = paramPerfCounterBlock.getStartTime();
          this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
          this.priority = paramPerfCounterBlock.getPriority();
          return true;
        case 2:
        case 3:
          this.state = OSProcess.State.RUNNING;
          this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
          this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
          this.kernelTime = paramPerfCounterBlock.getKernelTime();
          this.userTime = paramPerfCounterBlock.getUserTime();
          this.startTime = paramPerfCounterBlock.getStartTime();
          this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
          this.priority = paramPerfCounterBlock.getPriority();
          return true;
        case 4:
          this.state = OSProcess.State.STOPPED;
          this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
          this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
          this.kernelTime = paramPerfCounterBlock.getKernelTime();
          this.userTime = paramPerfCounterBlock.getUserTime();
          this.startTime = paramPerfCounterBlock.getStartTime();
          this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
          this.priority = paramPerfCounterBlock.getPriority();
          return true;
        case 5:
          this.state = OSProcess.State.SLEEPING;
          this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
          this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
          this.kernelTime = paramPerfCounterBlock.getKernelTime();
          this.userTime = paramPerfCounterBlock.getUserTime();
          this.startTime = paramPerfCounterBlock.getStartTime();
          this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
          this.priority = paramPerfCounterBlock.getPriority();
          return true;
        case 1:
        case 6:
          this.state = OSProcess.State.WAITING;
          this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
          this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
          this.kernelTime = paramPerfCounterBlock.getKernelTime();
          this.userTime = paramPerfCounterBlock.getUserTime();
          this.startTime = paramPerfCounterBlock.getStartTime();
          this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
          this.priority = paramPerfCounterBlock.getPriority();
          return true;
      } 
      this.state = OSProcess.State.OTHER;
    } 
    this.startMemoryAddress = paramPerfCounterBlock.getStartAddress();
    this.contextSwitches = paramPerfCounterBlock.getContextSwitches();
    this.kernelTime = paramPerfCounterBlock.getKernelTime();
    this.userTime = paramPerfCounterBlock.getUserTime();
    this.startTime = paramPerfCounterBlock.getStartTime();
    this.upTime = System.currentTimeMillis() - paramPerfCounterBlock.getStartTime();
    this.priority = paramPerfCounterBlock.getPriority();
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */