package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32Bios;
import oshi.driver.windows.wmi.Win32ComputerSystem;
import oshi.driver.windows.wmi.Win32ComputerSystemProduct;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@Immutable
final class WindowsComputerSystem extends AbstractComputerSystem {
  private final Supplier<Pair<String, String>> manufacturerModel = Memoizer.memoize(WindowsComputerSystem::queryManufacturerModel);
  
  private final Supplier<Pair<String, String>> serialNumberUUID = Memoizer.memoize(WindowsComputerSystem::querySystemSerialNumberUUID);
  
  public String getManufacturer() {
    return (String)((Pair)this.manufacturerModel.get()).getA();
  }
  
  public String getModel() {
    return (String)((Pair)this.manufacturerModel.get()).getB();
  }
  
  public String getSerialNumber() {
    return (String)((Pair)this.serialNumberUUID.get()).getA();
  }
  
  public String getHardwareUUID() {
    return (String)((Pair)this.serialNumberUUID.get()).getB();
  }
  
  public Firmware createFirmware() {
    return (Firmware)new WindowsFirmware();
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new WindowsBaseboard();
  }
  
  private static Pair<String, String> queryManufacturerModel() {
    String str1 = null;
    String str2 = null;
    WbemcliUtil.WmiResult wmiResult = Win32ComputerSystem.queryComputerSystem();
    if (wmiResult.getResultCount() > 0) {
      str1 = WmiUtil.getString(wmiResult, (Enum)Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
      str2 = WmiUtil.getString(wmiResult, (Enum)Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
    } 
    return new Pair(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2);
  }
  
  private static Pair<String, String> querySystemSerialNumberUUID() {
    String str1 = null;
    String str2 = null;
    WbemcliUtil.WmiResult wmiResult = Win32ComputerSystemProduct.queryIdentifyingNumberUUID();
    if (wmiResult.getResultCount() > 0) {
      str1 = WmiUtil.getString(wmiResult, (Enum)Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0);
      str2 = WmiUtil.getString(wmiResult, (Enum)Win32ComputerSystemProduct.ComputerSystemProductProperty.UUID, 0);
    } 
    if (Util.isBlank(str1))
      str1 = querySerialFromBios(); 
    if (Util.isBlank(str1))
      str1 = "unknown"; 
    if (Util.isBlank(str2))
      str2 = "unknown"; 
    return new Pair(str1, str2);
  }
  
  private static String querySerialFromBios() {
    WbemcliUtil.WmiResult wmiResult = Win32Bios.querySerialNumber();
    return (wmiResult.getResultCount() > 0) ? WmiUtil.getString(wmiResult, (Enum)Win32Bios.BiosSerialProperty.SERIALNUMBER, 0) : null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */