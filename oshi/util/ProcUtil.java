package oshi.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ProcUtil {
  public static Map<String, Map<String, Long>> parseNestedStatistics(String paramString, String... paramVarArgs) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List<String> list1 = Arrays.asList(paramVarArgs);
    List<String> list2 = FileUtil.readFile(paramString);
    String str = null;
    String[] arrayOfString = null;
    for (String str1 : list2) {
      String[] arrayOfString1 = ParseUtil.whitespaces.split(str1);
      if (arrayOfString1.length == 0)
        continue; 
      if (arrayOfString1[0].isEmpty())
        arrayOfString1 = Arrays.<String>copyOfRange(arrayOfString1, 1, arrayOfString1.length); 
      String str2 = arrayOfString1[0].substring(0, arrayOfString1[0].length() - 1);
      if (!list1.isEmpty() && !list1.contains(str2))
        continue; 
      if (str2.equals(str)) {
        if (arrayOfString1.length == arrayOfString.length) {
          HashMap<Object, Object> hashMap1 = new HashMap<>(arrayOfString1.length - 1);
          for (byte b = 1; b < arrayOfString1.length; b++)
            hashMap1.put(arrayOfString[b], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString1[b], 0L))); 
          hashMap.put(str2, hashMap1);
        } 
      } else {
        arrayOfString = arrayOfString1;
      } 
      str = str2;
    } 
    return (Map)hashMap;
  }
  
  public static Map<String, Long> parseStatistics(String paramString, Pattern paramPattern) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List<String> list = FileUtil.readFile(paramString);
    for (String str : list) {
      String[] arrayOfString = paramPattern.split(str);
      if (arrayOfString[0].isEmpty())
        arrayOfString = Arrays.<String>copyOfRange(arrayOfString, 1, arrayOfString.length); 
      if (arrayOfString.length == 2)
        hashMap.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[1], 0L))); 
    } 
    return (Map)hashMap;
  }
  
  public static Map<String, Long> parseStatistics(String paramString) {
    return parseStatistics(paramString, ParseUtil.whitespaces);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\ProcUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */