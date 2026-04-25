package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Devicetree;
import oshi.driver.linux.Dmidecode;
import oshi.driver.linux.Lshal;
import oshi.driver.linux.Lshw;
import oshi.driver.linux.Sysfs;
import oshi.driver.linux.proc.CpuInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;

@Immutable
final class LinuxComputerSystem extends AbstractComputerSystem {
  private final Supplier<String> manufacturer = Memoizer.memoize(LinuxComputerSystem::queryManufacturer);
  
  private final Supplier<String> model = Memoizer.memoize(LinuxComputerSystem::queryModel);
  
  private final Supplier<String> serialNumber = Memoizer.memoize(LinuxComputerSystem::querySerialNumber);
  
  private final Supplier<String> uuid = Memoizer.memoize(LinuxComputerSystem::queryUUID);
  
  public String getManufacturer() {
    return this.manufacturer.get();
  }
  
  public String getModel() {
    return this.model.get();
  }
  
  public String getSerialNumber() {
    return this.serialNumber.get();
  }
  
  public String getHardwareUUID() {
    return this.uuid.get();
  }
  
  public Firmware createFirmware() {
    return (Firmware)new LinuxFirmware();
  }
  
  public Baseboard createBaseboard() {
    return (Baseboard)new LinuxBaseboard();
  }
  
  private static String queryManufacturer() {
    String str = null;
    return ((str = Sysfs.querySystemVendor()) == null && (str = CpuInfo.queryCpuManufacturer()) == null) ? "unknown" : str;
  }
  
  private static String queryModel() {
    String str = null;
    return ((str = Sysfs.queryProductModel()) == null && (str = Devicetree.queryModel()) == null && (str = Lshw.queryModel()) == null) ? "unknown" : str;
  }
  
  private static String querySerialNumber() {
    String str = null;
    return ((str = Sysfs.queryProductSerial()) == null && (str = Dmidecode.querySerialNumber()) == null && (str = Lshal.querySerialNumber()) == null && (str = Lshw.querySerialNumber()) == null) ? "unknown" : str;
  }
  
  private static String queryUUID() {
    String str = null;
    return ((str = Sysfs.queryUUID()) == null && (str = Dmidecode.queryUUID()) == null && (str = Lshal.queryUUID()) == null && (str = Lshw.queryUUID()) == null) ? "unknown" : str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */