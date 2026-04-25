package oshi.util.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public final class DevPath {
  public static final String DEV = queryDevConfig() + "/";
  
  public static final String DISK_BY_UUID = DEV + "disk/by-uuid";
  
  public static final String DM = DEV + "dm";
  
  public static final String LOOP = DEV + "loop";
  
  public static final String MAPPER = DEV + "mapper/";
  
  public static final String RAM = DEV + "ram";
  
  private static String queryDevConfig() {
    String str = GlobalConfig.get("oshi.util.dev.path", "/dev");
    str = '/' + str.replaceAll("/$|^/", "");
    if (!(new File(str)).exists())
      throw new GlobalConfig.PropertyException("oshi.util.dev.path", "The path does not exist"); 
    return str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\linux\DevPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */