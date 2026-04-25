package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Quartet;

@ThreadSafe
final class FreeBsdCentralProcessor extends AbstractCentralProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(FreeBsdCentralProcessor.class);
  
  private static final Pattern CPUMASK = Pattern.compile(".*<cpu\\s.*mask=\"(\\p{XDigit}+(,\\p{XDigit}+)*)\".*>.*</cpu>.*");
  
  private static final long CPTIME_SIZE;
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    Pattern pattern1 = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
    Pattern pattern2 = Pattern.compile("Features=(\\S+)<.*");
    String str1 = "";
    String str2 = BsdSysctlUtil.sysctl("hw.model", "");
    String str3 = "";
    String str4 = "";
    String str5 = "";
    long l1 = BsdSysctlUtil.sysctl("hw.clockrate", 0L) * 1000000L;
    long l2 = 0L;
    List list = FileUtil.readFile("/var/run/dmesg.boot");
    for (String str : list) {
      str = str.trim();
      if (str.startsWith("CPU:") && str2.isEmpty()) {
        str2 = str.replace("CPU:", "").trim();
        continue;
      } 
      if (str.startsWith("Origin=")) {
        Matcher matcher = pattern1.matcher(str);
        if (matcher.matches()) {
          str1 = matcher.group(1);
          l2 |= Long.decode(matcher.group(2)).longValue();
          str3 = Integer.decode(matcher.group(3)).toString();
          str4 = Integer.decode(matcher.group(4)).toString();
          str5 = Integer.decode(matcher.group(5)).toString();
        } 
        continue;
      } 
      if (str.startsWith("Features=")) {
        Matcher matcher = pattern2.matcher(str);
        if (matcher.matches())
          l2 |= Long.decode(matcher.group(1)).longValue() << 32L; 
        break;
      } 
    } 
    boolean bool = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
    String str6 = getProcessorIDfromDmiDecode(l2);
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str4, str5, str6, bool, l1);
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    List<CentralProcessor.LogicalProcessor> list = parseTopology();
    if (list.isEmpty())
      list.add(new CentralProcessor.LogicalProcessor(0, 0, 0)); 
    HashMap<Object, Object> hashMap = new HashMap<>();
    Pattern pattern1 = Pattern.compile("cpu(\\\\d+): (.+) on .*");
    Pattern pattern2 = Pattern.compile("CPU\\\\s*(\\\\d+): (.+) affinity:.*");
    ArrayList<String> arrayList = new ArrayList();
    boolean bool = false;
    for (String str : FileUtil.readFile("/var/run/dmesg.boot")) {
      Matcher matcher = pattern2.matcher(str);
      if (matcher.matches()) {
        int i = ParseUtil.parseIntOrDefault(matcher.group(1), 0);
        hashMap.put(Integer.valueOf(i), matcher.group(2).trim());
      } else {
        Matcher matcher1 = pattern1.matcher(str);
        if (matcher1.matches()) {
          int i = ParseUtil.parseIntOrDefault(matcher1.group(1), 0);
          hashMap.putIfAbsent(Integer.valueOf(i), matcher1.group(2).trim());
        } 
      } 
      if (str.contains("Origin=")) {
        bool = true;
        continue;
      } 
      if (bool) {
        if (str.startsWith("  ")) {
          arrayList.add(str.trim());
          continue;
        } 
        bool = false;
      } 
    } 
    List list1 = hashMap.isEmpty() ? null : createProcListFromDmesg(list, hashMap);
    List<CentralProcessor.ProcessorCache> list2 = getCacheInfoFromLscpu();
    return new Quartet(list, list1, list2, arrayList);
  }
  
  private List<CentralProcessor.ProcessorCache> getCacheInfoFromLscpu() {
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    for (String str : ExecutingCommand.runNative("lscpu")) {
      if (str.contains("L1d cache:")) {
        hashSet.add(new CentralProcessor.ProcessorCache(1, 0, 0, ParseUtil.parseDecimalMemorySizeToBinary(str.split(":")[1].trim()), CentralProcessor.ProcessorCache.Type.DATA));
        continue;
      } 
      if (str.contains("L1i cache:")) {
        hashSet.add(new CentralProcessor.ProcessorCache(1, 0, 0, ParseUtil.parseDecimalMemorySizeToBinary(str.split(":")[1].trim()), CentralProcessor.ProcessorCache.Type.INSTRUCTION));
        continue;
      } 
      if (str.contains("L2 cache:")) {
        hashSet.add(new CentralProcessor.ProcessorCache(2, 0, 0, ParseUtil.parseDecimalMemorySizeToBinary(str.split(":")[1].trim()), CentralProcessor.ProcessorCache.Type.UNIFIED));
        continue;
      } 
      if (str.contains("L3 cache:"))
        hashSet.add(new CentralProcessor.ProcessorCache(3, 0, 0, ParseUtil.parseDecimalMemorySizeToBinary(str.split(":")[1].trim()), CentralProcessor.ProcessorCache.Type.UNIFIED)); 
    } 
    return orderedProcCaches(hashSet);
  }
  
  private static List<CentralProcessor.LogicalProcessor> parseTopology() {
    String[] arrayOfString = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("[\\n\\r]");
    long l = 1L;
    ArrayList<Long> arrayList1 = new ArrayList();
    ArrayList<Long> arrayList2 = new ArrayList();
    byte b = 0;
    for (String str : arrayOfString) {
      if (str.contains("<group level=")) {
        b++;
      } else if (str.contains("</group>")) {
        b--;
      } else if (str.contains("<cpu")) {
        Matcher matcher = CPUMASK.matcher(str);
        if (matcher.matches()) {
          String str1 = matcher.group(1);
          String[] arrayOfString1 = str1.split(",");
          String str2 = arrayOfString1[0];
          long l1 = ParseUtil.hexStringToLong(str2, 0L);
          switch (b) {
            case 1:
              l = l1;
              break;
            case 2:
              arrayList1.add(Long.valueOf(l1));
              break;
            case 3:
              arrayList2.add(Long.valueOf(l1));
              break;
          } 
        } 
      } 
    } 
    return matchBitmasks(l, arrayList1, arrayList2);
  }
  
  private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long paramLong, List<Long> paramList1, List<Long> paramList2) {
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    int i = Long.numberOfTrailingZeros(paramLong);
    int j = 63 - Long.numberOfLeadingZeros(paramLong);
    for (int k = i; k <= j; k++) {
      if ((paramLong & 1L << k) > 0L) {
        boolean bool = false;
        CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(k, getMatchingBitmask(paramList2, k), getMatchingBitmask(paramList1, k), bool);
        arrayList.add(logicalProcessor);
      } 
    } 
    return arrayList;
  }
  
  private static int getMatchingBitmask(List<Long> paramList, int paramInt) {
    for (byte b = 0; b < paramList.size(); b++) {
      if ((((Long)paramList.get(b)).longValue() & 1L << paramInt) != 0L)
        return b; 
    } 
    return 0;
  }
  
  public long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
    try {
      BsdSysctlUtil.sysctl("kern.cp_time", (Structure)cpTime);
      arrayOfLong[CentralProcessor.TickType.USER.getIndex()] = cpTime.cpu_ticks[0];
      arrayOfLong[CentralProcessor.TickType.NICE.getIndex()] = cpTime.cpu_ticks[1];
      arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] = cpTime.cpu_ticks[2];
      arrayOfLong[CentralProcessor.TickType.IRQ.getIndex()] = cpTime.cpu_ticks[3];
      arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()] = cpTime.cpu_ticks[4];
      cpTime.close();
    } catch (Throwable throwable) {
      try {
        cpTime.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L);
    if (arrayOfLong[0] > 0L) {
      arrayOfLong[0] = arrayOfLong[0] * 1000000L;
    } else {
      arrayOfLong[0] = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
    } 
    return arrayOfLong;
  }
  
  public long queryMaxFreq() {
    long l = -1L;
    String str = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");
    for (String str1 : ParseUtil.whitespaces.split(str)) {
      long l1 = ParseUtil.parseLongOrDefault(str1.split("/")[0], -1L);
      if (l < l1)
        l = l1; 
    } 
    if (l > 0L) {
      l *= 1000000L;
    } else {
      l = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
    } 
    return l;
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    int i = FreeBsdLibc.INSTANCE.getloadavg(arrayOfDouble, paramInt);
    if (i < paramInt)
      for (int j = Math.max(i, 0); j < arrayOfDouble.length; j++)
        arrayOfDouble[j] = -1.0D;  
    return arrayOfDouble;
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    long[][] arrayOfLong = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
    long l = CPTIME_SIZE * getLogicalProcessorCount();
    Memory memory = new Memory(l);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(l);
      try {
        String str = "kern.cp_times";
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(str, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          LOG.error("Failed sysctl call: {}, Error code: {}", str, Integer.valueOf(Native.getLastError()));
          long[][] arrayOfLong1 = arrayOfLong;
          closeableSizeTByReference.close();
          memory.close();
          return arrayOfLong1;
        } 
        for (byte b = 0; b < getLogicalProcessorCount(); b++) {
          arrayOfLong[b][CentralProcessor.TickType.USER.getIndex()] = memory.getLong(CPTIME_SIZE * b + (0 * FreeBsdLibc.UINT64_SIZE));
          arrayOfLong[b][CentralProcessor.TickType.NICE.getIndex()] = memory.getLong(CPTIME_SIZE * b + (1 * FreeBsdLibc.UINT64_SIZE));
          arrayOfLong[b][CentralProcessor.TickType.SYSTEM.getIndex()] = memory.getLong(CPTIME_SIZE * b + (2 * FreeBsdLibc.UINT64_SIZE));
          arrayOfLong[b][CentralProcessor.TickType.IRQ.getIndex()] = memory.getLong(CPTIME_SIZE * b + (3 * FreeBsdLibc.UINT64_SIZE));
          arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] = memory.getLong(CPTIME_SIZE * b + (4 * FreeBsdLibc.UINT64_SIZE));
        } 
        closeableSizeTByReference.close();
      } catch (Throwable throwable) {
        try {
          closeableSizeTByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      memory.close();
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  private static String getProcessorIDfromDmiDecode(long paramLong) {
    boolean bool = false;
    String str = "Processor Information";
    for (String str1 : ExecutingCommand.runNative("dmidecode -t system")) {
      if (!bool && str1.contains(str)) {
        str = "ID:";
        bool = true;
        continue;
      } 
      if (bool && str1.contains(str))
        return str1.split(str)[1].trim(); 
    } 
    return String.format(Locale.ROOT, "%016X", new Object[] { Long.valueOf(paramLong) });
  }
  
  public long queryContextSwitches() {
    String str = "vm.stats.sys.v_swtch";
    LibCAPI.size_t.ByReference byReference = new LibCAPI.size_t.ByReference(new LibCAPI.size_t(FreeBsdLibc.INT_SIZE));
    Memory memory = new Memory(byReference.longValue());
    try {
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(str, (Pointer)memory, byReference, null, LibCAPI.size_t.ZERO)) {
        long l1 = 0L;
        memory.close();
        return l1;
      } 
      long l = ParseUtil.unsignedIntToLong(memory.getInt(0L));
      memory.close();
      return l;
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public long queryInterrupts() {
    String str = "vm.stats.sys.v_intr";
    LibCAPI.size_t.ByReference byReference = new LibCAPI.size_t.ByReference(new LibCAPI.size_t(FreeBsdLibc.INT_SIZE));
    Memory memory = new Memory(byReference.longValue());
    try {
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(str, (Pointer)memory, byReference, null, LibCAPI.size_t.ZERO)) {
        long l1 = 0L;
        memory.close();
        return l1;
      } 
      long l = ParseUtil.unsignedIntToLong(memory.getInt(0L));
      memory.close();
      return l;
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  static {
    FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
    try {
      CPTIME_SIZE = cpTime.size();
      cpTime.close();
    } catch (Throwable throwable) {
      try {
        cpTime.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */