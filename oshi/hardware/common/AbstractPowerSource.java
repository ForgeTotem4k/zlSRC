package oshi.hardware.common;

import com.sun.jna.Platform;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.platform.linux.LinuxPowerSource;
import oshi.hardware.platform.mac.MacPowerSource;
import oshi.hardware.platform.unix.aix.AixPowerSource;
import oshi.hardware.platform.unix.freebsd.FreeBsdPowerSource;
import oshi.hardware.platform.unix.solaris.SolarisPowerSource;
import oshi.hardware.platform.windows.WindowsPowerSource;

@ThreadSafe
public abstract class AbstractPowerSource implements PowerSource {
  private String name;
  
  private String deviceName;
  
  private double remainingCapacityPercent;
  
  private double timeRemainingEstimated;
  
  private double timeRemainingInstant;
  
  private double powerUsageRate;
  
  private double voltage;
  
  private double amperage;
  
  private boolean powerOnLine;
  
  private boolean charging;
  
  private boolean discharging;
  
  private PowerSource.CapacityUnits capacityUnits;
  
  private int currentCapacity;
  
  private int maxCapacity;
  
  private int designCapacity;
  
  private int cycleCount;
  
  private String chemistry;
  
  private LocalDate manufactureDate;
  
  private String manufacturer;
  
  private String serialNumber;
  
  private double temperature;
  
  protected AbstractPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    this.name = paramString1;
    this.deviceName = paramString2;
    this.remainingCapacityPercent = paramDouble1;
    this.timeRemainingEstimated = paramDouble2;
    this.timeRemainingInstant = paramDouble3;
    this.powerUsageRate = paramDouble4;
    this.voltage = paramDouble5;
    this.amperage = paramDouble6;
    this.powerOnLine = paramBoolean1;
    this.charging = paramBoolean2;
    this.discharging = paramBoolean3;
    this.capacityUnits = paramCapacityUnits;
    this.currentCapacity = paramInt1;
    this.maxCapacity = paramInt2;
    this.designCapacity = paramInt3;
    this.cycleCount = paramInt4;
    this.chemistry = paramString3;
    this.manufactureDate = paramLocalDate;
    this.manufacturer = paramString4;
    this.serialNumber = paramString5;
    this.temperature = paramDouble7;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDeviceName() {
    return this.deviceName;
  }
  
  public double getRemainingCapacityPercent() {
    return this.remainingCapacityPercent;
  }
  
  public double getTimeRemainingEstimated() {
    return this.timeRemainingEstimated;
  }
  
  public double getTimeRemainingInstant() {
    return this.timeRemainingInstant;
  }
  
  public double getPowerUsageRate() {
    return this.powerUsageRate;
  }
  
  public double getVoltage() {
    return this.voltage;
  }
  
  public double getAmperage() {
    return this.amperage;
  }
  
  public boolean isPowerOnLine() {
    return this.powerOnLine;
  }
  
  public boolean isCharging() {
    return this.charging;
  }
  
  public boolean isDischarging() {
    return this.discharging;
  }
  
  public PowerSource.CapacityUnits getCapacityUnits() {
    return this.capacityUnits;
  }
  
  public int getCurrentCapacity() {
    return this.currentCapacity;
  }
  
  public int getMaxCapacity() {
    return this.maxCapacity;
  }
  
  public int getDesignCapacity() {
    return this.designCapacity;
  }
  
  public int getCycleCount() {
    return this.cycleCount;
  }
  
  public String getChemistry() {
    return this.chemistry;
  }
  
  public LocalDate getManufactureDate() {
    return this.manufactureDate;
  }
  
  public String getManufacturer() {
    return this.manufacturer;
  }
  
  public String getSerialNumber() {
    return this.serialNumber;
  }
  
  public double getTemperature() {
    return this.temperature;
  }
  
  public boolean updateAttributes() {
    List<PowerSource> list = getPowerSources();
    for (PowerSource powerSource : list) {
      if (powerSource.getName().equals(this.name)) {
        this.name = powerSource.getName();
        this.deviceName = powerSource.getDeviceName();
        this.remainingCapacityPercent = powerSource.getRemainingCapacityPercent();
        this.timeRemainingEstimated = powerSource.getTimeRemainingEstimated();
        this.timeRemainingInstant = powerSource.getTimeRemainingInstant();
        this.powerUsageRate = powerSource.getPowerUsageRate();
        this.voltage = powerSource.getVoltage();
        this.amperage = powerSource.getAmperage();
        this.powerOnLine = powerSource.isPowerOnLine();
        this.charging = powerSource.isCharging();
        this.discharging = powerSource.isDischarging();
        this.capacityUnits = powerSource.getCapacityUnits();
        this.currentCapacity = powerSource.getCurrentCapacity();
        this.maxCapacity = powerSource.getMaxCapacity();
        this.designCapacity = powerSource.getDesignCapacity();
        this.cycleCount = powerSource.getCycleCount();
        this.chemistry = powerSource.getChemistry();
        this.manufactureDate = powerSource.getManufactureDate();
        this.manufacturer = powerSource.getManufacturer();
        this.serialNumber = powerSource.getSerialNumber();
        this.temperature = powerSource.getTemperature();
        return true;
      } 
    } 
    return false;
  }
  
  private static List<PowerSource> getPowerSources() {
    switch (SystemInfo.getCurrentPlatform()) {
      case WINDOWS:
        return WindowsPowerSource.getPowerSources();
      case MACOS:
        return MacPowerSource.getPowerSources();
      case LINUX:
        return LinuxPowerSource.getPowerSources();
      case SOLARIS:
        return SolarisPowerSource.getPowerSources();
      case FREEBSD:
        return FreeBsdPowerSource.getPowerSources();
      case AIX:
        return AixPowerSource.getPowerSources();
    } 
    throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Name: ").append(getName()).append(", ");
    stringBuilder.append("Device Name: ").append(getDeviceName()).append(",\n ");
    stringBuilder.append("RemainingCapacityPercent: ").append(getRemainingCapacityPercent() * 100.0D).append("%, ");
    stringBuilder.append("Time Remaining: ").append(formatTimeRemaining(getTimeRemainingEstimated())).append(", ");
    stringBuilder.append("Time Remaining Instant: ").append(formatTimeRemaining(getTimeRemainingInstant())).append(",\n ");
    stringBuilder.append("Power Usage Rate: ").append(getPowerUsageRate()).append("mW, ");
    stringBuilder.append("Voltage: ");
    if (getVoltage() > 0.0D) {
      stringBuilder.append(getVoltage()).append("V, ");
    } else {
      stringBuilder.append("unknown").append(", ");
    } 
    stringBuilder.append("Amperage: ").append(getAmperage()).append("mA,\n ");
    stringBuilder.append("Power OnLine: ").append(isPowerOnLine()).append(", ");
    stringBuilder.append("Charging: ").append(isCharging()).append(", ");
    stringBuilder.append("Discharging: ").append(isDischarging()).append(",\n ");
    stringBuilder.append("Capacity Units: ").append(getCapacityUnits()).append(", ");
    stringBuilder.append("Current Capacity: ").append(getCurrentCapacity()).append(", ");
    stringBuilder.append("Max Capacity: ").append(getMaxCapacity()).append(", ");
    stringBuilder.append("Design Capacity: ").append(getDesignCapacity()).append(",\n ");
    stringBuilder.append("Cycle Count: ").append(getCycleCount()).append(", ");
    stringBuilder.append("Chemistry: ").append(getChemistry()).append(", ");
    stringBuilder.append("Manufacture Date: ").append((getManufactureDate() != null) ? getManufactureDate() : "unknown").append(", ");
    stringBuilder.append("Manufacturer: ").append(getManufacturer()).append(",\n ");
    stringBuilder.append("SerialNumber: ").append(getSerialNumber()).append(", ");
    stringBuilder.append("Temperature: ");
    if (getTemperature() > 0.0D) {
      stringBuilder.append(getTemperature()).append("°C");
    } else {
      stringBuilder.append("unknown");
    } 
    return stringBuilder.toString();
  }
  
  private static String formatTimeRemaining(double paramDouble) {
    String str;
    if (paramDouble < -1.5D) {
      str = "Charging";
    } else if (paramDouble < 0.0D) {
      str = "Unknown";
    } else {
      int i = (int)(paramDouble / 3600.0D);
      int j = (int)(paramDouble % 3600.0D / 60.0D);
      str = String.format(Locale.ROOT, "%d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
    } 
    return str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */