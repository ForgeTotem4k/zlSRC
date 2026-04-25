package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Bios {
  private static final String WIN32_BIOS_WHERE_PRIMARY_BIOS_TRUE = "Win32_BIOS where PrimaryBIOS=true";
  
  public static WbemcliUtil.WmiResult<BiosSerialProperty> querySerialNumber() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_BIOS where PrimaryBIOS=true", BiosSerialProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public static WbemcliUtil.WmiResult<BiosProperty> queryBiosInfo() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_BIOS where PrimaryBIOS=true", BiosProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum BiosSerialProperty {
    SERIALNUMBER;
  }
  
  public enum BiosProperty {
    MANUFACTURER, NAME, DESCRIPTION, VERSION, RELEASEDATE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32Bios.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */