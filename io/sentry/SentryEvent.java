package io.sentry;

import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryThread;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class SentryEvent extends SentryBaseEvent implements JsonUnknown, JsonSerializable {
  @NotNull
  private Date timestamp;
  
  @Nullable
  private Message message;
  
  @Nullable
  private String logger;
  
  @Nullable
  private SentryValues<SentryThread> threads;
  
  @Nullable
  private SentryValues<SentryException> exception;
  
  @Nullable
  private SentryLevel level;
  
  @Nullable
  private String transaction;
  
  @Nullable
  private List<String> fingerprint;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  private Map<String, String> modules;
  
  SentryEvent(@NotNull SentryId paramSentryId, @NotNull Date paramDate) {
    super(paramSentryId);
    this.timestamp = paramDate;
  }
  
  public SentryEvent(@Nullable Throwable paramThrowable) {
    this();
    this.throwable = paramThrowable;
  }
  
  public SentryEvent() {
    this(new SentryId(), DateUtils.getCurrentDateTime());
  }
  
  @TestOnly
  public SentryEvent(@NotNull Date paramDate) {
    this(new SentryId(), paramDate);
  }
  
  public Date getTimestamp() {
    return (Date)this.timestamp.clone();
  }
  
  public void setTimestamp(@NotNull Date paramDate) {
    this.timestamp = paramDate;
  }
  
  @Nullable
  public Message getMessage() {
    return this.message;
  }
  
  public void setMessage(@Nullable Message paramMessage) {
    this.message = paramMessage;
  }
  
  @Nullable
  public String getLogger() {
    return this.logger;
  }
  
  public void setLogger(@Nullable String paramString) {
    this.logger = paramString;
  }
  
  @Nullable
  public List<SentryThread> getThreads() {
    return (this.threads != null) ? this.threads.getValues() : null;
  }
  
  public void setThreads(@Nullable List<SentryThread> paramList) {
    this.threads = new SentryValues<>(paramList);
  }
  
  @Nullable
  public List<SentryException> getExceptions() {
    return (this.exception == null) ? null : this.exception.getValues();
  }
  
  public void setExceptions(@Nullable List<SentryException> paramList) {
    this.exception = new SentryValues<>(paramList);
  }
  
  @Nullable
  public SentryLevel getLevel() {
    return this.level;
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    this.level = paramSentryLevel;
  }
  
  @Nullable
  public String getTransaction() {
    return this.transaction;
  }
  
  public void setTransaction(@Nullable String paramString) {
    this.transaction = paramString;
  }
  
  @Nullable
  public List<String> getFingerprints() {
    return this.fingerprint;
  }
  
  public void setFingerprints(@Nullable List<String> paramList) {
    this.fingerprint = (paramList != null) ? new ArrayList<>(paramList) : null;
  }
  
  @Nullable
  Map<String, String> getModules() {
    return this.modules;
  }
  
  public void setModules(@Nullable Map<String, String> paramMap) {
    this.modules = CollectionUtils.newHashMap(paramMap);
  }
  
  public void setModule(@NotNull String paramString1, @NotNull String paramString2) {
    if (this.modules == null)
      this.modules = new HashMap<>(); 
    this.modules.put(paramString1, paramString2);
  }
  
  public void removeModule(@NotNull String paramString) {
    if (this.modules != null)
      this.modules.remove(paramString); 
  }
  
  @Nullable
  public String getModule(@NotNull String paramString) {
    return (this.modules != null) ? this.modules.get(paramString) : null;
  }
  
  public boolean isCrashed() {
    return (getUnhandledException() != null);
  }
  
  @Nullable
  public SentryException getUnhandledException() {
    if (this.exception != null)
      for (SentryException sentryException : this.exception.getValues()) {
        if (sentryException.getMechanism() != null && sentryException.getMechanism().isHandled() != null && !sentryException.getMechanism().isHandled().booleanValue())
          return sentryException; 
      }  
    return null;
  }
  
  public boolean isErrored() {
    return (this.exception != null && !this.exception.getValues().isEmpty());
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("timestamp").value(paramILogger, this.timestamp);
    if (this.message != null)
      paramObjectWriter.name("message").value(paramILogger, this.message); 
    if (this.logger != null)
      paramObjectWriter.name("logger").value(this.logger); 
    if (this.threads != null && !this.threads.getValues().isEmpty()) {
      paramObjectWriter.name("threads");
      paramObjectWriter.beginObject();
      paramObjectWriter.name("values").value(paramILogger, this.threads.getValues());
      paramObjectWriter.endObject();
    } 
    if (this.exception != null && !this.exception.getValues().isEmpty()) {
      paramObjectWriter.name("exception");
      paramObjectWriter.beginObject();
      paramObjectWriter.name("values").value(paramILogger, this.exception.getValues());
      paramObjectWriter.endObject();
    } 
    if (this.level != null)
      paramObjectWriter.name("level").value(paramILogger, this.level); 
    if (this.transaction != null)
      paramObjectWriter.name("transaction").value(this.transaction); 
    if (this.fingerprint != null)
      paramObjectWriter.name("fingerprint").value(paramILogger, this.fingerprint); 
    if (this.modules != null)
      paramObjectWriter.name("modules").value(paramILogger, this.modules); 
    (new SentryBaseEvent.Serializer()).serialize(this, paramObjectWriter, paramILogger);
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
    public static final String TIMESTAMP = "timestamp";
    
    public static final String MESSAGE = "message";
    
    public static final String LOGGER = "logger";
    
    public static final String THREADS = "threads";
    
    public static final String EXCEPTION = "exception";
    
    public static final String LEVEL = "level";
    
    public static final String TRANSACTION = "transaction";
    
    public static final String FINGERPRINT = "fingerprint";
    
    public static final String MODULES = "modules";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryEvent> {
    @NotNull
    public SentryEvent deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryEvent sentryEvent = new SentryEvent();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      SentryBaseEvent.Deserializer deserializer = new SentryBaseEvent.Deserializer();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Date date;
        List list;
        Map map;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "timestamp":
            date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            if (date != null)
              sentryEvent.timestamp = date; 
            continue;
          case "message":
            sentryEvent.message = param1JsonObjectReader.<Message>nextOrNull(param1ILogger, (JsonDeserializer<Message>)new Message.Deserializer());
            continue;
          case "logger":
            sentryEvent.logger = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "threads":
            param1JsonObjectReader.beginObject();
            param1JsonObjectReader.nextName();
            sentryEvent.threads = new SentryValues(param1JsonObjectReader.nextListOrNull(param1ILogger, (JsonDeserializer<?>)new SentryThread.Deserializer()));
            param1JsonObjectReader.endObject();
            continue;
          case "exception":
            param1JsonObjectReader.beginObject();
            param1JsonObjectReader.nextName();
            sentryEvent.exception = new SentryValues(param1JsonObjectReader.nextListOrNull(param1ILogger, (JsonDeserializer<?>)new SentryException.Deserializer()));
            param1JsonObjectReader.endObject();
            continue;
          case "level":
            sentryEvent.level = param1JsonObjectReader.<SentryLevel>nextOrNull(param1ILogger, new SentryLevel.Deserializer());
            continue;
          case "transaction":
            sentryEvent.transaction = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "fingerprint":
            list = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list != null)
              sentryEvent.fingerprint = list; 
            continue;
          case "modules":
            map = (Map)param1JsonObjectReader.nextObjectOrNull();
            sentryEvent.modules = CollectionUtils.newConcurrentHashMap(map);
            continue;
        } 
        if (!deserializer.deserializeValue(sentryEvent, str, param1JsonObjectReader, param1ILogger)) {
          if (concurrentHashMap == null)
            concurrentHashMap = new ConcurrentHashMap<>(); 
          param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str);
        } 
      } 
      sentryEvent.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryEvent;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */