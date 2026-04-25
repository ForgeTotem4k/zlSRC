package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32LogicalDisk {
  private static final String WIN32_LOGICAL_DISK = "Win32_LogicalDisk";
  
  public static WbemcliUtil.WmiResult<LogicalDiskProperty> queryLogicalDisk(String paramString, boolean paramBoolean) {
    StringBuilder stringBuilder = new StringBuilder("Win32_LogicalDisk");
    boolean bool = false;
    if (paramBoolean) {
      stringBuilder.append(" WHERE DriveType != 4");
      bool = true;
    } 
    if (paramString != null)
      stringBuilder.append(bool ? " AND" : " WHERE").append(" Name=\"").append(paramString).append('"'); 
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery(stringBuilder.toString(), LogicalDiskProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum LogicalDiskProperty {
    ACCESS, DESCRIPTION, DRIVETYPE, FILESYSTEM, FREESPACE, NAME, PROVIDERNAME, SIZE, VOLUMENAME;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32LogicalDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */