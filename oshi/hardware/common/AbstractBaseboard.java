package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;

@Immutable
public abstract class AbstractBaseboard implements Baseboard {
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("manufacturer=").append(getManufacturer()).append(", ");
    stringBuilder.append("model=").append(getModel()).append(", ");
    stringBuilder.append("version=").append(getVersion()).append(", ");
    stringBuilder.append("serial number=").append(getSerialNumber());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */