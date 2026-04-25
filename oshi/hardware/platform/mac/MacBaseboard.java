package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
final class MacBaseboard extends AbstractBaseboard {
  private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(MacBaseboard::queryPlatform);
  
  public String getManufacturer() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getA();
  }
  
  public String getModel() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getB();
  }
  
  public String getVersion() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getC();
  }
  
  public String getSerialNumber() {
    return (String)((Quartet)this.manufModelVersSerial.get()).getD();
  }
  
  private static Quartet<String, String, String, String> queryPlatform() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
    if (iOService != null) {
      byte[] arrayOfByte = iOService.getByteArrayProperty("manufacturer");
      if (arrayOfByte != null)
        str1 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      arrayOfByte = iOService.getByteArrayProperty("board-id");
      if (arrayOfByte != null)
        str2 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      if (Util.isBlank(str2)) {
        arrayOfByte = iOService.getByteArrayProperty("model-number");
        if (arrayOfByte != null)
          str2 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      } 
      arrayOfByte = iOService.getByteArrayProperty("version");
      if (arrayOfByte != null)
        str3 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      arrayOfByte = iOService.getByteArrayProperty("mlb-serial-number");
      if (arrayOfByte != null)
        str4 = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      if (Util.isBlank(str4))
        str4 = iOService.getStringProperty("IOPlatformSerialNumber"); 
      iOService.release();
    } 
    return new Quartet(Util.isBlank(str1) ? "Apple Inc." : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */