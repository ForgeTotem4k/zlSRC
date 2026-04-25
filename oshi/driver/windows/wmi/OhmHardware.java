package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class OhmHardware {
  private static final String HARDWARE = "Hardware";
  
  public static WbemcliUtil.WmiResult<IdentifierProperty> queryHwIdentifier(WmiQueryHandler paramWmiQueryHandler, String paramString1, String paramString2) {
    StringBuilder stringBuilder = new StringBuilder("Hardware");
    stringBuilder.append(" WHERE ").append(paramString1).append("Type=\"").append(paramString2).append('"');
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\OpenHardwareMonitor", stringBuilder.toString(), IdentifierProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public enum IdentifierProperty {
    IDENTIFIER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\OhmHardware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */