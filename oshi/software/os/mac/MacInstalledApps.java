package oshi.software.os.mac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.software.os.ApplicationInfo;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

public final class MacInstalledApps {
  private static final String COLON = ":";
  
  public static List<ApplicationInfo> queryInstalledApps() {
    List<String> list = ExecutingCommand.runNative("system_profiler SPApplicationsDataType");
    return parseMacAppInfo(list);
  }
  
  private static List<ApplicationInfo> parseMacAppInfo(List<String> paramList) {
    ArrayList<ApplicationInfo> arrayList = new ArrayList();
    String str = null;
    Map<String, String> map = null;
    boolean bool = false;
    for (String str1 : paramList) {
      str1 = str1.trim();
      if (str1.endsWith(":")) {
        if (str != null && !map.isEmpty())
          arrayList.add(createAppInfo(str, map)); 
        str = str1.substring(0, str1.length() - 1);
        map = new HashMap<>();
        bool = true;
        continue;
      } 
      if (bool && str1.contains(":")) {
        int i = str1.indexOf(":");
        String str2 = str1.substring(0, i).trim();
        String str3 = str1.substring(i + 1).trim();
        map.put(str2, str3);
      } 
    } 
    return arrayList;
  }
  
  private static ApplicationInfo createAppInfo(String paramString, Map<String, String> paramMap) {
    String str1 = ParseUtil.getValueOrUnknown(paramMap, "Obtained from");
    String str2 = ParseUtil.getValueOrUnknown(paramMap, "Signed by");
    String str3 = str1.equals("Identified Developer") ? str2 : str1;
    String str4 = paramMap.getOrDefault("Last Modified", "unknown");
    long l = ParseUtil.parseDateToEpoch(str4, "dd/MM/yy, HH:mm");
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("Kind", ParseUtil.getValueOrUnknown(paramMap, "Kind"));
    hashMap.put("Location", ParseUtil.getValueOrUnknown(paramMap, "Location"));
    hashMap.put("Get Info String", ParseUtil.getValueOrUnknown(paramMap, "Get Info String"));
    return new ApplicationInfo(paramString, ParseUtil.getValueOrUnknown(paramMap, "Version"), str3, l, hashMap);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacInstalledApps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */