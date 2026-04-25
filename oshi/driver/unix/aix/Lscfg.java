package oshi.driver.unix.aix;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class Lscfg {
  public static List<String> queryAllDevices() {
    return ExecutingCommand.runNative("lscfg -vp");
  }
  
  public static Triplet<String, String, String> queryBackplaneModelSerialVersion(List<String> paramList) {
    String str1 = "WAY BACKPLANE";
    String str2 = "Part Number";
    String str3 = "Serial Number";
    String str4 = "Version";
    String str5 = "Physical Location";
    String str6 = null;
    String str7 = null;
    String str8 = null;
    boolean bool = false;
    for (String str : paramList) {
      if (!bool && str.contains("WAY BACKPLANE")) {
        bool = true;
        continue;
      } 
      if (bool) {
        if (str.contains("Part Number")) {
          str6 = ParseUtil.removeLeadingDots(str.split("Part Number")[1].trim());
          continue;
        } 
        if (str.contains("Serial Number")) {
          str7 = ParseUtil.removeLeadingDots(str.split("Serial Number")[1].trim());
          continue;
        } 
        if (str.contains("Version")) {
          str8 = ParseUtil.removeLeadingDots(str.split("Version")[1].trim());
          continue;
        } 
        if (str.contains("Physical Location"))
          break; 
      } 
    } 
    return new Triplet(str6, str7, str8);
  }
  
  public static Pair<String, String> queryModelSerial(String paramString) {
    String str1 = "Machine Type and Model";
    String str2 = "Serial Number";
    String str3 = null;
    String str4 = null;
    for (String str : ExecutingCommand.runNative("lscfg -vl " + paramString)) {
      if (str3 == null && str.contains(paramString)) {
        String str5 = str.split(paramString)[1].trim();
        int i = str5.indexOf(' ');
        if (i > 0)
          str3 = str5.substring(i).trim(); 
      } 
      if (str.contains(str1)) {
        str3 = ParseUtil.removeLeadingDots(str.split(str1)[1].trim());
        continue;
      } 
      if (str.contains(str2))
        str4 = ParseUtil.removeLeadingDots(str.split(str2)[1].trim()); 
    } 
    return new Pair(str3, str4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\Lscfg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */