package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDrive {
  private static final String WIN32_DISK_DRIVE = "Win32_DiskDrive";
  
  public static WbemcliUtil.WmiResult<DiskDriveProperty> queryDiskDrive(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_DiskDrive", DiskDriveProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public enum DiskDriveProperty {
    INDEX, MANUFACTURER, MODEL, NAME, SERIALNUMBER, SIZE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32DiskDrive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */