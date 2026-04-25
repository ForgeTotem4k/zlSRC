package oshi.hardware.common;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractGlobalMemory implements GlobalMemory {
  public List<PhysicalMemory> getPhysicalMemory() {
    ArrayList<PhysicalMemory> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("dmidecode --type 17");
    byte b = 0;
    String str1 = "unknown";
    String str2 = "";
    long l1 = 0L;
    long l2 = 0L;
    String str3 = "unknown";
    String str4 = "unknown";
    String str5 = "unknown";
    String str6 = "unknown";
    for (String str : list) {
      if (str.trim().contains("DMI type 17")) {
        if (b++ > 0) {
          if (l1 > 0L)
            arrayList.add(new PhysicalMemory(str1 + str2, l1, l2, str3, str4, str5, str6)); 
          str1 = "unknown";
          str2 = "";
          l1 = 0L;
          l2 = 0L;
        } 
        continue;
      } 
      if (b > 0) {
        String[] arrayOfString = str.trim().split(":");
        if (arrayOfString.length == 2)
          switch (arrayOfString[0]) {
            case "Bank Locator":
              str1 = arrayOfString[1].trim();
            case "Locator":
              str2 = "/" + arrayOfString[1].trim();
            case "Size":
              l1 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1].trim());
            case "Type":
              str4 = arrayOfString[1].trim();
            case "Speed":
              l2 = ParseUtil.parseSpeed(arrayOfString[1]);
            case "Manufacturer":
              str3 = arrayOfString[1].trim();
            case "PartNumber":
            case "Part Number":
              str5 = arrayOfString[1].trim();
            case "Serial Number":
              str6 = arrayOfString[1].trim();
          }  
      } 
    } 
    if (l1 > 0L)
      arrayList.add(new PhysicalMemory(str1 + str2, l1, l2, str3, str4, str5, str6)); 
    return arrayList;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Available: ");
    stringBuilder.append(FormatUtil.formatBytes(getAvailable()));
    stringBuilder.append("/");
    stringBuilder.append(FormatUtil.formatBytes(getTotal()));
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */