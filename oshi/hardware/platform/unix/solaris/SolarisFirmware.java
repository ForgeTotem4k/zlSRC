package oshi.hardware.platform.unix.solaris;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;

@Immutable
final class SolarisFirmware extends AbstractFirmware {
  private final String manufacturer;
  
  private final String version;
  
  private final String releaseDate;
  
  SolarisFirmware(String paramString1, String paramString2, String paramString3) {
    this.manufacturer = paramString1;
    this.version = paramString2;
    this.releaseDate = paramString3;
  }
  
  public String getManufacturer() {
    return this.manufacturer;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public String getReleaseDate() {
    return this.releaseDate;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */