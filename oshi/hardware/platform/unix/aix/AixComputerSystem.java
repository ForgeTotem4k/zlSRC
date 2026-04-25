package oshi.hardware.platform.unix.aix;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
final class AixComputerSystem extends AbstractComputerSystem {
  private final Supplier<LsattrStrings> lsattrStrings = Memoizer.memoize(AixComputerSystem::readLsattr);
  
  private final Supplier<List<String>> lscfg;
  
  AixComputerSystem(Supplier<List<String>> paramSupplier) {
    this.lscfg = paramSupplier;
  }
  
  public String getManufacturer() {
    return (this.lsattrStrings.get()).manufacturer;
  }
  
  public String getModel() {
    return (this.lsattrStrings.get()).model;
  }
  
  public String getSerialNumber() {
    return (this.lsattrStrings.get()).serialNumber;
  }
  
  public String getHardwareUUID() {
    return (this.lsattrStrings.get()).uuid;
  }
  
  public Firmware createFirmware() {
    return (Firmware)new AixFirmware((this.lsattrStrings.get()).biosVendor, (this.lsattrStrings.get()).biosPlatformVersion, (this.lsattrStrings.get()).biosVersion);
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new AixBaseboard(this.lscfg);
  }
  
  private static LsattrStrings readLsattr() {
    String str1 = "IBM";
    String str2 = null;
    String str3 = null;
    String str4 = str1;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = "fwversion";
    String str9 = "modelname";
    String str10 = "systemid";
    String str11 = "os_uuid";
    String str12 = "Platform Firmware level is";
    for (String str : ExecutingCommand.runNative("lsattr -El sys0")) {
      if (str.startsWith("fwversion")) {
        str2 = str.split("fwversion")[1].trim();
        int i = str2.indexOf(',');
        if (i > 0 && str2.length() > i) {
          str1 = str2.substring(0, i);
          str2 = str2.substring(i + 1);
        } 
        str2 = ParseUtil.whitespaces.split(str2)[0];
        continue;
      } 
      if (str.startsWith("modelname")) {
        str5 = str.split("modelname")[1].trim();
        int i = str5.indexOf(',');
        if (i > 0 && str5.length() > i) {
          str4 = str5.substring(0, i);
          str5 = str5.substring(i + 1);
        } 
        str5 = ParseUtil.whitespaces.split(str5)[0];
        continue;
      } 
      if (str.startsWith("systemid")) {
        str6 = str.split("systemid")[1].trim();
        str6 = ParseUtil.whitespaces.split(str6)[0];
        continue;
      } 
      if (str.startsWith("os_uuid")) {
        str7 = str.split("os_uuid")[1].trim();
        str7 = ParseUtil.whitespaces.split(str7)[0];
      } 
    } 
    for (String str : ExecutingCommand.runNative("lsmcode -c")) {
      if (str.startsWith("Platform Firmware level is")) {
        str3 = str.split("Platform Firmware level is")[1].trim();
        break;
      } 
    } 
    return new LsattrStrings(str1, str3, str2, str4, str5, str6, str7);
  }
  
  private static final class LsattrStrings {
    private final String biosVendor;
    
    private final String biosPlatformVersion;
    
    private final String biosVersion;
    
    private final String manufacturer;
    
    private final String model;
    
    private final String serialNumber;
    
    private final String uuid;
    
    private LsattrStrings(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5, String param1String6, String param1String7) {
      this.biosVendor = Util.isBlank(param1String1) ? "unknown" : param1String1;
      this.biosPlatformVersion = Util.isBlank(param1String2) ? "unknown" : param1String2;
      this.biosVersion = Util.isBlank(param1String3) ? "unknown" : param1String3;
      this.manufacturer = Util.isBlank(param1String4) ? "unknown" : param1String4;
      this.model = Util.isBlank(param1String5) ? "unknown" : param1String5;
      this.serialNumber = Util.isBlank(param1String6) ? "unknown" : param1String6;
      this.uuid = Util.isBlank(param1String7) ? "unknown" : param1String7;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */