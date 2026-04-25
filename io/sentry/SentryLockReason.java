package io.sentry;

import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLockReason implements JsonUnknown, JsonSerializable {
  public static final int LOCKED = 1;
  
  public static final int WAITING = 2;
  
  public static final int SLEEPING = 4;
  
  public static final int BLOCKED = 8;
  
  public static final int ANY = 15;
  
  private int type;
  
  @Nullable
  private String address;
  
  @Nullable
  private String packageName;
  
  @Nullable
  private String className;
  
  @Nullable
  private Long threadId;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryLockReason() {}
  
  public SentryLockReason(@NotNull SentryLockReason paramSentryLockReason) {
    this.type = paramSentryLockReason.type;
    this.address = paramSentryLockReason.address;
    this.packageName = paramSentryLockReason.packageName;
    this.className = paramSentryLockReason.className;
    this.threadId = paramSentryLockReason.threadId;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramSentryLockReason.unknown);
  }
  
  public int getType() {
    return this.type;
  }
  
  public void setType(int paramInt) {
    this.type = paramInt;
  }
  
  @Nullable
  public String getAddress() {
    return this.address;
  }
  
  public void setAddress(@Nullable String paramString) {
    this.address = paramString;
  }
  
  @Nullable
  public String getPackageName() {
    return this.packageName;
  }
  
  public void setPackageName(@Nullable String paramString) {
    this.packageName = paramString;
  }
  
  @Nullable
  public String getClassName() {
    return this.className;
  }
  
  public void setClassName(@Nullable String paramString) {
    this.className = paramString;
  }
  
  @Nullable
  public Long getThreadId() {
    return this.threadId;
  }
  
  public void setThreadId(@Nullable Long paramLong) {
    this.threadId = paramLong;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SentryLockReason sentryLockReason = (SentryLockReason)paramObject;
    return Objects.equals(this.address, sentryLockReason.address);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.address });
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
    paramObjectWriter.name("type").value(this.type);
    if (this.address != null)
      paramObjectWriter.name("address").value(this.address); 
    if (this.packageName != null)
      paramObjectWriter.name("package_name").value(this.packageName); 
    if (this.className != null)
      paramObjectWriter.name("class_name").value(this.className); 
    if (this.threadId != null)
      paramObjectWriter.name("thread_id").value(this.threadId); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TYPE = "type";
    
    public static final String ADDRESS = "address";
    
    public static final String PACKAGE_NAME = "package_name";
    
    public static final String CLASS_NAME = "class_name";
    
    public static final String THREAD_ID = "thread_id";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryLockReason> {
    @NotNull
    public SentryLockReason deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryLockReason sentryLockReason = new SentryLockReason();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "type":
            sentryLockReason.type = param1JsonObjectReader.nextInt();
            continue;
          case "address":
            sentryLockReason.address = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "package_name":
            sentryLockReason.packageName = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "class_name":
            sentryLockReason.className = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "thread_id":
            sentryLockReason.threadId = param1JsonObjectReader.nextLongOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str);
      } 
      sentryLockReason.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryLockReason;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryLockReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */