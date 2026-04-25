package io.sentry;

import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryEnvelopeHeader implements JsonSerializable, JsonUnknown {
  @Nullable
  private final SentryId eventId;
  
  @Nullable
  private final SdkVersion sdkVersion;
  
  @Nullable
  private final TraceContext traceContext;
  
  @Nullable
  private Date sentAt;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryEnvelopeHeader(@Nullable SentryId paramSentryId, @Nullable SdkVersion paramSdkVersion) {
    this(paramSentryId, paramSdkVersion, null);
  }
  
  public SentryEnvelopeHeader(@Nullable SentryId paramSentryId, @Nullable SdkVersion paramSdkVersion, @Nullable TraceContext paramTraceContext) {
    this.eventId = paramSentryId;
    this.sdkVersion = paramSdkVersion;
    this.traceContext = paramTraceContext;
  }
  
  public SentryEnvelopeHeader(@Nullable SentryId paramSentryId) {
    this(paramSentryId, null);
  }
  
  public SentryEnvelopeHeader() {
    this(new SentryId());
  }
  
  @Nullable
  public SentryId getEventId() {
    return this.eventId;
  }
  
  @Nullable
  public SdkVersion getSdkVersion() {
    return this.sdkVersion;
  }
  
  @Nullable
  public TraceContext getTraceContext() {
    return this.traceContext;
  }
  
  @Nullable
  public Date getSentAt() {
    return this.sentAt;
  }
  
  public void setSentAt(@Nullable Date paramDate) {
    this.sentAt = paramDate;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.eventId != null)
      paramObjectWriter.name("event_id").value(paramILogger, this.eventId); 
    if (this.sdkVersion != null)
      paramObjectWriter.name("sdk").value(paramILogger, this.sdkVersion); 
    if (this.traceContext != null)
      paramObjectWriter.name("trace").value(paramILogger, this.traceContext); 
    if (this.sentAt != null)
      paramObjectWriter.name("sent_at").value(paramILogger, DateUtils.getTimestamp(this.sentAt)); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
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
    public static final String EVENT_ID = "event_id";
    
    public static final String SDK = "sdk";
    
    public static final String TRACE = "trace";
    
    public static final String SENT_AT = "sent_at";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryEnvelopeHeader> {
    @NotNull
    public SentryEnvelopeHeader deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryId sentryId = null;
      SdkVersion sdkVersion = null;
      TraceContext traceContext = null;
      Date date = null;
      HashMap<Object, Object> hashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "event_id":
            sentryId = param1JsonObjectReader.<SentryId>nextOrNull(param1ILogger, (JsonDeserializer<SentryId>)new SentryId.Deserializer());
            continue;
          case "sdk":
            sdkVersion = param1JsonObjectReader.<SdkVersion>nextOrNull(param1ILogger, (JsonDeserializer<SdkVersion>)new SdkVersion.Deserializer());
            continue;
          case "trace":
            traceContext = param1JsonObjectReader.<TraceContext>nextOrNull(param1ILogger, new TraceContext.Deserializer());
            continue;
          case "sent_at":
            date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str);
      } 
      SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(sentryId, sdkVersion, traceContext);
      sentryEnvelopeHeader.setSentAt(date);
      sentryEnvelopeHeader.setUnknown((Map)hashMap);
      param1JsonObjectReader.endObject();
      return sentryEnvelopeHeader;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryEnvelopeHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */