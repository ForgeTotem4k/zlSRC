package oshi.driver.unix.solaris.disk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Lshal {
  private static final String LSHAL_CMD = "lshal";
  
  public static Map<String, Integer> queryDiskToMajorMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = ExecutingCommand.runNative("lshal");
    String str = null;
    for (String str1 : list) {
      if (str1.startsWith("udi ")) {
        String str2 = ParseUtil.getSingleQuoteStringValue(str1);
        str = str2.substring(str2.lastIndexOf('/') + 1);
        continue;
      } 
      str1 = str1.trim();
      if (str1.startsWith("block.major") && str != null)
        hashMap.put(str, Integer.valueOf(ParseUtil.getFirstIntValue(str1))); 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\disk\Lshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */