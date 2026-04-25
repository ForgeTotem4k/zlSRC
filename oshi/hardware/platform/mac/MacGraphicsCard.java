package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class MacGraphicsCard extends AbstractGraphicsCard {
  MacGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    ArrayList<MacGraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("system_profiler SPDisplaysDataType");
    String str1 = "unknown";
    String str2 = "unknown";
    String str3 = "unknown";
    ArrayList<? extends CharSequence> arrayList1 = new ArrayList();
    long l = 0L;
    byte b = 0;
    for (String str : list) {
      String[] arrayOfString = str.trim().split(":", 2);
      if (arrayOfString.length == 2) {
        String str4 = arrayOfString[0].toLowerCase(Locale.ROOT);
        if (str4.equals("chipset model")) {
          if (b++ > 0) {
            arrayList.add(new MacGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), l));
            arrayList1.clear();
          } 
          str1 = arrayOfString[1].trim();
          continue;
        } 
        if (str4.equals("device id")) {
          str2 = arrayOfString[1].trim();
          continue;
        } 
        if (str4.equals("vendor")) {
          str3 = arrayOfString[1].trim();
          continue;
        } 
        if (str4.contains("version") || str4.contains("revision")) {
          arrayList1.add(str.trim());
          continue;
        } 
        if (str4.startsWith("vram"))
          l = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1].trim()); 
      } 
    } 
    arrayList.add(new MacGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), l));
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */