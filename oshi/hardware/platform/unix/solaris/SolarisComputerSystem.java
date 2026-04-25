package oshi.hardware.platform.unix.solaris;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.hardware.platform.unix.UnixBaseboard;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
final class SolarisComputerSystem extends AbstractComputerSystem {
  private final Supplier<SmbiosStrings> smbiosStrings = Memoizer.memoize(SolarisComputerSystem::readSmbios);
  
  public String getManufacturer() {
    return (this.smbiosStrings.get()).manufacturer;
  }
  
  public String getModel() {
    return (this.smbiosStrings.get()).model;
  }
  
  public String getSerialNumber() {
    return (this.smbiosStrings.get()).serialNumber;
  }
  
  public String getHardwareUUID() {
    return (this.smbiosStrings.get()).uuid;
  }
  
  public Firmware createFirmware() {
    return (Firmware)new SolarisFirmware((this.smbiosStrings.get()).biosVendor, (this.smbiosStrings.get()).biosVersion, (this.smbiosStrings.get()).biosDate);
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new UnixBaseboard((this.smbiosStrings.get()).boardManufacturer, (this.smbiosStrings.get()).boardModel, (this.smbiosStrings.get()).boardSerialNumber, (this.smbiosStrings.get()).boardVersion);
  }
  
  private static SmbiosStrings readSmbios() {
    String str = "Serial Number";
    SmbType smbType = null;
    EnumMap<SmbType, Object> enumMap = new EnumMap<>(SmbType.class);
    enumMap.put(SmbType.SMB_TYPE_BIOS, new HashMap<>());
    enumMap.put(SmbType.SMB_TYPE_SYSTEM, new HashMap<>());
    enumMap.put(SmbType.SMB_TYPE_BASEBOARD, new HashMap<>());
    for (String str1 : ExecutingCommand.runNative("smbios")) {
      if (str1.contains("SMB_TYPE_") && (smbType = getSmbType(str1)) == null)
        break; 
      Integer integer = Integer.valueOf(str1.indexOf(":"));
      if (smbType != null && integer.intValue() >= 0) {
        String str2 = str1.substring(0, integer.intValue()).trim();
        String str3 = str1.substring(integer.intValue() + 1).trim();
        ((Map<String, String>)enumMap.get(smbType)).put(str2, str3);
      } 
    } 
    Map map1 = (Map)enumMap.get(SmbType.SMB_TYPE_BIOS);
    Map<String, String> map = (Map)enumMap.get(SmbType.SMB_TYPE_SYSTEM);
    Map map2 = (Map)enumMap.get(SmbType.SMB_TYPE_BASEBOARD);
    if (!map.containsKey("Serial Number") || Util.isBlank((String)map.get("Serial Number")))
      map.put("Serial Number", readSerialNumber()); 
    return new SmbiosStrings(map1, map, map2);
  }
  
  private static SmbType getSmbType(String paramString) {
    for (SmbType smbType : SmbType.values()) {
      if (paramString.contains(smbType.name()))
        return smbType; 
    } 
    return null;
  }
  
  private static String readSerialNumber() {
    String str = ExecutingCommand.getFirstAnswer("sneep");
    if (str.isEmpty()) {
      String str1 = "chassis-sn:";
      for (String str2 : ExecutingCommand.runNative("prtconf -pv")) {
        if (str2.contains(str1)) {
          str = ParseUtil.getSingleQuoteStringValue(str2);
          break;
        } 
      } 
    } 
    return str;
  }
  
  private static final class SmbiosStrings {
    private final String biosVendor;
    
    private final String biosVersion;
    
    private final String biosDate;
    
    private final String manufacturer;
    
    private final String model;
    
    private final String serialNumber;
    
    private final String uuid;
    
    private final String boardManufacturer;
    
    private final String boardModel;
    
    private final String boardVersion;
    
    private final String boardSerialNumber;
    
    private SmbiosStrings(Map<String, String> param1Map1, Map<String, String> param1Map2, Map<String, String> param1Map3) {
      String str1 = "Vendor";
      String str2 = "Release Date";
      String str3 = "Version String";
      String str4 = "Manufacturer";
      String str5 = "Product";
      String str6 = "Serial Number";
      String str7 = "UUID";
      String str8 = "Version";
      this.biosVendor = ParseUtil.getValueOrUnknown(param1Map1, "Vendor");
      this.biosVersion = ParseUtil.getValueOrUnknown(param1Map1, "Version String");
      this.biosDate = ParseUtil.getValueOrUnknown(param1Map1, "Release Date");
      this.manufacturer = ParseUtil.getValueOrUnknown(param1Map2, "Manufacturer");
      this.model = ParseUtil.getValueOrUnknown(param1Map2, "Product");
      this.serialNumber = ParseUtil.getValueOrUnknown(param1Map2, "Serial Number");
      this.uuid = ParseUtil.getValueOrUnknown(param1Map2, "UUID");
      this.boardManufacturer = ParseUtil.getValueOrUnknown(param1Map3, "Manufacturer");
      this.boardModel = ParseUtil.getValueOrUnknown(param1Map3, "Product");
      this.boardVersion = ParseUtil.getValueOrUnknown(param1Map3, "Version");
      this.boardSerialNumber = ParseUtil.getValueOrUnknown(param1Map3, "Serial Number");
    }
  }
  
  public enum SmbType {
    SMB_TYPE_BIOS, SMB_TYPE_SYSTEM, SMB_TYPE_BASEBOARD;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */