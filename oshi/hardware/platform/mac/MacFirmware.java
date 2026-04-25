package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quintet;

@Immutable
final class MacFirmware extends AbstractFirmware {
  private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(MacFirmware::queryEfi);
  
  public String getManufacturer() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getA();
  }
  
  public String getName() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getB();
  }
  
  public String getDescription() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getC();
  }
  
  public String getVersion() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getD();
  }
  
  public String getReleaseDate() {
    return (String)((Quintet)this.manufNameDescVersRelease.get()).getE();
  }
  
  private static Quintet<String, String, String, String, String> queryEfi() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
    if (iOService != null) {
      IOKit.IOIterator iOIterator = iOService.getChildIterator("IODeviceTree");
      if (iOIterator != null) {
        for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
          byte[] arrayOfByte;
          switch (iORegistryEntry.getName()) {
            case "rom":
              arrayOfByte = iORegistryEntry.getByteArrayProperty("vendor");
              if (arrayOfByte != null)
                str1 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
              arrayOfByte = iORegistryEntry.getByteArrayProperty("version");
              if (arrayOfByte != null)
                str4 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
              arrayOfByte = iORegistryEntry.getByteArrayProperty("release-date");
              if (arrayOfByte != null)
                str5 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
              break;
            case "chosen":
              arrayOfByte = iORegistryEntry.getByteArrayProperty("booter-name");
              if (arrayOfByte != null)
                str2 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
              break;
            case "efi":
              arrayOfByte = iORegistryEntry.getByteArrayProperty("firmware-abi");
              if (arrayOfByte != null)
                str3 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
              break;
            default:
              if (Util.isBlank(str2))
                str2 = iORegistryEntry.getStringProperty("IONameMatch"); 
              break;
          } 
          iORegistryEntry.release();
        } 
        iOIterator.release();
      } 
      if (Util.isBlank(str1)) {
        byte[] arrayOfByte = iOService.getByteArrayProperty("manufacturer");
        if (arrayOfByte != null)
          str1 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      } 
      if (Util.isBlank(str4)) {
        byte[] arrayOfByte = iOService.getByteArrayProperty("target-type");
        if (arrayOfByte != null)
          str4 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      } 
      if (Util.isBlank(str2)) {
        byte[] arrayOfByte = iOService.getByteArrayProperty("device_type");
        if (arrayOfByte != null)
          str2 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      } 
      iOService.release();
    } 
    return new Quintet(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4, Util.isBlank(str5) ? "unknown" : str5);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */