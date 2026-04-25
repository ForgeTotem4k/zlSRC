package oshi.hardware.platform.unix.openbsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;

@Immutable
final class OpenBsdGraphicsCard extends AbstractGraphicsCard {
  private static final String PCI_CLASS_DISPLAY = "Class: 03 Display";
  
  private static final Pattern PCI_DUMP_HEADER = Pattern.compile(" \\d+:\\d+:\\d+: (.+)");
  
  OpenBsdGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    ArrayList<OpenBsdGraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("pcidump -v");
    if (list.isEmpty())
      return Collections.emptyList(); 
    String str1 = "";
    String str2 = "";
    String str3 = "";
    boolean bool = false;
    String str4 = "";
    for (String str : list) {
      Matcher matcher = PCI_DUMP_HEADER.matcher(str);
      if (matcher.matches()) {
        if (bool)
          arrayList.add(new OpenBsdGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "0x0000" : str3, str2.isEmpty() ? "0x0000" : str2, str4.isEmpty() ? "unknown" : str4, 0L)); 
        str1 = matcher.group(1);
        str2 = "";
        str3 = "";
        bool = false;
        str4 = "";
        continue;
      } 
      if (!bool) {
        int i = str.indexOf("Vendor ID: ");
        if (i >= 0 && str.length() >= i + 15)
          str2 = "0x" + str.substring(i + 11, i + 15); 
        i = str.indexOf("Product ID: ");
        if (i >= 0 && str.length() >= i + 16)
          str3 = "0x" + str.substring(i + 12, i + 16); 
        if (str.contains("Class: 03 Display"))
          bool = true; 
        continue;
      } 
      if (str4.isEmpty()) {
        int i = str.indexOf("Revision: ");
        if (i >= 0)
          str4 = str.substring(i); 
      } 
    } 
    if (bool)
      arrayList.add(new OpenBsdGraphicsCard(str1.isEmpty() ? "unknown" : str1, str3.isEmpty() ? "0x0000" : str3, str2.isEmpty() ? "0x0000" : str2, str4.isEmpty() ? "unknown" : str4, 0L)); 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */