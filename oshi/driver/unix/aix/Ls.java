package oshi.driver.unix.aix;

import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Ls {
  public static Map<String, Pair<Integer, Integer>> queryDeviceMajorMinor() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (String str : ExecutingCommand.runNative("ls -l /dev")) {
      if (!str.isEmpty() && str.charAt(0) == 'b') {
        int i = str.lastIndexOf(' ');
        if (i > 0 && i < str.length()) {
          String str1 = str.substring(i + 1);
          int j = ParseUtil.getNthIntValue(str, 2);
          int k = ParseUtil.getNthIntValue(str, 3);
          hashMap.put(str1, new Pair(Integer.valueOf(j), Integer.valueOf(k)));
        } 
      } 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\Ls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */