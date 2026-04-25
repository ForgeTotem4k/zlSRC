package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class MonitorConfig implements JsonUnknown, JsonSerializable {
  @NotNull
  private MonitorSchedule schedule;
  
  @Nullable
  private Long checkinMargin;
  
  @Nullable
  private Long maxRuntime;
  
  @Nullable
  private String timezone;
  
  @Nullable
  private Long failureIssueThreshold;
  
  @Nullable
  private Long recoveryThreshold;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public MonitorConfig(@NotNull MonitorSchedule paramMonitorSchedule) {
    this.schedule = paramMonitorSchedule;
    SentryOptions.Cron cron = ScopesAdapter.getInstance().getOptions().getCron();
    if (cron != null) {
      this.checkinMargin = cron.getDefaultCheckinMargin();
      this.maxRuntime = cron.getDefaultMaxRuntime();
      this.timezone = cron.getDefaultTimezone();
      this.failureIssueThreshold = cron.getDefaultFailureIssueThreshold();
      this.recoveryThreshold = cron.getDefaultRecoveryThreshold();
    } 
  }
  
  @NotNull
  public MonitorSchedule getSchedule() {
    return this.schedule;
  }
  
  public void setSchedule(@NotNull MonitorSchedule paramMonitorSchedule) {
    this.schedule = paramMonitorSchedule;
  }
  
  @Nullable
  public Long getCheckinMargin() {
    return this.checkinMargin;
  }
  
  public void setCheckinMargin(@Nullable Long paramLong) {
    this.checkinMargin = paramLong;
  }
  
  @Nullable
  public Long getMaxRuntime() {
    return this.maxRuntime;
  }
  
  public void setMaxRuntime(@Nullable Long paramLong) {
    this.maxRuntime = paramLong;
  }
  
  @Nullable
  public String getTimezone() {
    return this.timezone;
  }
  
  public void setTimezone(@Nullable String paramString) {
    this.timezone = paramString;
  }
  
  @Nullable
  public Long getFailureIssueThreshold() {
    return this.failureIssueThreshold;
  }
  
  public void setFailureIssueThreshold(@Nullable Long paramLong) {
    this.failureIssueThreshold = paramLong;
  }
  
  @Nullable
  public Long getRecoveryThreshold() {
    return this.recoveryThreshold;
  }
  
  public void setRecoveryThreshold(@Nullable Long paramLong) {
    this.recoveryThreshold = paramLong;
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
    paramObjectWriter.name("schedule");
    this.schedule.serialize(paramObjectWriter, paramILogger);
    if (this.checkinMargin != null)
      paramObjectWriter.name("checkin_margin").value(this.checkinMargin); 
    if (this.maxRuntime != null)
      paramObjectWriter.name("max_runtime").value(this.maxRuntime); 
    if (this.timezone != null)
      paramObjectWriter.name("timezone").value(this.timezone); 
    if (this.failureIssueThreshold != null)
      paramObjectWriter.name("failure_issue_threshold").value(this.failureIssueThreshold); 
    if (this.recoveryThreshold != null)
      paramObjectWriter.name("recovery_threshold").value(this.recoveryThreshold); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String SCHEDULE = "schedule";
    
    public static final String CHECKIN_MARGIN = "checkin_margin";
    
    public static final String MAX_RUNTIME = "max_runtime";
    
    public static final String TIMEZONE = "timezone";
    
    public static final String FAILURE_ISSUE_THRESHOLD = "failure_issue_threshold";
    
    public static final String RECOVERY_THRESHOLD = "recovery_threshold";
  }
  
  public static final class Deserializer implements JsonDeserializer<MonitorConfig> {
    @NotNull
    public MonitorConfig deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      MonitorSchedule monitorSchedule = null;
      Long long_1 = null;
      Long long_2 = null;
      String str = null;
      Long long_3 = null;
      Long long_4 = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "schedule":
            monitorSchedule = (new MonitorSchedule.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "checkin_margin":
            long_1 = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "max_runtime":
            long_2 = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "timezone":
            str = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "failure_issue_threshold":
            long_3 = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "recovery_threshold":
            long_4 = param1JsonObjectReader.nextLongOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str1);
      } 
      param1JsonObjectReader.endObject();
      if (monitorSchedule == null) {
        String str1 = "Missing required field \"schedule\"";
        IllegalStateException illegalStateException = new IllegalStateException(str1);
        param1ILogger.log(SentryLevel.ERROR, str1, illegalStateException);
        throw illegalStateException;
      } 
      MonitorConfig monitorConfig = new MonitorConfig(monitorSchedule);
      monitorConfig.setCheckinMargin(long_1);
      monitorConfig.setMaxRuntime(long_2);
      monitorConfig.setTimezone(str);
      monitorConfig.setFailureIssueThreshold(long_3);
      monitorConfig.setRecoveryThreshold(long_4);
      monitorConfig.setUnknown((Map)hashMap);
      return monitorConfig;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MonitorConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */