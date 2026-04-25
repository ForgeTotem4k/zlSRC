package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class SpanContext implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "trace";
  
  public static final String DEFAULT_ORIGIN = "manual";
  
  @NotNull
  private final SentryId traceId;
  
  @NotNull
  private final SpanId spanId;
  
  @Nullable
  private SpanId parentSpanId;
  
  @Nullable
  private transient TracesSamplingDecision samplingDecision;
  
  @NotNull
  protected String op;
  
  @Nullable
  protected String description;
  
  @Nullable
  protected SpanStatus status;
  
  @NotNull
  protected Map<String, String> tags = new ConcurrentHashMap<>();
  
  @Nullable
  protected String origin = "manual";
  
  @Nullable
  private Map<String, Object> unknown;
  
  @NotNull
  private Instrumenter instrumenter = Instrumenter.SENTRY;
  
  @Nullable
  protected Baggage baggage;
  
  public SpanContext(@NotNull String paramString, @Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    this(new SentryId(), new SpanId(), paramString, null, paramTracesSamplingDecision);
  }
  
  public SpanContext(@NotNull String paramString) {
    this(new SentryId(), new SpanId(), paramString, null, null);
  }
  
  public SpanContext(@NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId1, @NotNull String paramString, @Nullable SpanId paramSpanId2, @Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    this(paramSentryId, paramSpanId1, paramSpanId2, paramString, null, paramTracesSamplingDecision, null, "manual");
  }
  
  @Internal
  public SpanContext(@NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId1, @Nullable SpanId paramSpanId2, @NotNull String paramString1, @Nullable String paramString2, @Nullable TracesSamplingDecision paramTracesSamplingDecision, @Nullable SpanStatus paramSpanStatus, @Nullable String paramString3) {
    this.traceId = (SentryId)Objects.requireNonNull(paramSentryId, "traceId is required");
    this.spanId = (SpanId)Objects.requireNonNull(paramSpanId1, "spanId is required");
    this.op = (String)Objects.requireNonNull(paramString1, "operation is required");
    this.parentSpanId = paramSpanId2;
    this.samplingDecision = paramTracesSamplingDecision;
    this.description = paramString2;
    this.status = paramSpanStatus;
    this.origin = paramString3;
  }
  
  public SpanContext(@NotNull SpanContext paramSpanContext) {
    this.traceId = paramSpanContext.traceId;
    this.spanId = paramSpanContext.spanId;
    this.parentSpanId = paramSpanContext.parentSpanId;
    this.samplingDecision = paramSpanContext.samplingDecision;
    this.op = paramSpanContext.op;
    this.description = paramSpanContext.description;
    this.status = paramSpanContext.status;
    Map<String, String> map = CollectionUtils.newConcurrentHashMap(paramSpanContext.tags);
    if (map != null)
      this.tags = map; 
  }
  
  public void setOperation(@NotNull String paramString) {
    this.op = (String)Objects.requireNonNull(paramString, "operation is required");
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    Objects.requireNonNull(paramString1, "name is required");
    Objects.requireNonNull(paramString2, "value is required");
    this.tags.put(paramString1, paramString2);
  }
  
  public void setDescription(@Nullable String paramString) {
    this.description = paramString;
  }
  
  public void setStatus(@Nullable SpanStatus paramSpanStatus) {
    this.status = paramSpanStatus;
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
  @TestOnly
  public SpanId getParentSpanId() {
    return this.parentSpanId;
  }
  
  @NotNull
  public String getOperation() {
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
  public TracesSamplingDecision getSamplingDecision() {
    return this.samplingDecision;
  }
  
  @Nullable
  public Boolean getSampled() {
    return (this.samplingDecision == null) ? null : this.samplingDecision.getSampled();
  }
  
  @Nullable
  public Boolean getProfileSampled() {
    return (this.samplingDecision == null) ? null : this.samplingDecision.getProfileSampled();
  }
  
  @Internal
  public void setSampled(@Nullable Boolean paramBoolean) {
    if (paramBoolean == null) {
      setSamplingDecision(null);
    } else {
      setSamplingDecision(new TracesSamplingDecision(paramBoolean));
    } 
  }
  
  @Internal
  public void setSampled(@Nullable Boolean paramBoolean1, @Nullable Boolean paramBoolean2) {
    if (paramBoolean1 == null) {
      setSamplingDecision(null);
    } else if (paramBoolean2 == null) {
      setSamplingDecision(new TracesSamplingDecision(paramBoolean1));
    } else {
      setSamplingDecision(new TracesSamplingDecision(paramBoolean1, null, paramBoolean2, null));
    } 
  }
  
  @Internal
  public void setSamplingDecision(@Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    this.samplingDecision = paramTracesSamplingDecision;
  }
  
  @Nullable
  public String getOrigin() {
    return this.origin;
  }
  
  public void setOrigin(@Nullable String paramString) {
    this.origin = paramString;
  }
  
  @NotNull
  public Instrumenter getInstrumenter() {
    return this.instrumenter;
  }
  
  public void setInstrumenter(@NotNull Instrumenter paramInstrumenter) {
    this.instrumenter = paramInstrumenter;
  }
  
  @Nullable
  public Baggage getBaggage() {
    return this.baggage;
  }
  
  @Internal
  public SpanContext copyForChild(@NotNull String paramString, @Nullable SpanId paramSpanId1, @Nullable SpanId paramSpanId2) {
    return new SpanContext(this.traceId, (paramSpanId2 == null) ? new SpanId() : paramSpanId2, paramSpanId1, paramString, null, this.samplingDecision, null, "manual");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SpanContext))
      return false; 
    SpanContext spanContext = (SpanContext)paramObject;
    return (this.traceId.equals(spanContext.traceId) && this.spanId.equals(spanContext.spanId) && Objects.equals(this.parentSpanId, spanContext.parentSpanId) && this.op.equals(spanContext.op) && Objects.equals(this.description, spanContext.description) && getStatus() == spanContext.getStatus());
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.traceId, this.spanId, this.parentSpanId, this.op, this.description, getStatus() });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("trace_id");
    this.traceId.serialize(paramObjectWriter, paramILogger);
    paramObjectWriter.name("span_id");
    this.spanId.serialize(paramObjectWriter, paramILogger);
    if (this.parentSpanId != null) {
      paramObjectWriter.name("parent_span_id");
      this.parentSpanId.serialize(paramObjectWriter, paramILogger);
    } 
    paramObjectWriter.name("op").value(this.op);
    if (this.description != null)
      paramObjectWriter.name("description").value(this.description); 
    if (getStatus() != null)
      paramObjectWriter.name("status").value(paramILogger, getStatus()); 
    if (this.origin != null)
      paramObjectWriter.name("origin").value(paramILogger, this.origin); 
    if (!this.tags.isEmpty())
      paramObjectWriter.name("tags").value(paramILogger, this.tags); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public static final class JsonKeys {
    public static final String TRACE_ID = "trace_id";
    
    public static final String SPAN_ID = "span_id";
    
    public static final String PARENT_SPAN_ID = "parent_span_id";
    
    public static final String OP = "op";
    
    public static final String DESCRIPTION = "description";
    
    public static final String STATUS = "status";
    
    public static final String TAGS = "tags";
    
    public static final String ORIGIN = "origin";
  }
  
  public static final class Deserializer implements JsonDeserializer<SpanContext> {
    @NotNull
    public SpanContext deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryId sentryId = null;
      SpanId spanId1 = null;
      SpanId spanId2 = null;
      String str1 = null;
      String str2 = null;
      SpanStatus spanStatus = null;
      String str3 = null;
      Map<String, String> map = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "trace_id":
            sentryId = (new SentryId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "span_id":
            spanId1 = (new SpanId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "parent_span_id":
            spanId2 = param1JsonObjectReader.<SpanId>nextOrNull(param1ILogger, new SpanId.Deserializer());
            continue;
          case "op":
            str1 = param1JsonObjectReader.nextString();
            continue;
          case "description":
            str2 = param1JsonObjectReader.nextString();
            continue;
          case "status":
            spanStatus = param1JsonObjectReader.<SpanStatus>nextOrNull(param1ILogger, new SpanStatus.Deserializer());
            continue;
          case "origin":
            str3 = param1JsonObjectReader.nextString();
            continue;
          case "tags":
            map = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str);
      } 
      if (sentryId == null) {
        String str = "Missing required field \"trace_id\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (spanId1 == null) {
        String str = "Missing required field \"span_id\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str1 == null)
        str1 = ""; 
      SpanContext spanContext = new SpanContext(sentryId, spanId1, str1, spanId2, null);
      spanContext.setDescription(str2);
      spanContext.setStatus(spanStatus);
      spanContext.setOrigin(str3);
      if (map != null)
        spanContext.tags = map; 
      spanContext.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return spanContext;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SpanContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */