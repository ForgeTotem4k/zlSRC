package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class SolarisGraphicsCard extends AbstractGraphicsCard {
  private static final String PCI_CLASS_DISPLAY = "0003";
  
  SolarisGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    ArrayList<GraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("prtconf -pv");
    if (list.isEmpty())
      return arrayList; 
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    ArrayList<? extends CharSequence> arrayList1 = new ArrayList();
    for (String str : list) {
      if (str.contains("Node 0x")) {
        if ("0003".equals(str4))
          arrayList.add(new SolarisGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "unknown" : str3, str2.isEmpty() ? "unknown" : str2, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), 0L)); 
        str1 = "";
        str2 = "unknown";
        str3 = "unknown";
        str4 = "";
        arrayList1.clear();
        continue;
      } 
      String[] arrayOfString = str.trim().split(":", 2);
      if (arrayOfString.length == 2) {
        if (arrayOfString[0].equals("model")) {
          str1 = ParseUtil.getSingleQuoteStringValue(str);
          continue;
        } 
        if (arrayOfString[0].equals("name")) {
          if (str1.isEmpty())
            str1 = ParseUtil.getSingleQuoteStringValue(str); 
          continue;
        } 
        if (arrayOfString[0].equals("vendor-id")) {
          str2 = "0x" + str.substring(str.length() - 4);
          continue;
        } 
        if (arrayOfString[0].equals("device-id")) {
          str3 = "0x" + str.substring(str.length() - 4);
          continue;
        } 
        if (arrayOfString[0].equals("revision-id")) {
          arrayList1.add(str.trim());
          continue;
        } 
        if (arrayOfString[0].equals("class-code"))
          str4 = str.substring(str.length() - 8, str.length() - 4); 
      } 
    } 
    if ("0003".equals(str4))
      arrayList.add(new SolarisGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "unknown" : str3, str2.isEmpty() ? "unknown" : str2, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), 0L)); 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */