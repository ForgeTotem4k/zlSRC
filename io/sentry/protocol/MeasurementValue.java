package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class MeasurementValue implements JsonUnknown, JsonSerializable {
  public static final String KEY_APP_START_COLD = "app_start_cold";
  
  public static final String KEY_APP_START_WARM = "app_start_warm";
  
  public static final String KEY_FRAMES_TOTAL = "frames_total";
  
  public static final String KEY_FRAMES_SLOW = "frames_slow";
  
  public static final String KEY_FRAMES_FROZEN = "frames_frozen";
  
  public static final String KEY_FRAMES_DELAY = "frames_delay";
  
  public static final String KEY_TIME_TO_INITIAL_DISPLAY = "time_to_initial_display";
  
  public static final String KEY_TIME_TO_FULL_DISPLAY = "time_to_full_display";
  
  @NotNull
  private final Number value;
  
  @Nullable
  private final String unit;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public MeasurementValue(@NotNull Number paramNumber, @Nullable String paramString) {
    this.value = paramNumber;
    this.unit = paramString;
  }
  
  @TestOnly
  public MeasurementValue(@NotNull Number paramNumber, @Nullable String paramString, @Nullable Map<String, Object> paramMap) {
    this.value = paramNumber;
    this.unit = paramString;
    this.unknown = paramMap;
  }
  
  @TestOnly
  @NotNull
  public Number getValue() {
    return this.value;
  }
  
  @Nullable
  public String getUnit() {
    return this.unit;
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
    paramObjectWriter.name("value").value(this.value);
    if (this.unit != null)
      paramObjectWriter.name("unit").value(this.unit); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String VALUE = "value";
    
    public static final String UNIT = "unit";
  }
  
  public static final class Deserializer implements JsonDeserializer<MeasurementValue> {
    @NotNull
    public MeasurementValue deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      String str = null;
      Number number = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "value":
            number = (Number)param1JsonObjectReader.nextObjectOrNull();
            continue;
          case "unit":
            str = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str1);
      } 
      param1JsonObjectReader.endObject();
      if (number == null) {
        String str1 = "Missing required field \"value\"";
        IllegalStateException illegalStateException = new IllegalStateException("Missing required field \"value\"");
        param1ILogger.log(SentryLevel.ERROR, "Missing required field \"value\"", illegalStateException);
        throw illegalStateException;
      } 
      MeasurementValue measurementValue = new MeasurementValue(number, str);
      measurementValue.setUnknown((Map)concurrentHashMap);
      return measurementValue;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\MeasurementValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */