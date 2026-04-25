package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class CheckIn implements JsonUnknown, JsonSerializable {
  @NotNull
  private final SentryId checkInId;
  
  @NotNull
  private String monitorSlug;
  
  @NotNull
  private String status;
  
  @Nullable
  private Double duration;
  
  @Nullable
  private String release;
  
  @Nullable
  private String environment;
  
  @NotNull
  private final MonitorContexts contexts = new MonitorContexts();
  
  @Nullable
  private MonitorConfig monitorConfig;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public CheckIn(@NotNull String paramString, @NotNull CheckInStatus paramCheckInStatus) {
    this((SentryId)null, paramString, paramCheckInStatus.apiName());
  }
  
  public CheckIn(@Nullable SentryId paramSentryId, @NotNull String paramString, @NotNull CheckInStatus paramCheckInStatus) {
    this(paramSentryId, paramString, paramCheckInStatus.apiName());
  }
  
  @Internal
  public CheckIn(@Nullable SentryId paramSentryId, @NotNull String paramString1, @NotNull String paramString2) {
    this.checkInId = (paramSentryId == null) ? new SentryId() : paramSentryId;
    this.monitorSlug = paramString1;
    this.status = paramString2;
  }
  
  @NotNull
  public SentryId getCheckInId() {
    return this.checkInId;
  }
  
  @NotNull
  public String getMonitorSlug() {
    return this.monitorSlug;
  }
  
  public void setMonitorSlug(@NotNull String paramString) {
    this.monitorSlug = paramString;
  }
  
  @NotNull
  public String getStatus() {
    return this.status;
  }
  
  public void setStatus(@NotNull String paramString) {
    this.status = paramString;
  }
  
  public void setStatus(@NotNull CheckInStatus paramCheckInStatus) {
    this.status = paramCheckInStatus.apiName();
  }
  
  @Nullable
  public Double getDuration() {
    return this.duration;
  }
  
  public void setDuration(@Nullable Double paramDouble) {
    this.duration = paramDouble;
  }
  
  @Nullable
  public String getRelease() {
    return this.release;
  }
  
  public void setRelease(@Nullable String paramString) {
    this.release = paramString;
  }
  
  @Nullable
  public String getEnvironment() {
    return this.environment;
  }
  
  public void setEnvironment(@Nullable String paramString) {
    this.environment = paramString;
  }
  
  @Nullable
  public MonitorConfig getMonitorConfig() {
    return this.monitorConfig;
  }
  
  public void setMonitorConfig(@Nullable MonitorConfig paramMonitorConfig) {
    this.monitorConfig = paramMonitorConfig;
  }
  
  @NotNull
  public MonitorContexts getContexts() {
    return this.contexts;
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
    paramObjectWriter.name("check_in_id");
    this.checkInId.serialize(paramObjectWriter, paramILogger);
    paramObjectWriter.name("monitor_slug").value(this.monitorSlug);
    paramObjectWriter.name("status").value(this.status);
    if (this.duration != null)
      paramObjectWriter.name("duration").value(this.duration); 
    if (this.release != null)
      paramObjectWriter.name("release").value(this.release); 
    if (this.environment != null)
      paramObjectWriter.name("environment").value(this.environment); 
    if (this.monitorConfig != null) {
      paramObjectWriter.name("monitor_config");
      this.monitorConfig.serialize(paramObjectWriter, paramILogger);
    } 
    if (this.contexts != null) {
      paramObjectWriter.name("contexts");
      this.contexts.serialize(paramObjectWriter, paramILogger);
    } 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String CHECK_IN_ID = "check_in_id";
    
    public static final String MONITOR_SLUG = "monitor_slug";
    
    public static final String STATUS = "status";
    
    public static final String DURATION = "duration";
    
    public static final String RELEASE = "release";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String CONTEXTS = "contexts";
    
    public static final String MONITOR_CONFIG = "monitor_config";
  }
  
  public static final class Deserializer implements JsonDeserializer<CheckIn> {
    @NotNull
    public CheckIn deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryId sentryId = null;
      MonitorConfig monitorConfig = null;
      String str1 = null;
      String str2 = null;
      Double double_ = null;
      String str3 = null;
      String str4 = null;
      MonitorContexts monitorContexts = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "check_in_id":
            sentryId = (new SentryId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "monitor_slug":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "status":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "duration":
            double_ = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "release":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "environment":
            str4 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "monitor_config":
            monitorConfig = (new MonitorConfig.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "contexts":
            monitorContexts = (new MonitorContexts.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (sentryId == null) {
        String str = "Missing required field \"check_in_id\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str1 == null) {
        String str = "Missing required field \"monitor_slug\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str2 == null) {
        String str = "Missing required field \"status\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      CheckIn checkIn = new CheckIn(sentryId, str1, str2);
      checkIn.setDuration(double_);
      checkIn.setRelease(str3);
      checkIn.setEnvironment(str4);
      checkIn.setMonitorConfig(monitorConfig);
      checkIn.getContexts().putAll(monitorContexts);
      checkIn.setUnknown((Map)hashMap);
      return checkIn;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CheckIn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */