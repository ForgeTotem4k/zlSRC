package oshi.driver.unix.freebsd.disk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class GeomDiskList {
  private static final String GEOM_DISK_LIST = "geom disk list";
  
  public static Map<String, Triplet<String, String, Long>> queryDisks() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    String str1 = null;
    String str2 = "unknown";
    String str3 = "unknown";
    long l = 0L;
    List list = ExecutingCommand.runNative("geom disk list");
    for (String str : list) {
      str = str.trim();
      if (str.startsWith("Geom name:")) {
        if (str1 != null) {
          hashMap.put(str1, new Triplet(str2, str3, Long.valueOf(l)));
          str2 = "unknown";
          str3 = "unknown";
          l = 0L;
        } 
        str1 = str.substring(str.lastIndexOf(' ') + 1);
      } 
      if (str1 != null) {
        str = str.trim();
        if (str.startsWith("Mediasize:")) {
          String[] arrayOfString = ParseUtil.whitespaces.split(str);
          if (arrayOfString.length > 1)
            l = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L); 
        } 
        if (str.startsWith("descr:"))
          str2 = str.replace("descr:", "").trim(); 
        if (str.startsWith("ident:"))
          str3 = str.replace("ident:", "").replace("(null)", "").trim(); 
      } 
    } 
    if (str1 != null)
      hashMap.put(str1, new Triplet(str2, str3, Long.valueOf(l))); 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\freebsd\disk\GeomDiskList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */