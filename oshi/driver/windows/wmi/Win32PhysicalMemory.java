package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32PhysicalMemory {
  private static final String WIN32_PHYSICAL_MEMORY = "Win32_PhysicalMemory";
  
  public static WbemcliUtil.WmiResult<PhysicalMemoryProperty> queryphysicalMemory() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_PhysicalMemory", PhysicalMemoryProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public static WbemcliUtil.WmiResult<PhysicalMemoryPropertyWin8> queryphysicalMemoryWin8() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_PhysicalMemory", PhysicalMemoryPropertyWin8.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum PhysicalMemoryProperty {
    BANKLABEL, CAPACITY, SPEED, MANUFACTURER, PARTNUMBER, SMBIOSMEMORYTYPE, SERIALNUMBER;
  }
  
  public enum PhysicalMemoryPropertyWin8 {
    BANKLABEL, CAPACITY, SPEED, MANUFACTURER, MEMORYTYPE, PARTNUMBER, SERIALNUMBER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32PhysicalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */