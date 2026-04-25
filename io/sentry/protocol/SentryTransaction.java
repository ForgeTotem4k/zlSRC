package io.sentry.protocol;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryTracer;
import io.sentry.Span;
import io.sentry.SpanContext;
import io.sentry.SpanStatus;
import io.sentry.TracesSamplingDecision;
import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryTransaction extends SentryBaseEvent implements JsonUnknown, JsonSerializable {
  @Nullable
  private String transaction;
  
  @NotNull
  private Double startTimestamp;
  
  @Nullable
  private Double timestamp;
  
  @NotNull
  private final List<SentrySpan> spans = new ArrayList<>();
  
  @NotNull
  private final String type = "transaction";
  
  @NotNull
  private final Map<String, MeasurementValue> measurements = new HashMap<>();
  
  @Nullable
  private Map<String, List<MetricSummary>> metricSummaries;
  
  @NotNull
  private TransactionInfo transactionInfo;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryTransaction(@NotNull SentryTracer paramSentryTracer) {
    super(paramSentryTracer.getEventId());
    Objects.requireNonNull(paramSentryTracer, "sentryTracer is required");
    this.startTimestamp = Double.valueOf(DateUtils.nanosToSeconds(paramSentryTracer.getStartDate().nanoTimestamp()));
    this.timestamp = Double.valueOf(DateUtils.nanosToSeconds(paramSentryTracer.getStartDate().laterDateNanosTimestampByDiff(paramSentryTracer.getFinishDate())));
    this.transaction = paramSentryTracer.getName();
    for (Span span : paramSentryTracer.getChildren()) {
      if (Boolean.TRUE.equals(span.isSampled()))
        this.spans.add(new SentrySpan(span)); 
    } 
    Contexts contexts = getContexts();
    contexts.putAll(paramSentryTracer.getContexts());
    SpanContext spanContext = paramSentryTracer.getSpanContext();
    contexts.setTrace(new SpanContext(spanContext.getTraceId(), spanContext.getSpanId(), spanContext.getParentSpanId(), spanContext.getOperation(), spanContext.getDescription(), spanContext.getSamplingDecision(), spanContext.getStatus(), spanContext.getOrigin()));
    for (Map.Entry entry : spanContext.getTags().entrySet())
      setTag((String)entry.getKey(), (String)entry.getValue()); 
    Map map = paramSentryTracer.getData();
    if (map != null)
      for (Map.Entry entry : map.entrySet())
        setExtra((String)entry.getKey(), entry.getValue());  
    this.transactionInfo = new TransactionInfo(paramSentryTracer.getTransactionNameSource().apiName());
    LocalMetricsAggregator localMetricsAggregator = paramSentryTracer.getLocalMetricsAggregator();
    if (localMetricsAggregator != null) {
      this.metricSummaries = localMetricsAggregator.getSummaries();
    } else {
      this.metricSummaries = null;
    } 
  }
  
  @Internal
  public SentryTransaction(@Nullable String paramString, @NotNull Double paramDouble1, @Nullable Double paramDouble2, @NotNull List<SentrySpan> paramList, @NotNull Map<String, MeasurementValue> paramMap, @Nullable Map<String, List<MetricSummary>> paramMap1, @NotNull TransactionInfo paramTransactionInfo) {
    this.transaction = paramString;
    this.startTimestamp = paramDouble1;
    this.timestamp = paramDouble2;
    this.spans.addAll(paramList);
    this.measurements.putAll(paramMap);
    for (SentrySpan sentrySpan : paramList)
      this.measurements.putAll(sentrySpan.getMeasurements()); 
    this.transactionInfo = paramTransactionInfo;
    this.metricSummaries = paramMap1;
  }
  
  @NotNull
  public List<SentrySpan> getSpans() {
    return this.spans;
  }
  
  public boolean isFinished() {
    return (this.timestamp != null);
  }
  
  @Nullable
  public String getTransaction() {
    return this.transaction;
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
  public String getType() {
    return "transaction";
  }
  
  @Nullable
  public SpanStatus getStatus() {
    SpanContext spanContext = getContexts().getTrace();
    return (spanContext != null) ? spanContext.getStatus() : null;
  }
  
  public boolean isSampled() {
    TracesSamplingDecision tracesSamplingDecision = getSamplingDecision();
    return (tracesSamplingDecision == null) ? false : tracesSamplingDecision.getSampled().booleanValue();
  }
  
  @Nullable
  public TracesSamplingDecision getSamplingDecision() {
    SpanContext spanContext = getContexts().getTrace();
    return (spanContext == null) ? null : spanContext.getSamplingDecision();
  }
  
  @NotNull
  public Map<String, MeasurementValue> getMeasurements() {
    return this.measurements;
  }
  
  @Nullable
  public Map<String, List<MetricSummary>> getMetricSummaries() {
    return this.metricSummaries;
  }
  
  public void setMetricSummaries(@Nullable Map<String, List<MetricSummary>> paramMap) {
    this.metricSummaries = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.transaction != null)
      paramObjectWriter.name("transaction").value(this.transaction); 
    paramObjectWriter.name("start_timestamp").value(paramILogger, doubleToBigDecimal(this.startTimestamp));
    if (this.timestamp != null)
      paramObjectWriter.name("timestamp").value(paramILogger, doubleToBigDecimal(this.timestamp)); 
    if (!this.spans.isEmpty())
      paramObjectWriter.name("spans").value(paramILogger, this.spans); 
    paramObjectWriter.name("type").value("transaction");
    if (!this.measurements.isEmpty())
      paramObjectWriter.name("measurements").value(paramILogger, this.measurements); 
    if (this.metricSummaries != null && !this.metricSummaries.isEmpty())
      paramObjectWriter.name("_metrics_summary").value(paramILogger, this.metricSummaries); 
    paramObjectWriter.name("transaction_info").value(paramILogger, this.transactionInfo);
    (new SentryBaseEvent.Serializer()).serialize(this, paramObjectWriter, paramILogger);
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
    public static final String TRANSACTION = "transaction";
    
    public static final String START_TIMESTAMP = "start_timestamp";
    
    public static final String TIMESTAMP = "timestamp";
    
    public static final String SPANS = "spans";
    
    public static final String TYPE = "type";
    
    public static final String MEASUREMENTS = "measurements";
    
    public static final String METRICS_SUMMARY = "_metrics_summary";
    
    public static final String TRANSACTION_INFO = "transaction_info";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryTransaction> {
    @NotNull
    public SentryTransaction deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryTransaction sentryTransaction = new SentryTransaction("", Double.valueOf(0.0D), null, new ArrayList<>(), new HashMap<>(), null, new TransactionInfo(TransactionNameSource.CUSTOM.apiName()));
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      SentryBaseEvent.Deserializer deserializer = new SentryBaseEvent.Deserializer();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List list;
        Map map;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "transaction":
            sentryTransaction.transaction = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "start_timestamp":
            try {
              Double double_ = param1JsonObjectReader.nextDoubleOrNull();
              if (double_ != null)
                sentryTransaction.startTimestamp = double_; 
            } catch (NumberFormatException numberFormatException) {
              Date date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
              if (date != null)
                sentryTransaction.startTimestamp = Double.valueOf(DateUtils.dateToSeconds(date)); 
            } 
            continue;
          case "timestamp":
            try {
              Double double_ = param1JsonObjectReader.nextDoubleOrNull();
              if (double_ != null)
                sentryTransaction.timestamp = double_; 
            } catch (NumberFormatException numberFormatException) {
              Date date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
              if (date != null)
                sentryTransaction.timestamp = Double.valueOf(DateUtils.dateToSeconds(date)); 
            } 
            continue;
          case "spans":
            list = param1JsonObjectReader.nextListOrNull(param1ILogger, new SentrySpan.Deserializer());
            if (list != null)
              sentryTransaction.spans.addAll(list); 
            continue;
          case "type":
            param1JsonObjectReader.nextString();
            continue;
          case "measurements":
            map = param1JsonObjectReader.nextMapOrNull(param1ILogger, new MeasurementValue.Deserializer());
            if (map != null)
              sentryTransaction.measurements.putAll(map); 
            continue;
          case "_metrics_summary":
            sentryTransaction.metricSummaries = param1JsonObjectReader.nextMapOfListOrNull(param1ILogger, new MetricSummary.Deserializer());
            continue;
          case "transaction_info":
            sentryTransaction.transactionInfo = (new TransactionInfo.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
        } 
        if (!deserializer.deserializeValue(sentryTransaction, str, param1JsonObjectReader, param1ILogger)) {
          if (concurrentHashMap == null)
            concurrentHashMap = new ConcurrentHashMap<>(); 
          param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
        } 
      } 
      sentryTransaction.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryTransaction;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */