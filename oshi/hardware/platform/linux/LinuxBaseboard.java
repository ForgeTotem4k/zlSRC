package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Sysfs;
import oshi.driver.linux.proc.CpuInfo;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.tuples.Quartet;

@Immutable
final class LinuxBaseboard extends AbstractBaseboard {
  private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
  
  private final Supplier<String> model = Memoizer.memoize(this::queryModel);
  
  private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
  
  private final Supplier<String> serialNumber = Memoizer.memoize(this::querySerialNumber);
  
  private final Supplier<Quartet<String, String, String, String>> manufacturerModelVersionSerial = Memoizer.memoize(CpuInfo::queryBoardInfo);
  
  public String getManufacturer() {
    return this.manufacturer.get();
  }
  
  public String getModel() {
    return this.model.get();
  }
  
  public String getVersion() {
    return this.version.get();
  }
  
  public String getSerialNumber() {
    return this.serialNumber.get();
  }
  
  private String queryManufacturer() {
    String str = null;
    return ((str = Sysfs.queryBoardVendor()) == null && (str = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getA()) == null) ? "unknown" : str;
  }
  
  private String queryModel() {
    String str = null;
    return ((str = Sysfs.queryBoardModel()) == null && (str = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getB()) == null) ? "unknown" : str;
  }
  
  private String queryVersion() {
    String str = null;
    return ((str = Sysfs.queryBoardVersion()) == null && (str = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getC()) == null) ? "unknown" : str;
  }
  
  private String querySerialNumber() {
    String str = null;
    return ((str = Sysfs.queryBoardSerial()) == null && (str = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getD()) == null) ? "unknown" : str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */