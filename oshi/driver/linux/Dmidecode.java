package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.UserGroupInfo;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Dmidecode {
  public static String querySerialNumber() {
    if (UserGroupInfo.isElevated()) {
      String str = "Serial Number:";
      for (String str1 : ExecutingCommand.runNative("dmidecode -t system")) {
        if (str1.contains(str))
          return str1.split(str)[1].trim(); 
      } 
    } 
    return null;
  }
  
  public static String queryUUID() {
    if (UserGroupInfo.isElevated()) {
      String str = "UUID:";
      for (String str1 : ExecutingCommand.runNative("dmidecode -t system")) {
        if (str1.contains(str))
          return str1.split(str)[1].trim(); 
      } 
    } 
    return null;
  }
  
  public static Pair<String, String> queryBiosNameRev() {
    String str1 = null;
    String str2 = null;
    if (UserGroupInfo.isElevated()) {
      String str3 = "SMBIOS";
      String str4 = "Bios Revision:";
      for (String str : ExecutingCommand.runNative("dmidecode -t bios")) {
        if (str.contains("SMBIOS")) {
          String[] arrayOfString = ParseUtil.whitespaces.split(str);
          if (arrayOfString.length >= 2)
            str1 = arrayOfString[0] + " " + arrayOfString[1]; 
        } 
        if (str.contains("Bios Revision:")) {
          str2 = str.split("Bios Revision:")[1].trim();
          break;
        } 
      } 
    } 
    return new Pair(str1, str2);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Dmidecode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */