package oshi.hardware.common;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import oshi.util.Memoizer;

@Immutable
public abstract class AbstractComputerSystem implements ComputerSystem {
  private final Supplier<Firmware> firmware = Memoizer.memoize(this::createFirmware);
  
  private final Supplier<Baseboard> baseboard = Memoizer.memoize(this::createBaseboard);
  
  public Firmware getFirmware() {
    return this.firmware.get();
  }
  
  protected abstract Firmware createFirmware();
  
  public Baseboard getBaseboard() {
    return this.baseboard.get();
  }
  
  protected abstract Baseboard createBaseboard();
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("manufacturer=").append(getManufacturer()).append(", ");
    stringBuilder.append("model=").append(getModel()).append(", ");
    stringBuilder.append("serial number=").append(getSerialNumber()).append(", ");
    stringBuilder.append("uuid=").append(getHardwareUUID());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */