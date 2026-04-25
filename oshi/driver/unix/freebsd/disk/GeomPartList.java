package oshi.driver.unix.freebsd.disk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class GeomPartList {
  private static final String GEOM_PART_LIST = "geom part list";
  
  private static final String STAT_FILESIZE = "stat -f %i /dev/";
  
  public static Map<String, List<HWPartition>> queryPartitions() {
    Map<String, String> map = Mount.queryPartitionToMountMap();
    HashMap<Object, Object> hashMap = new HashMap<>();
    String str1 = null;
    ArrayList<HWPartition> arrayList = new ArrayList();
    String str2 = null;
    String str3 = "unknown";
    String str4 = "unknown";
    String str5 = "unknown";
    long l = 0L;
    String str6 = "";
    List list = ExecutingCommand.runNative("geom part list");
    for (String str : list) {
      str = str.trim();
      if (str.startsWith("Geom name:")) {
        if (str1 != null && !arrayList.isEmpty()) {
          hashMap.put(str1, arrayList);
          arrayList = new ArrayList();
        } 
        str1 = str.substring(str.lastIndexOf(' ') + 1);
      } 
      if (str1 != null) {
        if (str.contains("Name:")) {
          if (str2 != null) {
            int i = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + str2), 0);
            arrayList.add(new HWPartition(str3, str2, str4, str5, l, 0, i, str6));
            str2 = null;
            str3 = "unknown";
            str4 = "unknown";
            str5 = "unknown";
            l = 0L;
          } 
          String str7 = str.substring(str.lastIndexOf(' ') + 1);
          if (str7.startsWith(str1)) {
            str2 = str7;
            str3 = str7;
            str6 = map.getOrDefault(str7, "");
          } 
        } 
        if (str2 != null) {
          String[] arrayOfString = ParseUtil.whitespaces.split(str);
          if (arrayOfString.length >= 2) {
            if (str.startsWith("Mediasize:")) {
              l = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
              continue;
            } 
            if (str.startsWith("rawuuid:")) {
              str5 = arrayOfString[1];
              continue;
            } 
            if (str.startsWith("type:"))
              str4 = arrayOfString[1]; 
          } 
        } 
      } 
    } 
    if (str1 != null) {
      if (str2 != null) {
        int i = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + str2), 0);
        arrayList.add(new HWPartition(str3, str2, str4, str5, l, 0, i, str6));
      } 
      if (!arrayList.isEmpty()) {
        List list1 = (List)arrayList.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList());
        hashMap.put(str1, list1);
      } 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\freebsd\disk\GeomPartList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */