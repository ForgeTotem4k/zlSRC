package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@Immutable
final class LinuxGraphicsCard extends AbstractGraphicsCard {
  LinuxGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    List<GraphicsCard> list = getGraphicsCardsFromLspci();
    if (list.isEmpty())
      list = getGraphicsCardsFromLshw(); 
    return list;
  }
  
  private static List<GraphicsCard> getGraphicsCardsFromLspci() {
    ArrayList<LinuxGraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("lspci -vnnm");
    String str1 = "unknown";
    String str2 = "unknown";
    String str3 = "unknown";
    ArrayList<? extends CharSequence> arrayList1 = new ArrayList();
    boolean bool = false;
    String str4 = null;
    for (String str5 : list) {
      String[] arrayOfString = str5.trim().split(":", 2);
      String str6 = arrayOfString[0];
      if (str6.equals("Class") && (str5.contains("VGA") || str5.contains("3D controller"))) {
        bool = true;
      } else if (str6.equals("Device") && !bool && arrayOfString.length > 1) {
        str4 = arrayOfString[1].trim();
      } 
      if (bool) {
        if (arrayOfString.length < 2) {
          arrayList.add(new LinuxGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), queryLspciMemorySize(str4)));
          arrayList1.clear();
          bool = false;
          continue;
        } 
        if (str6.equals("Device")) {
          Pair pair = ParseUtil.parseLspciMachineReadable(arrayOfString[1].trim());
          if (pair != null) {
            str1 = (String)pair.getA();
            str2 = "0x" + (String)pair.getB();
          } 
          continue;
        } 
        if (str6.equals("Vendor")) {
          Pair pair = ParseUtil.parseLspciMachineReadable(arrayOfString[1].trim());
          if (pair != null) {
            str3 = (String)pair.getA() + " (0x" + (String)pair.getB() + ")";
            continue;
          } 
          str3 = arrayOfString[1].trim();
          continue;
        } 
        if (str6.equals("Rev:"))
          arrayList1.add(str5.trim()); 
      } 
    } 
    if (bool)
      arrayList.add(new LinuxGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), queryLspciMemorySize(str4))); 
    return (List)arrayList;
  }
  
  private static long queryLspciMemorySize(String paramString) {
    long l = 0L;
    List list = ExecutingCommand.runNative("lspci -v -s " + paramString);
    for (String str : list) {
      if (str.contains(" prefetchable"))
        l += ParseUtil.parseLspciMemorySize(str); 
    } 
    return l;
  }
  
  private static List<GraphicsCard> getGraphicsCardsFromLshw() {
    ArrayList<LinuxGraphicsCard> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("lshw -C display");
    String str1 = "unknown";
    String str2 = "unknown";
    String str3 = "unknown";
    ArrayList<? extends CharSequence> arrayList1 = new ArrayList();
    long l = 0L;
    byte b = 0;
    for (String str : list) {
      String[] arrayOfString = str.trim().split(":");
      if (arrayOfString[0].startsWith("*-display")) {
        if (b++ > 0) {
          arrayList.add(new LinuxGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), l));
          arrayList1.clear();
        } 
        continue;
      } 
      if (arrayOfString.length == 2) {
        String str4 = arrayOfString[0];
        if (str4.equals("product")) {
          str1 = arrayOfString[1].trim();
          continue;
        } 
        if (str4.equals("vendor")) {
          str3 = arrayOfString[1].trim();
          continue;
        } 
        if (str4.equals("version")) {
          arrayList1.add(str.trim());
          continue;
        } 
        if (str4.startsWith("resources"))
          l = ParseUtil.parseLshwResourceString(arrayOfString[1].trim()); 
      } 
    } 
    arrayList.add(new LinuxGraphicsCard(str1, str2, str3, arrayList1.isEmpty() ? "unknown" : String.join(", ", arrayList1), l));
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */