package oshi.software.common;

import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSThread;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractOSThread implements OSThread {
  private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad, Memoizer.defaultExpiration());
  
  private final int owningProcessId;
  
  protected AbstractOSThread(int paramInt) {
    this.owningProcessId = paramInt;
  }
  
  public int getOwningProcessId() {
    return this.owningProcessId;
  }
  
  public double getThreadCpuLoadCumulative() {
    return ((Double)this.cumulativeCpuLoad.get()).doubleValue();
  }
  
  private double queryCumulativeCpuLoad() {
    return (getUpTime() > 0.0D) ? ((getKernelTime() + getUserTime()) / getUpTime()) : 0.0D;
  }
  
  public double getThreadCpuLoadBetweenTicks(OSThread paramOSThread) {
    return (paramOSThread != null && this.owningProcessId == paramOSThread.getOwningProcessId() && getThreadId() == paramOSThread.getThreadId() && getUpTime() > paramOSThread.getUpTime()) ? ((getUserTime() - paramOSThread.getUserTime() + getKernelTime() - paramOSThread.getKernelTime()) / (getUpTime() - paramOSThread.getUpTime())) : getThreadCpuLoadCumulative();
  }
  
  public String toString() {
    return "OSThread [threadId=" + getThreadId() + ", owningProcessId=" + getOwningProcessId() + ", name=" + getName() + ", state=" + getState() + ", kernelTime=" + getKernelTime() + ", userTime=" + getUserTime() + ", upTime=" + getUpTime() + ", startTime=" + getStartTime() + ", startMemoryAddress=0x" + String.format(Locale.ROOT, "%x", new Object[] { Long.valueOf(getStartMemoryAddress()) }) + ", contextSwitches=" + getContextSwitches() + ", minorFaults=" + getMinorFaults() + ", majorFaults=" + getMajorFaults() + "]";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */