package oshi.hardware.platform.unix.aix;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
final class AixGraphicsCard extends AbstractGraphicsCard {
  AixGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards(Supplier<List<String>> paramSupplier) {
    ArrayList<AixGraphicsCard> arrayList = new ArrayList();
    boolean bool = false;
    String str1 = null;
    String str2 = null;
    ArrayList<String> arrayList1 = new ArrayList();
    for (String str3 : paramSupplier.get()) {
      String str4 = str3.trim();
      if (str4.startsWith("Name:") && str4.contains("display")) {
        bool = true;
        continue;
      } 
      if (bool && str4.toLowerCase(Locale.ROOT).contains("graphics")) {
        str1 = str4;
        continue;
      } 
      if (bool && str1 != null) {
        if (str4.startsWith("Manufacture ID")) {
          str2 = ParseUtil.removeLeadingDots(str4.substring(14));
          continue;
        } 
        if (str4.contains("Level")) {
          arrayList1.add(str4.replaceAll("\\.\\.+", "="));
          continue;
        } 
        if (str4.startsWith("Hardware Location Code")) {
          arrayList.add(new AixGraphicsCard(str1, "unknown", Util.isBlank(str2) ? "unknown" : str2, arrayList1.isEmpty() ? "unknown" : String.join(",", (Iterable)arrayList1), 0L));
          bool = false;
        } 
      } 
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */