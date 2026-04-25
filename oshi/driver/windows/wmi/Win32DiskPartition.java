package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskPartition {
  private static final String WIN32_DISK_PARTITION = "Win32_DiskPartition";
  
  public static WbemcliUtil.WmiResult<DiskPartitionProperty> queryPartition(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_DiskPartition", DiskPartitionProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public enum DiskPartitionProperty {
    INDEX, DESCRIPTION, DEVICEID, DISKINDEX, NAME, SIZE, TYPE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32DiskPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */