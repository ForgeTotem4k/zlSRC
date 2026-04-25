package pro.gravit.launchermodules.sentryl.utils;

import io.sentry.Scope;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;

public class OshiUtils {
  private static final SystemInfo systemInfo = new SystemInfo();
  
  private static final OSProcess process = systemInfo.getOperatingSystem().getCurrentProcess();
  
  private static final HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
  
  private static final GlobalMemory globalMemory = hardwareAbstractionLayer.getMemory();
  
  private static final Runtime runtime = Runtime.getRuntime();
  
  public static void systemProperties(Scope paramScope) {
    try {
      String str = String.valueOf(systemInfo.getOperatingSystem().getBitness());
      CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
      HashMap<Object, Object> hashMap = new HashMap<>();
      hashMap.put("Bits", str);
      hashMap.put("CPU(s) Logical", String.valueOf(centralProcessor.getLogicalProcessorCount()));
      hashMap.put("CPU(s) Physical", String.valueOf(centralProcessor.getPhysicalProcessorCount()));
      hashMap.put("CPU(s) Max Freq", formatGHz(centralProcessor.getMaxFreq()));
      hashMap.put("Total memory", humanReadableByteCountBin(globalMemory.getTotal()));
      paramScope.setContexts("Computer System Properties", hashMap);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static Map<String, String> makeMemoryProperties() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("Global Free memory", humanReadableByteCountBin(globalMemory.getAvailable()));
    hashMap.put("Process Resident memory", humanReadableByteCountBin(process.getResidentSetSize()));
    hashMap.put("Process Virtual memory", humanReadableByteCountBin(process.getVirtualSize()));
    hashMap.put("Java Max memory", humanReadableByteCountBin(runtime.maxMemory()));
    hashMap.put("Java Free memory", humanReadableByteCountBin(runtime.freeMemory()));
    hashMap.put("Java Total memory", humanReadableByteCountBin(runtime.totalMemory()));
    return (Map)hashMap;
  }
  
  public static String humanReadableByteCountBin(long paramLong) {
    long l1 = (paramLong == Long.MIN_VALUE) ? Long.MAX_VALUE : Math.abs(paramLong);
    if (l1 < 1024L)
      return "" + paramLong + " B"; 
    long l2 = l1;
    StringCharacterIterator stringCharacterIterator = new StringCharacterIterator("KMGTPE");
    for (byte b = 40; b >= 0 && l1 > 1152865209611504844L >> b; b -= 10) {
      l2 >>= 10L;
      stringCharacterIterator.next();
    } 
    l2 *= Long.signum(paramLong);
    return String.format("%.1f %ciB", new Object[] { Double.valueOf(l2 / 1024.0D), Character.valueOf(stringCharacterIterator.current()) });
  }
  
  public static String formatGHz(long paramLong) {
    return String.format("%.2fGHz", new Object[] { Double.valueOf(paramLong / 1024.0D / 1024.0D / 1024.0D) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentry\\utils\OshiUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */