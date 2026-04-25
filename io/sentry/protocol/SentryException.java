package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryException implements JsonUnknown, JsonSerializable {
  @Nullable
  private String type;
  
  @Nullable
  private String value;
  
  @Nullable
  private String module;
  
  @Nullable
  private Long threadId;
  
  @Nullable
  private SentryStackTrace stacktrace;
  
  @Nullable
  private Mechanism mechanism;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public String getType() {
    return this.type;
  }
  
  public void setType(@Nullable String paramString) {
    this.type = paramString;
  }
  
  @Nullable
  public String getValue() {
    return this.value;
  }
  
  public void setValue(@Nullable String paramString) {
    this.value = paramString;
  }
  
  @Nullable
  public String getModule() {
    return this.module;
  }
  
  public void setModule(@Nullable String paramString) {
    this.module = paramString;
  }
  
  @Nullable
  public Long getThreadId() {
    return this.threadId;
  }
  
  public void setThreadId(@Nullable Long paramLong) {
    this.threadId = paramLong;
  }
  
  @Nullable
  public SentryStackTrace getStacktrace() {
    return this.stacktrace;
  }
  
  public void setStacktrace(@Nullable SentryStackTrace paramSentryStackTrace) {
    this.stacktrace = paramSentryStackTrace;
  }
  
  @Nullable
  public Mechanism getMechanism() {
    return this.mechanism;
  }
  
  public void setMechanism(@Nullable Mechanism paramMechanism) {
    this.mechanism = paramMechanism;
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.type != null)
      paramObjectWriter.name("type").value(this.type); 
    if (this.value != null)
      paramObjectWriter.name("value").value(this.value); 
    if (this.module != null)
      paramObjectWriter.name("module").value(this.module); 
    if (this.threadId != null)
      paramObjectWriter.name("thread_id").value(this.threadId); 
    if (this.stacktrace != null)
      paramObjectWriter.name("stacktrace").value(paramILogger, this.stacktrace); 
    if (this.mechanism != null)
      paramObjectWriter.name("mechanism").value(paramILogger, this.mechanism); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TYPE = "type";
    
    public static final String VALUE = "value";
    
    public static final String MODULE = "module";
    
    public static final String THREAD_ID = "thread_id";
    
    public static final String STACKTRACE = "stacktrace";
    
    public static final String MECHANISM = "mechanism";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryException> {
    @NotNull
    public SentryException deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryException sentryException = new SentryException();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "type":
            sentryException.type = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "value":
            sentryException.value = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "module":
            sentryException.module = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "thread_id":
            sentryException.threadId = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "stacktrace":
            sentryException.stacktrace = (SentryStackTrace)param1JsonObjectReader.nextOrNull(param1ILogger, new SentryStackTrace.Deserializer());
            continue;
          case "mechanism":
            sentryException.mechanism = (Mechanism)param1JsonObjectReader.nextOrNull(param1ILogger, new Mechanism.Deserializer());
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      sentryException.setUnknown((Map)hashMap);
      return sentryException;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */