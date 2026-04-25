package oshi.driver.linux.proc;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class CpuStat {
  public static long[] getSystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    List<String> list = FileUtil.readLines(ProcPath.STAT, 1);
    if (list.isEmpty())
      return arrayOfLong; 
    String str = list.get(0);
    String[] arrayOfString = ParseUtil.whitespaces.split(str);
    if (arrayOfString.length <= CentralProcessor.TickType.IDLE.getIndex())
      return arrayOfLong; 
    for (byte b = 0; b < (CentralProcessor.TickType.values()).length; b++)
      arrayOfLong[b] = ParseUtil.parseLongOrDefault(arrayOfString[b + 1], 0L); 
    return arrayOfLong;
  }
  
  public static long[][] getProcessorCpuLoadTicks(int paramInt) {
    long[][] arrayOfLong = new long[paramInt][(CentralProcessor.TickType.values()).length];
    byte b = 0;
    List list = FileUtil.readFile(ProcPath.STAT);
    for (String str : list) {
      if (str.startsWith("cpu") && !str.startsWith("cpu ")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        if (arrayOfString.length <= CentralProcessor.TickType.IDLE.getIndex())
          return arrayOfLong; 
        for (byte b1 = 0; b1 < (CentralProcessor.TickType.values()).length; b1++)
          arrayOfLong[b][b1] = ParseUtil.parseLongOrDefault(arrayOfString[b1 + 1], 0L); 
        if (++b >= paramInt)
          break; 
      } 
    } 
    return arrayOfLong;
  }
  
  public static long getContextSwitches() {
    List list = FileUtil.readFile(ProcPath.STAT);
    for (String str : list) {
      if (str.startsWith("ctxt ")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        if (arrayOfString.length == 2)
          return ParseUtil.parseLongOrDefault(arrayOfString[1], 0L); 
      } 
    } 
    return 0L;
  }
  
  public static long getInterrupts() {
    List list = FileUtil.readFile(ProcPath.STAT);
    for (String str : list) {
      if (str.startsWith("intr ")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        if (arrayOfString.length > 2)
          return ParseUtil.parseLongOrDefault(arrayOfString[1], 0L); 
      } 
    } 
    return 0L;
  }
  
  public static long getBootTime() {
    List list = FileUtil.readFile(ProcPath.STAT);
    for (String str : list) {
      if (str.startsWith("btime")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        return ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
      } 
    } 
    return 0L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\CpuStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */