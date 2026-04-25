package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Firmware;

@Immutable
public abstract class AbstractFirmware implements Firmware {
  public String getName() {
    return "unknown";
  }
  
  public String getDescription() {
    return "unknown";
  }
  
  public String getReleaseDate() {
    return "unknown";
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("manufacturer=").append(getManufacturer()).append(", ");
    stringBuilder.append("name=").append(getName()).append(", ");
    stringBuilder.append("description=").append(getDescription()).append(", ");
    stringBuilder.append("version=").append(getVersion()).append(", ");
    stringBuilder.append("release date=").append((getReleaseDate() == null) ? "unknown" : getReleaseDate());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */