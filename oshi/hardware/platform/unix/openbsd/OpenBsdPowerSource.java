package oshi.hardware.platform.unix.openbsd;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class OpenBsdPowerSource extends AbstractPowerSource {
  public OpenBsdPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    HashSet<String> hashSet = new HashSet();
    for (String str : ExecutingCommand.runNative("systat -ab sensors")) {
      if (str.contains(".amphour") || str.contains(".watthour")) {
        int i = str.indexOf('.');
        hashSet.add(str.substring(0, i));
      } 
    } 
    ArrayList<OpenBsdPowerSource> arrayList = new ArrayList();
    for (String str : hashSet)
      arrayList.add(getPowerSource(str)); 
    return (List)arrayList;
  }
  
  private static OpenBsdPowerSource getPowerSource(String paramString) {
    String str1 = paramString.startsWith("acpi") ? paramString.substring(4) : paramString;
    double d1 = 1.0D;
    double d2 = -1.0D;
    double d3 = 0.0D;
    double d4 = -1.0D;
    double d5 = 0.0D;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    int i = 0;
    int j = 1;
    int k = 1;
    byte b = -1;
    LocalDate localDate = null;
    double d6 = 0.0D;
    for (String str : ExecutingCommand.runNative("systat -ab sensors")) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 1 && arrayOfString[0].startsWith(paramString)) {
        if (arrayOfString[0].contains("volt0") || (arrayOfString[0].contains("volt") && str.contains("current"))) {
          d4 = ParseUtil.parseDoubleOrDefault(arrayOfString[1], -1.0D);
          continue;
        } 
        if (arrayOfString[0].contains("current0")) {
          d5 = ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D);
          continue;
        } 
        if (arrayOfString[0].contains("temp0")) {
          d6 = ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D);
          continue;
        } 
        if (arrayOfString[0].contains("watthour") || arrayOfString[0].contains("amphour")) {
          capacityUnits = arrayOfString[0].contains("watthour") ? PowerSource.CapacityUnits.MWH : PowerSource.CapacityUnits.MAH;
          if (str.contains("remaining")) {
            i = (int)(1000.0D * ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D));
            continue;
          } 
          if (str.contains("full")) {
            j = (int)(1000.0D * ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D));
            continue;
          } 
          if (str.contains("new") || str.contains("design"))
            k = (int)(1000.0D * ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D)); 
        } 
      } 
    } 
    int m = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("apm -b"), 255);
    if (m < 4) {
      bool1 = true;
      if (m == 3) {
        bool2 = true;
      } else {
        int i1 = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("apm -m"), -1);
        d2 = (i1 < 0) ? -1.0D : (60.0D * i1);
        bool3 = true;
      } 
    } 
    int n = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("apm -l"), -1);
    if (n > 0)
      d1 = n / 100.0D; 
    if (j < k && j < i) {
      j = k;
    } else if (k < j && k < i) {
      k = j;
    } 
    String str2 = "unknown";
    String str3 = "unknown";
    String str4 = "unknown";
    String str5 = "unknown";
    double d7 = d2;
    if (d4 > 0.0D)
      if (d5 > 0.0D && d3 == 0.0D) {
        d3 = d5 * d4;
      } else if (d5 == 0.0D && d3 > 0.0D) {
        d5 = d3 / d4;
      }  
    return new OpenBsdPowerSource(str1, str2, d1, d2, d7, d3, d4, d5, bool1, bool2, bool3, capacityUnits, i, j, k, b, str4, localDate, str5, str3, d6);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */