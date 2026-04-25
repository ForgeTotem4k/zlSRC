package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32BaseBoard {
  private static final String WIN32_BASEBOARD = "Win32_BaseBoard";
  
  public static WbemcliUtil.WmiResult<BaseBoardProperty> queryBaseboardInfo() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_BaseBoard", BaseBoardProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum BaseBoardProperty {
    MANUFACTURER, MODEL, PRODUCT, VERSION, SERIALNUMBER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32BaseBoard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */