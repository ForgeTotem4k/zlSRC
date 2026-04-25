package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class MonitorSchedule implements JsonUnknown, JsonSerializable {
  @NotNull
  private String type;
  
  @NotNull
  private String value;
  
  @Nullable
  private String unit;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @NotNull
  public static MonitorSchedule crontab(@NotNull String paramString) {
    return new MonitorSchedule(MonitorScheduleType.CRONTAB.apiName(), paramString, null);
  }
  
  @NotNull
  public static MonitorSchedule interval(@NotNull Integer paramInteger, @NotNull MonitorScheduleUnit paramMonitorScheduleUnit) {
    return new MonitorSchedule(MonitorScheduleType.INTERVAL.apiName(), paramInteger.toString(), paramMonitorScheduleUnit.apiName());
  }
  
  @Internal
  public MonitorSchedule(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3) {
    this.type = paramString1;
    this.value = paramString2;
    this.unit = paramString3;
  }
  
  @NotNull
  public String getType() {
    return this.type;
  }
  
  public void setType(@NotNull String paramString) {
    this.type = paramString;
  }
  
  @NotNull
  public String getValue() {
    return this.value;
  }
  
  public void setValue(@NotNull String paramString) {
    this.value = paramString;
  }
  
  public void setValue(@NotNull Integer paramInteger) {
    this.value = paramInteger.toString();
  }
  
  @Nullable
  public String getUnit() {
    return this.unit;
  }
  
  public void setUnit(@Nullable String paramString) {
    this.unit = paramString;
  }
  
  public void setUnit(@Nullable MonitorScheduleUnit paramMonitorScheduleUnit) {
    this.unit = (paramMonitorScheduleUnit == null) ? null : paramMonitorScheduleUnit.apiName();
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
    if (MonitorScheduleType.INTERVAL.apiName().equalsIgnoreCase(this.type)) {
      try {
        paramObjectWriter.name("value").value(Integer.valueOf(this.value));
      } catch (Throwable throwable) {
        paramILogger.log(SentryLevel.ERROR, "Unable to serialize monitor schedule value: %s", new Object[] { this.value });
      } 
    } else {
      paramObjectWriter.name("value").value(this.value);
    } 
    if (this.unit != null)
      paramObjectWriter.name("unit").value(this.unit); 
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
    
    public static final String UNIT = "unit";
  }
  
  public static final class Deserializer implements JsonDeserializer<MonitorSchedule> {
    @NotNull
    public MonitorSchedule deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "type":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "value":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "unit":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (str1 == null) {
        String str = "Missing required field \"type\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str2 == null) {
        String str = "Missing required field \"value\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      MonitorSchedule monitorSchedule = new MonitorSchedule(str1, str2, str3);
      monitorSchedule.setUnknown((Map)hashMap);
      return monitorSchedule;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MonitorSchedule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */