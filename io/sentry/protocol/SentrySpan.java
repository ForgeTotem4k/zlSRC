package io.sentry.protocol;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.Span;
import io.sentry.SpanId;
import io.sentry.SpanStatus;
import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentrySpan implements JsonUnknown, JsonSerializable {
  @NotNull
  private final Double startTimestamp;
  
  @Nullable
  private final Double timestamp;
  
  @NotNull
  private final SentryId traceId;
  
  @NotNull
  private final SpanId spanId;
  
  @Nullable
  private final SpanId parentSpanId;
  
  @NotNull
  private final String op;
  
  @Nullable
  private final String description;
  
  @Nullable
  private final SpanStatus status;
  
  @Nullable
  private final String origin;
  
  @NotNull
  private final Map<String, String> tags;
  
  @Nullable
  private Map<String, Object> data;
  
  @NotNull
  private final Map<String, MeasurementValue> measurements;
  
  @Nullable
  private final Map<String, List<MetricSummary>> metricsSummaries;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentrySpan(@NotNull Span paramSpan) {
    this(paramSpan, paramSpan.getData());
  }
  
  @Internal
  public SentrySpan(@NotNull Span paramSpan, @Nullable Map<String, Object> paramMap) {
    Objects.requireNonNull(paramSpan, "span is required");
    this.description = paramSpan.getDescription();
    this.op = paramSpan.getOperation();
    this.spanId = paramSpan.getSpanId();
    this.parentSpanId = paramSpan.getParentSpanId();
    this.traceId = paramSpan.getTraceId();
    this.status = paramSpan.getStatus();
    this.origin = paramSpan.getSpanContext().getOrigin();
    Map<String, String> map = CollectionUtils.newConcurrentHashMap(paramSpan.getTags());
    this.tags = (map != null) ? map : new ConcurrentHashMap<>();
    Map<String, MeasurementValue> map1 = CollectionUtils.newConcurrentHashMap(paramSpan.getMeasurements());
    this.measurements = (map1 != null) ? map1 : new ConcurrentHashMap<>();
    this.timestamp = (paramSpan.getFinishDate() == null) ? null : Double.valueOf(DateUtils.nanosToSeconds(paramSpan.getStartDate().laterDateNanosTimestampByDiff(paramSpan.getFinishDate())));
    this.startTimestamp = Double.valueOf(DateUtils.nanosToSeconds(paramSpan.getStartDate().nanoTimestamp()));
    this.data = paramMap;
    LocalMetricsAggregator localMetricsAggregator = paramSpan.getLocalMetricsAggregator();
    if (localMetricsAggregator != null) {
      this.metricsSummaries = localMetricsAggregator.getSummaries();
    } else {
      this.metricsSummaries = null;
    } 
  }
  
  @Internal
  public SentrySpan(@NotNull Double paramDouble1, @Nullable Double paramDouble2, @NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId1, @Nullable SpanId paramSpanId2, @NotNull String paramString1, @Nullable String paramString2, @Nullable SpanStatus paramSpanStatus, @Nullable String paramString3, @NotNull Map<String, String> paramMap, @NotNull Map<String, MeasurementValue> paramMap1, @Nullable Map<String, List<MetricSummary>> paramMap2, @Nullable Map<String, Object> paramMap3) {
    this.startTimestamp = paramDouble1;
    this.timestamp = paramDouble2;
    this.traceId = paramSentryId;
    this.spanId = paramSpanId1;
    this.parentSpanId = paramSpanId2;
    this.op = paramString1;
    this.description = paramString2;
    this.status = paramSpanStatus;
    this.origin = paramString3;
    this.tags = paramMap;
    this.measurements = paramMap1;
    this.metricsSummaries = paramMap2;
    this.data = paramMap3;
  }
  
  public boolean isFinished() {
    return (this.timestamp != null);
  }
  
  @NotNull
  public Double getStartTimestamp() {
    return this.startTimestamp;
  }
  
  @Nullable
  public Double getTimestamp() {
    return this.timestamp;
  }
  
  @NotNull
  public SentryId getTraceId() {
    return this.traceId;
  }
  
  @NotNull
  public SpanId getSpanId() {
    return this.spanId;
  }
  
  @Nullable
  public SpanId getParentSpanId() {
    return this.parentSpanId;
  }
  
  @NotNull
  public String getOp() {
    return this.op;
  }
  
  @Nullable
  public String getDescription() {
    return this.description;
  }
  
  @Nullable
  public SpanStatus getStatus() {
    return this.status;
  }
  
  @NotNull
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  @Nullable
  public Map<String, Object> getData() {
    return this.data;
  }
  
  public void setData(@Nullable Map<String, Object> paramMap) {
    this.data = paramMap;
  }
  
  @Nullable
  public String getOrigin() {
    return this.origin;
  }
  
  @NotNull
  public Map<String, MeasurementValue> getMeasurements() {
    return this.measurements;
  }
  
  @Nullable
  public Map<String, List<MetricSummary>> getMetricsSummaries() {
    return this.metricsSummaries;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("start_timestamp").value(paramILogger, doubleToBigDecimal(this.startTimestamp));
    if (this.timestamp != null)
      paramObjectWriter.name("timestamp").value(paramILogger, doubleToBigDecimal(this.timestamp)); 
    paramObjectWriter.name("trace_id").value(paramILogger, this.traceId);
    paramObjectWriter.name("span_id").value(paramILogger, this.spanId);
    if (this.parentSpanId != null)
      paramObjectWriter.name("parent_span_id").value(paramILogger, this.parentSpanId); 
    paramObjectWriter.name("op").value(this.op);
    if (this.description != null)
      paramObjectWriter.name("description").value(this.description); 
    if (this.status != null)
      paramObjectWriter.name("status").value(paramILogger, this.status); 
    if (this.origin != null)
      paramObjectWriter.name("origin").value(paramILogger, this.origin); 
    if (!this.tags.isEmpty())
      paramObjectWriter.name("tags").value(paramILogger, this.tags); 
    if (this.data != null)
      paramObjectWriter.name("data").value(paramILogger, this.data); 
    if (!this.measurements.isEmpty())
      paramObjectWriter.name("measurements").value(paramILogger, this.measurements); 
    if (this.metricsSummaries != null && !this.metricsSummaries.isEmpty())
      paramObjectWriter.name("_metrics_summary").value(paramILogger, this.metricsSummaries); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @NotNull
  private BigDecimal doubleToBigDecimal(@NotNull Double paramDouble) {
    return BigDecimal.valueOf(paramDouble.doubleValue()).setScale(6, RoundingMode.DOWN);
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public static final class JsonKeys {
    public static final String START_TIMESTAMP = "start_timestamp";
    
    public static final String TIMESTAMP = "timestamp";
    
    public static final String TRACE_ID = "trace_id";
    
    public static final String SPAN_ID = "span_id";
    
    public static final String PARENT_SPAN_ID = "parent_span_id";
    
    public static final String OP = "op";
    
    public static final String DESCRIPTION = "description";
    
    public static final String STATUS = "status";
    
    public static final String ORIGIN = "origin";
    
    public static final String TAGS = "tags";
    
    public static final String MEASUREMENTS = "measurements";
    
    public static final String METRICS_SUMMARY = "_metrics_summary";
    
    public static final String DATA = "data";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentrySpan> {
    @NotNull
    public SentrySpan deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Double double_1 = null;
      Double double_2 = null;
      SentryId sentryId = null;
      SpanId spanId1 = null;
      SpanId spanId2 = null;
      String str1 = null;
      String str2 = null;
      SpanStatus spanStatus = null;
      String str3 = null;
      Map<Object, Object> map1 = null;
      Map<Object, Object> map2 = null;
      Map<String, List<MetricSummary>> map = null;
      Map<String, Object> map3 = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "start_timestamp":
            try {
              double_1 = param1JsonObjectReader.nextDoubleOrNull();
            } catch (NumberFormatException numberFormatException) {
              Date date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
              double_1 = (date != null) ? Double.valueOf(DateUtils.dateToSeconds(date)) : null;
            } 
            continue;
          case "timestamp":
            try {
              double_2 = param1JsonObjectReader.nextDoubleOrNull();
            } catch (NumberFormatException numberFormatException) {
              Date date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
              double_2 = (date != null) ? Double.valueOf(DateUtils.dateToSeconds(date)) : null;
            } 
            continue;
          case "trace_id":
            sentryId = (new SentryId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "span_id":
            spanId1 = (new SpanId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "parent_span_id":
            spanId2 = (SpanId)param1JsonObjectReader.nextOrNull(param1ILogger, (JsonDeserializer)new SpanId.Deserializer());
            continue;
          case "op":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "description":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "status":
            spanStatus = (SpanStatus)param1JsonObjectReader.nextOrNull(param1ILogger, (JsonDeserializer)new SpanStatus.Deserializer());
            continue;
          case "origin":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "tags":
            map1 = (Map)param1JsonObjectReader.nextObjectOrNull();
            continue;
          case "data":
            map3 = (Map)param1JsonObjectReader.nextObjectOrNull();
            continue;
          case "measurements":
            map2 = param1JsonObjectReader.nextMapOrNull(param1ILogger, new MeasurementValue.Deserializer());
            continue;
          case "_metrics_summary":
            map = param1JsonObjectReader.nextMapOfListOrNull(param1ILogger, new MetricSummary.Deserializer());
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      if (double_1 == null)
        throw missingRequiredFieldException("start_timestamp", param1ILogger); 
      if (sentryId == null)
        throw missingRequiredFieldException("trace_id", param1ILogger); 
      if (spanId1 == null)
        throw missingRequiredFieldException("span_id", param1ILogger); 
      if (str1 == null)
        throw missingRequiredFieldException("op", param1ILogger); 
      if (map1 == null)
        map1 = new HashMap<>(); 
      if (map2 == null)
        map2 = new HashMap<>(); 
      SentrySpan sentrySpan = new SentrySpan(double_1, double_2, sentryId, spanId1, spanId2, str1, str2, spanStatus, str3, (Map)map1, (Map)map2, map, map3);
      sentrySpan.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentrySpan;
    }
    
    private Exception missingRequiredFieldException(String param1String, ILogger param1ILogger) {
      String str = "Missing required field \"" + param1String + "\"";
      IllegalStateException illegalStateException = new IllegalStateException(str);
      param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
      return illegalStateException;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentrySpan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */