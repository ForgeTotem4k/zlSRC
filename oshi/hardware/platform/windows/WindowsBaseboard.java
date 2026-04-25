package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32BaseBoard;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Quartet;

@Immutable
final class WindowsBaseboard extends AbstractBaseboard {
  private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(WindowsBaseboard::queryManufModelVersSerial);
  
  public String getManufacturer() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getA();
  }
  
  public String getModel() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getB();
  }
  
  public String getVersion() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getC();
  }
  
  public String getSerialNumber() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getD();
  }
  
  private static Quartet<String, String, String, String> queryManufModelVersSerial() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    WbemcliUtil.WmiResult wmiResult = Win32BaseBoard.queryBaseboardInfo();
    if (wmiResult.getResultCount() > 0) {
      str1 = WmiUtil.getString(wmiResult, (Enum)Win32BaseBoard.BaseBoardProperty.MANUFACTURER, 0);
      str2 = WmiUtil.getString(wmiResult, (Enum)Win32BaseBoard.BaseBoardProperty.MODEL, 0);
      String str = WmiUtil.getString(wmiResult, (Enum)Win32BaseBoard.BaseBoardProperty.PRODUCT, 0);
      if (!Util.isBlank(str))
        str2 = Util.isBlank(str2) ? str : (str2 + " (" + str + ")"); 
      str3 = WmiUtil.getString(wmiResult, (Enum)Win32BaseBoard.BaseBoardProperty.VERSION, 0);
      str4 = WmiUtil.getString(wmiResult, (Enum)Win32BaseBoard.BaseBoardProperty.SERIALNUMBER, 0);
    } 
    return new Quartet(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */