package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.MSAcpiThermalZoneTemperature;
import oshi.driver.windows.wmi.OhmHardware;
import oshi.driver.windows.wmi.OhmSensor;
import oshi.driver.windows.wmi.Win32Fan;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
final class WindowsSensors extends AbstractSensors {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsSensors.class);
  
  private static final String COM_EXCEPTION_MSG = "COM exception: {}";
  
  private static final String REFLECT_EXCEPTION_MSG = "Reflect exception: {}";
  
  private static final String JLIBREHARDWAREMONITOR_PACKAGE = "io.github.pandalxb.jlibrehardwaremonitor";
  
  public double queryCpuTemperature() {
    null = getTempFromOHM();
    if (null > 0.0D)
      return null; 
    null = getTempFromLHM();
    return (null > 0.0D) ? null : getTempFromWMI();
  }
  
  private static double getTempFromOHM() {
    WbemcliUtil.WmiResult<OhmSensor.ValueProperty> wmiResult = getOhmSensors("Hardware", "CPU", "Temperature", (paramWmiQueryHandler, paramWmiResult) -> {
          String str = WmiUtil.getString(paramWmiResult, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0);
          return !str.isEmpty() ? OhmSensor.querySensorValue(paramWmiQueryHandler, str, "Temperature") : null;
        });
    if (wmiResult != null && wmiResult.getResultCount() > 0) {
      double d = 0.0D;
      for (byte b = 0; b < wmiResult.getResultCount(); b++)
        d += WmiUtil.getFloat(wmiResult, (Enum)OhmSensor.ValueProperty.VALUE, b); 
      return d / wmiResult.getResultCount();
    } 
    return 0.0D;
  }
  
  private static double getTempFromLHM() {
    return getAverageValueFromLHM("CPU", "Temperature", (paramString, paramDouble) -> Boolean.valueOf((!paramString.contains("Max") && !paramString.contains("Average") && paramDouble.doubleValue() > 0.0D)));
  }
  
  private static double getTempFromWMI() {
    double d = 0.0D;
    long l = 0L;
    WbemcliUtil.WmiResult wmiResult = MSAcpiThermalZoneTemperature.queryCurrentTemperature();
    if (wmiResult.getResultCount() > 0) {
      LOG.debug("Found Temperature data in WMI");
      l = WmiUtil.getUint32asLong(wmiResult, (Enum)MSAcpiThermalZoneTemperature.TemperatureProperty.CURRENTTEMPERATURE, 0);
    } 
    if (l > 2732L) {
      d = l / 10.0D - 273.15D;
    } else if (l > 274L) {
      d = l - 273.0D;
    } 
    return Math.max(d, 0.0D);
  }
  
  public int[] queryFanSpeeds() {
    int[] arrayOfInt = getFansFromOHM();
    if (arrayOfInt.length > 0)
      return arrayOfInt; 
    arrayOfInt = getFansFromLHM();
    if (arrayOfInt.length > 0)
      return arrayOfInt; 
    arrayOfInt = getFansFromWMI();
    return (arrayOfInt.length > 0) ? arrayOfInt : new int[0];
  }
  
  private static int[] getFansFromOHM() {
    WbemcliUtil.WmiResult<OhmSensor.ValueProperty> wmiResult = getOhmSensors("Hardware", "CPU", "Fan", (paramWmiQueryHandler, paramWmiResult) -> {
          String str = WmiUtil.getString(paramWmiResult, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0);
          return !str.isEmpty() ? OhmSensor.querySensorValue(paramWmiQueryHandler, str, "Fan") : null;
        });
    if (wmiResult != null && wmiResult.getResultCount() > 0) {
      int[] arrayOfInt = new int[wmiResult.getResultCount()];
      for (byte b = 0; b < wmiResult.getResultCount(); b++)
        arrayOfInt[b] = (int)WmiUtil.getFloat(wmiResult, (Enum)OhmSensor.ValueProperty.VALUE, b); 
      return arrayOfInt;
    } 
    return new int[0];
  }
  
  private static int[] getFansFromLHM() {
    List<?> list = getLhmSensors("SuperIO", "Fan");
    if (list == null || list.isEmpty())
      return new int[0]; 
    try {
      Class<?> clazz = Class.forName("io.github.pandalxb.jlibrehardwaremonitor.model.Sensor");
      Method method = clazz.getMethod("getValue", new Class[0]);
      return list.stream().filter(paramObject -> {
            try {
              double d = ((Double)paramMethod.invoke(paramObject, new Object[0])).doubleValue();
              return (d > 0.0D);
            } catch (Exception exception) {
              LOG.warn("Reflect exception: {}", exception.getMessage());
              return false;
            } 
          }).mapToInt(paramObject -> {
            try {
              return (int)((Double)paramMethod.invoke(paramObject, new Object[0])).doubleValue();
            } catch (Exception exception) {
              LOG.warn("Reflect exception: {}", exception.getMessage());
              return 0;
            } 
          }).toArray();
    } catch (Exception exception) {
      LOG.warn("Reflect exception: {}", exception.getMessage());
      return new int[0];
    } 
  }
  
  private static int[] getFansFromWMI() {
    WbemcliUtil.WmiResult wmiResult = Win32Fan.querySpeed();
    if (wmiResult.getResultCount() > 1) {
      LOG.debug("Found Fan data in WMI");
      int[] arrayOfInt = new int[wmiResult.getResultCount()];
      for (byte b = 0; b < wmiResult.getResultCount(); b++)
        arrayOfInt[b] = (int)WmiUtil.getUint64(wmiResult, (Enum)Win32Fan.SpeedProperty.DESIREDSPEED, b); 
      return arrayOfInt;
    } 
    return new int[0];
  }
  
  public double queryCpuVoltage() {
    null = getVoltsFromOHM();
    if (null > 0.0D)
      return null; 
    null = getVoltsFromLHM();
    return (null > 0.0D) ? null : getVoltsFromWMI();
  }
  
  private static double getVoltsFromOHM() {
    WbemcliUtil.WmiResult<OhmSensor.ValueProperty> wmiResult = getOhmSensors("Sensor", "Voltage", "Voltage", (paramWmiQueryHandler, paramWmiResult) -> {
          String str = null;
          for (byte b = 0; b < paramWmiResult.getResultCount(); b++) {
            String str1 = WmiUtil.getString(paramWmiResult, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, b);
            if (str1.toLowerCase(Locale.ROOT).contains("cpu")) {
              str = str1;
              break;
            } 
          } 
          if (str == null)
            str = WmiUtil.getString(paramWmiResult, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0); 
          return OhmSensor.querySensorValue(paramWmiQueryHandler, str, "Voltage");
        });
    return (wmiResult != null && wmiResult.getResultCount() > 0) ? WmiUtil.getFloat(wmiResult, (Enum)OhmSensor.ValueProperty.VALUE, 0) : 0.0D;
  }
  
  private static double getVoltsFromLHM() {
    return getAverageValueFromLHM("SuperIO", "Voltage", (paramString, paramDouble) -> Boolean.valueOf((paramString.toLowerCase(Locale.ROOT).contains("vcore") && paramDouble.doubleValue() > 0.0D)));
  }
  
  private static double getVoltsFromWMI() {
    WbemcliUtil.WmiResult wmiResult = Win32Processor.queryVoltage();
    if (wmiResult.getResultCount() > 1) {
      LOG.debug("Found Voltage data in WMI");
      int i = WmiUtil.getUint16(wmiResult, (Enum)Win32Processor.VoltProperty.CURRENTVOLTAGE, 0);
      if (i > 0)
        if ((i & 0x80) == 0) {
          i = WmiUtil.getUint32(wmiResult, (Enum)Win32Processor.VoltProperty.VOLTAGECAPS, 0);
          if ((i & 0x1) > 0)
            return 5.0D; 
          if ((i & 0x2) > 0)
            return 3.3D; 
          if ((i & 0x4) > 0)
            return 2.9D; 
        } else {
          return (i & 0x7F) / 10.0D;
        }  
    } 
    return 0.0D;
  }
  
  private static WbemcliUtil.WmiResult<OhmSensor.ValueProperty> getOhmSensors(String paramString1, String paramString2, String paramString3, BiFunction<WmiQueryHandler, WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty>, WbemcliUtil.WmiResult<OhmSensor.ValueProperty>> paramBiFunction) {
    WmiQueryHandler wmiQueryHandler = Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance());
    boolean bool = false;
    WbemcliUtil.WmiResult<OhmSensor.ValueProperty> wmiResult = null;
    try {
      bool = wmiQueryHandler.initCOM();
      WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> wmiResult1 = OhmHardware.queryHwIdentifier(wmiQueryHandler, paramString1, paramString2);
      if (wmiResult1.getResultCount() > 0) {
        LOG.debug("Found {} data in Open Hardware Monitor", paramString3);
        wmiResult = paramBiFunction.apply(wmiQueryHandler, wmiResult1);
      } 
    } catch (COMException cOMException) {
      LOG.warn("COM exception: {}", cOMException.getMessage());
    } finally {
      if (bool)
        wmiQueryHandler.unInitCOM(); 
    } 
    return wmiResult;
  }
  
  private static double getAverageValueFromLHM(String paramString1, String paramString2, BiFunction<String, Double, Boolean> paramBiFunction) {
    List<?> list = getLhmSensors(paramString1, paramString2);
    if (list == null || list.isEmpty())
      return 0.0D; 
    try {
      Class<?> clazz = Class.forName("io.github.pandalxb.jlibrehardwaremonitor.model.Sensor");
      Method method1 = clazz.getMethod("getName", new Class[0]);
      Method method2 = clazz.getMethod("getValue", new Class[0]);
      double d = 0.0D;
      byte b = 0;
      for (Object object : list) {
        String str = (String)method1.invoke(object, new Object[0]);
        double d1 = ((Double)method2.invoke(object, new Object[0])).doubleValue();
        if (((Boolean)paramBiFunction.apply(str, Double.valueOf(d1))).booleanValue()) {
          d += d1;
          b++;
        } 
      } 
      return (b > 0) ? (d / b) : 0.0D;
    } catch (Exception exception) {
      LOG.warn("Reflect exception: {}", exception.getMessage());
      return 0.0D;
    } 
  }
  
  private static List<?> getLhmSensors(String paramString1, String paramString2) {
    try {
      Class<?> clazz1 = Class.forName("io.github.pandalxb.jlibrehardwaremonitor.config.ComputerConfig");
      Class<?> clazz2 = Class.forName("io.github.pandalxb.jlibrehardwaremonitor.manager.LibreHardwareManager");
      Method method1 = clazz1.getMethod("getInstance", new Class[0]);
      Object object1 = method1.invoke(null, new Object[0]);
      Method method2 = clazz1.getMethod("setCpuEnabled", new Class[] { boolean.class });
      method2.invoke(object1, new Object[] { Boolean.valueOf(true) });
      method2 = clazz1.getMethod("setMotherboardEnabled", new Class[] { boolean.class });
      method2.invoke(object1, new Object[] { Boolean.valueOf(true) });
      Method method3 = clazz2.getMethod("getInstance", new Class[] { clazz1 });
      Object object2 = method3.invoke(null, new Object[] { object1 });
      Method method4 = clazz2.getMethod("querySensors", new Class[] { String.class, String.class });
      return (List)method4.invoke(object2, new Object[] { paramString1, paramString2 });
    } catch (Exception exception) {
      LOG.warn("Reflect exception: {}", exception.getMessage());
      return Collections.emptyList();
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */