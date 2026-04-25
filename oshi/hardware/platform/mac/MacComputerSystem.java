package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
final class MacComputerSystem extends AbstractComputerSystem {
  private final Supplier<Quartet<String, String, String, String>> manufacturerModelSerialUUID = Memoizer.memoize(MacComputerSystem::platformExpert);
  
  public String getManufacturer() {
    return (String)((Quartet)this.manufacturerModelSerialUUID.get()).getA();
  }
  
  public String getModel() {
    return (String)((Quartet)this.manufacturerModelSerialUUID.get()).getB();
  }
  
  public String getSerialNumber() {
    return (String)((Quartet)this.manufacturerModelSerialUUID.get()).getC();
  }
  
  public String getHardwareUUID() {
    return (String)((Quartet)this.manufacturerModelSerialUUID.get()).getD();
  }
  
  public Firmware createFirmware() {
    return (Firmware)new MacFirmware();
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new MacBaseboard();
  }
  
  private static Quartet<String, String, String, String> platformExpert() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
    if (iOService != null) {
      byte[] arrayOfByte = iOService.getByteArrayProperty("manufacturer");
      if (arrayOfByte != null)
        str1 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      arrayOfByte = iOService.getByteArrayProperty("model");
      if (arrayOfByte != null)
        str2 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      str3 = iOService.getStringProperty("IOPlatformSerialNumber");
      str4 = iOService.getStringProperty("IOPlatformUUID");
      iOService.release();
    } 
    return new Quartet(Util.isBlank(str1) ? "Apple Inc." : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */