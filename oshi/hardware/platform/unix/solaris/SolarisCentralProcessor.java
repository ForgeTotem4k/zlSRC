package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Quartet;

@ThreadSafe
final class SolarisCentralProcessor extends AbstractCentralProcessor {
  private static final String KSTAT_SYSTEM_CPU = "kstat:/system/cpu/";
  
  private static final String INFO = "/info";
  
  private static final String SYS = "/sys";
  
  private static final String KSTAT_PM_CPU = "kstat:/pm/cpu/";
  
  private static final String PSTATE = "/pstate";
  
  private static final String CPU_INFO = "cpu_info";
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    boolean bool = "64".equals(ExecutingCommand.getFirstAnswer("isainfo -b").trim());
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryProcessorId2(bool); 
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    long l = 0L;
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup("cpu_info", -1, null);
      if (kstat != null && kstatChain.read(kstat)) {
        str1 = KstatUtil.dataLookupString(kstat, "vendor_id");
        str2 = KstatUtil.dataLookupString(kstat, "brand");
        str3 = KstatUtil.dataLookupString(kstat, "family");
        str4 = KstatUtil.dataLookupString(kstat, "model");
        str5 = KstatUtil.dataLookupString(kstat, "stepping");
        l = KstatUtil.dataLookupLong(kstat, "clock_MHz") * 1000000L;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    String str6 = getProcessorID(str5, str4, str3);
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str4, str5, str6, bool, l);
  }
  
  private static CentralProcessor.ProcessorIdentifier queryProcessorId2(boolean paramBoolean) {
    Object[] arrayOfObject = KstatUtil.queryKstat2("kstat:/system/cpu/0/info", new String[] { "vendor_id", "brand", "family", "model", "stepping", "clock_MHz" });
    String str1 = (arrayOfObject[0] == null) ? "" : (String)arrayOfObject[0];
    String str2 = (arrayOfObject[1] == null) ? "" : (String)arrayOfObject[1];
    String str3 = (arrayOfObject[2] == null) ? "" : arrayOfObject[2].toString();
    String str4 = (arrayOfObject[3] == null) ? "" : arrayOfObject[3].toString();
    String str5 = (arrayOfObject[4] == null) ? "" : arrayOfObject[4].toString();
    long l = (arrayOfObject[5] == null) ? 0L : ((Long)arrayOfObject[5]).longValue();
    String str6 = getProcessorID(str5, str4, str3);
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str4, str5, str6, paramBoolean, l);
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    List<CentralProcessor.LogicalProcessor> list;
    Map<Integer, Integer> map = mapNumaNodes();
    if (SolarisOperatingSystem.HAS_KSTAT2) {
      list = initProcessorCounts2(map);
    } else {
      list = new ArrayList();
      KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
      try {
        List list2 = kstatChain.lookupAll("cpu_info", -1, null);
        for (LibKstat.Kstat kstat : list2) {
          if (kstat != null && kstatChain.read(kstat)) {
            int i = list.size();
            String str1 = KstatUtil.dataLookupString(kstat, "chip_id");
            String str2 = KstatUtil.dataLookupString(kstat, "core_id");
            CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(i, ParseUtil.parseIntOrDefault(str2, 0), ParseUtil.parseIntOrDefault(str1, 0), ((Integer)map.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue());
            list.add(logicalProcessor);
          } 
        } 
        if (kstatChain != null)
          kstatChain.close(); 
      } catch (Throwable throwable) {
        if (kstatChain != null)
          try {
            kstatChain.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    if (list.isEmpty())
      list.add(new CentralProcessor.LogicalProcessor(0, 0, 0)); 
    HashMap<Object, Object> hashMap = new HashMap<>();
    Pattern pattern = Pattern.compile(".* cpu(\\\\d+): ((ARM|AMD|Intel).+)");
    for (String str : ExecutingCommand.runNative("dmesg")) {
      Matcher matcher = pattern.matcher(str);
      if (matcher.matches()) {
        int i = ParseUtil.parseIntOrDefault(matcher.group(1), 0);
        hashMap.put(Integer.valueOf(i), matcher.group(2).trim());
      } 
    } 
    if (hashMap.isEmpty())
      return new Quartet(list, null, null, Collections.emptyList()); 
    List list1 = ExecutingCommand.runNative("isainfo -x");
    return new Quartet(list, createProcListFromDmesg(list, hashMap), null, list1);
  }
  
  private static List<CentralProcessor.LogicalProcessor> initProcessorCounts2(Map<Integer, Integer> paramMap) {
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    List list = KstatUtil.queryKstat2List("kstat:/system/cpu/", "/info", new String[] { "chip_id", "core_id" });
    for (Object[] arrayOfObject : list) {
      int i = arrayList.size();
      long l1 = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
      long l2 = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
      CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(i, (int)l2, (int)l1, ((Integer)paramMap.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue());
      arrayList.add(logicalProcessor);
    } 
    if (arrayList.isEmpty())
      arrayList.add(new CentralProcessor.LogicalProcessor(0, 0, 0)); 
    return arrayList;
  }
  
  private static Map<Integer, Integer> mapNumaNodes() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    int i = 0;
    for (String str : ExecutingCommand.runNative("lgrpinfo -c leaves")) {
      if (str.startsWith("lgroup")) {
        i = ParseUtil.getFirstIntValue(str);
        continue;
      } 
      if (str.contains("CPUs:") || str.contains("CPU:"))
        for (Integer integer : ParseUtil.parseHyphenatedIntList(str.split(":")[1]))
          hashMap.put(integer, Integer.valueOf(i));  
    } 
    return (Map)hashMap;
  }
  
  public long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = new long[(CentralProcessor.TickType.values()).length];
    long[][] arrayOfLong1 = getProcessorCpuLoadTicks();
    for (byte b = 0; b < arrayOfLong.length; b++) {
      for (long[] arrayOfLong2 : arrayOfLong1)
        arrayOfLong[b] = arrayOfLong[b] + arrayOfLong2[b]; 
      arrayOfLong[b] = arrayOfLong[b] / arrayOfLong1.length;
    } 
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryCurrentFreq2(getLogicalProcessorCount()); 
    long[] arrayOfLong = new long[getLogicalProcessorCount()];
    Arrays.fill(arrayOfLong, -1L);
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      for (byte b = 0; b < arrayOfLong.length; b++) {
        for (LibKstat.Kstat kstat : kstatChain.lookupAll("cpu_info", b, null)) {
          if (kstat != null && kstatChain.read(kstat))
            arrayOfLong[b] = KstatUtil.dataLookupLong(kstat, "current_clock_Hz"); 
        } 
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  private static long[] queryCurrentFreq2(int paramInt) {
    long[] arrayOfLong = new long[paramInt];
    Arrays.fill(arrayOfLong, -1L);
    List list = KstatUtil.queryKstat2List("kstat:/system/cpu/", "/info", new String[] { "current_clock_Hz" });
    byte b = -1;
    for (Object[] arrayOfObject : list) {
      if (++b >= arrayOfLong.length)
        break; 
      arrayOfLong[b] = (arrayOfObject[0] == null) ? -1L : ((Long)arrayOfObject[0]).longValue();
    } 
    return arrayOfLong;
  }
  
  public long queryMaxFreq() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryMaxFreq2(); 
    long l = -1L;
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      for (LibKstat.Kstat kstat : kstatChain.lookupAll("cpu_info", 0, null)) {
        if (kstatChain.read(kstat)) {
          String str = KstatUtil.dataLookupString(kstat, "supported_frequencies_Hz");
          if (!str.isEmpty())
            for (String str1 : str.split(":")) {
              long l1 = ParseUtil.parseLongOrDefault(str1, -1L);
              if (l < l1)
                l = l1; 
            }  
        } 
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return l;
  }
  
  private static long queryMaxFreq2() {
    long l = -1L;
    List list = KstatUtil.queryKstat2List("kstat:/pm/cpu/", "/pstate", new String[] { "supported_frequencies" });
    for (Object[] arrayOfObject : list) {
      for (long l1 : (arrayOfObject[0] == null) ? new long[0] : (long[])arrayOfObject[0]) {
        if (l1 > l)
          l = l1; 
      } 
    } 
    return l;
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    int i = SolarisLibc.INSTANCE.getloadavg(arrayOfDouble, paramInt);
    if (i < paramInt)
      for (int j = Math.max(i, 0); j < arrayOfDouble.length; j++)
        arrayOfDouble[j] = -1.0D;  
    return arrayOfDouble;
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryProcessorCpuLoadTicks2(getLogicalProcessorCount()); 
    long[][] arrayOfLong = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
    byte b = -1;
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      for (LibKstat.Kstat kstat : kstatChain.lookupAll("cpu", -1, "sys")) {
        if (++b >= arrayOfLong.length)
          break; 
        if (kstatChain.read(kstat)) {
          arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_idle");
          arrayOfLong[b][CentralProcessor.TickType.SYSTEM.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_kernel");
          arrayOfLong[b][CentralProcessor.TickType.USER.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_user");
        } 
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return arrayOfLong;
  }
  
  private static long[][] queryProcessorCpuLoadTicks2(int paramInt) {
    long[][] arrayOfLong = new long[paramInt][(CentralProcessor.TickType.values()).length];
    List list = KstatUtil.queryKstat2List("kstat:/system/cpu/", "/sys", new String[] { "cpu_ticks_idle", "cpu_ticks_kernel", "cpu_ticks_user" });
    byte b = -1;
    for (Object[] arrayOfObject : list) {
      if (++b >= arrayOfLong.length)
        break; 
      arrayOfLong[b][CentralProcessor.TickType.IDLE.getIndex()] = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
      arrayOfLong[b][CentralProcessor.TickType.SYSTEM.getIndex()] = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
      arrayOfLong[b][CentralProcessor.TickType.USER.getIndex()] = (arrayOfObject[2] == null) ? 0L : ((Long)arrayOfObject[2]).longValue();
    } 
    return arrayOfLong;
  }
  
  private static String getProcessorID(String paramString1, String paramString2, String paramString3) {
    List list = ExecutingCommand.runNative("isainfo -v");
    StringBuilder stringBuilder = new StringBuilder();
    for (String str : list) {
      if (str.startsWith("32-bit"))
        break; 
      if (!str.startsWith("64-bit"))
        stringBuilder.append(' ').append(str.trim()); 
    } 
    return createProcessorID(paramString1, paramString2, paramString3, ParseUtil.whitespaces.split(stringBuilder.toString().toLowerCase(Locale.ROOT)));
  }
  
  public long queryContextSwitches() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryContextSwitches2(); 
    long l = 0L;
    List list = ExecutingCommand.runNative("kstat -p cpu_stat:::/pswitch\\\\|inv_swtch/");
    for (String str : list)
      l += ParseUtil.parseLastLong(str, 0L); 
    return l;
  }
  
  private static long queryContextSwitches2() {
    long l = 0L;
    List list = KstatUtil.queryKstat2List("kstat:/system/cpu/", "/sys", new String[] { "pswitch", "inv_swtch" });
    for (Object[] arrayOfObject : list) {
      l += (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
      l += (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
    } 
    return l;
  }
  
  public long queryInterrupts() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryInterrupts2(); 
    long l = 0L;
    List list = ExecutingCommand.runNative("kstat -p cpu_stat:::/intr/");
    for (String str : list)
      l += ParseUtil.parseLastLong(str, 0L); 
    return l;
  }
  
  private static long queryInterrupts2() {
    long l = 0L;
    List list = KstatUtil.queryKstat2List("kstat:/system/cpu/", "/sys", new String[] { "intr" });
    for (Object[] arrayOfObject : list)
      l += (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue(); 
    return l;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */