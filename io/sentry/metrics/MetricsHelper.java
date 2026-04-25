package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class MetricsHelper {
  public static final long FLUSHER_SLEEP_TIME_MS = 5000L;
  
  public static final int MAX_TOTAL_WEIGHT = 100000;
  
  private static final int ROLLUP_IN_SECONDS = 10;
  
  private static final Pattern UNIT_PATTERN = Pattern.compile("\\W+");
  
  private static final Pattern NAME_PATTERN = Pattern.compile("[^\\w\\-.]+");
  
  private static final Pattern TAG_KEY_PATTERN = Pattern.compile("[^\\w\\-./]+");
  
  private static final char TAGS_PAIR_DELIMITER = ',';
  
  private static final char TAGS_KEY_VALUE_DELIMITER = '=';
  
  private static final char TAGS_ESCAPE_CHAR = '\\';
  
  private static long FLUSH_SHIFT_MS = (long)((new SecureRandom()).nextFloat() * 10000.0F);
  
  public static long getTimeBucketKey(long paramLong) {
    long l1 = paramLong / 1000L;
    long l2 = l1 / 10L * 10L;
    return (paramLong >= 0L) ? l2 : (l2 - 1L);
  }
  
  public static long getCutoffTimestampMs(long paramLong) {
    return paramLong - 10000L - FLUSH_SHIFT_MS;
  }
  
  @NotNull
  public static String sanitizeUnit(@NotNull String paramString) {
    return UNIT_PATTERN.matcher(paramString).replaceAll("");
  }
  
  @NotNull
  public static String sanitizeName(@NotNull String paramString) {
    return NAME_PATTERN.matcher(paramString).replaceAll("_");
  }
  
  @NotNull
  public static String sanitizeTagKey(@NotNull String paramString) {
    return TAG_KEY_PATTERN.matcher(paramString).replaceAll("");
  }
  
  @NotNull
  public static String sanitizeTagValue(@NotNull String paramString) {
    StringBuilder stringBuilder = new StringBuilder(paramString.length());
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '\n') {
        stringBuilder.append("\\n");
      } else if (c == '\r') {
        stringBuilder.append("\\r");
      } else if (c == '\t') {
        stringBuilder.append("\\t");
      } else if (c == '\\') {
        stringBuilder.append("\\\\");
      } else if (c == '|') {
        stringBuilder.append("\\u{7c}");
      } else if (c == ',') {
        stringBuilder.append("\\u{2c}");
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  @NotNull
  public static String getMetricBucketKey(@NotNull MetricType paramMetricType, @NotNull String paramString, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    String str1 = paramMetricType.statsdCode;
    String str2 = getTagsKey(paramMap);
    String str3 = getUnitName(paramMeasurementUnit);
    return String.format("%s_%s_%s_%s", new Object[] { str1, paramString, str3, str2 });
  }
  
  @NotNull
  private static String getUnitName(@Nullable MeasurementUnit paramMeasurementUnit) {
    return (paramMeasurementUnit != null) ? paramMeasurementUnit.apiName() : "none";
  }
  
  @NotNull
  private static String getTagsKey(@Nullable Map<String, String> paramMap) {
    if (paramMap == null || paramMap.isEmpty())
      return ""; 
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
      String str1 = escapeString((String)entry.getKey());
      String str2 = escapeString((String)entry.getValue());
      if (stringBuilder.length() > 0)
        stringBuilder.append(','); 
      stringBuilder.append(str1).append('=').append(str2);
    } 
    return stringBuilder.toString();
  }
  
  @NotNull
  private static String escapeString(@NotNull String paramString) {
    StringBuilder stringBuilder = new StringBuilder(paramString.length());
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == ',' || c == '=')
        stringBuilder.append('\\'); 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  @NotNull
  public static String getExportKey(@NotNull MetricType paramMetricType, @NotNull String paramString, @Nullable MeasurementUnit paramMeasurementUnit) {
    String str = getUnitName(paramMeasurementUnit);
    return String.format("%s:%s@%s", new Object[] { paramMetricType.statsdCode, paramString, str });
  }
  
  public static double convertNanosTo(@NotNull MeasurementUnit.Duration paramDuration, long paramLong) {
    switch (paramDuration) {
      case NANOSECOND:
        return paramLong;
      case MICROSECOND:
        return paramLong / 1000.0D;
      case MILLISECOND:
        return paramLong / 1000000.0D;
      case SECOND:
        return paramLong / 1.0E9D;
      case MINUTE:
        return paramLong / 6.0E10D;
      case HOUR:
        return paramLong / 3.6E12D;
      case DAY:
        return paramLong / 8.64E13D;
      case WEEK:
        return paramLong / 8.64E13D / 7.0D;
    } 
    throw new IllegalArgumentException("Unknown Duration unit: " + paramDuration.name());
  }
  
  public static void encodeMetrics(long paramLong, @NotNull Collection<Metric> paramCollection, @NotNull StringBuilder paramStringBuilder) {
    for (Metric metric : paramCollection) {
      paramStringBuilder.append(sanitizeName(metric.getKey()));
      paramStringBuilder.append("@");
      MeasurementUnit measurementUnit = metric.getUnit();
      String str1 = getUnitName(measurementUnit);
      String str2 = sanitizeUnit(str1);
      paramStringBuilder.append(str2);
      for (Object object : metric.serialize()) {
        paramStringBuilder.append(":");
        paramStringBuilder.append(object);
      } 
      paramStringBuilder.append("|");
      paramStringBuilder.append((metric.getType()).statsdCode);
      Map<String, String> map = metric.getTags();
      if (map != null) {
        paramStringBuilder.append("|#");
        boolean bool = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
          String str = sanitizeTagKey((String)entry.getKey());
          if (bool) {
            bool = false;
          } else {
            paramStringBuilder.append(",");
          } 
          paramStringBuilder.append(str);
          paramStringBuilder.append(":");
          paramStringBuilder.append(sanitizeTagValue((String)entry.getValue()));
        } 
      } 
      paramStringBuilder.append("|T");
      paramStringBuilder.append(paramLong);
      paramStringBuilder.append("\n");
    } 
  }
  
  @NotNull
  public static Map<String, String> mergeTags(@Nullable Map<String, String> paramMap1, @NotNull Map<String, String> paramMap2) {
    if (paramMap1 == null)
      return Collections.unmodifiableMap(paramMap2); 
    HashMap<String, String> hashMap = new HashMap<>(paramMap1);
    for (Map.Entry<String, String> entry : paramMap2.entrySet()) {
      String str = (String)entry.getKey();
      if (!hashMap.containsKey(str))
        hashMap.put(str, (String)entry.getValue()); 
    } 
    return hashMap;
  }
  
  @TestOnly
  public static void setFlushShiftMs(long paramLong) {
    FLUSH_SHIFT_MS = paramLong;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\MetricsHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */