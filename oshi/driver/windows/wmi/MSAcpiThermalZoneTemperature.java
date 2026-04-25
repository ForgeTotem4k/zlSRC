package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class MSAcpiThermalZoneTemperature {
  public static final String WMI_NAMESPACE = "ROOT\\WMI";
  
  private static final String MS_ACPI_THERMAL_ZONE_TEMPERATURE = "MSAcpi_ThermalZoneTemperature";
  
  public static WbemcliUtil.WmiResult<TemperatureProperty> queryCurrentTemperature() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\WMI", "MSAcpi_ThermalZoneTemperature", TemperatureProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum TemperatureProperty {
    CURRENTTEMPERATURE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\MSAcpiThermalZoneTemperature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */