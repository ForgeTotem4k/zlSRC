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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ProfileMeasurementValue implements JsonUnknown, JsonSerializable {
  @Nullable
  private Map<String, Object> unknown;
  
  @NotNull
  private String relativeStartNs;
  
  private double value;
  
  public ProfileMeasurementValue() {
    this(Long.valueOf(0L), Integer.valueOf(0));
  }
  
  public ProfileMeasurementValue(@NotNull Long paramLong, @NotNull Number paramNumber) {
    this.relativeStartNs = paramLong.toString();
    this.value = paramNumber.doubleValue();
  }
  
  public double getValue() {
    return this.value;
  }
  
  @NotNull
  public String getRelativeStartNs() {
    return this.relativeStartNs;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ProfileMeasurementValue profileMeasurementValue = (ProfileMeasurementValue)paramObject;
    return (Objects.equals(this.unknown, profileMeasurementValue.unknown) && this.relativeStartNs.equals(profileMeasurementValue.relativeStartNs) && this.value == profileMeasurementValue.value);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.unknown, this.relativeStartNs, Double.valueOf(this.value) });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("value").value(paramILogger, Double.valueOf(this.value));
    paramObjectWriter.name("elapsed_since_start_ns").value(paramILogger, this.relativeStartNs);
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
    public static final String VALUE = "value";
    
    public static final String START_NS = "elapsed_since_start_ns";
  }
  
  public static final class Deserializer implements JsonDeserializer<ProfileMeasurementValue> {
    @NotNull
    public ProfileMeasurementValue deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      ProfileMeasurementValue profileMeasurementValue = new ProfileMeasurementValue();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Double double_;
        String str2;
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "value":
            double_ = param1JsonObjectReader.nextDoubleOrNull();
            if (double_ != null)
              profileMeasurementValue.value = double_.doubleValue(); 
            continue;
          case "elapsed_since_start_ns":
            str2 = param1JsonObjectReader.nextStringOrNull();
            if (str2 != null)
              profileMeasurementValue.relativeStartNs = str2; 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str1);
      } 
      profileMeasurementValue.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return profileMeasurementValue;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\profilemeasurements\ProfileMeasurementValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */