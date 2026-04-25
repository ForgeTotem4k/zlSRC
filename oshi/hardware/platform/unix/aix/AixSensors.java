package oshi.hardware.platform.unix.aix;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;

@ThreadSafe
final class AixSensors extends AbstractSensors {
  private final Supplier<List<String>> lscfg;
  
  AixSensors(Supplier<List<String>> paramSupplier) {
    this.lscfg = paramSupplier;
  }
  
  public double queryCpuTemperature() {
    return 0.0D;
  }
  
  public int[] queryFanSpeeds() {
    byte b = 0;
    for (String str : this.lscfg.get()) {
      if (str.contains("Air Mover"))
        b++; 
    } 
    return new int[b];
  }
  
  public double queryCpuVoltage() {
    return 0.0D;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */