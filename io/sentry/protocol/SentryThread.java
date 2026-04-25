package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLockReason;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryThread implements JsonUnknown, JsonSerializable {
  @Nullable
  private Long id;
  
  @Nullable
  private Integer priority;
  
  @Nullable
  private String name;
  
  @Nullable
  private String state;
  
  @Nullable
  private Boolean crashed;
  
  @Nullable
  private Boolean current;
  
  @Nullable
  private Boolean daemon;
  
  @Nullable
  private Boolean main;
  
  @Nullable
  private SentryStackTrace stacktrace;
  
  @Nullable
  private Map<String, SentryLockReason> heldLocks;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public Long getId() {
    return this.id;
  }
  
  public void setId(@Nullable Long paramLong) {
    this.id = paramLong;
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(@Nullable String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public Boolean isCrashed() {
    return this.crashed;
  }
  
  public void setCrashed(@Nullable Boolean paramBoolean) {
    this.crashed = paramBoolean;
  }
  
  @Nullable
  public Boolean isCurrent() {
    return this.current;
  }
  
  public void setCurrent(@Nullable Boolean paramBoolean) {
    this.current = paramBoolean;
  }
  
  @Nullable
  public SentryStackTrace getStacktrace() {
    return this.stacktrace;
  }
  
  public void setStacktrace(@Nullable SentryStackTrace paramSentryStackTrace) {
    this.stacktrace = paramSentryStackTrace;
  }
  
  @Nullable
  public Integer getPriority() {
    return this.priority;
  }
  
  public void setPriority(@Nullable Integer paramInteger) {
    this.priority = paramInteger;
  }
  
  @Nullable
  public Boolean isDaemon() {
    return this.daemon;
  }
  
  public void setDaemon(@Nullable Boolean paramBoolean) {
    this.daemon = paramBoolean;
  }
  
  @Nullable
  public Boolean isMain() {
    return this.main;
  }
  
  public void setMain(@Nullable Boolean paramBoolean) {
    this.main = paramBoolean;
  }
  
  @Nullable
  public String getState() {
    return this.state;
  }
  
  public void setState(@Nullable String paramString) {
    this.state = paramString;
  }
  
  @Nullable
  public Map<String, SentryLockReason> getHeldLocks() {
    return this.heldLocks;
  }
  
  public void setHeldLocks(@Nullable Map<String, SentryLockReason> paramMap) {
    this.heldLocks = paramMap;
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
    if (this.id != null)
      paramObjectWriter.name("id").value(this.id); 
    if (this.priority != null)
      paramObjectWriter.name("priority").value(this.priority); 
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.state != null)
      paramObjectWriter.name("state").value(this.state); 
    if (this.crashed != null)
      paramObjectWriter.name("crashed").value(this.crashed); 
    if (this.current != null)
      paramObjectWriter.name("current").value(this.current); 
    if (this.daemon != null)
      paramObjectWriter.name("daemon").value(this.daemon); 
    if (this.main != null)
      paramObjectWriter.name("main").value(this.main); 
    if (this.stacktrace != null)
      paramObjectWriter.name("stacktrace").value(paramILogger, this.stacktrace); 
    if (this.heldLocks != null)
      paramObjectWriter.name("held_locks").value(paramILogger, this.heldLocks); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String ID = "id";
    
    public static final String PRIORITY = "priority";
    
    public static final String NAME = "name";
    
    public static final String STATE = "state";
    
    public static final String CRASHED = "crashed";
    
    public static final String CURRENT = "current";
    
    public static final String DAEMON = "daemon";
    
    public static final String MAIN = "main";
    
    public static final String STACKTRACE = "stacktrace";
    
    public static final String HELD_LOCKS = "held_locks";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryThread> {
    @NotNull
    public SentryThread deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryThread sentryThread = new SentryThread();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Map<?, ?> map;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "id":
            sentryThread.id = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "priority":
            sentryThread.priority = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "name":
            sentryThread.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "state":
            sentryThread.state = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "crashed":
            sentryThread.crashed = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "current":
            sentryThread.current = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "daemon":
            sentryThread.daemon = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "main":
            sentryThread.main = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "stacktrace":
            sentryThread.stacktrace = (SentryStackTrace)param1JsonObjectReader.nextOrNull(param1ILogger, new SentryStackTrace.Deserializer());
            continue;
          case "held_locks":
            map = param1JsonObjectReader.nextMapOrNull(param1ILogger, (JsonDeserializer)new SentryLockReason.Deserializer());
            if (map != null)
              sentryThread.heldLocks = (Map)new HashMap<>(map); 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      sentryThread.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryThread;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */