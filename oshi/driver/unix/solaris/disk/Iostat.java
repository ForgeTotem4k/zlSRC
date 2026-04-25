package oshi.driver.unix.solaris.disk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class Iostat {
  private static final String IOSTAT_ER_DETAIL = "iostat -Er";
  
  private static final String IOSTAT_ER = "iostat -er";
  
  private static final String IOSTAT_ERN = "iostat -ern";
  
  private static final String DEVICE_HEADER = "device";
  
  public static Map<String, String> queryPartitionToMountMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List<String> list1 = ExecutingCommand.runNative("iostat -er");
    List<String> list2 = ExecutingCommand.runNative("iostat -ern");
    for (byte b = 0; b < list1.size() && b < list2.size(); b++) {
      String str = list1.get(b);
      String[] arrayOfString = str.split(",");
      if (arrayOfString.length >= 5 && !"device".equals(arrayOfString[0])) {
        String str1 = list2.get(b);
        String[] arrayOfString1 = str1.split(",");
        if (arrayOfString1.length >= 5 && !"device".equals(arrayOfString1[4]))
          hashMap.put(arrayOfString[0], arrayOfString1[4]); 
      } 
    } 
    return (Map)hashMap;
  }
  
  public static Map<String, Quintet<String, String, String, String, Long>> queryDeviceStrings(Set<String> paramSet) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = ExecutingCommand.runNative("iostat -Er");
    String str1 = null;
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    long l = 0L;
    for (String str : list) {
      String[] arrayOfString = str.split(",");
      for (String str6 : arrayOfString) {
        str6 = str6.trim();
        if (paramSet.contains(str6)) {
          if (str1 != null)
            hashMap.put(str1, new Quintet(str2, str3, str4, str5, Long.valueOf(l))); 
          str1 = str6;
          str2 = "";
          str3 = "";
          str4 = "";
          str5 = "";
          l = 0L;
        } else if (str6.startsWith("Model:")) {
          str2 = str6.replace("Model:", "").trim();
        } else if (str6.startsWith("Serial No:")) {
          str5 = str6.replace("Serial No:", "").trim();
        } else if (str6.startsWith("Vendor:")) {
          str3 = str6.replace("Vendor:", "").trim();
        } else if (str6.startsWith("Product:")) {
          str4 = str6.replace("Product:", "").trim();
        } else if (str6.startsWith("Size:")) {
          String[] arrayOfString1 = str6.split("<");
          if (arrayOfString1.length > 1) {
            arrayOfString1 = ParseUtil.whitespaces.split(arrayOfString1[1]);
            l = ParseUtil.parseLongOrDefault(arrayOfString1[0], 0L);
          } 
        } 
      } 
      if (str1 != null)
        hashMap.put(str1, new Quintet(str2, str3, str4, str5, Long.valueOf(l))); 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\disk\Iostat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */