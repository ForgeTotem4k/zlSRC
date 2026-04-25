package oshi.software.os.linux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import oshi.software.os.ApplicationInfo;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

public final class LinuxInstalledApps {
  private static final Pattern PIPE_PATTERN = Pattern.compile("\\|");
  
  private static final Map<String, String> PACKAGE_MANAGER_COMMANDS = initializePackageManagerCommands();
  
  private static Map<String, String> initializePackageManagerCommands() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    if (isPackageManagerAvailable("dpkg")) {
      hashMap.put("dpkg", "dpkg-query -W -f=${Package}|${Version}|${Architecture}|${Installed-Size}|${db-fsys:Last-Modified}|${Maintainer}|${Source}|${Homepage}\\n");
    } else if (isPackageManagerAvailable("rpm")) {
      hashMap.put("rpm", "rpm -qa --queryformat %{NAME}|%{VERSION}-%{RELEASE}|%{ARCH}|%{SIZE}|%{INSTALLTIME}|%{PACKAGER}|%{SOURCERPM}|%{URL}\\n");
    } 
    return (Map)hashMap;
  }
  
  public static List<ApplicationInfo> queryInstalledApps() {
    List<String> list = fetchInstalledApps();
    return parseLinuxAppInfo(list);
  }
  
  private static List<String> fetchInstalledApps() {
    if (PACKAGE_MANAGER_COMMANDS.isEmpty())
      return Collections.emptyList(); 
    String str = PACKAGE_MANAGER_COMMANDS.values().iterator().next();
    return ExecutingCommand.runNative(str);
  }
  
  private static boolean isPackageManagerAvailable(String paramString) {
    List list = ExecutingCommand.runNative(paramString + " --version");
    return !list.isEmpty();
  }
  
  private static List<ApplicationInfo> parseLinuxAppInfo(List<String> paramList) {
    ArrayList<ApplicationInfo> arrayList = new ArrayList();
    for (String str : paramList) {
      String[] arrayOfString = PIPE_PATTERN.split(str, -1);
      if (arrayOfString.length >= 8) {
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("architecture", ParseUtil.getStringValueOrUnknown(arrayOfString[2]));
        hashMap.put("installedSize", String.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[3], 0L)));
        hashMap.put("source", ParseUtil.getStringValueOrUnknown(arrayOfString[6]));
        hashMap.put("homepage", ParseUtil.getStringValueOrUnknown(arrayOfString[7]));
        ApplicationInfo applicationInfo = new ApplicationInfo(ParseUtil.getStringValueOrUnknown(arrayOfString[0]), ParseUtil.getStringValueOrUnknown(arrayOfString[1]), ParseUtil.getStringValueOrUnknown(arrayOfString[5]), ParseUtil.parseLongOrDefault(arrayOfString[4], 0L), hashMap);
        arrayList.add(applicationInfo);
      } 
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxInstalledApps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */