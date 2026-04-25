package oshi.driver.windows.registry;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinPerf;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.SuppressForbidden;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class HkeyPerformanceDataUtil {
  private static final Logger LOG = LoggerFactory.getLogger(HkeyPerformanceDataUtil.class);
  
  private static final String HKEY_PERFORMANCE_TEXT = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
  
  private static final String COUNTER = "Counter";
  
  private static final Map<String, Integer> COUNTER_INDEX_MAP = mapCounterIndicesFromRegistry();
  
  private static int maxPerfBufferSize = 16384;
  
  public static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Triplet<List<Map<T, Object>>, Long, Long> readPerfDataFromRegistry(String paramString, Class<T> paramClass) {
    Pair<Integer, EnumMap<T, Integer>> pair = getCounterIndices(paramString, paramClass);
    if (pair == null)
      return null; 
    Memory memory = readPerfDataBuffer(paramString);
    try {
      if (memory == null) {
        Triplet triplet = null;
        if (memory != null)
          memory.close(); 
        return triplet;
      } 
      WinPerf.PERF_DATA_BLOCK pERF_DATA_BLOCK = new WinPerf.PERF_DATA_BLOCK(memory.share(0L));
      long l1 = pERF_DATA_BLOCK.PerfTime100nSec.getValue();
      long l2 = WinBase.FILETIME.filetimeToDate((int)(l1 >> 32L), (int)(l1 & 0xFFFFFFFFL)).getTime();
      long l3 = pERF_DATA_BLOCK.HeaderLength;
      for (byte b = 0; b < pERF_DATA_BLOCK.NumObjectTypes; b++) {
        WinPerf.PERF_OBJECT_TYPE pERF_OBJECT_TYPE = new WinPerf.PERF_OBJECT_TYPE(memory.share(l3));
        if (pERF_OBJECT_TYPE.ObjectNameTitleIndex == ((Integer)COUNTER_INDEX_MAP.get(paramString)).intValue()) {
          long l4 = l3 + pERF_OBJECT_TYPE.HeaderLength;
          HashMap<Object, Object> hashMap1 = new HashMap<>();
          HashMap<Object, Object> hashMap2 = new HashMap<>();
          for (byte b1 = 0; b1 < pERF_OBJECT_TYPE.NumCounters; b1++) {
            WinPerf.PERF_COUNTER_DEFINITION pERF_COUNTER_DEFINITION = new WinPerf.PERF_COUNTER_DEFINITION(memory.share(l4));
            hashMap1.put(Integer.valueOf(pERF_COUNTER_DEFINITION.CounterNameTitleIndex), Integer.valueOf(pERF_COUNTER_DEFINITION.CounterOffset));
            hashMap2.put(Integer.valueOf(pERF_COUNTER_DEFINITION.CounterNameTitleIndex), Integer.valueOf(pERF_COUNTER_DEFINITION.CounterSize));
            l4 += pERF_COUNTER_DEFINITION.ByteLength;
          } 
          long l5 = l3 + pERF_OBJECT_TYPE.DefinitionLength;
          ArrayList<EnumMap<T, Object>> arrayList = new ArrayList(pERF_OBJECT_TYPE.NumInstances);
          for (byte b2 = 0; b2 < pERF_OBJECT_TYPE.NumInstances; b2++) {
            WinPerf.PERF_INSTANCE_DEFINITION pERF_INSTANCE_DEFINITION = new WinPerf.PERF_INSTANCE_DEFINITION(memory.share(l5));
            long l = l5 + pERF_INSTANCE_DEFINITION.ByteLength;
            EnumMap<T, Object> enumMap = new EnumMap<>(paramClass);
            Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
            enumMap.put((T)arrayOfEnum[0], memory.getWideString(l5 + pERF_INSTANCE_DEFINITION.NameOffset));
            for (byte b3 = 1; b3 < arrayOfEnum.length; b3++) {
              Enum enum_ = arrayOfEnum[b3];
              int i = ((Integer)COUNTER_INDEX_MAP.get(((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum_).getCounter())).intValue();
              int j = ((Integer)hashMap2.getOrDefault(Integer.valueOf(i), Integer.valueOf(0))).intValue();
              if (j == 4) {
                enumMap.put((T)enum_, Integer.valueOf(memory.getInt(l + ((Integer)hashMap1.get(Integer.valueOf(i))).intValue())));
              } else if (j == 8) {
                enumMap.put((T)enum_, Long.valueOf(memory.getLong(l + ((Integer)hashMap1.get(Integer.valueOf(i))).intValue())));
              } else {
                Triplet triplet1 = null;
                if (memory != null)
                  memory.close(); 
                return triplet1;
              } 
            } 
            arrayList.add(enumMap);
            l5 = l + (new WinPerf.PERF_COUNTER_BLOCK(memory.share(l))).ByteLength;
          } 
          Triplet<List<Map<T, Object>>, Long, Long> triplet = new Triplet(arrayList, Long.valueOf(l1), Long.valueOf(l2));
          if (memory != null)
            memory.close(); 
          return triplet;
        } 
        l3 += pERF_OBJECT_TYPE.TotalByteLength;
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
    return null;
  }
  
  private static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Pair<Integer, EnumMap<T, Integer>> getCounterIndices(String paramString, Class<T> paramClass) {
    if (!COUNTER_INDEX_MAP.containsKey(paramString)) {
      LOG.debug("Couldn't find counter index of {}.", paramString);
      return null;
    } 
    int i = ((Integer)COUNTER_INDEX_MAP.get(paramString)).intValue();
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    EnumMap<T, Object> enumMap = new EnumMap<>(paramClass);
    for (byte b = 1; b < arrayOfEnum.length; b++) {
      Enum enum_ = arrayOfEnum[b];
      String str = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum_).getCounter();
      if (!COUNTER_INDEX_MAP.containsKey(str)) {
        LOG.debug("Couldn't find counter index of {}.", str);
        return null;
      } 
      enumMap.put((T)enum_, COUNTER_INDEX_MAP.get(str));
    } 
    return new Pair(Integer.valueOf(i), enumMap);
  }
  
  private static synchronized Memory readPerfDataBuffer(String paramString) {
    String str = Integer.toString(((Integer)COUNTER_INDEX_MAP.get(paramString)).intValue());
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(maxPerfBufferSize);
    try {
      Memory memory1 = new Memory(maxPerfBufferSize);
      int i = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, str, 0, null, (Pointer)memory1, (IntByReference)closeableIntByReference);
      if (i != 0 && i != 234) {
        LOG.error("Error reading performance data from registry for {}.", paramString);
        memory1.close();
        Memory memory = null;
        closeableIntByReference.close();
        return memory;
      } 
      while (i == 234) {
        maxPerfBufferSize += 8192;
        closeableIntByReference.setValue(maxPerfBufferSize);
        memory1.close();
        memory1 = new Memory(maxPerfBufferSize);
        i = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, str, 0, null, (Pointer)memory1, (IntByReference)closeableIntByReference);
      } 
      Memory memory2 = memory1;
      closeableIntByReference.close();
      return memory2;
    } catch (Throwable throwable) {
      try {
        closeableIntByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  @SuppressForbidden(reason = "Catching the error here")
  private static Map<String, Integer> mapCounterIndicesFromRegistry() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    try {
      String[] arrayOfString = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");
      for (byte b = 1; b < arrayOfString.length; b += 2)
        hashMap.putIfAbsent(arrayOfString[b], Integer.valueOf(Integer.parseInt(arrayOfString[b - 1]))); 
    } catch (Win32Exception win32Exception) {
      LOG.error("Unable to locate English counter names in registry Perflib 009. Counters may need to be rebuilt: ", (Throwable)win32Exception);
    } catch (NumberFormatException numberFormatException) {
      LOG.error("Unable to parse English counter names in registry Perflib 009.");
    } 
    return (Map)Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\HkeyPerformanceDataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */