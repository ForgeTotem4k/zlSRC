package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.SysPath;

@ThreadSafe
public final class LinuxPowerSource extends AbstractPowerSource {
  public LinuxPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    double d1 = -1.0D;
    double d2 = -1.0D;
    double d3 = -1.0D;
    double d4 = 0.0D;
    double d5 = -1.0D;
    double d6 = 0.0D;
    boolean bool = false;
    boolean bool1 = false;
    boolean bool2 = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    int i = -1;
    int j = -1;
    int k = -1;
    int m = -1;
    LocalDate localDate = null;
    double d7 = 0.0D;
    ArrayList<LinuxPowerSource> arrayList = new ArrayList();
    if (LinuxOperatingSystem.HAS_UDEV) {
      Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
      try {
        Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
        try {
          udevEnumerate.addMatchSubsystem("power_supply");
          udevEnumerate.scanDevices();
          for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
            String str1 = udevListEntry.getName();
            String str2 = str1.substring(str1.lastIndexOf(File.separatorChar) + 1);
            if (!str2.startsWith("ADP") && !str2.startsWith("AC")) {
              Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(str1);
              if (udevDevice != null)
                try {
                  if (ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_PRESENT"), 1) > 0 && ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_ONLINE"), 1) > 0) {
                    String str3 = getOrDefault(udevDevice, "POWER_SUPPLY_NAME", str2);
                    String str8 = udevDevice.getPropertyValue("POWER_SUPPLY_STATUS");
                    bool1 = "Charging".equals(str8);
                    bool2 = "Discharging".equals(str8);
                    d1 = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_CAPACITY"), -100) / 100.0D;
                    i = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_ENERGY_NOW"), -1);
                    if (i < 0)
                      i = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_CHARGE_NOW"), -1); 
                    j = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_ENERGY_FULL"), 1);
                    if (j < 0)
                      j = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_CHARGE_FULL"), 1); 
                    k = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
                    if (k < 0)
                      k = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1); 
                    d5 = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_VOLTAGE_NOW"), -1);
                    if (d5 > 0.0D) {
                      String str9 = udevDevice.getPropertyValue("POWER_SUPPLY_POWER_NOW");
                      String str10 = udevDevice.getPropertyValue("POWER_SUPPLY_CURRENT_NOW");
                      if (str9 == null) {
                        d6 = ParseUtil.parseIntOrDefault(str10, 0);
                        d4 = d6 * d5;
                      } else if (str10 == null) {
                        d4 = ParseUtil.parseIntOrDefault(str9, 0);
                        d6 = d4 / d5;
                      } else {
                        d6 = ParseUtil.parseIntOrDefault(str10, 0);
                        d4 = ParseUtil.parseIntOrDefault(str9, 0);
                      } 
                    } 
                    m = ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("POWER_SUPPLY_CYCLE_COUNT"), -1);
                    String str5 = getOrDefault(udevDevice, "POWER_SUPPLY_TECHNOLOGY", "unknown");
                    String str4 = getOrDefault(udevDevice, "POWER_SUPPLY_MODEL_NAME", "unknown");
                    String str6 = getOrDefault(udevDevice, "POWER_SUPPLY_MANUFACTURER", "unknown");
                    String str7 = getOrDefault(udevDevice, "POWER_SUPPLY_SERIAL_NUMBER", "unknown");
                    arrayList.add(new LinuxPowerSource(str3, str4, d1, d2, d3, d4, d5, d6, bool, bool1, bool2, capacityUnits, i, j, k, m, str5, localDate, str6, str7, d7));
                  } 
                } finally {
                  udevDevice.unref();
                }  
            } 
          } 
        } finally {
          udevEnumerate.unref();
        } 
      } finally {
        udevContext.unref();
      } 
    } else {
      File file = new File(SysPath.POWER_SUPPLY);
      File[] arrayOfFile = file.listFiles();
      if (arrayOfFile == null)
        return Collections.emptyList(); 
      for (File file1 : arrayOfFile) {
        String str = file1.getName();
        if (!str.startsWith("ADP") && !str.startsWith("AC")) {
          List list = FileUtil.readFile(SysPath.POWER_SUPPLY + "/" + str + "/uevent", false);
          HashMap<Object, Object> hashMap = new HashMap<>();
          for (String str7 : list) {
            String[] arrayOfString = str7.split("=");
            if (arrayOfString.length > 1 && !arrayOfString[1].isEmpty())
              hashMap.put(arrayOfString[0], arrayOfString[1]); 
          } 
          String str1 = (String)hashMap.getOrDefault("POWER_SUPPLY_NAME", str);
          String str6 = (String)hashMap.get("POWER_SUPPLY_STATUS");
          bool1 = "Charging".equals(str6);
          bool2 = "Discharging".equals(str6);
          if (hashMap.containsKey("POWER_SUPPLY_CAPACITY"))
            d1 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CAPACITY"), -100) / 100.0D; 
          if (hashMap.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
            i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_NOW"), -1);
          } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
            i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_NOW"), -1);
          } 
          if (hashMap.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
            i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_FULL"), 1);
          } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
            i = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_FULL"), 1);
          } 
          if (hashMap.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
            j = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
          } else if (hashMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
            j = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
          } 
          if (hashMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW"))
            d5 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_VOLTAGE_NOW"), -1); 
          if (d5 > 0.0D) {
            if (hashMap.containsKey("POWER_SUPPLY_POWER_NOW"))
              d4 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_POWER_NOW"), -1); 
            if (hashMap.containsKey("POWER_SUPPLY_CURRENT_NOW"))
              d6 = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CURRENT_NOW"), -1); 
            if (d4 < 0.0D && d6 >= 0.0D) {
              d4 = d6 * d5;
            } else if (d4 >= 0.0D && d6 < 0.0D) {
              d6 = d4 / d5;
            } else {
              d6 = 0.0D;
              d4 = 0.0D;
            } 
          } 
          if (hashMap.containsKey("POWER_SUPPLY_CYCLE_COUNT"))
            m = ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_CYCLE_COUNT"), -1); 
          String str3 = (String)hashMap.getOrDefault("POWER_SUPPLY_TECHNOLOGY", "unknown");
          String str2 = (String)hashMap.getOrDefault("POWER_SUPPLY_MODEL_NAME", "unknown");
          String str4 = (String)hashMap.getOrDefault("POWER_SUPPLY_MANUFACTURER", "unknown");
          String str5 = (String)hashMap.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", "unknown");
          if (ParseUtil.parseIntOrDefault((String)hashMap.get("POWER_SUPPLY_PRESENT"), 1) > 0)
            arrayList.add(new LinuxPowerSource(str1, str2, d1, d2, d3, d4, d5, d6, bool, bool1, bool2, capacityUnits, i, j, k, m, str3, localDate, str4, str5, d7)); 
        } 
      } 
    } 
    return (List)arrayList;
  }
  
  private static String getOrDefault(Udev.UdevDevice paramUdevDevice, String paramString1, String paramString2) {
    String str = paramUdevDevice.getPropertyValue(paramString1);
    return (str == null) ? paramString2 : str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */