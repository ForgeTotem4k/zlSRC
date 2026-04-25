package pro.gravit.launcher.runtime.console.test;

import pro.gravit.launcher.runtime.utils.HWIDProvider;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class PrintHardwareInfoCommand extends Command {
  public String getArgsDescription() {
    return "[]";
  }
  
  public String getUsageDescription() {
    return "print your hardware info and timings";
  }
  
  public void invoke(String... paramVarArgs) {
    LogHelper.info("Your Hardware ID:");
    long l1 = System.currentTimeMillis();
    HWIDProvider hWIDProvider = new HWIDProvider();
    long l2 = System.currentTimeMillis();
    LogHelper.info("Create HWIDProvider instance: %d ms", new Object[] { Long.valueOf(l2 - l1) });
    l1 = System.currentTimeMillis();
    int i = hWIDProvider.getBitness();
    long l3 = hWIDProvider.getTotalMemory();
    boolean bool = hWIDProvider.isBattery();
    l2 = System.currentTimeMillis();
    LogHelper.info("Bitness: %d, totalMemory: %d(%.3f GB), battery %s, TIME: %d ms", new Object[] { Integer.valueOf(i), Long.valueOf(l3), Double.valueOf(l3 / 1.073741824E9D), Boolean.toString(bool), Long.valueOf(l2 - l1) });
    l1 = System.currentTimeMillis();
    int j = hWIDProvider.getProcessorLogicalCount();
    int k = hWIDProvider.getProcessorPhysicalCount();
    long l4 = hWIDProvider.getProcessorMaxFreq();
    l2 = System.currentTimeMillis();
    LogHelper.info("Processors || logical: %d physical %d freq %d, TIME: %d ms", new Object[] { Integer.valueOf(j), Integer.valueOf(k), Long.valueOf(l4), Long.valueOf(l2 - l1) });
    l1 = System.currentTimeMillis();
    String str1 = hWIDProvider.getHWDiskID();
    l2 = System.currentTimeMillis();
    LogHelper.info("HWDiskID %s, TIME: %d ms", new Object[] { str1, Long.valueOf(l2 - l1) });
    l1 = System.currentTimeMillis();
    String str2 = hWIDProvider.getBaseboardSerialNumber();
    l2 = System.currentTimeMillis();
    LogHelper.info("BaseboardSerial %s, TIME: %d ms", new Object[] { str2, Long.valueOf(l2 - l1) });
    l1 = System.currentTimeMillis();
    String str3 = hWIDProvider.getGraphicCardName();
    long l5 = hWIDProvider.getGraphicCardMemory();
    l2 = System.currentTimeMillis();
    LogHelper.info("GraphicCard %s (%.3f vram), TIME: %d ms", new Object[] { str3, Double.valueOf(l5), Long.valueOf(l2 - l1) });
    LogHelper.info("Hardware ID end");
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\test\PrintHardwareInfoCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */