package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Lshal {
  public static String querySerialNumber() {
    String str = "system.hardware.serial =";
    for (String str1 : ExecutingCommand.runNative("lshal")) {
      if (str1.contains(str))
        return ParseUtil.getSingleQuoteStringValue(str1); 
    } 
    return null;
  }
  
  public static String queryUUID() {
    String str = "system.hardware.uuid =";
    for (String str1 : ExecutingCommand.runNative("lshal")) {
      if (str1.contains(str))
        return ParseUtil.getSingleQuoteStringValue(str1); 
    } 
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Lshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */