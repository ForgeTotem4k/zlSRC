package oshi.hardware.platform.unix.openbsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.hardware.platform.unix.UnixBaseboard;
import oshi.util.Memoizer;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;

@Immutable
public class OpenBsdComputerSystem extends AbstractComputerSystem {
  private final Supplier<String> manufacturer = Memoizer.memoize(OpenBsdComputerSystem::queryManufacturer);
  
  private final Supplier<String> model = Memoizer.memoize(OpenBsdComputerSystem::queryModel);
  
  private final Supplier<String> serialNumber = Memoizer.memoize(OpenBsdComputerSystem::querySerialNumber);
  
  private final Supplier<String> uuid = Memoizer.memoize(OpenBsdComputerSystem::queryUUID);
  
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
  
  protected Firmware createFirmware() {
    return (Firmware)new OpenBsdFirmware();
  }
  
  protected Baseboard createBaseboard() {
    return (Baseboard)new UnixBaseboard(this.manufacturer.get(), this.model.get(), this.serialNumber.get(), OpenBsdSysctlUtil.sysctl("hw.product", "unknown"));
  }
  
  private static String queryManufacturer() {
    return OpenBsdSysctlUtil.sysctl("hw.vendor", "unknown");
  }
  
  private static String queryModel() {
    return OpenBsdSysctlUtil.sysctl("hw.version", "unknown");
  }
  
  private static String querySerialNumber() {
    return OpenBsdSysctlUtil.sysctl("hw.serialno", "unknown");
  }
  
  private static String queryUUID() {
    return OpenBsdSysctlUtil.sysctl("hw.uuid", "unknown");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */