package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.util.ExecutingCommand;
import oshi.util.FormatUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Quartet;

@ThreadSafe
final class MacCentralProcessor extends AbstractCentralProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(MacCentralProcessor.class);
  
  private static final Set<String> ARM_P_CORES = (Set<String>)Stream.<String>of(new String[] { "apple,firestorm arm,v8", "apple,avalanche arm,v8", "apple,everest arm,v8" }).collect(Collectors.toSet());
  
  private static final int ARM_CPUTYPE = 16777228;
  
  private static final int M1_CPUFAMILY = 458787763;
  
  private static final int M2_CPUFAMILY = -634136515;
  
  private static final int M3_CPUFAMILY = -2023363094;
  
  private static final long DEFAULT_FREQUENCY = 2400000000L;
  
  private static final Pattern CPU_N = Pattern.compile("^cpu(\\d+)");
  
  private final Supplier<String> vendor = Memoizer.memoize(MacCentralProcessor::platformExpert);
  
  private final boolean isArmCpu = isArmCpu();
  
  private long performanceCoreFrequency = 2400000000L;
  
  private long efficiencyCoreFrequency = 2400000000L;
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    String str2;
    String str3;
    String str4;
    String str5;
    String str6;
    String str1 = SysctlUtil.sysctl("machdep.cpu.brand_string", "");
    if (str1.startsWith("Apple")) {
      int i;
      int j;
      str2 = this.vendor.get();
      str3 = "0";
      str4 = "0";
      if (this.isArmCpu) {
        i = 16777228;
        int k = ParseUtil.getFirstIntValue(str1);
        switch (k) {
          case 2:
            j = -634136515;
            break;
          case 3:
            j = -2023363094;
            break;
          default:
            j = 458787763;
            break;
        } 
      } else {
        i = SysctlUtil.sysctl("hw.cputype", 0);
        j = SysctlUtil.sysctl("hw.cpufamily", 0);
      } 
      str5 = String.format(Locale.ROOT, "0x%08x", new Object[] { Integer.valueOf(j) });
      str6 = String.format(Locale.ROOT, "%08x%08x", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
    } else {
      str2 = SysctlUtil.sysctl("machdep.cpu.vendor", "");
      int i = SysctlUtil.sysctl("machdep.cpu.stepping", -1);
      str3 = (i < 0) ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.model", -1);
      str4 = (i < 0) ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.family", -1);
      str5 = (i < 0) ? "" : Integer.toString(i);
      long l1 = 0L;
      l1 |= SysctlUtil.sysctl("machdep.cpu.signature", 0);
      l1 |= (SysctlUtil.sysctl("machdep.cpu.feature_bits", 0L) & 0xFFFFFFFFFFFFFFFFL) << 32L;
      str6 = String.format(Locale.ROOT, "%016x", new Object[] { Long.valueOf(l1) });
    } 
    if (this.isArmCpu)
      calculateNominalFrequencies(); 
    long l = this.isArmCpu ? this.performanceCoreFrequency : SysctlUtil.sysctl("hw.cpufrequency", 0L);
    boolean bool = (SysctlUtil.sysctl("hw.cpu64bit_capable", 0) != 0) ? true : false;
    return new CentralProcessor.ProcessorIdentifier(str2, str1, str5, str4, str3, str6, bool, l);
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    int i = SysctlUtil.sysctl("hw.logicalcpu", 1);
    int j = SysctlUtil.sysctl("hw.physicalcpu", 1);
    int k = SysctlUtil.sysctl("hw.packages", 1);
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList(i);
    HashSet<Integer> hashSet = new HashSet();
    for (byte b = 0; b < i; b++) {
      int n = b * j / i;
      int i1 = b * k / i;
      arrayList.add(new CentralProcessor.LogicalProcessor(b, n, i1));
      hashSet.add(Integer.valueOf((i1 << 16) + n));
    } 
    Map<Integer, String> map = queryCompatibleStrings();
    int m = SysctlUtil.sysctl("hw.nperflevels", 1, false);
    List list1 = (List)hashSet.stream().sorted().map(paramInteger -> {
          String str = ((String)paramMap.getOrDefault(paramInteger, "")).toLowerCase(Locale.ROOT);
          boolean bool = ARM_P_CORES.contains(str) ? true : false;
          return new CentralProcessor.PhysicalProcessor(paramInteger.intValue() >> 16, paramInteger.intValue() & 0xFFFF, bool, str);
        }).collect(Collectors.toList());
    List list2 = orderedProcCaches(getCacheValues(m));
    List<String> list = getFeatureFlagsFromSysctl();
    return new Quartet(arrayList, list1, list2, list);
  }
  
  private Set<CentralProcessor.ProcessorCache> getCacheValues(int paramInt) {
    int i = (int)SysctlUtil.sysctl("hw.cachelinesize", 0L);
    int j = SysctlUtil.sysctl("machdep.cpu.cache.L1_associativity", 0, false);
    int k = SysctlUtil.sysctl("machdep.cpu.cache.L2_associativity", 0, false);
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    for (byte b = 0; b < paramInt; b++) {
      int m = SysctlUtil.sysctl("hw.perflevel" + b + ".l1icachesize", 0, false);
      if (m > 0)
        hashSet.add(new CentralProcessor.ProcessorCache(1, j, i, m, CentralProcessor.ProcessorCache.Type.INSTRUCTION)); 
      m = SysctlUtil.sysctl("hw.perflevel" + b + ".l1dcachesize", 0, false);
      if (m > 0)
        hashSet.add(new CentralProcessor.ProcessorCache(1, j, i, m, CentralProcessor.ProcessorCache.Type.DATA)); 
      m = SysctlUtil.sysctl("hw.perflevel" + b + ".l2cachesize", 0, false);
      if (m > 0)
        hashSet.add(new CentralProcessor.ProcessorCache(2, k, i, m, CentralProcessor.ProcessorCache.Type.UNIFIED)); 
      m = SysctlUtil.sysctl("hw.perflevel" + b + ".l3cachesize", 0, false);
      if (m > 0)
        hashSet.add(new CentralProcessor.ProcessorCache(3, 0, i, m, CentralProcessor.ProcessorCache.Type.UNIFIED)); 
    } 
    return hashSet;
  }
  
  private List<String> getFeatureFlagsFromSysctl() {
    List<String> list = (List)Arrays.<String>asList(new String[] { "features", "extfeatures", "leaf7_features" }).stream().map(paramString -> {
          String str1 = "machdep.cpu." + paramString;
          String str2 = SysctlUtil.sysctl(str1, "", false);
          return Util.isBlank(str2) ? null : (str1 + ": " + str2);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    return list.isEmpty() ? ExecutingCommand.runNative("sysctl -a hw.optional") : list;
  }
  
  public long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    int i = SystemB.INSTANCE.mach_host_self();
    Struct.CloseableHostCpuLoadInfo closeableHostCpuLoadInfo = new Struct.CloseableHostCpuLoadInfo();
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(closeableHostCpuLoadInfo.size());
      try {
        if (0 != SystemB.INSTANCE.host_statistics(i, 3, (Structure)closeableHostCpuLoadInfo, (IntByReference)closeableIntByReference)) {
          LOG.error("Failed to get System CPU ticks. Error code: {} ", Integer.valueOf(Native.getLastError()));
          long[] arrayOfLong1 = arrayOfLong;
          closeableIntByReference.close();
          closeableHostCpuLoadInfo.close();
          return arrayOfLong1;
        } 
        arrayOfLong[CentralProcessor.TickType.USER.getIndex()] = closeableHostCpuLoadInfo.cpu_ticks[0];
        arrayOfLong[CentralProcessor.TickType.NICE.getIndex()] = closeableHostCpuLoadInfo.cpu_ticks[3];
        arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] = closeableHostCpuLoadInfo.cpu_ticks[1];
        arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()] = closeableHostCpuLoadInfo.cpu_ticks[2];
        closeableIntByReference.close();
      } catch (Throwable throwable) {
        try {
          closeableIntByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      closeableHostCpuLoadInfo.close();
    } catch (Throwable throwable) {
      try {
        closeableHostCpuLoadInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    if (this.isArmCpu) {
      HashMap<Object, Object> hashMap = new HashMap<>();
      getPhysicalProcessors().stream().forEach(paramPhysicalProcessor -> paramMap.put(Integer.valueOf(paramPhysicalProcessor.getPhysicalProcessorNumber()), Long.valueOf((paramPhysicalProcessor.getEfficiency() > 0) ? this.performanceCoreFrequency : this.efficiencyCoreFrequency)));
      return getLogicalProcessors().stream().map(CentralProcessor.LogicalProcessor::getPhysicalProcessorNumber).map(paramInteger -> (Long)paramMap.getOrDefault(paramInteger, Long.valueOf(this.performanceCoreFrequency))).mapToLong(paramLong -> paramLong.longValue()).toArray();
    } 
    return new long[] { getProcessorIdentifier().getVendorFreq() };
  }
  
  public long queryMaxFreq() {
    return this.isArmCpu ? this.performanceCoreFrequency : SysctlUtil.sysctl("hw.cpufrequency_max", getProcessorIdentifier().getVendorFreq());
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    int i = SystemB.INSTANCE.getloadavg(arrayOfDouble, paramInt);
    if (i < paramInt)
      Arrays.fill(arrayOfDouble, -1.0D); 
    return arrayOfDouble;
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    long[][] arrayOfLong = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
    int i = SystemB.INSTANCE.mach_host_self();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
        try {
          if (0 != SystemB.INSTANCE.host_processor_info(i, 2, (IntByReference)closeableIntByReference, (PointerByReference)closeablePointerByReference, (IntByReference)closeableIntByReference1)) {
            LOG.error("Failed to update CPU Load. Error code: {}", Integer.valueOf(Native.getLastError()));
            long[][] arrayOfLong1 = arrayOfLong;
            closeableIntByReference1.close();
            closeablePointerByReference.close();
            closeableIntByReference.close();
            return arrayOfLong1;
          } 
          int[] arrayOfInt = closeablePointerByReference.getValue().getIntArray(0L, closeableIntByReference1.getValue());
          for (byte b = 0; b < closeableIntByReference.getValue(); b++) {
            int j = b * 4;
            arrayOfLong[b][CentralProcessor.TickType.USER.getIndex()] = FormatUtil.getUnsignedInt(arrayOfInt[j + 0]);
            arrayOfLong[b][CentralProcessor.TickType.NICE.getIndex()] = FormatUtil.getUnsignedInt(arrayOfInt[j + 3]);
            arrayOfLong[b][CentralProcessor.TickType.SYSTEM.getIndex()] = FormatUtil.getUnsignedInt(arrayOfInt[j + 1]);
            arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] = FormatUtil.getUnsignedInt(arrayOfInt[j + 2]);
          } 
          closeableIntByReference1.close();
        } catch (Throwable throwable) {
          try {
            closeableIntByReference1.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        closeablePointerByReference.close();
      } catch (Throwable throwable) {
        try {
          closeablePointerByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      closeableIntByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableIntByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  public long queryContextSwitches() {
    return 0L;
  }
  
  public long queryInterrupts() {
    return 0L;
  }
  
  private static String platformExpert() {
    String str = null;
    IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
    if (iOService != null) {
      byte[] arrayOfByte = iOService.getByteArrayProperty("manufacturer");
      if (arrayOfByte != null)
        str = Native.toString(arrayOfByte, StandardCharsets.UTF_8); 
      iOService.release();
    } 
    return Util.isBlank(str) ? "Apple Inc." : str;
  }
  
  private static Map<Integer, String> queryCompatibleStrings() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices("IOPlatformDevice");
    if (iOIterator != null) {
      for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
        Matcher matcher = CPU_N.matcher(iORegistryEntry.getName().toLowerCase(Locale.ROOT));
        if (matcher.matches()) {
          int i = ParseUtil.parseIntOrDefault(matcher.group(1), 0);
          byte[] arrayOfByte = iORegistryEntry.getByteArrayProperty("compatible");
          if (arrayOfByte != null)
            hashMap.put(Integer.valueOf(i), (new String(arrayOfByte, StandardCharsets.UTF_8)).replace(false, ' ').trim()); 
        } 
        iORegistryEntry.release();
      } 
      iOIterator.release();
    } 
    return (Map)hashMap;
  }
  
  private boolean isArmCpu() {
    return getPhysicalProcessors().stream().map(CentralProcessor.PhysicalProcessor::getIdString).anyMatch(paramString -> paramString.contains("arm"));
  }
  
  private void calculateNominalFrequencies() {
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices("AppleARMIODevice");
    if (iOIterator != null)
      try {
        IOKit.IORegistryEntry iORegistryEntry = iOIterator.next();
        try {
          while (iORegistryEntry != null) {
            if (iORegistryEntry.getName().equalsIgnoreCase("pmgr")) {
              this.performanceCoreFrequency = getMaxFreqFromByteArray(iORegistryEntry.getByteArrayProperty("voltage-states5-sram"));
              this.efficiencyCoreFrequency = getMaxFreqFromByteArray(iORegistryEntry.getByteArrayProperty("voltage-states1-sram"));
              return;
            } 
            iORegistryEntry.release();
            iORegistryEntry = iOIterator.next();
          } 
        } finally {
          if (iORegistryEntry != null)
            iORegistryEntry.release(); 
        } 
      } finally {
        iOIterator.release();
      }  
  }
  
  private long getMaxFreqFromByteArray(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte != null && paramArrayOfbyte.length >= 8) {
      byte[] arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, paramArrayOfbyte.length - 8, paramArrayOfbyte.length - 4);
      return ParseUtil.byteArrayToLong(arrayOfByte, 4, false);
    } 
    return 2400000000L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */