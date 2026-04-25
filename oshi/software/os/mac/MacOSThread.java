package oshi.software.os.mac;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;

@ThreadSafe
public class MacOSThread extends AbstractOSThread {
  private final int threadId;
  
  private final OSProcess.State state;
  
  private final long kernelTime;
  
  private final long userTime;
  
  private final long startTime;
  
  private final long upTime;
  
  private final int priority;
  
  public MacOSThread(int paramInt1, int paramInt2, OSProcess.State paramState, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt3) {
    super(paramInt1);
    this.threadId = paramInt2;
    this.state = paramState;
    this.kernelTime = paramLong1;
    this.userTime = paramLong2;
    this.startTime = paramLong3;
    this.upTime = paramLong4;
    this.priority = paramInt3;
  }
  
  public MacOSThread(int paramInt) {
    this(paramInt, 0, OSProcess.State.INVALID, 0L, 0L, 0L, 0L, 0);
  }
  
  public int getThreadId() {
    return this.threadId;
  }
  
  public OSProcess.State getState() {
    return this.state;
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */