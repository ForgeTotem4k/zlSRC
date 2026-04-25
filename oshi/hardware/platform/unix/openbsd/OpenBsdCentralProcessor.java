package oshi.hardware.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Quartet;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class OpenBsdCentralProcessor extends AbstractCentralProcessor {
  private final Supplier<Pair<Long, Long>> vmStats = Memoizer.memoize(OpenBsdCentralProcessor::queryVmStats, Memoizer.defaultExpiration());
  
  private static final Pattern DMESG_CPU = Pattern.compile("cpu(\\d+): smt (\\d+), core (\\d+), package (\\d+)");
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    String str1 = OpenBsdSysctlUtil.sysctl("machdep.cpuvendor", "");
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 6;
    arrayOfInt[1] = 2;
    String str2 = OpenBsdSysctlUtil.sysctl(arrayOfInt, "");
    int i = ParseUtil.hexStringToInt(OpenBsdSysctlUtil.sysctl("machdep.cpuid", ""), 0);
    int j = ParseUtil.hexStringToInt(OpenBsdSysctlUtil.sysctl("machdep.cpufeature", ""), 0);
    Triplet<Integer, Integer, Integer> triplet = cpuidToFamilyModelStepping(i);
    String str3 = ((Integer)triplet.getA()).toString();
    String str4 = ((Integer)triplet.getB()).toString();
    String str5 = ((Integer)triplet.getC()).toString();
    long l = ParseUtil.parseHertz(str2);
    if (l < 0L)
      l = queryMaxFreq(); 
    arrayOfInt[1] = 1;
    String str6 = OpenBsdSysctlUtil.sysctl(arrayOfInt, "");
    boolean bool = ((str6 != null && str6.contains("64")) || ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64")) ? true : false;
    String str7 = String.format(Locale.ROOT, "%08x%08x", new Object[] { Integer.valueOf(j), Integer.valueOf(i) });
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str4, str5, str7, bool, l);
  }
  
  private static Triplet<Integer, Integer, Integer> cpuidToFamilyModelStepping(int paramInt) {
    int i = paramInt >> 16 & 0xFF0 | paramInt >> 8 & 0xF;
    int j = paramInt >> 12 & 0xF0 | paramInt >> 4 & 0xF;
    int k = paramInt & 0xF;
    return new Triplet(Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k));
  }
  
  protected long[] queryCurrentFreq() {
    long[] arrayOfLong = new long[1];
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 6;
    arrayOfInt[1] = 12;
    arrayOfLong[0] = OpenBsdSysctlUtil.sysctl(arrayOfInt, 0L) * 1000000L;
    return arrayOfLong;
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    for (String str : ExecutingCommand.runNative("dmesg")) {
      Matcher matcher = DMESG_CPU.matcher(str);
      if (matcher.matches()) {
        int j = ParseUtil.parseIntOrDefault(matcher.group(1), 0);
        hashMap1.put(Integer.valueOf(j), Integer.valueOf(ParseUtil.parseIntOrDefault(matcher.group(3), 0)));
        hashMap2.put(Integer.valueOf(j), Integer.valueOf(ParseUtil.parseIntOrDefault(matcher.group(4), 0)));
      } 
    } 
    int i = OpenBsdSysctlUtil.sysctl("hw.ncpuonline", 1);
    if (i < hashMap1.keySet().size())
      i = hashMap1.keySet().size(); 
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++)
      arrayList.add(new CentralProcessor.LogicalProcessor(b, ((Integer)hashMap1.getOrDefault(Integer.valueOf(b), Integer.valueOf(0))).intValue(), ((Integer)hashMap2.getOrDefault(Integer.valueOf(b), Integer.valueOf(0))).intValue())); 
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    Pattern pattern1 = Pattern.compile("cpu(\\\\d+).*: ((ARM|AMD|Intel|Apple).+)");
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    Pattern pattern2 = Pattern.compile("cpu(\\\\d+).*: (.+(I-|D-|L\\d+\\s)cache)");
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
    for (String str : ExecutingCommand.runNative("dmesg")) {
      Matcher matcher = pattern1.matcher(str);
      if (matcher.matches()) {
        int j = ParseUtil.parseIntOrDefault(matcher.group(1), 0);
        hashMap3.put(Integer.valueOf(j), matcher.group(2).trim());
      } else {
        Matcher matcher1 = pattern2.matcher(str);
        if (matcher1.matches())
          for (String str1 : matcher1.group(1).split(",")) {
            CentralProcessor.ProcessorCache processorCache = parseCacheStr(str1);
            if (processorCache != null)
              hashSet.add(processorCache); 
          }  
      } 
      if (str.startsWith("cpu")) {
        String[] arrayOfString = str.trim().split(": ");
        if (arrayOfString.length == 2 && (arrayOfString[1].split(",")).length > 3)
          linkedHashSet.add(arrayOfString[1]); 
      } 
    } 
    List list = hashMap3.isEmpty() ? null : createProcListFromDmesg(arrayList, hashMap3);
    return new Quartet(arrayList, list, orderedProcCaches(hashSet), new ArrayList<>(linkedHashSet));
  }
  
  private CentralProcessor.ProcessorCache parseCacheStr(String paramString) {
    String[] arrayOfString = ParseUtil.whitespaces.split(paramString);
    if (arrayOfString.length > 3) {
      switch (arrayOfString[arrayOfString.length - 1]) {
        case "I-cache":
          return new CentralProcessor.ProcessorCache(1, ParseUtil.getFirstIntValue(arrayOfString[2]), ParseUtil.getFirstIntValue(arrayOfString[1]), ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[0]), CentralProcessor.ProcessorCache.Type.INSTRUCTION);
        case "D-cache":
          return new CentralProcessor.ProcessorCache(1, ParseUtil.getFirstIntValue(arrayOfString[2]), ParseUtil.getFirstIntValue(arrayOfString[1]), ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[0]), CentralProcessor.ProcessorCache.Type.DATA);
      } 
      return new CentralProcessor.ProcessorCache(ParseUtil.getFirstIntValue(arrayOfString[3]), ParseUtil.getFirstIntValue(arrayOfString[2]), ParseUtil.getFirstIntValue(arrayOfString[1]), ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[0]), CentralProcessor.ProcessorCache.Type.UNIFIED);
    } 
    return null;
  }
  
  protected long queryContextSwitches() {
    return ((Long)((Pair)this.vmStats.get()).getA()).longValue();
  }
  
  protected long queryInterrupts() {
    return ((Long)((Pair)this.vmStats.get()).getB()).longValue();
  }
  
  private static Pair<Long, Long> queryVmStats() {
    long l1 = 0L;
    long l2 = 0L;
    List list = ExecutingCommand.runNative("vmstat -s");
    for (String str : list) {
      if (str.endsWith("cpu context switches")) {
        l1 = ParseUtil.getFirstIntValue(str);
        continue;
      } 
      if (str.endsWith("interrupts"))
        l2 = ParseUtil.getFirstIntValue(str); 
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  protected long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 40;
    Memory memory = OpenBsdSysctlUtil.sysctl(arrayOfInt);
    try {
      long[] arrayOfLong1 = cpTimeToTicks(memory, false);
      if (arrayOfLong1.length >= 5) {
        arrayOfLong[CentralProcessor.TickType.USER.getIndex()] = arrayOfLong1[0];
        arrayOfLong[CentralProcessor.TickType.NICE.getIndex()] = arrayOfLong1[1];
        arrayOfLong[CentralProcessor.TickType.SYSTEM.getIndex()] = arrayOfLong1[2];
        byte b = (arrayOfLong1.length > 5) ? 1 : 0;
        arrayOfLong[CentralProcessor.TickType.IRQ.getIndex()] = arrayOfLong1[3 + b];
        arrayOfLong[CentralProcessor.TickType.IDLE.getIndex()] = arrayOfLong1[4 + b];
      } 
      if (memory != null)
        memory.close(); 
    } catch (Throwable throwable) {
      if (memory != null)
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  protected long[][] queryProcessorCpuLoadTicks() {
    long[][] arrayOfLong = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 71;
    for (byte b = 0; b < getLogicalProcessorCount(); b++) {
      arrayOfInt[2] = b;
      Memory memory = OpenBsdSysctlUtil.sysctl(arrayOfInt);
      try {
        long[] arrayOfLong1 = cpTimeToTicks(memory, true);
        if (arrayOfLong1.length >= 5) {
          arrayOfLong[b][CentralProcessor.TickType.USER.getIndex()] = arrayOfLong1[0];
          arrayOfLong[b][CentralProcessor.TickType.NICE.getIndex()] = arrayOfLong1[1];
          arrayOfLong[b][CentralProcessor.TickType.SYSTEM.getIndex()] = arrayOfLong1[2];
          byte b1 = (arrayOfLong1.length > 5) ? 1 : 0;
          arrayOfLong[b][CentralProcessor.TickType.IRQ.getIndex()] = arrayOfLong1[3 + b1];
          arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] = arrayOfLong1[4 + b1];
        } 
        if (memory != null)
          memory.close(); 
      } catch (Throwable throwable) {
        if (memory != null)
          try {
            memory.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    return arrayOfLong;
  }
  
  private static long[] cpTimeToTicks(Memory paramMemory, boolean paramBoolean) {
    long l = paramBoolean ? 8L : Native.LONG_SIZE;
    byte b1 = (paramMemory == null) ? 0 : (int)(paramMemory.size() / l);
    if (paramBoolean && paramMemory != null)
      return paramMemory.getLongArray(0L, b1); 
    long[] arrayOfLong = new long[b1];
    for (byte b2 = 0; b2 < b1; b2++)
      arrayOfLong[b2] = paramMemory.getNativeLong(b2 * l).longValue(); 
    return arrayOfLong;
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    int i = OpenBsdLibc.INSTANCE.getloadavg(arrayOfDouble, paramInt);
    if (i < paramInt)
      Arrays.fill(arrayOfDouble, -1.0D); 
    return arrayOfDouble;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */