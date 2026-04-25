package oshi.software.common;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSProcess;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractOSProcess implements OSProcess {
  private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad, Memoizer.defaultExpiration());
  
  private int processID;
  
  protected AbstractOSProcess(int paramInt) {
    this.processID = paramInt;
  }
  
  public int getProcessID() {
    return this.processID;
  }
  
  public double getProcessCpuLoadCumulative() {
    return ((Double)this.cumulativeCpuLoad.get()).doubleValue();
  }
  
  private double queryCumulativeCpuLoad() {
    return (getUpTime() > 0.0D) ? ((getKernelTime() + getUserTime()) / getUpTime()) : 0.0D;
  }
  
  public double getProcessCpuLoadBetweenTicks(OSProcess paramOSProcess) {
    return (paramOSProcess != null && this.processID == paramOSProcess.getProcessID() && getUpTime() > paramOSProcess.getUpTime()) ? ((getUserTime() - paramOSProcess.getUserTime() + getKernelTime() - paramOSProcess.getKernelTime()) / (getUpTime() - paramOSProcess.getUpTime())) : getProcessCpuLoadCumulative();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("OSProcess@");
    stringBuilder.append(Integer.toHexString(hashCode()));
    stringBuilder.append("[processID=").append(this.processID);
    stringBuilder.append(", name=").append(getName()).append(']');
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */