package oshi.hardware.platform.unix.freebsd;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdPowerSource extends AbstractPowerSource {
  public FreeBsdPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    return Arrays.asList(new PowerSource[] { (PowerSource)getPowerSource("BAT0") });
  }
  
  private static FreeBsdPowerSource getPowerSource(String paramString) {
    String str1 = paramString;
    double d1 = 1.0D;
    double d2 = -1.0D;
    double d3 = 0.0D;
    int i = -1;
    double d4 = 0.0D;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    boolean bool4 = false;
    int j = 1;
    int k = 1;
    byte b = -1;
    LocalDate localDate = null;
    double d5 = 0.0D;
    int m = BsdSysctlUtil.sysctl("hw.acpi.battery.state", 0);
    if (m == 2) {
      bool2 = true;
    } else {
      int i1 = BsdSysctlUtil.sysctl("hw.acpi.battery.time", -1);
      d2 = (i1 < 0) ? -1.0D : (60.0D * i1);
      if (m == 1)
        bool3 = true; 
    } 
    int n = BsdSysctlUtil.sysctl("hw.acpi.battery.life", -1);
    if (n > 0)
      d1 = n / 100.0D; 
    List list = ExecutingCommand.runNative("acpiconf -i 0");
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (String str : list) {
      String[] arrayOfString = str.split(":", 2);
      if (arrayOfString.length > 1) {
        String str10 = arrayOfString[1].trim();
        if (!str10.isEmpty())
          hashMap.put(arrayOfString[0], str10); 
      } 
    } 
    String str2 = (String)hashMap.getOrDefault("Model number", "unknown");
    String str3 = (String)hashMap.getOrDefault("Serial number", "unknown");
    String str4 = (String)hashMap.getOrDefault("Type", "unknown");
    String str5 = (String)hashMap.getOrDefault("OEM info", "unknown");
    String str6 = (String)hashMap.get("Design capacity");
    if (str6 != null) {
      k = ParseUtil.getFirstIntValue(str6);
      if (str6.toLowerCase(Locale.ROOT).contains("mah")) {
        capacityUnits = PowerSource.CapacityUnits.MAH;
      } else if (str6.toLowerCase(Locale.ROOT).contains("mwh")) {
        capacityUnits = PowerSource.CapacityUnits.MWH;
      } 
    } 
    str6 = (String)hashMap.get("Last full capacity");
    if (str6 != null) {
      j = ParseUtil.getFirstIntValue(str6);
    } else {
      j = k;
    } 
    double d6 = d2;
    String str7 = (String)hashMap.get("Remaining time");
    if (str7 != null) {
      String[] arrayOfString = str7.split(":");
      if (arrayOfString.length == 2)
        d6 = 3600.0D * ParseUtil.parseIntOrDefault(arrayOfString[0], 0) + 60.0D * ParseUtil.parseIntOrDefault(arrayOfString[1], 0); 
    } 
    String str8 = (String)hashMap.get("Present rate");
    if (str8 != null)
      d3 = ParseUtil.getFirstIntValue(str8); 
    String str9 = (String)hashMap.get("Present voltage");
    if (str9 != null) {
      i = ParseUtil.getFirstIntValue(str9);
      if (i != 0)
        d4 = d3 / i; 
    } 
    return new FreeBsdPowerSource(str1, str2, d1, d2, d6, d3, i, d4, bool1, bool2, bool3, capacityUnits, bool4, j, k, b, str4, localDate, str5, str3, d5);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */