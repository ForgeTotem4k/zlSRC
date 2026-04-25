package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.PdhUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;
import oshi.util.Util;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class PerfCounterWildcardQuery {
  private static final Logger LOG = LoggerFactory.getLogger(PerfCounterWildcardQuery.class);
  
  private static final boolean PERF_DISABLE_ALL_ON_FAILURE = GlobalConfig.get("oshi.os.windows.perf.disable.all.on.failure", false);
  
  private static final Set<String> FAILED_QUERY_CACHE = ConcurrentHashMap.newKeySet();
  
  public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValues(Class<T> paramClass, String paramString1, String paramString2) {
    return queryInstancesAndValues(paramClass, paramString1, paramString2, null);
  }
  
  public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValues(Class<T> paramClass, String paramString1, String paramString2, String paramString3) {
    if (FAILED_QUERY_CACHE.isEmpty() || (!PERF_DISABLE_ALL_ON_FAILURE && !FAILED_QUERY_CACHE.contains(paramString1))) {
      Pair<List<String>, Map<T, List<Long>>> pair = queryInstancesAndValuesFromPDH(paramClass, paramString1, paramString3);
      if (!((List)pair.getA()).isEmpty())
        return pair; 
      if (Util.isBlank(paramString3)) {
        if (PERF_DISABLE_ALL_ON_FAILURE) {
          LOG.info("Disabling further attempts to query performance counters.");
        } else {
          LOG.info("Disabling further attempts to query {}.", paramString1);
        } 
        FAILED_QUERY_CACHE.add(paramString1);
      } 
    } 
    return queryInstancesAndValuesFromWMI(paramClass, paramString2);
  }
  
  public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromPDH(Class<T> paramClass, String paramString) {
    return queryInstancesAndValuesFromPDH(paramClass, paramString, null);
  }
  
  public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromPDH(Class<T> paramClass, String paramString1, String paramString2) {
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    if (arrayOfEnum.length < 2)
      throw new IllegalArgumentException("Enum " + paramClass.getName() + " must have at least two elements, an instance filter and a counter."); 
    String str1 = Util.isBlank(paramString2) ? ((PdhCounterWildcardProperty)((Enum[])paramClass.getEnumConstants())[0]).getCounter().toLowerCase(Locale.ROOT) : paramString2;
    String str2 = PerfCounterQuery.localizeIfNeeded(paramString1, true);
    PdhUtil.PdhEnumObjectItems pdhEnumObjectItems = null;
    try {
      pdhEnumObjectItems = PdhUtil.PdhEnumObjectItems(null, null, str2, 100);
    } catch (com.sun.jna.platform.win32.PdhUtil.PdhException pdhException) {
      LOG.warn("Failed to locate performance object for {} in the registry. Performance counters may be corrupt. {}", str2, pdhException.getMessage());
    } 
    if (pdhEnumObjectItems == null)
      return new Pair(Collections.emptyList(), Collections.emptyMap()); 
    List list = pdhEnumObjectItems.getInstances();
    list.removeIf(paramString2 -> !Util.wildcardMatch(paramString2.toLowerCase(Locale.ROOT), paramString1));
    EnumMap<T, Object> enumMap = new EnumMap<>(paramClass);
    PerfCounterQueryHandler perfCounterQueryHandler = new PerfCounterQueryHandler();
    try {
      EnumMap<T, Object> enumMap1 = new EnumMap<>(paramClass);
      byte b;
      for (b = 1; b < arrayOfEnum.length; b++) {
        Enum enum_ = arrayOfEnum[b];
        ArrayList<PerfDataUtil.PerfCounter> arrayList = new ArrayList(list.size());
        for (String str : list) {
          PerfDataUtil.PerfCounter perfCounter = PerfDataUtil.createCounter(paramString1, str, ((PdhCounterWildcardProperty)enum_).getCounter());
          if (!perfCounterQueryHandler.addCounterToQuery(perfCounter)) {
            Pair<List<String>, Map<T, List<Long>>> pair = new Pair(Collections.emptyList(), Collections.emptyMap());
            perfCounterQueryHandler.close();
            return pair;
          } 
          arrayList.add(perfCounter);
        } 
        enumMap1.put((T)enum_, arrayList);
      } 
      if (0L < perfCounterQueryHandler.updateQuery())
        for (b = 1; b < arrayOfEnum.length; b++) {
          Enum enum_ = arrayOfEnum[b];
          ArrayList<Long> arrayList = new ArrayList();
          for (PerfDataUtil.PerfCounter perfCounter : enumMap1.get(enum_))
            arrayList.add(Long.valueOf(perfCounterQueryHandler.queryCounter(perfCounter))); 
          enumMap.put((T)enum_, arrayList);
        }  
      perfCounterQueryHandler.close();
    } catch (Throwable throwable) {
      try {
        perfCounterQueryHandler.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return new Pair(list, enumMap);
  }
  
  public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromWMI(Class<T> paramClass, String paramString) {
    ArrayList<String> arrayList = new ArrayList();
    EnumMap<T, Object> enumMap = new EnumMap<>(paramClass);
    WbemcliUtil.WmiQuery<Enum> wmiQuery = new WbemcliUtil.WmiQuery(paramString, paramClass);
    WbemcliUtil.WmiResult<Enum> wmiResult = ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
    if (wmiResult.getResultCount() > 0)
      for (Enum enum_ : (Enum[])paramClass.getEnumConstants()) {
        if (enum_.ordinal() == 0) {
          for (byte b = 0; b < wmiResult.getResultCount(); b++)
            arrayList.add(WmiUtil.getString(wmiResult, enum_, b)); 
        } else {
          ArrayList<Long> arrayList1 = new ArrayList();
          for (byte b = 0; b < wmiResult.getResultCount(); b++) {
            switch (wmiResult.getCIMType(enum_)) {
              case 18:
                arrayList1.add(Long.valueOf(WmiUtil.<Enum>getUint16(wmiResult, enum_, b)));
                break;
              case 19:
                arrayList1.add(Long.valueOf(WmiUtil.getUint32asLong(wmiResult, enum_, b)));
                break;
              case 21:
                arrayList1.add(Long.valueOf(WmiUtil.getUint64(wmiResult, enum_, b)));
                break;
              case 101:
                arrayList1.add(Long.valueOf(WmiUtil.<Enum>getDateTime(wmiResult, enum_, b).toInstant().toEpochMilli()));
                break;
              default:
                throw new ClassCastException("Unimplemented CIM Type Mapping.");
            } 
          } 
          enumMap.put((T)enum_, arrayList1);
        } 
      }  
    return new Pair(arrayList, enumMap);
  }
  
  public static interface PdhCounterWildcardProperty {
    String getCounter();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\PerfCounterWildcardQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */