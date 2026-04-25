package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class FreeBsdGraphicsCard extends AbstractGraphicsCard {
  private static final String PCI_CLASS_DISPLAY = "0x03";
  
  FreeBsdGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    ArrayList<FreeBsdGraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("pciconf -lv");
    if (list.isEmpty())
      return Collections.emptyList(); 
    String str1 = "unknown";
    String str2 = "unknown";
    String str3 = "unknown";
    String str4 = "";
    String str5 = "unknown";
    for (String str : list) {
      if (str.contains("class=0x")) {
        if ("0x03".equals(str4))
          arrayList.add(new FreeBsdGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "unknown" : str3, str2.isEmpty() ? "unknown" : str2, str5.isEmpty() ? "unknown" : str5, 0L)); 
        String[] arrayOfString1 = ParseUtil.whitespaces.split(str);
        for (String str6 : arrayOfString1) {
          String[] arrayOfString2 = str6.split("=");
          if (arrayOfString2.length > 1)
            if (arrayOfString2[0].equals("class") && arrayOfString2[1].length() >= 4) {
              str4 = arrayOfString2[1].substring(0, 4);
            } else if (arrayOfString2[0].equals("chip") && arrayOfString2[1].length() >= 10) {
              str3 = arrayOfString2[1].substring(0, 6);
              str2 = "0x" + arrayOfString2[1].substring(6, 10);
            } else if (arrayOfString2[0].contains("rev")) {
              str5 = str6;
            }  
        } 
        str1 = "unknown";
        continue;
      } 
      String[] arrayOfString = str.trim().split("=", 2);
      if (arrayOfString.length == 2) {
        String str6 = arrayOfString[0].trim();
        if (str6.equals("vendor")) {
          str2 = ParseUtil.getSingleQuoteStringValue(str) + (str2.equals("unknown") ? "" : (" (" + str2 + ")"));
          continue;
        } 
        if (str6.equals("device"))
          str1 = ParseUtil.getSingleQuoteStringValue(str); 
      } 
    } 
    if ("0x03".equals(str4))
      arrayList.add(new FreeBsdGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "unknown" : str3, str2.isEmpty() ? "unknown" : str2, str5.isEmpty() ? "unknown" : str5, 0L)); 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */