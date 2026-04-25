package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32LogicalDiskToPartition {
  private static final String WIN32_LOGICAL_DISK_TO_PARTITION = "Win32_LogicalDiskToPartition";
  
  public static WbemcliUtil.WmiResult<DiskToPartitionProperty> queryDiskToPartition(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_LogicalDiskToPartition", DiskToPartitionProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public enum DiskToPartitionProperty {
    ANTECEDENT, DEPENDENT, ENDINGADDRESS, STARTINGADDRESS;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32LogicalDiskToPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */