package oshi.hardware.common;

import java.util.Arrays;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.Sensors;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractSensors implements Sensors {
  private final Supplier<Double> cpuTemperature = Memoizer.memoize(this::queryCpuTemperature, Memoizer.defaultExpiration());
  
  private final Supplier<int[]> fanSpeeds = Memoizer.memoize(this::queryFanSpeeds, Memoizer.defaultExpiration());
  
  private final Supplier<Double> cpuVoltage = Memoizer.memoize(this::queryCpuVoltage, Memoizer.defaultExpiration());
  
  public double getCpuTemperature() {
    return ((Double)this.cpuTemperature.get()).doubleValue();
  }
  
  protected abstract double queryCpuTemperature();
  
  public int[] getFanSpeeds() {
    return this.fanSpeeds.get();
  }
  
  protected abstract int[] queryFanSpeeds();
  
  public double getCpuVoltage() {
    return ((Double)this.cpuVoltage.get()).doubleValue();
  }
  
  protected abstract double queryCpuVoltage();
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("CPU Temperature=").append(getCpuTemperature()).append("C, ");
    stringBuilder.append("Fan Speeds=").append(Arrays.toString(getFanSpeeds())).append(", ");
    stringBuilder.append("CPU Voltage=").append(getCpuVoltage());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */