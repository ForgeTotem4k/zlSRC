package oshi.hardware.platform.linux;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.SysPath;

@ThreadSafe
final class LinuxSensors extends AbstractSensors {
  public static final String OSHI_THERMAL_ZONE_TYPE_PRIORITY = "oshi.os.linux.sensors.cpuTemperature.types";
  
  private static final List<String> THERMAL_ZONE_TYPE_PRIORITY;
  
  private static final String TYPE = "type";
  
  private static final String TEMP = "temp";
  
  private static final String FAN = "fan";
  
  private static final String VOLTAGE = "in";
  
  private static final String[] SENSORS = new String[] { "temp", "fan", "in" };
  
  private static final String HWMON = "hwmon";
  
  private static final String HWMON_PATH = SysPath.HWMON + "hwmon";
  
  private static final String THERMAL_ZONE = "thermal_zone";
  
  private static final String THERMAL_ZONE_PATH = SysPath.THERMAL + "thermal_zone";
  
  private static final boolean IS_PI = (queryCpuTemperatureFromVcGenCmd() > 0.0D);
  
  private final Map<String, String> sensorsMap = new HashMap<>();
  
  LinuxSensors() {
    if (!IS_PI) {
      populateSensorsMapFromHwmon();
      if (!this.sensorsMap.containsKey("temp"))
        populateSensorsMapFromThermalZone(); 
    } 
  }
  
  private void populateSensorsMapFromHwmon() {
    for (String str1 : SENSORS) {
      String str2 = str1;
      getSensorFilesFromPath(HWMON_PATH, str1, paramFile -> {
            try {
              return (paramFile.getName().startsWith(paramString) && paramFile.getName().endsWith("_input") && FileUtil.getIntFromFile(paramFile.getCanonicalPath()) > 0);
            } catch (IOException iOException) {
              return false;
            } 
          });
    } 
  }
  
  private void populateSensorsMapFromThermalZone() {
    getSensorFilesFromPath(THERMAL_ZONE_PATH, "temp", paramFile -> (paramFile.getName().equals("type") || paramFile.getName().equals("temp")), paramArrayOfFile -> {
          Objects.requireNonNull(THERMAL_ZONE_TYPE_PRIORITY);
          return ((Integer)Stream.<File>of(paramArrayOfFile).filter(()).findFirst().map(File::getPath).map(FileUtil::getStringFromFile).map(THERMAL_ZONE_TYPE_PRIORITY::indexOf).filter(()).orElse(Integer.valueOf(THERMAL_ZONE_TYPE_PRIORITY.size()))).intValue();
        });
  }
  
  private void getSensorFilesFromPath(String paramString1, String paramString2, FileFilter paramFileFilter) {
    getSensorFilesFromPath(paramString1, paramString2, paramFileFilter, paramArrayOfFile -> 0);
  }
  
  private void getSensorFilesFromPath(String paramString1, String paramString2, FileFilter paramFileFilter, ToIntFunction<File[]> paramToIntFunction) {
    String str = null;
    int i = Integer.MAX_VALUE;
    for (byte b = 0; Paths.get(paramString1 + b, new String[0]).toFile().isDirectory(); b++) {
      String str1 = paramString1 + b;
      File file = new File(str1);
      File[] arrayOfFile = file.listFiles(paramFileFilter);
      if (arrayOfFile != null && arrayOfFile.length > 0) {
        int j = paramToIntFunction.applyAsInt(arrayOfFile);
        if (j < i) {
          i = j;
          str = str1;
        } 
      } 
    } 
    if (str != null)
      this.sensorsMap.put(paramString2, String.format(Locale.ROOT, "%s/%s", new Object[] { str, paramString2 })); 
  }
  
  public double queryCpuTemperature() {
    if (IS_PI)
      return queryCpuTemperatureFromVcGenCmd(); 
    String str = this.sensorsMap.get("temp");
    if (str != null) {
      long l = 0L;
      if (str.contains("hwmon")) {
        l = FileUtil.getLongFromFile(String.format(Locale.ROOT, "%s1_input", new Object[] { str }));
        if (l > 0L)
          return l / 1000.0D; 
        long l1 = 0L;
        byte b1 = 0;
        for (byte b2 = 2; b2 <= 6; b2++) {
          l = FileUtil.getLongFromFile(String.format(Locale.ROOT, "%s%d_input", new Object[] { str, Integer.valueOf(b2) }));
          if (l > 0L) {
            l1 += l;
            b1++;
          } 
        } 
        if (b1 > 0)
          return l1 / b1 * 1000.0D; 
      } else if (str.contains("thermal_zone")) {
        l = FileUtil.getLongFromFile(str);
        if (l > 0L)
          return l / 1000.0D; 
      } 
    } 
    return 0.0D;
  }
  
  private static double queryCpuTemperatureFromVcGenCmd() {
    String str = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
    return str.startsWith("temp=") ? ParseUtil.parseDoubleOrDefault(str.replaceAll("[^\\d|\\.]+", ""), 0.0D) : 0.0D;
  }
  
  public int[] queryFanSpeeds() {
    if (!IS_PI) {
      String str = this.sensorsMap.get("fan");
      if (str != null) {
        ArrayList<Integer> arrayList = new ArrayList();
        byte b1 = 1;
        String str1 = String.format(Locale.ROOT, "%s%d_input", new Object[] { str, Integer.valueOf(b1) });
        while ((new File(str1)).exists()) {
          arrayList.add(Integer.valueOf(FileUtil.getIntFromFile(str1)));
          b1++;
        } 
        int[] arrayOfInt = new int[arrayList.size()];
        for (byte b2 = 0; b2 < arrayList.size(); b2++)
          arrayOfInt[b2] = ((Integer)arrayList.get(b2)).intValue(); 
        return arrayOfInt;
      } 
    } 
    return new int[0];
  }
  
  public double queryCpuVoltage() {
    if (IS_PI)
      return queryCpuVoltageFromVcGenCmd(); 
    String str = this.sensorsMap.get("in");
    return (str != null) ? (FileUtil.getIntFromFile(String.format(Locale.ROOT, "%s1_input", new Object[] { str })) / 1000.0D) : 0.0D;
  }
  
  private static double queryCpuVoltageFromVcGenCmd() {
    String str = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
    return str.startsWith("volt=") ? ParseUtil.parseDoubleOrDefault(str.replaceAll("[^\\d|\\.]+", ""), 0.0D) : 0.0D;
  }
  
  static {
    THERMAL_ZONE_TYPE_PRIORITY = (List<String>)Stream.<String>of(GlobalConfig.get("oshi.os.linux.sensors.cpuTemperature.types", "").split(",")).filter(paramString -> !paramString.isEmpty()).collect(Collectors.toList());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */