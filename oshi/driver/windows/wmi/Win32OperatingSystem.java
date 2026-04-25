package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32OperatingSystem {
  private static final String WIN32_OPERATING_SYSTEM = "Win32_OperatingSystem";
  
  public static WbemcliUtil.WmiResult<OSVersionProperty> queryOsVersion() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_OperatingSystem", OSVersionProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum OSVersionProperty {
    VERSION, PRODUCTTYPE, BUILDNUMBER, CSDVERSION, SUITEMASK;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32OperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */