package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.Lshw;
import oshi.driver.linux.proc.CpuInfo;
import oshi.driver.linux.proc.CpuStat;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.linux.ProcPath;
import oshi.util.platform.linux.SysPath;
import oshi.util.tuples.Quartet;

@ThreadSafe
final class LinuxCentralProcessor extends AbstractCentralProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxCentralProcessor.class);
  
  protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    long l = 0L;
    boolean bool = false;
    StringBuilder stringBuilder = new StringBuilder();
    String[] arrayOfString = new String[0];
    List list = FileUtil.readFile(ProcPath.CPUINFO);
    for (String str : list) {
      String[] arrayOfString1 = ParseUtil.whitespacesColonWhitespace.split(str);
      if (arrayOfString1.length < 2) {
        if (str.startsWith("CPU architecture: "))
          str3 = str.replace("CPU architecture: ", "").trim(); 
        continue;
      } 
      switch (arrayOfString1[0].toLowerCase(Locale.ROOT)) {
        case "vendor_id":
        case "cpu implementer":
          str1 = arrayOfString1[1];
        case "model name":
        case "processor":
          if (!arrayOfString1[1].matches("[0-9]+"))
            str2 = arrayOfString1[1]; 
        case "flags":
          arrayOfString = arrayOfString1[1].toLowerCase(Locale.ROOT).split(" ");
          for (String str7 : arrayOfString) {
            if ("lm".equals(str7)) {
              bool = true;
              break;
            } 
          } 
        case "stepping":
          str5 = arrayOfString1[1];
        case "cpu variant":
          if (!stringBuilder.toString().startsWith("r")) {
            int i = ParseUtil.parseLastInt(arrayOfString1[1], 0);
            stringBuilder.insert(0, "r" + i);
          } 
        case "cpu revision":
          if (!stringBuilder.toString().contains("p"))
            stringBuilder.append('p').append(arrayOfString1[1]); 
        case "model":
        case "cpu part":
          str4 = arrayOfString1[1];
        case "cpu family":
          str3 = arrayOfString1[1];
        case "cpu mhz":
          l = ParseUtil.parseHertz(arrayOfString1[1]);
      } 
    } 
    if (str2.isEmpty())
      str2 = FileUtil.getStringFromFile(ProcPath.MODEL).trim(); 
    if (str2.contains("Hz")) {
      l = -1L;
    } else {
      long l1 = Lshw.queryCpuCapacity();
      if (l1 > l)
        l = l1; 
    } 
    if (str5.isEmpty())
      str5 = stringBuilder.toString(); 
    String str6 = getProcessorID(str1, str5, str4, str3, arrayOfString);
    if (str1.startsWith("0x") || str4.isEmpty() || str2.isEmpty()) {
      List list1 = ExecutingCommand.runNative("lscpu");
      for (String str : list1) {
        if (str.startsWith("Architecture:") && str1.startsWith("0x")) {
          str1 = str.replace("Architecture:", "").trim();
          continue;
        } 
        if (str.startsWith("Vendor ID:")) {
          str1 = str.replace("Vendor ID:", "").trim();
          continue;
        } 
        if (str.startsWith("Model name:")) {
          String str7 = str.replace("Model name:", "").trim();
          str4 = str4.isEmpty() ? str7 : str4;
          str2 = str2.isEmpty() ? str7 : str2;
        } 
      } 
    } 
    return new CentralProcessor.ProcessorIdentifier(str1, str2, str3, str4, str5, str6, bool, l);
  }
  
  protected Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> initProcessorCounts() {
    Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.ProcessorCache>, Map<Integer, Integer>, Map<Integer, String>> quartet = LinuxOperatingSystem.HAS_UDEV ? readTopologyFromUdev() : readTopologyFromSysfs();
    if (((List)quartet.getA()).isEmpty())
      quartet = readTopologyFromCpuinfo(); 
    List<CentralProcessor.LogicalProcessor> list = (List)quartet.getA();
    List list1 = (List)quartet.getB();
    Map<Integer, Integer> map = (Map)quartet.getC();
    Map map1 = (Map)quartet.getD();
    if (list.isEmpty())
      list.add(new CentralProcessor.LogicalProcessor(0, 0, 0)); 
    if (map.isEmpty())
      map.put(Integer.valueOf(0), Integer.valueOf(0)); 
    list.sort(Comparator.comparingInt(CentralProcessor.LogicalProcessor::getProcessorNumber));
    List list2 = (List)map.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(paramEntry -> {
          int i = ((Integer)paramEntry.getKey()).intValue() >> 16;
          int j = ((Integer)paramEntry.getKey()).intValue() & 0xFFFF;
          return new CentralProcessor.PhysicalProcessor(i, j, ((Integer)paramEntry.getValue()).intValue(), (String)paramMap.getOrDefault(paramEntry.getKey(), ""));
        }).collect(Collectors.toList());
    List list3 = CpuInfo.queryFeatureFlags();
    return new Quartet(list, list2, list1, list3);
  }
  
  private static Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.ProcessorCache>, Map<Integer, Integer>, Map<Integer, String>> readTopologyFromUdev() {
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("cpu");
        udevEnumerate.scanDevices();
        for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
          String str1 = udevListEntry.getName();
          Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(str1);
          String str2 = null;
          if (udevDevice != null)
            try {
              str2 = udevDevice.getPropertyValue("MODALIAS");
            } finally {
              udevDevice.unref();
            }  
          arrayList.add(getLogicalProcessorFromSyspath(str1, hashSet, str2, (Map)hashMap1, (Map)hashMap2));
        } 
      } finally {
        udevEnumerate.unref();
      } 
    } finally {
      udevContext.unref();
    } 
    return new Quartet(arrayList, orderedProcCaches(hashSet), hashMap1, hashMap2);
  }
  
  private static Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.ProcessorCache>, Map<Integer, Integer>, Map<Integer, String>> readTopologyFromSysfs() {
    ArrayList arrayList = new ArrayList();
    HashSet hashSet = new HashSet();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    try {
      Stream<Path> stream = Files.find(Paths.get(SysPath.CPU, new String[0]), 2147483647, (paramPath, paramBasicFileAttributes) -> paramPath.toFile().getName().matches("cpu\\d+"), new java.nio.file.FileVisitOption[0]);
      try {
        stream.forEach(paramPath -> {
              String str1 = paramPath.toString();
              Map map = FileUtil.getKeyValueMapFromFile(str1 + "/uevent", "=");
              String str2 = (String)map.get("MODALIAS");
              paramList.add(getLogicalProcessorFromSyspath(str1, paramSet, str2, paramMap1, paramMap2));
            });
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {
      LOG.warn("Unable to find CPU information in sysfs at path {}", SysPath.CPU);
    } 
    return new Quartet(arrayList, orderedProcCaches(hashSet), hashMap1, hashMap2);
  }
  
  private static CentralProcessor.LogicalProcessor getLogicalProcessorFromSyspath(String paramString1, Set<CentralProcessor.ProcessorCache> paramSet, String paramString2, Map<Integer, Integer> paramMap, Map<Integer, String> paramMap1) {
    int i = ParseUtil.getFirstIntValue(paramString1);
    int j = FileUtil.getIntFromFile(paramString1 + "/topology/core_id");
    int k = FileUtil.getIntFromFile(paramString1 + "/topology/physical_package_id");
    int m = (k << 16) + j;
    paramMap.put(Integer.valueOf(m), Integer.valueOf(FileUtil.getIntFromFile(paramString1 + "/cpu_capacity")));
    if (!Util.isBlank(paramString2))
      paramMap1.put(Integer.valueOf(m), paramString2); 
    int n = 0;
    String str1 = paramString1 + "/node";
    try {
      Stream<Path> stream = Files.list(Paths.get(paramString1, new String[0]));
      try {
        Optional<Path> optional = stream.filter(paramPath -> paramPath.toString().startsWith(paramString)).findFirst();
        if (optional.isPresent())
          n = ParseUtil.getFirstIntValue(((Path)optional.get()).getFileName().toString()); 
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {}
    String str2 = paramString1 + "/cache";
    String str3 = str2 + "/index";
    try {
      Stream<Path> stream = Files.list(Paths.get(str2, new String[0]));
      try {
        stream.filter(paramPath -> paramPath.toString().startsWith(paramString)).forEach(paramPath -> {
              int i = FileUtil.getIntFromFile(paramPath + "/level");
              CentralProcessor.ProcessorCache.Type type = parseCacheType(FileUtil.getStringFromFile(paramPath + "/type"));
              int j = FileUtil.getIntFromFile(paramPath + "/ways_of_associativity");
              int k = FileUtil.getIntFromFile(paramPath + "/coherency_line_size");
              long l = ParseUtil.parseDecimalMemorySizeToBinary(FileUtil.getStringFromFile(paramPath + "/size"));
              paramSet.add(new CentralProcessor.ProcessorCache(i, j, k, l, type));
            });
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {}
    return new CentralProcessor.LogicalProcessor(i, j, k, n);
  }
  
  private static CentralProcessor.ProcessorCache.Type parseCacheType(String paramString) {
    try {
      return CentralProcessor.ProcessorCache.Type.valueOf(paramString.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException illegalArgumentException) {
      return CentralProcessor.ProcessorCache.Type.UNIFIED;
    } 
  }
  
  private static Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.ProcessorCache>, Map<Integer, Integer>, Map<Integer, String>> readTopologyFromCpuinfo() {
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    Set<CentralProcessor.ProcessorCache> set = mapCachesFromLscpu();
    Map<Integer, Integer> map = mapNumaNodesFromLscpu();
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = FileUtil.readFile(ProcPath.CPUINFO);
    int i = 0;
    int j = 0;
    int k = 0;
    boolean bool = true;
    for (String str : list) {
      if (str.startsWith("processor")) {
        if (bool) {
          bool = false;
        } else {
          arrayList.add(new CentralProcessor.LogicalProcessor(i, j, k, ((Integer)map.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue()));
          hashMap.put(Integer.valueOf((k << 16) + j), Integer.valueOf(0));
        } 
        i = ParseUtil.parseLastInt(str, 0);
        continue;
      } 
      if (str.startsWith("core id") || str.startsWith("cpu number")) {
        j = ParseUtil.parseLastInt(str, 0);
        continue;
      } 
      if (str.startsWith("physical id"))
        k = ParseUtil.parseLastInt(str, 0); 
    } 
    arrayList.add(new CentralProcessor.LogicalProcessor(i, j, k, ((Integer)map.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue()));
    hashMap.put(Integer.valueOf((k << 16) + j), Integer.valueOf(0));
    return new Quartet(arrayList, orderedProcCaches(set), hashMap, Collections.emptyMap());
  }
  
  private static Map<Integer, Integer> mapNumaNodesFromLscpu() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = ExecutingCommand.runNative("lscpu -p=cpu,node");
    for (String str : list) {
      if (!str.startsWith("#")) {
        int i = str.indexOf(',');
        if (i > 0 && i < str.length())
          hashMap.put(Integer.valueOf(ParseUtil.parseIntOrDefault(str.substring(0, i), 0)), Integer.valueOf(ParseUtil.parseIntOrDefault(str.substring(i + 1), 0))); 
      } 
    } 
    return (Map)hashMap;
  }
  
  private static Set<CentralProcessor.ProcessorCache> mapCachesFromLscpu() {
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    int i = 0;
    CentralProcessor.ProcessorCache.Type type = null;
    int j = 0;
    int k = 0;
    long l = 0L;
    List list = ExecutingCommand.runNative("lscpu -B -C --json");
    for (String str1 : list) {
      String str2 = str1.trim();
      if (str2.startsWith("}")) {
        if (i && type != null)
          hashSet.add(new CentralProcessor.ProcessorCache(i, j, k, l, type)); 
        i = 0;
        type = null;
        j = 0;
        k = 0;
        l = 0L;
        continue;
      } 
      if (str2.contains("one-size")) {
        String[] arrayOfString = ParseUtil.notDigits.split(str2);
        if (arrayOfString.length > 1)
          l = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L); 
        continue;
      } 
      if (str2.contains("ways")) {
        String[] arrayOfString = ParseUtil.notDigits.split(str2);
        if (arrayOfString.length > 1)
          j = ParseUtil.parseIntOrDefault(arrayOfString[1], 0); 
        continue;
      } 
      if (str2.contains("type")) {
        String[] arrayOfString = str2.split("\"");
        if (arrayOfString.length > 2)
          type = parseCacheType(arrayOfString[arrayOfString.length - 2]); 
        continue;
      } 
      if (str2.contains("level")) {
        String[] arrayOfString = ParseUtil.notDigits.split(str2);
        if (arrayOfString.length > 1)
          i = ParseUtil.parseIntOrDefault(arrayOfString[1], 0); 
        continue;
      } 
      if (str2.contains("coherency-size")) {
        String[] arrayOfString = ParseUtil.notDigits.split(str2);
        if (arrayOfString.length > 1)
          k = ParseUtil.parseIntOrDefault(arrayOfString[1], 0); 
      } 
    } 
    return hashSet;
  }
  
  public long[] querySystemCpuLoadTicks() {
    long[] arrayOfLong = CpuStat.getSystemCpuLoadTicks();
    if (LongStream.of(arrayOfLong).sum() == 0L)
      arrayOfLong = CpuStat.getSystemCpuLoadTicks(); 
    long l = LinuxOperatingSystem.getHz();
    for (byte b = 0; b < arrayOfLong.length; b++)
      arrayOfLong[b] = arrayOfLong[b] * 1000L / l; 
    return arrayOfLong;
  }
  
  public long[] queryCurrentFreq() {
    long[] arrayOfLong = new long[getLogicalProcessorCount()];
    long l = 0L;
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("cpu");
        udevEnumerate.scanDevices();
        for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
          String str = udevListEntry.getName();
          int i = ParseUtil.getFirstIntValue(str);
          if (i >= 0 && i < arrayOfLong.length) {
            arrayOfLong[i] = FileUtil.getLongFromFile(str + "/cpufreq/scaling_cur_freq");
            if (arrayOfLong[i] == 0L)
              arrayOfLong[i] = FileUtil.getLongFromFile(str + "/cpufreq/cpuinfo_cur_freq"); 
          } 
          if (l < arrayOfLong[i])
            l = arrayOfLong[i]; 
        } 
        if (l > 0L) {
          for (byte b1 = 0; b1 < arrayOfLong.length; b1++)
            arrayOfLong[b1] = arrayOfLong[b1] * 1000L; 
          return arrayOfLong;
        } 
      } finally {
        udevEnumerate.unref();
      } 
    } finally {
      udevContext.unref();
    } 
    Arrays.fill(arrayOfLong, -1L);
    List list = FileUtil.readFile(ProcPath.CPUINFO);
    byte b = 0;
    for (String str : list) {
      if (str.toLowerCase(Locale.ROOT).contains("cpu mhz")) {
        arrayOfLong[b] = Math.round(ParseUtil.parseLastDouble(str, 0.0D) * 1000000.0D);
        if (++b >= arrayOfLong.length)
          break; 
      } 
    } 
    return arrayOfLong;
  }
  
  public long queryMaxFreq() {
    long l1 = -1L;
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("cpu");
        udevEnumerate.scanDevices();
        Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry();
        if (udevListEntry != null) {
          String str1 = udevListEntry.getName();
          String str2 = str1.substring(0, str1.lastIndexOf(File.separatorChar)) + "/cpufreq";
          String str3 = str2 + "/policy";
          try {
            Stream<Path> stream = Files.list(Paths.get(str2, new String[0]));
            try {
              Optional<Long> optional = stream.filter(paramPath -> paramPath.toString().startsWith(paramString)).map(paramPath -> {
                    long l = FileUtil.getLongFromFile(paramPath.toString() + "/scaling_max_freq");
                    if (l == 0L)
                      l = FileUtil.getLongFromFile(paramPath.toString() + "/cpuinfo_max_freq"); 
                    return Long.valueOf(l);
                  }).max(Long::compare);
              if (optional.isPresent())
                l1 = ((Long)optional.get()).longValue() * 1000L; 
              if (stream != null)
                stream.close(); 
            } catch (Throwable throwable) {
              if (stream != null)
                try {
                  stream.close();
                } catch (Throwable throwable1) {
                  throwable.addSuppressed(throwable1);
                }  
              throw throwable;
            } 
          } catch (IOException iOException) {}
        } 
      } finally {
        udevEnumerate.unref();
      } 
    } finally {
      udevContext.unref();
    } 
    long l2 = Lshw.queryCpuCapacity();
    return LongStream.concat(LongStream.of(new long[] { l1, l2 }, ), Arrays.stream(getCurrentFreq())).max().orElse(-1L);
  }
  
  public double[] getSystemLoadAverage(int paramInt) {
    if (paramInt < 1 || paramInt > 3)
      throw new IllegalArgumentException("Must include from one to three elements."); 
    double[] arrayOfDouble = new double[paramInt];
    int i = LinuxLibc.INSTANCE.getloadavg(arrayOfDouble, paramInt);
    if (i < paramInt)
      for (int j = Math.max(i, 0); j < arrayOfDouble.length; j++)
        arrayOfDouble[j] = -1.0D;  
    return arrayOfDouble;
  }
  
  public long[][] queryProcessorCpuLoadTicks() {
    long[][] arrayOfLong = CpuStat.getProcessorCpuLoadTicks(getLogicalProcessorCount());
    if (LongStream.of(arrayOfLong[0]).sum() == 0L)
      arrayOfLong = CpuStat.getProcessorCpuLoadTicks(getLogicalProcessorCount()); 
    long l = LinuxOperatingSystem.getHz();
    for (byte b = 0; b < arrayOfLong.length; b++) {
      for (byte b1 = 0; b1 < (arrayOfLong[b]).length; b1++)
        arrayOfLong[b][b1] = arrayOfLong[b][b1] * 1000L / l; 
    } 
    return arrayOfLong;
  }
  
  private static String getProcessorID(String paramString1, String paramString2, String paramString3, String paramString4, String[] paramArrayOfString) {
    boolean bool = false;
    String str = "Processor Information";
    for (String str1 : ExecutingCommand.runNative("dmidecode -t 4")) {
      if (!bool && str1.contains(str)) {
        str = "ID:";
        bool = true;
        continue;
      } 
      if (bool && str1.contains(str))
        return str1.split(str)[1].trim(); 
    } 
    str = "eax=";
    for (String str1 : ExecutingCommand.runNative("cpuid -1r")) {
      if (str1.contains(str) && str1.trim().startsWith("0x00000001")) {
        String str2 = "";
        String str3 = "";
        for (String str4 : ParseUtil.whitespaces.split(str1)) {
          if (str4.startsWith("eax=")) {
            str2 = ParseUtil.removeMatchingString(str4, "eax=0x");
          } else if (str4.startsWith("edx=")) {
            str3 = ParseUtil.removeMatchingString(str4, "edx=0x");
          } 
        } 
        return str3 + str2;
      } 
    } 
    return paramString1.startsWith("0x") ? (createMIDR(paramString1, paramString2, paramString3, paramString4) + "00000000") : createProcessorID(paramString2, paramString3, paramString4, paramArrayOfString);
  }
  
  private static String createMIDR(String paramString1, String paramString2, String paramString3, String paramString4) {
    int i = 0;
    if (paramString2.startsWith("r") && paramString2.contains("p")) {
      String[] arrayOfString = paramString2.substring(1).split("p");
      i |= ParseUtil.parseLastInt(arrayOfString[1], 0);
      i |= ParseUtil.parseLastInt(arrayOfString[0], 0) << 20;
    } 
    i |= ParseUtil.parseLastInt(paramString3, 0) << 4;
    i |= ParseUtil.parseLastInt(paramString4, 0) << 16;
    i |= ParseUtil.parseLastInt(paramString1, 0) << 24;
    return String.format(Locale.ROOT, "%08X", new Object[] { Integer.valueOf(i) });
  }
  
  public long queryContextSwitches() {
    return CpuStat.getContextSwitches();
  }
  
  public long queryInterrupts() {
    return CpuStat.getInterrupts();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */