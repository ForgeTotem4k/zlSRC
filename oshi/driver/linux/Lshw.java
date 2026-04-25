package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.UserGroupInfo;

@ThreadSafe
public final class Lshw {
  private static final String MODEL;
  
  private static final String SERIAL;
  
  private static final String UUID;
  
  public static String queryModel() {
    return MODEL;
  }
  
  public static String querySerialNumber() {
    return SERIAL;
  }
  
  public static String queryUUID() {
    return UUID;
  }
  
  public static long queryCpuCapacity() {
    String str = "capacity:";
    for (String str1 : ExecutingCommand.runNative("lshw -class processor")) {
      if (str1.contains(str))
        return ParseUtil.parseHertz(str1.split(str)[1].trim()); 
    } 
    return -1L;
  }
  
  static {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    if (UserGroupInfo.isElevated()) {
      String str4 = "product:";
      String str5 = "serial:";
      String str6 = "uuid:";
      for (String str : ExecutingCommand.runNative("lshw -C system")) {
        if (str.contains(str4)) {
          str1 = str.split(str4)[1].trim();
          continue;
        } 
        if (str.contains(str5)) {
          str2 = str.split(str5)[1].trim();
          continue;
        } 
        if (str.contains(str6))
          str3 = str.split(str6)[1].trim(); 
      } 
    } 
    MODEL = str1;
    SERIAL = str2;
    UUID = str3;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Lshw.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */