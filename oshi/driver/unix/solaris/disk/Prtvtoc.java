package oshi.driver.unix.solaris.disk;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Prtvtoc {
  private static final String PRTVTOC_DEV_DSK = "prtvtoc /dev/dsk/";
  
  public static List<HWPartition> queryPartitions(String paramString, int paramInt) {
    ArrayList<HWPartition> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("prtvtoc /dev/dsk/" + paramString);
    if (list.size() > 1) {
      int i = 0;
      for (String str : list) {
        if (str.startsWith("*")) {
          if (str.endsWith("bytes/sector")) {
            String[] arrayOfString = ParseUtil.whitespaces.split(str);
            if (arrayOfString.length > 0)
              i = ParseUtil.parseIntOrDefault(arrayOfString[1], 0); 
          } 
          continue;
        } 
        if (i > 0) {
          String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
          if (arrayOfString.length >= 6 && !"2".equals(arrayOfString[0])) {
            String str2;
            String str3;
            String str1 = paramString + "s" + arrayOfString[0];
            int j = ParseUtil.parseIntOrDefault(arrayOfString[0], 0);
            switch (ParseUtil.parseIntOrDefault(arrayOfString[1], 0)) {
              case 1:
              case 24:
                str2 = "boot";
                break;
              case 2:
                str2 = "root";
                break;
              case 3:
                str2 = "swap";
                break;
              case 4:
                str2 = "usr";
                break;
              case 5:
                str2 = "backup";
                break;
              case 6:
                str2 = "stand";
                break;
              case 7:
                str2 = "var";
                break;
              case 8:
                str2 = "home";
                break;
              case 9:
                str2 = "altsctr";
                break;
              case 10:
                str2 = "cache";
                break;
              case 11:
                str2 = "reserved";
                break;
              case 12:
                str2 = "system";
                break;
              case 14:
                str2 = "public region";
                break;
              case 15:
                str2 = "private region";
                break;
              default:
                str2 = "unknown";
                break;
            } 
            switch (arrayOfString[2]) {
              case "00":
                str3 = "wm";
                break;
              case "10":
                str3 = "rm";
                break;
              case "01":
                str3 = "wu";
                break;
              default:
                str3 = "ru";
                break;
            } 
            long l = i * ParseUtil.parseLongOrDefault(arrayOfString[4], 0L);
            String str4 = "";
            if (arrayOfString.length > 6)
              str4 = arrayOfString[6]; 
            arrayList.add(new HWPartition(str1, str2, str3, "", l, paramInt, j, str4));
          } 
        } 
      } 
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\disk\Prtvtoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */