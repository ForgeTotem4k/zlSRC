package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryRuntime implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "runtime";
  
  @Nullable
  private String name;
  
  @Nullable
  private String version;
  
  @Nullable
  private String rawDescription;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryRuntime() {}
  
  SentryRuntime(@NotNull SentryRuntime paramSentryRuntime) {
    this.name = paramSentryRuntime.name;
    this.version = paramSentryRuntime.version;
    this.rawDescription = paramSentryRuntime.rawDescription;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramSentryRuntime.unknown);
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(@Nullable String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public String getVersion() {
    return this.version;
  }
  
  public void setVersion(@Nullable String paramString) {
    this.version = paramString;
  }
  
  @Nullable
  public String getRawDescription() {
    return this.rawDescription;
  }
  
  public void setRawDescription(@Nullable String paramString) {
    this.rawDescription = paramString;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.version != null)
      paramObjectWriter.name("version").value(this.version); 
    if (this.rawDescription != null)
      paramObjectWriter.name("raw_description").value(this.rawDescription); 
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
    public static final String NAME = "name";
    
    public static final String VERSION = "version";
    
    public static final String RAW_DESCRIPTION = "raw_description";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryRuntime> {
    @NotNull
    public SentryRuntime deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryRuntime sentryRuntime = new SentryRuntime();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            sentryRuntime.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "version":
            sentryRuntime.version = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "raw_description":
            sentryRuntime.rawDescription = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      sentryRuntime.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryRuntime;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryRuntime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */