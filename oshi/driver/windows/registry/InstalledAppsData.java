package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.software.os.ApplicationInfo;
import oshi.util.ParseUtil;

public final class InstalledAppsData {
  private static final Map<WinReg.HKEY, List<String>> REGISTRY_PATHS = new HashMap<>();
  
  public static List<ApplicationInfo> queryInstalledApps() {
    ArrayList<ApplicationInfo> arrayList = new ArrayList();
    for (Map.Entry<WinReg.HKEY, List<String>> entry : REGISTRY_PATHS.entrySet()) {
      WinReg.HKEY hKEY = (WinReg.HKEY)entry.getKey();
      List list = (List)entry.getValue();
      for (String str : list) {
        String[] arrayOfString = Advapi32Util.registryGetKeys(hKEY, str);
        for (String str1 : arrayOfString) {
          String str2 = str + "\\" + str1;
          try {
            String str3 = getRegistryValueOrUnknown(hKEY, str2, "DisplayName");
            String str4 = getRegistryValueOrUnknown(hKEY, str2, "DisplayVersion");
            String str5 = getRegistryValueOrUnknown(hKEY, str2, "Publisher");
            String str6 = getRegistryValueOrUnknown(hKEY, str2, "InstallDate");
            String str7 = getRegistryValueOrUnknown(hKEY, str2, "InstallLocation");
            String str8 = getRegistryValueOrUnknown(hKEY, str2, "InstallSource");
            long l = ParseUtil.parseDateToEpoch(str6, "yyyyMMdd");
            HashMap<Object, Object> hashMap = new HashMap<>();
            hashMap.put("installLocation", str7);
            hashMap.put("installSource", str8);
            ApplicationInfo applicationInfo = new ApplicationInfo(str3, str4, str5, l, hashMap);
            arrayList.add(applicationInfo);
          } catch (Win32Exception win32Exception) {}
        } 
      } 
    } 
    return arrayList;
  }
  
  private static String getRegistryValueOrUnknown(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    String str = Advapi32Util.registryGetStringValue(paramHKEY, paramString1, paramString2);
    return ParseUtil.getStringValueOrUnknown(str);
  }
  
  static {
    REGISTRY_PATHS.put(WinReg.HKEY_LOCAL_MACHINE, Arrays.asList(new String[] { "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall", "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall" }));
    REGISTRY_PATHS.put(WinReg.HKEY_CURRENT_USER, Arrays.asList(new String[] { "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall" }));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\InstalledAppsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */