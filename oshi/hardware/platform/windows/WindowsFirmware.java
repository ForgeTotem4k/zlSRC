package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32Bios;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Quintet;

@Immutable
final class WindowsFirmware extends AbstractFirmware {
  private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(WindowsFirmware::queryManufNameDescVersRelease);
  
  public String getManufacturer() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getA();
  }
  
  public String getName() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getB();
  }
  
  public String getDescription() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getC();
  }
  
  public String getVersion() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getD();
  }
  
  public String getReleaseDate() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getE();
  }
  
  private static Quintet<String, String, String, String, String> queryManufNameDescVersRelease() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    WbemcliUtil.WmiResult wmiResult = Win32Bios.queryBiosInfo();
    if (wmiResult.getResultCount() > 0) {
      str1 = WmiUtil.getString(wmiResult, (Enum)Win32Bios.BiosProperty.MANUFACTURER, 0);
      str2 = WmiUtil.getString(wmiResult, (Enum)Win32Bios.BiosProperty.NAME, 0);
      str3 = WmiUtil.getString(wmiResult, (Enum)Win32Bios.BiosProperty.DESCRIPTION, 0);
      str4 = WmiUtil.getString(wmiResult, (Enum)Win32Bios.BiosProperty.VERSION, 0);
      str5 = WmiUtil.getDateString(wmiResult, (Enum)Win32Bios.BiosProperty.RELEASEDATE, 0);
    } 
    return new Quintet(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4, Util.isBlank(str5) ? "unknown" : str5);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */