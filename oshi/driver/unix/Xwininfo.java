package oshi.driver.unix;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSDesktopWindow;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class Xwininfo {
  private static final String[] NET_CLIENT_LIST_STACKING = ParseUtil.whitespaces.split("xprop -root _NET_CLIENT_LIST_STACKING");
  
  private static final String[] XWININFO_ROOT_TREE = ParseUtil.whitespaces.split("xwininfo -root -tree");
  
  private static final String[] XPROP_NET_WM_PID_ID = ParseUtil.whitespaces.split("xprop _NET_WM_PID -id");
  
  public static List<OSDesktopWindow> queryXWindows(boolean paramBoolean) {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    byte b = 0;
    List<String> list = ExecutingCommand.runNative(NET_CLIENT_LIST_STACKING, null);
    if (!list.isEmpty()) {
      String str = list.get(0);
      int i = str.indexOf("0x");
      if (i >= 0)
        for (String str1 : str.substring(i).split(", "))
          hashMap1.put(str1, Integer.valueOf(++b));  
    } 
    Pattern pattern = Pattern.compile("(0x\\S+) (?:\"(.+)\")?.*: \\((?:\"(.+)\" \".+\")?\\)  (\\d+)x(\\d+)\\+.+  \\+(-?\\d+)\\+(-?\\d+)");
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    for (String str : ExecutingCommand.runNative(XWININFO_ROOT_TREE, null)) {
      Matcher matcher = pattern.matcher(str.trim());
      if (matcher.matches()) {
        String str1 = matcher.group(1);
        if (!paramBoolean || hashMap1.containsKey(str1)) {
          String str2 = matcher.group(2);
          if (!Util.isBlank(str2))
            hashMap2.put(str1, str2); 
          String str3 = matcher.group(3);
          if (!Util.isBlank(str3))
            hashMap3.put(str1, str3); 
          linkedHashMap.put(str1, new Rectangle(ParseUtil.parseIntOrDefault(matcher.group(6), 0), ParseUtil.parseIntOrDefault(matcher.group(7), 0), ParseUtil.parseIntOrDefault(matcher.group(4), 0), ParseUtil.parseIntOrDefault(matcher.group(5), 0)));
        } 
      } 
    } 
    ArrayList<OSDesktopWindow> arrayList = new ArrayList();
    for (Map.Entry<Object, Object> entry : linkedHashMap.entrySet()) {
      String str = (String)entry.getKey();
      long l = queryPidFromId(str);
      boolean bool = hashMap1.containsKey(str);
      arrayList.add(new OSDesktopWindow(ParseUtil.hexStringToLong(str, 0L), (String)hashMap2.getOrDefault(str, ""), (String)hashMap3.getOrDefault(str, ""), (Rectangle)entry.getValue(), l, ((Integer)hashMap1.getOrDefault(str, Integer.valueOf(0))).intValue(), bool));
    } 
    return arrayList;
  }
  
  private static long queryPidFromId(String paramString) {
    String[] arrayOfString = new String[XPROP_NET_WM_PID_ID.length + 1];
    System.arraycopy(XPROP_NET_WM_PID_ID, 0, arrayOfString, 0, XPROP_NET_WM_PID_ID.length);
    arrayOfString[XPROP_NET_WM_PID_ID.length] = paramString;
    List<String> list = ExecutingCommand.runNative(arrayOfString, null);
    return list.isEmpty() ? 0L : ParseUtil.getFirstIntValue(list.get(0));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\Xwininfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */