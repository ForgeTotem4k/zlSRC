package oshi.driver.unix.openbsd.disk;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Quartet;

@ThreadSafe
public final class Disklabel {
  public static Quartet<String, String, Long, List<HWPartition>> getDiskParams(String paramString) {
    ArrayList<HWPartition> arrayList = new ArrayList();
    String str1 = "total sectors:";
    long l = 1L;
    String str2 = "bytes/sector:";
    int i = 1;
    String str3 = "label:";
    String str4 = "";
    String str5 = "duid:";
    String str6 = "";
    for (String str : ExecutingCommand.runNative("disklabel -n " + paramString)) {
      if (str.contains(str1)) {
        l = ParseUtil.getFirstIntValue(str);
      } else if (str.contains(str2)) {
        i = ParseUtil.getFirstIntValue(str);
      } else if (str.contains(str3)) {
        str4 = str.split(str3)[1].trim();
      } else if (str.contains(str5)) {
        str6 = str.split(str5)[1].trim();
      } 
      if (str.trim().indexOf(':') == 1) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str.trim(), 9);
        String str7 = arrayOfString[0].substring(0, 1);
        Pair<Integer, Integer> pair = getMajorMinor(paramString, str7);
        if (arrayOfString.length > 4)
          arrayList.add(new HWPartition(paramString + str7, str7, arrayOfString[3], str6 + "." + str7, ParseUtil.parseLongOrDefault(arrayOfString[1], 0L) * i, ((Integer)pair.getA()).intValue(), ((Integer)pair.getB()).intValue(), (arrayOfString.length > 5) ? arrayOfString[arrayOfString.length - 1] : "")); 
      } 
    } 
    return arrayList.isEmpty() ? getDiskParamsNoRoot(paramString) : new Quartet(str4, str6, Long.valueOf(l * i), arrayList);
  }
  
  private static Quartet<String, String, Long, List<HWPartition>> getDiskParamsNoRoot(String paramString) {
    ArrayList<HWPartition> arrayList = new ArrayList();
    for (String str : ExecutingCommand.runNative("df")) {
      if (str.startsWith("/dev/" + paramString)) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        String str1 = arrayOfString[0].substring(5 + paramString.length());
        Pair<Integer, Integer> pair = getMajorMinor(paramString, str1);
        if (arrayOfString.length > 5) {
          long l = ParseUtil.parseLongOrDefault(arrayOfString[1], 1L) * 512L;
          arrayList.add(new HWPartition(arrayOfString[0], arrayOfString[0].substring(5), "unknown", "unknown", l, ((Integer)pair.getA()).intValue(), ((Integer)pair.getB()).intValue(), arrayOfString[5]));
        } 
      } 
    } 
    return new Quartet("unknown", "unknown", Long.valueOf(0L), arrayList);
  }
  
  private static Pair<Integer, Integer> getMajorMinor(String paramString1, String paramString2) {
    int i = 0;
    int j = 0;
    String str = ExecutingCommand.getFirstAnswer("stat -f %Hr,%Lr /dev/" + paramString1 + paramString2);
    int k = str.indexOf(',');
    if (k > 0 && k < str.length()) {
      i = ParseUtil.parseIntOrDefault(str.substring(0, k), 0);
      j = ParseUtil.parseIntOrDefault(str.substring(k + 1), 0);
    } 
    return new Pair(Integer.valueOf(i), Integer.valueOf(j));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\openbsd\disk\Disklabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */