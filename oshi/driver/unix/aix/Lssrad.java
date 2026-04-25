package oshi.driver.unix.aix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Lssrad {
  public static Map<Integer, Pair<Integer, Integer>> queryNodesPackages() {
    int i = 0;
    int j = 0;
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = ExecutingCommand.runNative("lssrad -av");
    if (!list.isEmpty())
      list.remove(0); 
    for (String str1 : list) {
      String str2 = str1.trim();
      if (!str2.isEmpty()) {
        if (Character.isDigit(str1.charAt(0))) {
          i = ParseUtil.parseIntOrDefault(str2, 0);
          continue;
        } 
        if (str2.contains(".")) {
          String[] arrayOfString = ParseUtil.whitespaces.split(str2, 3);
          j = ParseUtil.parseIntOrDefault(arrayOfString[0], 0);
          str2 = (arrayOfString.length > 2) ? arrayOfString[2] : "";
        } 
        for (Integer integer : ParseUtil.parseHyphenatedIntList(str2))
          hashMap.put(integer, new Pair(Integer.valueOf(i), Integer.valueOf(j))); 
      } 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\Lssrad.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */