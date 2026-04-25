package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.PdhUtil;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfCounterQuery {
  private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQuery.class);
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  private static final Set<String> FAILED_QUERY_CACHE = ConcurrentHashMap.newKeySet();
  
  private static final ConcurrentHashMap<String, String> LOCALIZE_CACHE = new ConcurrentHashMap<>();
  
  public static final String TOTAL_INSTANCE = "_Total";
  
  public static final String TOTAL_OR_IDLE_INSTANCES = "_Total|Idle";
  
  public static final String TOTAL_INSTANCES = "*_Total";
  
  public static final String NOT_TOTAL_INSTANCE = "^_Total";
  
  public static final String NOT_TOTAL_INSTANCES = "^*_Total";
  
  public static <T extends Enum<T>> Map<T, Long> queryValues(Class<T> paramClass, String paramString1, String paramString2) {
    if (!FAILED_QUERY_CACHE.contains(paramString1)) {
      Map<T, Long> map = queryValuesFromPDH(paramClass, paramString1);
      if (!map.isEmpty())
        return map; 
      LOG.info("Disabling further attempts to query {}.", paramString1);
      FAILED_QUERY_CACHE.add(paramString1);
    } 
    return queryValuesFromWMI(paramClass, paramString2);
  }
  
  public static <T extends Enum<T>> Map<T, Long> queryValuesFromPDH(Class<T> paramClass, String paramString) {
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    String str = localizeIfNeeded(paramString, false);
    EnumMap<T, Object> enumMap1 = new EnumMap<>(paramClass);
    EnumMap<T, Object> enumMap2 = new EnumMap<>(paramClass);
    PerfCounterQueryHandler perfCounterQueryHandler = new PerfCounterQueryHandler();
    try {
      for (Enum enum_ : arrayOfEnum) {
        PerfDataUtil.PerfCounter perfCounter = PerfDataUtil.createCounter(str, ((PdhCounterProperty)enum_).getInstance(), ((PdhCounterProperty)enum_).getCounter());
        enumMap1.put((T)enum_, perfCounter);
        if (!perfCounterQueryHandler.addCounterToQuery(perfCounter)) {
          EnumMap<T, Object> enumMap = enumMap2;
          perfCounterQueryHandler.close();
          return (Map)enumMap;
        } 
      } 
      if (0L < perfCounterQueryHandler.updateQuery())
        for (Enum enum_ : arrayOfEnum)
          enumMap2.put((T)enum_, Long.valueOf(perfCounterQueryHandler.queryCounter((PerfDataUtil.PerfCounter)enumMap1.get(enum_))));  
      perfCounterQueryHandler.close();
    } catch (Throwable throwable) {
      try {
        perfCounterQueryHandler.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return (Map)enumMap2;
  }
  
  public static <T extends Enum<T>> Map<T, Long> queryValuesFromWMI(Class<T> paramClass, String paramString) {
    WbemcliUtil.WmiQuery<Enum> wmiQuery = new WbemcliUtil.WmiQuery(paramString, paramClass);
    WbemcliUtil.WmiResult<Enum> wmiResult = ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
    EnumMap<T, Object> enumMap = new EnumMap<>(paramClass);
    if (wmiResult.getResultCount() > 0)
      for (Enum enum_ : (Enum[])paramClass.getEnumConstants()) {
        switch (wmiResult.getCIMType(enum_)) {
          case 18:
            enumMap.put((T)enum_, Long.valueOf(WmiUtil.<Enum>getUint16(wmiResult, enum_, 0)));
            break;
          case 19:
            enumMap.put((T)enum_, Long.valueOf(WmiUtil.getUint32asLong(wmiResult, enum_, 0)));
            break;
          case 21:
            enumMap.put((T)enum_, Long.valueOf(WmiUtil.getUint64(wmiResult, enum_, 0)));
            break;
          case 101:
            enumMap.put((T)enum_, Long.valueOf(WmiUtil.<Enum>getDateTime(wmiResult, enum_, 0).toInstant().toEpochMilli()));
            break;
          default:
            throw new ClassCastException("Unimplemented CIM Type Mapping.");
        } 
      }  
    return (Map)enumMap;
  }
  
  public static String localizeIfNeeded(String paramString, boolean paramBoolean) {
    return (!paramBoolean && IS_VISTA_OR_GREATER) ? paramString : LOCALIZE_CACHE.computeIfAbsent(paramString, PerfCounterQuery::localizeUsingPerfIndex);
  }
  
  private static String localizeUsingPerfIndex(String paramString) {
    String str = paramString;
    try {
      str = PdhUtil.PdhLookupPerfNameByIndex(null, PdhUtil.PdhLookupPerfIndexByEnglishName(paramString));
    } catch (Win32Exception win32Exception) {
      LOG.warn("Unable to locate English counter names in registry Perflib 009. Assuming English counters. Error {}. {}", String.format(Locale.ROOT, "0x%x", new Object[] { Integer.valueOf(win32Exception.getHR().intValue()) }), "See https://support.microsoft.com/en-us/help/300956/how-to-manually-rebuild-performance-counter-library-values");
    } catch (com.sun.jna.platform.win32.PdhUtil.PdhException pdhException) {
      LOG.debug("Unable to localize {} performance counter.  Error {}.", paramString, String.format(Locale.ROOT, "0x%x", new Object[] { Integer.valueOf(pdhException.getErrorCode()) }));
    } 
    if (str.isEmpty())
      return paramString; 
    LOG.debug("Localized {} to {}", paramString, str);
    return str;
  }
  
  public static interface PdhCounterProperty {
    String getInstance();
    
    String getCounter();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\PerfCounterQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */