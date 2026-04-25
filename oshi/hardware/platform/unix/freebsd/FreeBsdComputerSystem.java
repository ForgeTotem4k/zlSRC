package oshi.hardware.platform.unix.freebsd;

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
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Quintet;

@Immutable
final class FreeBsdComputerSystem extends AbstractComputerSystem {
  private final Supplier<Quintet<String, String, String, String, String>> manufModelSerialUuidVers = Memoizer.memoize(FreeBsdComputerSystem::readDmiDecode);
  
  public String getManufacturer() {
    return (String)((Quintet)this.manufModelSerialUuidVers.get()).getA();
  }
  
  public String getModel() {
    return (String)((Quintet)this.manufModelSerialUuidVers.get()).getB();
  }
  
  public String getSerialNumber() {
    return (String)((Quintet)this.manufModelSerialUuidVers.get()).getC();
  }
  
  public String getHardwareUUID() {
    return (String)((Quintet)this.manufModelSerialUuidVers.get()).getD();
  }
  
  public Firmware createFirmware() {
    return (Firmware)new FreeBsdFirmware();
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new UnixBaseboard((String)((Quintet)this.manufModelSerialUuidVers.get()).getA(), (String)((Quintet)this.manufModelSerialUuidVers.get()).getB(), (String)((Quintet)this.manufModelSerialUuidVers.get()).getC(), (String)((Quintet)this.manufModelSerialUuidVers.get()).getE());
  }
  
  private static Quintet<String, String, String, String, String> readDmiDecode() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = "Manufacturer:";
    String str7 = "Product Name:";
    String str8 = "Serial Number:";
    String str9 = "UUID:";
    String str10 = "Version:";
    for (String str : ExecutingCommand.runNative("dmidecode -t system")) {
      if (str.contains("Manufacturer:")) {
        str1 = str.split("Manufacturer:")[1].trim();
        continue;
      } 
      if (str.contains("Product Name:")) {
        str2 = str.split("Product Name:")[1].trim();
        continue;
      } 
      if (str.contains("Serial Number:")) {
        str3 = str.split("Serial Number:")[1].trim();
        continue;
      } 
      if (str.contains("UUID:")) {
        str4 = str.split("UUID:")[1].trim();
        continue;
      } 
      if (str.contains("Version:"))
        str5 = str.split("Version:")[1].trim(); 
    } 
    if (Util.isBlank(str3))
      str3 = querySystemSerialNumber(); 
    if (Util.isBlank(str4))
      str4 = BsdSysctlUtil.sysctl("kern.hostuuid", "unknown"); 
    return new Quintet(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4, Util.isBlank(str5) ? "unknown" : str5);
  }
  
  private static String querySystemSerialNumber() {
    String str = "system.hardware.serial =";
    for (String str1 : ExecutingCommand.runNative("lshal")) {
      if (str1.contains(str))
        return ParseUtil.getSingleQuoteStringValue(str1); 
    } 
    return "unknown";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */