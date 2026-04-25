package oshi.hardware.platform.unix;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;

@Immutable
public final class UnixBaseboard extends AbstractBaseboard {
  private final String manufacturer;
  
  private final String model;
  
  private final String serialNumber;
  
  private final String version;
  
  public UnixBaseboard(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.manufacturer = paramString1;
    this.model = paramString2;
    this.serialNumber = paramString3;
    this.version = paramString4;
  }
  
  public String getManufacturer() {
    return this.manufacturer;
  }
  
  public String getModel() {
    return this.model;
  }
  
  public String getSerialNumber() {
    return this.serialNumber;
  }
  
  public String getVersion() {
    return this.version;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\UnixBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */