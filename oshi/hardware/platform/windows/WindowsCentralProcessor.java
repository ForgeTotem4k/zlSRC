package oshi.hardware.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinReg;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.LogicalProcessorInformation;
import oshi.driver.windows.perfmon.LoadAverage;
import oshi.driver.windows.perfmon.ProcessorInformation;
import oshi.driver.windows.perfmon.SystemInformation;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.Struct;
import oshi.jna.platform.windows.Kernel32;
import oshi.jna.platform.windows.PowrProf;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Quartet;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class WindowsCentralProcessor extends AbstractCentralProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsCentralProcessor.class);
  
  private Map<String, Integer> numaNodeProcToLogicalProcMap;
  
  private static final boolean USE_LEGACY_SYSTEM_COUNTERS = GlobalConfig.get("oshi.os.windows.legacy.system.counters", false);
  
  private static final boolean USE_LOAD_AVERAGE = GlobalConfig.get("oshi.os.windows.loadaverage", false);
  
  private static final boolean USE_CPU_UTILITY = (VersionHelpers.IsWindows8OrGreater() && GlobalConfig.get("oshi.os.windows.cpu.utility", false));
  
  private final Supplier<Pair<List<String>, Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>>> processorUtilityCounters = USE_CPU_UTILITY ? Memoizer.memoize(WindowsCentralProcessor::queryProcessorUtilityCounters, TimeUnit.MILLISECONDS.toNanos(300L)) : null;
  
  private Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>> initialUtilityCounters = USE_CPU_UTILITY ? (Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>)((Pair)this.processorUtilityCounters.get()).getB() : null;
  
  private Long utilityBaseMultiplier = null;
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    String str7;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    String str6 = "";
    long l = 0L;
    boolean bool = false;
    String str8 = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\";
    String[] arrayOfString = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
    if (arrayOfString.length > 0) {
      String str = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + arrayOfString[0];
      str1 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str, "VendorIdentifier");
      str2 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str, "ProcessorNameString");
      str3 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str, "Identifier");
      try {
        l = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, str, "~MHz") * 1000000L;
      } catch (Win32Exception win32Exception) {}
    } 
    if (!str3.isEmpty()) {
      str4 = parseIdentifier(str3, "Family");
      str5 = parseIdentifier(str3, "Model");
      str6 = parseIdentifier(str3, "Stepping");
    } 
    Struct.CloseableSystemInfo closeableSystemInfo = new Struct.CloseableSystemInfo();
    try {
      Kernel32.INSTANCE.GetNativeSystemInfo((WinBase.SYSTEM_INFO)closeableSystemInfo);
      int i = closeableSystemInfo.processorArchitecture.pi.wProcessorArchitecture.intValue();
      if (i == 9 || i == 12 || i == 6)
        bool = true; 
      closeableSystemInfo.close();
    } catch (Throwable throwable) {
      try {
        closeableSystemInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    WbemcliUtil.WmiResult wmiResult = Win32Processor.queryProcessorId();
    if (wmiResult.getResultCount() > 0) {
      str7 = WmiUtil.getString(wmiResult, (Enum)Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
    } else {
      (new String[1])[0] = "ia64";
      str7 = createProcessorID(str6, str5, str4, bool ? new String[1] : new String[0]);
    } 
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str4, str5, str6, str7, bool, l);
  }
  
  private static String parseIdentifier(String paramString1, String paramString2) {
    String[] arrayOfString = ParseUtil.whitespaces.split(paramString1);
    boolean bool = false;
    for (String str : arrayOfString) {
      if (bool)
        return str; 
      bool = str.equals(paramString2);
    } 
    return "";
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    Triplet triplet;
    if (VersionHelpers.IsWindows7OrGreater()) {
      triplet = LogicalProcessorInformation.getLogicalProcessorInformationEx();
      int i = -1;
      byte b1 = 0;
      byte b2 = 0;
      this.numaNodeProcToLogicalProcMap = new HashMap<>();
      for (CentralProcessor.LogicalProcessor logicalProcessor : triplet.getA()) {
        int j = logicalProcessor.getNumaNode();
        if (j != i) {
          i = j;
          b1 = 0;
        } 
        this.numaNodeProcToLogicalProcMap.put(String.format(Locale.ROOT, "%d,%d", new Object[] { Integer.valueOf(logicalProcessor.getNumaNode()), Integer.valueOf(b1++) }), Integer.valueOf(b2++));
      } 
    } else {
      triplet = LogicalProcessorInformation.getLogicalProcessorInformation();
    } 
    List list = (List)Arrays.<Kernel32.ProcessorFeature>stream(Kernel32.ProcessorFeature.values()).filter(paramProcessorFeature -> Kernel32.INSTANCE.IsProcessorFeaturePresent(paramProcessorFeature.value())).map(Enum::name).collect(Collectors.toList());
    return new Quartet(triplet.getA(), triplet.getB(), triplet.getC(), list);
  }
  
  public long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    if (USE_LEGACY_SYSTEM_COUNTERS) {
      WinBase.FILETIME fILETIME1 = new WinBase.FILETIME();
      WinBase.FILETIME fILETIME2 = new WinBase.FILETIME();
      WinBase.FILETIME fILETIME3 = new WinBase.FILETIME();
      if (!Kernel32.INSTANCE.GetSystemTimes(fILETIME1, fILETIME2, fILETIME3)) {
        LOG.error("Failed to update system idle/kernel/user times. Error code: {}", Integer.valueOf(Native.getLastError()));
        return arrayOfLong;
      } 
      Map map = ProcessorInformation.querySystemCounters();
      arrayOfLong[CentralProcessor.TickType.IRQ.getIndex()] = ((Long)map.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTINTERRUPTTIME, (V)Long.valueOf(0L))).longValue() / 10000L;
      arrayOfLong[CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((Long)map.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTDPCTIME, (V)Long.valueOf(0L))).longValue() / 10000L;
      arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()] = fILETIME1.toDWordLong().longValue() / 10000L;
      arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] = fILETIME2.toDWordLong().longValue() / 10000L - arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()];
      arrayOfLong[CentralProcessor.TickType.USER.getIndex()] = fILETIME3.toDWordLong().longValue() / 10000L;
      arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] = arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] - arrayOfLong[CentralProcessor.TickType.IRQ.getIndex()] + arrayOfLong[CentralProcessor.TickType.SOFTIRQ.getIndex()];
      return arrayOfLong;
    } 
    long[][] arrayOfLong1 = getProcessorCpuLoadTicks();
    for (byte b = 0; b < arrayOfLong.length; b++) {
      for (long[] arrayOfLong2 : arrayOfLong1)
        arrayOfLong[b] = arrayOfLong[b] + arrayOfLong2[b]; 
    } 
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    if (VersionHelpers.IsWindows7OrGreater()) {
      Pair pair = ProcessorInformation.queryFrequencyCounters();
      List list1 = (List)pair.getA();
      Map map = (Map)pair.getB();
      List list2 = (List)map.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
      if (!list1.isEmpty()) {
        long l = getMaxFreq();
        long[] arrayOfLong = new long[getLogicalProcessorCount()];
        for (String str : list1) {
          int i = str.contains(",") ? ((Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(str, Integer.valueOf(0))).intValue() : ParseUtil.parseIntOrDefault(str, 0);
          if (i >= getLogicalProcessorCount())
            continue; 
          arrayOfLong[i] = ((Long)list2.get(i)).longValue() * l / 100L;
        } 
        return arrayOfLong;
      } 
    } 
    return queryNTPower(2);
  }
  
  public long queryMaxFreq() {
    long[] arrayOfLong = queryNTPower(1);
    return Arrays.stream(arrayOfLong).max().orElse(-1L);
  }
  
  private long[] queryNTPower(int paramInt) {
    PowrProf.ProcessorPowerInformation processorPowerInformation = new PowrProf.ProcessorPowerInformation();
    PowrProf.ProcessorPowerInformation[] arrayOfProcessorPowerInformation = (PowrProf.ProcessorPowerInformation[])processorPowerInformation.toArray(getLogicalProcessorCount());
    long[] arrayOfLong = new long[getLogicalProcessorCount()];
    if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, null, 0, arrayOfProcessorPowerInformation[0].getPointer(), processorPowerInformation.size() * arrayOfProcessorPowerInformation.length)) {
      LOG.error("Unable to get Processor Information");
      Arrays.fill(arrayOfLong, -1L);
      return arrayOfLong;
    } 
    for (byte b = 0; b < arrayOfLong.length; b++) {
      if (paramInt == 1) {
        arrayOfLong[b] = (arrayOfProcessorPowerInformation[b]).maxMhz * 1000000L;
      } else if (paramInt == 2) {
        arrayOfLong[b] = (arrayOfProcessorPowerInformation[b]).currentMhz * 1000000L;
      } else {
        arrayOfLong[b] = -1L;
      } 
      if (arrayOfLong[b] == 0L)
        arrayOfLong[b] = getProcessorIdentifier().getVendorFreq(); 
    } 
    return arrayOfLong;
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    return LoadAverage.queryLoadAverage(paramInt);
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    List list1;
    List list2;
    List list3;
    List list4;
    List list5;
    List list6;
    List<Long> list7 = null;
    List<Long> list8 = null;
    List<Long> list9 = null;
    List<Long> list10 = null;
    List<Long> list11 = null;
    List<Long> list12 = null;
    List<Long> list13 = null;
    List<Long> list14 = null;
    List<Long> list15 = null;
    List<Long> list16 = null;
    if (USE_CPU_UTILITY) {
      Pair pair = this.processorUtilityCounters.get();
      list1 = (List)pair.getA();
      Map map = (Map)pair.getB();
      list2 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDTIME);
      list3 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTUSERTIME);
      list4 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTINTERRUPTTIME);
      list5 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTDPCTIME);
      list6 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORTIME);
      list7 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.TIMESTAMP_SYS100NS);
      list8 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDUTILITY);
      list9 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY);
      list10 = (List)map.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY_BASE);
      list11 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDTIME);
      list12 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTUSERTIME);
      list13 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.TIMESTAMP_SYS100NS);
      list14 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDUTILITY);
      list15 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY);
      list16 = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY_BASE);
    } else {
      Pair pair = ProcessorInformation.queryProcessorCounters();
      list1 = (List)pair.getA();
      Map map = (Map)pair.getB();
      list2 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
      list3 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
      list4 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
      list5 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
      list6 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
    } 
    int i = getLogicalProcessorCount();
    long[][] arrayOfLong = new long[i][(CentralProcessor.TickType.values()).length];
    if (list1.isEmpty() || list2 == null || list3 == null || list4 == null || list5 == null || list6 == null || (USE_CPU_UTILITY && (list7 == null || list8 == null || list9 == null || list10 == null || list11 == null || list12 == null || list13 == null || list14 == null || list15 == null || list16 == null)))
      return arrayOfLong; 
    for (String str : list1) {
      int j = str.contains(",") ? ((Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(str, Integer.valueOf(0))).intValue() : ParseUtil.parseIntOrDefault(str, 0);
      if (j >= i)
        continue; 
      arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] = ((Long)list2.get(j)).longValue();
      arrayOfLong[j][CentralProcessor.TickType.USER.getIndex()] = ((Long)list3.get(j)).longValue();
      arrayOfLong[j][CentralProcessor.TickType.IRQ.getIndex()] = ((Long)list4.get(j)).longValue();
      arrayOfLong[j][CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((Long)list5.get(j)).longValue();
      arrayOfLong[j][CentralProcessor.TickType.IDLE.getIndex()] = ((Long)list6.get(j)).longValue();
      if (USE_CPU_UTILITY) {
        long l = ((Long)list7.get(j)).longValue() - ((Long)list13.get(j)).longValue();
        if (l > 0L) {
          long l1 = ((Long)list10.get(j)).longValue() - ((Long)list16.get(j)).longValue();
          long l2 = lazilyCalculateMultiplier(l1, l);
          if (l2 > 0L) {
            long l3 = ((Long)list9.get(j)).longValue() - ((Long)list15.get(j)).longValue();
            long l4 = ((Long)list8.get(j)).longValue() - ((Long)list14.get(j)).longValue();
            long l5 = ((Long)list12.get(j)).longValue() + l2 * (l3 - l4) / 100L;
            long l6 = ((Long)list11.get(j)).longValue() + l2 * l4 / 100L;
            long l7 = l5 - arrayOfLong[j][CentralProcessor.TickType.USER.getIndex()];
            arrayOfLong[j][CentralProcessor.TickType.USER.getIndex()] = l5;
            l7 += l6 - arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()];
            arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] = l6;
            arrayOfLong[j][CentralProcessor.TickType.IDLE.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.IDLE.getIndex()] - l7;
          } 
        } 
      } 
      arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] - arrayOfLong[j][CentralProcessor.TickType.IRQ.getIndex()] + arrayOfLong[j][CentralProcessor.TickType.SOFTIRQ.getIndex()];
      arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.SYSTEM.getIndex()] / 10000L;
      arrayOfLong[j][CentralProcessor.TickType.USER.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.USER.getIndex()] / 10000L;
      arrayOfLong[j][CentralProcessor.TickType.IRQ.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.IRQ.getIndex()] / 10000L;
      arrayOfLong[j][CentralProcessor.TickType.SOFTIRQ.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.SOFTIRQ.getIndex()] / 10000L;
      arrayOfLong[j][CentralProcessor.TickType.IDLE.getIndex()] = arrayOfLong[j][CentralProcessor.TickType.IDLE.getIndex()] / 10000L;
    } 
    return arrayOfLong;
  }
  
  private synchronized long lazilyCalculateMultiplier(long paramLong1, long paramLong2) {
    if (this.utilityBaseMultiplier == null) {
      if (paramLong2 >> 32L > 0L) {
        this.initialUtilityCounters = (Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>)((Pair)this.processorUtilityCounters.get()).getB();
        return 0L;
      } 
      if (paramLong1 <= 0L)
        paramLong1 += 4294967296L; 
      long l = Math.round(paramLong2 / paramLong1);
      if (paramLong2 < 50000000L)
        return l; 
      this.utilityBaseMultiplier = Long.valueOf(l);
    } 
    return this.utilityBaseMultiplier.longValue();
  }
  
  private static Pair<List<String>, Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>> queryProcessorUtilityCounters() {
    return ProcessorInformation.queryProcessorCapacityCounters();
  }
  
  public long queryContextSwitches() {
    return ((Long)SystemInformation.queryContextSwitchCounters().getOrDefault(SystemInformation.ContextSwitchProperty.CONTEXTSWITCHESPERSEC, Long.valueOf(0L))).longValue();
  }
  
  public long queryInterrupts() {
    return ((Long)ProcessorInformation.queryInterruptCounters().getOrDefault(ProcessorInformation.InterruptsProperty.INTERRUPTSPERSEC, Long.valueOf(0L))).longValue();
  }
  
  static {
    if (USE_LOAD_AVERAGE)
      LoadAverage.startDaemon(); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */