package oshi.util.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public final class SysPath {
  public static final String SYS = querySysConfig() + "/";
  
  public static final String CPU = SYS + "devices/system/cpu/";
  
  public static final String DMI_ID = SYS + "devices/virtual/dmi/id/";
  
  public static final String NET = SYS + "class/net/";
  
  public static final String MODEL = SYS + "firmware/devicetree/base/model";
  
  public static final String POWER_SUPPLY = SYS + "class/power_supply";
  
  public static final String HWMON = SYS + "class/hwmon/";
  
  public static final String THERMAL = SYS + "class/thermal/";
  
  private static String querySysConfig() {
    String str = GlobalConfig.get("oshi.util.sys.path", "/sys");
    str = '/' + str.replaceAll("/$|^/", "");
    if (!(new File(str)).exists())
      throw new GlobalConfig.PropertyException("oshi.util.sys.path", "The path does not exist"); 
    return str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\linux\SysPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */