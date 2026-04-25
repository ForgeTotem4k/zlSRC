package oshi.driver.linux.proc;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Quartet;

@ThreadSafe
public final class CpuInfo {
  public static String queryCpuManufacturer() {
    List list = FileUtil.readFile(ProcPath.CPUINFO);
    for (String str : list) {
      if (str.startsWith("CPU implementer")) {
        int i = ParseUtil.parseLastInt(str, 0);
        switch (i) {
          case 65:
            return "ARM";
          case 66:
            return "Broadcom";
          case 67:
            return "Cavium";
          case 68:
            return "DEC";
          case 78:
            return "Nvidia";
          case 80:
            return "APM";
          case 81:
            return "Qualcomm";
          case 83:
            return "Samsung";
          case 86:
            return "Marvell";
          case 102:
            return "Faraday";
          case 105:
            return "Intel";
        } 
        return null;
      } 
    } 
    return null;
  }
  
  public static Quartet<String, String, String, String> queryBoardInfo() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    List list = FileUtil.readFile(ProcPath.CPUINFO);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespacesColonWhitespace.split(str);
      if (arrayOfString.length < 2)
        continue; 
      switch (arrayOfString[0]) {
        case "Hardware":
          str2 = arrayOfString[1];
        case "Revision":
          str3 = arrayOfString[1];
          if (str3.length() > 1)
            str1 = queryBoardManufacturer(str3.charAt(1)); 
        case "Serial":
          str4 = arrayOfString[1];
      } 
    } 
    return new Quartet(str1, str2, str3, str4);
  }
  
  private static String queryBoardManufacturer(char paramChar) {
    switch (paramChar) {
      case '0':
        return "Sony UK";
      case '1':
        return "Egoman";
      case '2':
        return "Embest";
      case '3':
        return "Sony Japan";
      case '4':
        return "Embest";
      case '5':
        return "Stadium";
    } 
    return "unknown";
  }
  
  public static List<String> queryFeatureFlags() {
    return (List<String>)FileUtil.readFile(ProcPath.CPUINFO).stream().filter(paramString -> {
          String str = paramString.toLowerCase(Locale.ROOT);
          return (str.startsWith("flags") || str.startsWith("features"));
        }).distinct().collect(Collectors.toList());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\CpuInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */