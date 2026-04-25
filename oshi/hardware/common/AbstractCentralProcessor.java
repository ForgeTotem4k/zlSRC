package oshi.hardware.common;

import com.sun.jna.Platform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.Auxv;
import oshi.hardware.CentralProcessor;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Quartet;

@ThreadSafe
public abstract class AbstractCentralProcessor implements CentralProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractCentralProcessor.class);
  
  private final Supplier<CentralProcessor.ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
  
  private final Supplier<Long> maxFreq = Memoizer.memoize(this::queryMaxFreq, Memoizer.defaultExpiration());
  
  private final Supplier<long[]> currentFreq = Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration() / 2L);
  
  private final Supplier<Long> contextSwitches = Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
  
  private final Supplier<Long> interrupts = Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());
  
  private final Supplier<long[]> systemCpuLoadTicks = Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
  
  private final Supplier<long[][]> processorCpuLoadTicks = Memoizer.memoize(this::queryProcessorCpuLoadTicks, Memoizer.defaultExpiration());
  
  private final int physicalPackageCount;
  
  private final int physicalProcessorCount;
  
  private final int logicalProcessorCount;
  
  private final List<CentralProcessor.LogicalProcessor> logicalProcessors;
  
  private final List<CentralProcessor.PhysicalProcessor> physicalProcessors;
  
  private final List<CentralProcessor.ProcessorCache> processorCaches;
  
  private final List<String> featureFlags;
  
  protected AbstractCentralProcessor() {
    Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> quartet = initProcessorCounts();
    this.logicalProcessors = Collections.unmodifiableList((List<? extends CentralProcessor.LogicalProcessor>)quartet.getA());
    if (quartet.getB() == null) {
      Set set = (Set)this.logicalProcessors.stream().map(paramLogicalProcessor -> Integer.valueOf((paramLogicalProcessor.getPhysicalPackageNumber() << 16) + paramLogicalProcessor.getPhysicalProcessorNumber())).collect(Collectors.toSet());
      List<? extends CentralProcessor.PhysicalProcessor> list = (List)set.stream().sorted().map(paramInteger -> new CentralProcessor.PhysicalProcessor(paramInteger.intValue() >> 16, paramInteger.intValue() & 0xFFFF)).collect(Collectors.toList());
      this.physicalProcessors = Collections.unmodifiableList(list);
    } else {
      this.physicalProcessors = Collections.unmodifiableList((List<? extends CentralProcessor.PhysicalProcessor>)quartet.getB());
    } 
    this.processorCaches = (quartet.getC() == null) ? Collections.<CentralProcessor.ProcessorCache>emptyList() : Collections.<CentralProcessor.ProcessorCache>unmodifiableList((List<? extends CentralProcessor.ProcessorCache>)quartet.getC());
    HashSet<Integer> hashSet = new HashSet();
    for (CentralProcessor.LogicalProcessor logicalProcessor : this.logicalProcessors) {
      int i = logicalProcessor.getPhysicalPackageNumber();
      hashSet.add(Integer.valueOf(i));
    } 
    this.logicalProcessorCount = this.logicalProcessors.size();
    this.physicalProcessorCount = this.physicalProcessors.size();
    this.physicalPackageCount = hashSet.size();
    this.featureFlags = Collections.unmodifiableList((List<? extends String>)quartet.getD());
  }
  
  protected abstract Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts();
  
  protected abstract CentralProcessor.ProcessorIdentifier queryProcessorId();
  
  public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
    return this.cpuid.get();
  }
  
  public long getMaxFreq() {
    return ((Long)this.maxFreq.get()).longValue();
  }
  
  protected long queryMaxFreq() {
    return Arrays.stream(getCurrentFreq()).max().orElse(-1L);
  }
  
  public long[] getCurrentFreq() {
    long[] arrayOfLong1 = this.currentFreq.get();
    if (arrayOfLong1.length == getLogicalProcessorCount())
      return arrayOfLong1; 
    long[] arrayOfLong2 = new long[getLogicalProcessorCount()];
    Arrays.fill(arrayOfLong2, arrayOfLong1[0]);
    return arrayOfLong2;
  }
  
  protected abstract long[] queryCurrentFreq();
  
  public long getContextSwitches() {
    return ((Long)this.contextSwitches.get()).longValue();
  }
  
  protected abstract long queryContextSwitches();
  
  public long getInterrupts() {
    return ((Long)this.interrupts.get()).longValue();
  }
  
  protected abstract long queryInterrupts();
  
  public List<CentralProcessor.LogicalProcessor> getLogicalProcessors() {
    return this.logicalProcessors;
  }
  
  public List<CentralProcessor.PhysicalProcessor> getPhysicalProcessors() {
    return this.physicalProcessors;
  }
  
  public List<CentralProcessor.ProcessorCache> getProcessorCaches() {
    return this.processorCaches;
  }
  
  public List<String> getFeatureFlags() {
    return this.featureFlags;
  }
  
  public long[] getSystemCpuLoadTicks() {
    return this.systemCpuLoadTicks.get();
  }
  
  protected abstract long[] querySystemCpuLoadTicks();
  
  public long[][] getProcessorCpuLoadTicks() {
    return this.processorCpuLoadTicks.get();
  }
  
  protected abstract long[][] queryProcessorCpuLoadTicks();
  
  public double getSystemCpuLoadBetweenTicks(long[] paramArrayOflong) {
    if (paramArrayOflong.length != (CentralProcessor.TickType.values()).length)
      throw new IllegalArgumentException("Provited tick array length " + paramArrayOflong.length + " should have " + (CentralProcessor.TickType.values()).length + " elements"); 
    long[] arrayOfLong = getSystemCpuLoadTicks();
    long l1 = 0L;
    for (byte b = 0; b < arrayOfLong.length; b++)
      l1 += arrayOfLong[b] - paramArrayOflong[b]; 
    long l2 = arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()] + arrayOfLong[CentralProcessor.TickType.IOWAIT.getIndex()] - paramArrayOflong[CentralProcessor.TickType.IDLE.getIndex()] - paramArrayOflong[CentralProcessor.TickType.IOWAIT.getIndex()];
    LOG.trace("Total ticks: {}  Idle ticks: {}", Long.valueOf(l1), Long.valueOf(l2));
    return (l1 > 0L) ? ((l1 - l2) / l1) : 0.0D;
  }
  
  public double[] getProcessorCpuLoadBetweenTicks(long[][] paramArrayOflong) {
    long[][] arrayOfLong = getProcessorCpuLoadTicks();
    if (paramArrayOflong.length != arrayOfLong.length || (paramArrayOflong[0]).length != (CentralProcessor.TickType.values()).length)
      throw new IllegalArgumentException("Provided tick array length " + paramArrayOflong.length + " should be " + arrayOfLong.length + ", each subarray having " + (CentralProcessor.TickType.values()).length + " elements"); 
    double[] arrayOfDouble = new double[arrayOfLong.length];
    for (byte b = 0; b < arrayOfLong.length; b++) {
      long l1 = 0L;
      for (byte b1 = 0; b1 < (arrayOfLong[b]).length; b1++)
        l1 += arrayOfLong[b][b1] - paramArrayOflong[b][b1]; 
      long l2 = arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] + arrayOfLong[b][CentralProcessor.TickType.IOWAIT.getIndex()] - paramArrayOflong[b][CentralProcessor.TickType.IDLE.getIndex()] - paramArrayOflong[b][CentralProcessor.TickType.IOWAIT.getIndex()];
      LOG.trace("CPU: {}  Total ticks: {}  Idle ticks: {}", new Object[] { Integer.valueOf(b), Long.valueOf(l1), Long.valueOf(l2) });
      arrayOfDouble[b] = (l1 > 0L && l2 >= 0L) ? ((l1 - l2) / l1) : 0.0D;
    } 
    return arrayOfDouble;
  }
  
  public int getLogicalProcessorCount() {
    return this.logicalProcessorCount;
  }
  
  public int getPhysicalProcessorCount() {
    return this.physicalProcessorCount;
  }
  
  public int getPhysicalPackageCount() {
    return this.physicalPackageCount;
  }
  
  protected static String createProcessorID(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    long l1 = 0L;
    long l2 = ParseUtil.parseLongOrDefault(paramString1, 0L);
    long l3 = ParseUtil.parseLongOrDefault(paramString2, 0L);
    long l4 = ParseUtil.parseLongOrDefault(paramString3, 0L);
    l1 |= l2 & 0xFL;
    l1 |= (l3 & 0xFL) << 4L;
    l1 |= (l3 & 0xF0L) << 12L;
    l1 |= (l4 & 0xFL) << 8L;
    l1 |= (l4 & 0xFF0L) << 16L;
    long l5 = 0L;
    if (Platform.isLinux())
      l5 = ((Long)Auxv.queryAuxv().getOrDefault(Integer.valueOf(16), Long.valueOf(0L))).longValue(); 
    if (l5 > 0L) {
      l1 |= l5 << 32L;
    } else {
      for (String str : paramArrayOfString) {
        switch (str) {
          case "fpu":
            l1 |= 0x100000000L;
            break;
          case "vme":
            l1 |= 0x200000000L;
            break;
          case "de":
            l1 |= 0x400000000L;
            break;
          case "pse":
            l1 |= 0x800000000L;
            break;
          case "tsc":
            l1 |= 0x1000000000L;
            break;
          case "msr":
            l1 |= 0x2000000000L;
            break;
          case "pae":
            l1 |= 0x4000000000L;
            break;
          case "mce":
            l1 |= 0x8000000000L;
            break;
          case "cx8":
            l1 |= 0x10000000000L;
            break;
          case "apic":
            l1 |= 0x20000000000L;
            break;
          case "sep":
            l1 |= 0x80000000000L;
            break;
          case "mtrr":
            l1 |= 0x100000000000L;
            break;
          case "pge":
            l1 |= 0x200000000000L;
            break;
          case "mca":
            l1 |= 0x400000000000L;
            break;
          case "cmov":
            l1 |= 0x800000000000L;
            break;
          case "pat":
            l1 |= 0x1000000000000L;
            break;
          case "pse-36":
            l1 |= 0x2000000000000L;
            break;
          case "psn":
            l1 |= 0x4000000000000L;
            break;
          case "clfsh":
            l1 |= 0x8000000000000L;
            break;
          case "ds":
            l1 |= 0x20000000000000L;
            break;
          case "acpi":
            l1 |= 0x40000000000000L;
            break;
          case "mmx":
            l1 |= 0x80000000000000L;
            break;
          case "fxsr":
            l1 |= 0x100000000000000L;
            break;
          case "sse":
            l1 |= 0x200000000000000L;
            break;
          case "sse2":
            l1 |= 0x400000000000000L;
            break;
          case "ss":
            l1 |= 0x800000000000000L;
            break;
          case "htt":
            l1 |= 0x1000000000000000L;
            break;
          case "tm":
            l1 |= 0x2000000000000000L;
            break;
          case "ia64":
            l1 |= 0x4000000000000000L;
            break;
          case "pbe":
            l1 |= Long.MIN_VALUE;
            break;
        } 
      } 
    } 
    return String.format(Locale.ROOT, "%016X", new Object[] { Long.valueOf(l1) });
  }
  
  protected List<CentralProcessor.PhysicalProcessor> createProcListFromDmesg(List<CentralProcessor.LogicalProcessor> paramList, Map<Integer, String> paramMap) {
    boolean bool = (paramMap.values().stream().distinct().count() > 1L) ? true : false;
    ArrayList<CentralProcessor.PhysicalProcessor> arrayList = new ArrayList();
    HashSet<Integer> hashSet = new HashSet();
    for (CentralProcessor.LogicalProcessor logicalProcessor : paramList) {
      int i = logicalProcessor.getPhysicalPackageNumber();
      int j = logicalProcessor.getPhysicalProcessorNumber();
      int k = (i << 16) + j;
      if (!hashSet.contains(Integer.valueOf(k))) {
        hashSet.add(Integer.valueOf(k));
        String str = paramMap.getOrDefault(Integer.valueOf(logicalProcessor.getProcessorNumber()), "");
        boolean bool1 = false;
        if (bool && ((str.startsWith("ARM Cortex") && ParseUtil.getFirstIntValue(str) >= 70) || (str.startsWith("Apple") && (str.contains("Firestorm") || str.contains("Avalanche")))))
          bool1 = true; 
        arrayList.add(new CentralProcessor.PhysicalProcessor(i, j, bool1, str));
      } 
    } 
    arrayList.sort(Comparator.<CentralProcessor.PhysicalProcessor>comparingInt(CentralProcessor.PhysicalProcessor::getPhysicalPackageNumber).thenComparingInt(CentralProcessor.PhysicalProcessor::getPhysicalProcessorNumber));
    return arrayList;
  }
  
  public static List<CentralProcessor.ProcessorCache> orderedProcCaches(Set<CentralProcessor.ProcessorCache> paramSet) {
    return (List<CentralProcessor.ProcessorCache>)paramSet.stream().sorted(Comparator.comparing(paramProcessorCache -> Integer.valueOf(-1000 * paramProcessorCache.getLevel() + 100 * paramProcessorCache.getType().ordinal() - Integer.highestOneBit(paramProcessorCache.getCacheSize())))).collect(Collectors.toList());
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getProcessorIdentifier().getName());
    stringBuilder.append("\n ").append(getPhysicalPackageCount()).append(" physical CPU package(s)");
    stringBuilder.append("\n ").append(getPhysicalProcessorCount()).append(" physical CPU core(s)");
    HashMap<Object, Object> hashMap = new HashMap<>();
    int i = 0;
    for (CentralProcessor.PhysicalProcessor physicalProcessor : getPhysicalProcessors()) {
      int m = physicalProcessor.getEfficiency();
      hashMap.merge(Integer.valueOf(m), Integer.valueOf(1), Integer::sum);
      if (m > i)
        i = m; 
    } 
    int j = ((Integer)hashMap.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue();
    int k = getPhysicalProcessorCount() - j;
    if (k > 0)
      stringBuilder.append(" (").append(j).append(" performance + ").append(k).append(" efficiency)"); 
    stringBuilder.append("\n ").append(getLogicalProcessorCount()).append(" logical CPU(s)");
    stringBuilder.append('\n').append("Identifier: ").append(getProcessorIdentifier().getIdentifier());
    stringBuilder.append('\n').append("ProcessorID: ").append(getProcessorIdentifier().getProcessorID());
    stringBuilder.append('\n').append("Microarchitecture: ").append(getProcessorIdentifier().getMicroarchitecture());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */