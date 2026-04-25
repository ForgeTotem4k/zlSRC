package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Geo implements JsonUnknown, JsonSerializable {
  @Nullable
  private String city;
  
  @Nullable
  private String countryCode;
  
  @Nullable
  private String region;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Geo() {}
  
  public Geo(@NotNull Geo paramGeo) {
    this.city = paramGeo.city;
    this.countryCode = paramGeo.countryCode;
    this.region = paramGeo.region;
  }
  
  public static Geo fromMap(@NotNull Map<String, Object> paramMap) {
    Geo geo = new Geo();
    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
      Object object = entry.getValue();
      switch ((String)entry.getKey()) {
        case "city":
          geo.city = (object instanceof String) ? (String)object : null;
        case "country_code":
          geo.countryCode = (object instanceof String) ? (String)object : null;
        case "region":
          geo.region = (object instanceof String) ? (String)object : null;
      } 
    } 
    return geo;
  }
  
  @Nullable
  public String getCity() {
    return this.city;
  }
  
  public void setCity(@Nullable String paramString) {
    this.city = paramString;
  }
  
  @Nullable
  public String getCountryCode() {
    return this.countryCode;
  }
  
  public void setCountryCode(@Nullable String paramString) {
    this.countryCode = paramString;
  }
  
  @Nullable
  public String getRegion() {
    return this.region;
  }
  
  public void setRegion(@Nullable String paramString) {
    this.region = paramString;
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
    if (this.city != null)
      paramObjectWriter.name("city").value(this.city); 
    if (this.countryCode != null)
      paramObjectWriter.name("country_code").value(this.countryCode); 
    if (this.region != null)
      paramObjectWriter.name("region").value(this.region); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String CITY = "city";
    
    public static final String COUNTRY_CODE = "country_code";
    
    public static final String REGION = "region";
  }
  
  public static final class Deserializer implements JsonDeserializer<Geo> {
    public Geo deserialize(JsonObjectReader param1JsonObjectReader, ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Geo geo = new Geo();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "city":
            geo.city = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "country_code":
            geo.countryCode = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "region":
            geo.region = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      geo.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return geo;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Geo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */