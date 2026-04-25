package io.sentry.profilemeasurements;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ProfileMeasurement implements JsonUnknown, JsonSerializable {
  public static final String ID_FROZEN_FRAME_RENDERS = "frozen_frame_renders";
  
  public static final String ID_SLOW_FRAME_RENDERS = "slow_frame_renders";
  
  public static final String ID_SCREEN_FRAME_RATES = "screen_frame_rates";
  
  public static final String ID_CPU_USAGE = "cpu_usage";
  
  public static final String ID_MEMORY_FOOTPRINT = "memory_footprint";
  
  public static final String ID_MEMORY_NATIVE_FOOTPRINT = "memory_native_footprint";
  
  public static final String ID_UNKNOWN = "unknown";
  
  public static final String UNIT_HZ = "hz";
  
  public static final String UNIT_NANOSECONDS = "nanosecond";
  
  public static final String UNIT_BYTES = "byte";
  
  public static final String UNIT_PERCENT = "percent";
  
  public static final String UNIT_UNKNOWN = "unknown";
  
  @Nullable
  private Map<String, Object> unknown;
  
  @NotNull
  private String unit;
  
  @NotNull
  private Collection<ProfileMeasurementValue> values;
  
  public ProfileMeasurement() {
    this("unknown", new ArrayList<>());
  }
  
  public ProfileMeasurement(@NotNull String paramString, @NotNull Collection<ProfileMeasurementValue> paramCollection) {
    this.unit = paramString;
    this.values = paramCollection;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ProfileMeasurement profileMeasurement = (ProfileMeasurement)paramObject;
    return (Objects.equals(this.unknown, profileMeasurement.unknown) && this.unit.equals(profileMeasurement.unit) && (new ArrayList(this.values)).equals(new ArrayList<>(profileMeasurement.values)));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.unknown, this.unit, this.values });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("unit").value(paramILogger, this.unit);
    paramObjectWriter.name("values").value(paramILogger, this.values);
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
  
  @NotNull
  public String getUnit() {
    return this.unit;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void setUnit(@NotNull String paramString) {
    this.unit = paramString;
  }
  
  @NotNull
  public Collection<ProfileMeasurementValue> getValues() {
    return this.values;
  }
  
  public void setValues(@NotNull Collection<ProfileMeasurementValue> paramCollection) {
    this.values = paramCollection;
  }
  
  public static final class JsonKeys {
    public static final String UNIT = "unit";
    
    public static final String VALUES = "values";
  }
  
  public static final class Deserializer implements JsonDeserializer<ProfileMeasurement> {
    @NotNull
    public ProfileMeasurement deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      ProfileMeasurement profileMeasurement = new ProfileMeasurement();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str2;
        List list;
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "unit":
            str2 = param1JsonObjectReader.nextStringOrNull();
            if (str2 != null)
              profileMeasurement.unit = str2; 
            continue;
          case "values":
            list = param1JsonObjectReader.nextListOrNull(param1ILogger, new ProfileMeasurementValue.Deserializer());
            if (list != null)
              profileMeasurement.values = list; 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str1);
      } 
      profileMeasurement.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return profileMeasurement;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\profilemeasurements\ProfileMeasurement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */