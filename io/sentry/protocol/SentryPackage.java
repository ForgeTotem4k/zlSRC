package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryPackage implements JsonUnknown, JsonSerializable {
  @NotNull
  private String name;
  
  @NotNull
  private String version;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryPackage(@NotNull String paramString1, @NotNull String paramString2) {
    this.name = (String)Objects.requireNonNull(paramString1, "name is required.");
    this.version = (String)Objects.requireNonNull(paramString2, "version is required.");
  }
  
  @NotNull
  public String getName() {
    return this.name;
  }
  
  public void setName(@NotNull String paramString) {
    this.name = (String)Objects.requireNonNull(paramString, "name is required.");
  }
  
  @NotNull
  public String getVersion() {
    return this.version;
  }
  
  public void setVersion(@NotNull String paramString) {
    this.version = (String)Objects.requireNonNull(paramString, "version is required.");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SentryPackage sentryPackage = (SentryPackage)paramObject;
    return (Objects.equals(this.name, sentryPackage.name) && Objects.equals(this.version, sentryPackage.version));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.version });
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
    paramObjectWriter.name("name").value(this.name);
    paramObjectWriter.name("version").value(this.version);
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String NAME = "name";
    
    public static final String VERSION = "version";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryPackage> {
    @NotNull
    public SentryPackage deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      String str1 = null;
      String str2 = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            str1 = param1JsonObjectReader.nextString();
            continue;
          case "version":
            str2 = param1JsonObjectReader.nextString();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (str1 == null) {
        String str = "Missing required field \"name\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str2 == null) {
        String str = "Missing required field \"version\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      SentryPackage sentryPackage = new SentryPackage(str1, str2);
      sentryPackage.setUnknown((Map)hashMap);
      return sentryPackage;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryPackage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */