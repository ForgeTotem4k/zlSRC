package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32VideoController {
  private static final String WIN32_VIDEO_CONTROLLER = "Win32_VideoController";
  
  public static WbemcliUtil.WmiResult<VideoControllerProperty> queryVideoController() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_VideoController", VideoControllerProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum VideoControllerProperty {
    ADAPTERCOMPATIBILITY, ADAPTERRAM, DRIVERVERSION, NAME, PNPDEVICEID;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32VideoController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */