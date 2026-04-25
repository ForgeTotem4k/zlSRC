package oshi.driver.unix.aix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Lspv {
  private static final Map<String, List<HWPartition>> PARTITION_CACHE = new ConcurrentHashMap<>();
  
  public static List<HWPartition> queryLogicalVolumes(String paramString, Map<String, Pair<Integer, Integer>> paramMap) {
    return PARTITION_CACHE.computeIfAbsent(paramString, paramString -> Collections.unmodifiableList((List)computeLogicalVolumes(paramString, paramMap).stream().sorted(Comparator.comparing(HWPartition::getMinor).thenComparing(HWPartition::getName)).collect(Collectors.toList())));
  }
  
  private static List<HWPartition> computeLogicalVolumes(String paramString, Map<String, Pair<Integer, Integer>> paramMap) {
    ArrayList<HWPartition> arrayList = new ArrayList();
    String str1 = "PV STATE:";
    String str2 = "PP SIZE:";
    long l = 0L;
    for (String str : ExecutingCommand.runNative("lspv -L " + paramString)) {
      if (str.startsWith(str1)) {
        if (!str.contains("active"))
          return arrayList; 
        continue;
      } 
      if (str.contains(str2))
        l = ParseUtil.getFirstIntValue(str); 
    } 
    if (l == 0L)
      return arrayList; 
    l <<= 20L;
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    for (String str : ExecutingCommand.runNative("lspv -p " + paramString)) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
      if (arrayOfString.length >= 6 && "used".equals(arrayOfString[1])) {
        String str3 = arrayOfString[arrayOfString.length - 3];
        hashMap1.put(str3, arrayOfString[arrayOfString.length - 1]);
        hashMap2.put(str3, arrayOfString[arrayOfString.length - 2]);
        int i = 1 + ParseUtil.getNthIntValue(arrayOfString[0], 2) - ParseUtil.getNthIntValue(arrayOfString[0], 1);
        hashMap3.put(str3, Integer.valueOf(i + ((Integer)hashMap3.getOrDefault(str3, Integer.valueOf(0))).intValue()));
      } 
    } 
    for (Map.Entry<Object, Object> entry : hashMap1.entrySet()) {
      String str3 = "N/A".equals(entry.getValue()) ? "" : (String)entry.getValue();
      String str4 = (String)entry.getKey();
      String str5 = (String)hashMap2.get(str4);
      long l1 = l * ((Integer)hashMap3.get(str4)).intValue();
      Pair pair = paramMap.get(str4);
      int i = (pair == null) ? ParseUtil.getFirstIntValue(str4) : ((Integer)pair.getA()).intValue();
      int j = (pair == null) ? ParseUtil.getFirstIntValue(str4) : ((Integer)pair.getB()).intValue();
      arrayList.add(new HWPartition(str4, str4, str5, "", l1, i, j, str3));
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\Lspv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */