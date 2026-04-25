package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Lssrad;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatCpu;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Quartet;

@ThreadSafe
final class AixCentralProcessor extends AbstractCentralProcessor {
  private final Supplier<Perfstat.perfstat_cpu_total_t> cpuTotal = Memoizer.memoize(PerfstatCpu::queryCpuTotal, Memoizer.defaultExpiration());
  
  private final Supplier<Perfstat.perfstat_cpu_t[]> cpuProc = Memoizer.memoize(PerfstatCpu::queryCpu, Memoizer.defaultExpiration());
  
  private static final int SBITS = querySbits();
  
  private Perfstat.perfstat_partition_config_t config;
  
  private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    String str1 = "unknown";
    String str2 = "";
    String str3 = "";
    boolean bool = false;
    String str4 = "Processor Type:";
    String str5 = "Processor Version:";
    String str6 = "CPU Type:";
    for (String str : ExecutingCommand.runNative("prtconf")) {
      if (str.startsWith("Processor Type:")) {
        str2 = str.split("Processor Type:")[1].trim();
        if (str2.startsWith("P")) {
          str1 = "IBM";
          continue;
        } 
        if (str2.startsWith("I"))
          str1 = "Intel"; 
        continue;
      } 
      if (str.startsWith("Processor Version:")) {
        str3 = str.split("Processor Version:")[1].trim();
        continue;
      } 
      if (str.startsWith("CPU Type:"))
        bool = str.split("CPU Type:")[1].contains("64"); 
    } 
    String str7 = "";
    String str8 = "";
    String str9 = Native.toString(this.config.machineID);
    if (str9.isEmpty())
      str9 = ExecutingCommand.getFirstAnswer("uname -m"); 
    if (str9.length() > 10) {
      int i = str9.length() - 4;
      int j = str9.length() - 2;
      str7 = str9.substring(i, j);
      str8 = str9.substring(j);
    } 
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str7, str8, str9, bool, (long)(this.config.processorMHz * 1000000.0D));
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    this.config = PerfstatConfig.queryConfig();
    int i = (int)this.config.numProcessors.max;
    if (i < 1)
      i = 1; 
    int j = (int)this.config.vcpus.max;
    if (j < 1)
      j = 1; 
    if (i > j)
      i = j; 
    int k = j / i;
    Map map = Lssrad.queryNodesPackages();
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    for (byte b = 0; b < j; b++) {
      Pair pair = (Pair)map.get(Integer.valueOf(b));
      int m = b / k;
      arrayList.add(new CentralProcessor.LogicalProcessor(b, m, (pair == null) ? 0 : ((Integer)pair.getB()).intValue(), (pair == null) ? 0 : ((Integer)pair.getA()).intValue()));
    } 
    return new Quartet(arrayList, null, getCachesForModel(i), Collections.emptyList());
  }
  
  private List<CentralProcessor.ProcessorCache> getCachesForModel(int paramInt) {
    ArrayList<CentralProcessor.ProcessorCache> arrayList = new ArrayList();
    int i = ParseUtil.getFirstIntValue(ExecutingCommand.getFirstAnswer("uname -n"));
    switch (i) {
      case 7:
        arrayList.add(new CentralProcessor.ProcessorCache(3, 8, 128, 67108864L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(2, 8, 128, 262144L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 8, 128, 32768L, CentralProcessor.ProcessorCache.Type.DATA));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 4, 128, 32768L, CentralProcessor.ProcessorCache.Type.INSTRUCTION));
        break;
      case 8:
        arrayList.add(new CentralProcessor.ProcessorCache(4, 8, 128, 268435456L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(3, 8, 128, 41943040L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(2, 8, 128, 524288L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 8, 128, 65536L, CentralProcessor.ProcessorCache.Type.DATA));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 8, 128, 32768L, CentralProcessor.ProcessorCache.Type.INSTRUCTION));
        break;
      case 9:
        arrayList.add(new CentralProcessor.ProcessorCache(3, 20, 128, (paramInt * 10 << 20), CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(2, 8, 128, 524288L, CentralProcessor.ProcessorCache.Type.UNIFIED));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 8, 128, 32768L, CentralProcessor.ProcessorCache.Type.DATA));
        arrayList.add(new CentralProcessor.ProcessorCache(1, 8, 128, 32768L, CentralProcessor.ProcessorCache.Type.INSTRUCTION));
        break;
    } 
    return arrayList;
  }
  
  public long[] querySystemCpuLoadTicks() {
    Perfstat.perfstat_cpu_total_t perfstat_cpu_total_t = this.cpuTotal.get();
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    arrayOfLong[CentralProcessor.TickType.USER.ordinal()] = perfstat_cpu_total_t.user * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.SYSTEM.ordinal()] = perfstat_cpu_total_t.sys * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.IDLE.ordinal()] = perfstat_cpu_total_t.idle * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.IOWAIT.ordinal()] = perfstat_cpu_total_t.wait * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.IRQ.ordinal()] = perfstat_cpu_total_t.devintrs * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.SOFTIRQ.ordinal()] = perfstat_cpu_total_t.softintrs * 1000L / USER_HZ;
    arrayOfLong[CentralProcessor.TickType.STEAL.ordinal()] = (perfstat_cpu_total_t.idle_stolen_purr + perfstat_cpu_total_t.busy_stolen_purr) * 1000L / USER_HZ;
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    long[] arrayOfLong = new long[getLogicalProcessorCount()];
    Arrays.fill(arrayOfLong, -1L);
    String str = "runs at";
    byte b = 0;
    for (String str1 : ExecutingCommand.runNative("pmcycles -m")) {
      if (str1.contains(str)) {
        arrayOfLong[b++] = ParseUtil.parseHertz(str1.split(str)[1].trim());
        if (b >= arrayOfLong.length)
          break; 
      } 
    } 
    return arrayOfLong;
  }
  
  protected long queryMaxFreq() {
    Perfstat.perfstat_cpu_total_t perfstat_cpu_total_t = this.cpuTotal.get();
    return perfstat_cpu_total_t.processorHZ;
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    long[] arrayOfLong = ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).loadavg;
    for (byte b = 0; b < paramInt; b++)
      arrayOfDouble[b] = arrayOfLong[b] / (1L << SBITS); 
    return arrayOfDouble;
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    Perfstat.perfstat_cpu_t[] arrayOfPerfstat_cpu_t = this.cpuProc.get();
    long[][] arrayOfLong = new long[arrayOfPerfstat_cpu_t.length][(CentralProcessor.TickType.values()).length];
    for (byte b = 0; b < arrayOfPerfstat_cpu_t.length; b++) {
      arrayOfLong[b] = new long[(CentralProcessor.TickType.values()).length];
      arrayOfLong[b][CentralProcessor.TickType.USER.ordinal()] = (arrayOfPerfstat_cpu_t[b]).user * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.SYSTEM.ordinal()] = (arrayOfPerfstat_cpu_t[b]).sys * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.IDLE.ordinal()] = (arrayOfPerfstat_cpu_t[b]).idle * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.IOWAIT.ordinal()] = (arrayOfPerfstat_cpu_t[b]).wait * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.IRQ.ordinal()] = (arrayOfPerfstat_cpu_t[b]).devintrs * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.SOFTIRQ.ordinal()] = (arrayOfPerfstat_cpu_t[b]).softintrs * 1000L / USER_HZ;
      arrayOfLong[b][CentralProcessor.TickType.STEAL.ordinal()] = ((arrayOfPerfstat_cpu_t[b]).idle_stolen_purr + (arrayOfPerfstat_cpu_t[b]).busy_stolen_purr) * 1000L / USER_HZ;
    } 
    return arrayOfLong;
  }
  
  public long queryContextSwitches() {
    return ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).pswitch;
  }
  
  public long queryInterrupts() {
    Perfstat.perfstat_cpu_total_t perfstat_cpu_total_t = this.cpuTotal.get();
    return perfstat_cpu_total_t.devintrs + perfstat_cpu_total_t.softintrs;
  }
  
  private static int querySbits() {
    for (String str : FileUtil.readFile("/usr/include/sys/proc.h")) {
      if (str.contains("SBITS") && str.contains("#define"))
        return ParseUtil.parseLastInt(str, 16); 
    } 
    return 16;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */