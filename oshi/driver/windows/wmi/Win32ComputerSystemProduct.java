package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32ComputerSystemProduct {
  private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";
  
  public static WbemcliUtil.WmiResult<ComputerSystemProductProperty> queryIdentifyingNumberUUID() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystemProduct", ComputerSystemProductProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum ComputerSystemProductProperty {
    IDENTIFYINGNUMBER, UUID;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32ComputerSystemProduct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */