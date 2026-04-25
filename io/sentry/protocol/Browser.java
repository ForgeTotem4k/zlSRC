package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Browser implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "browser";
  
  @Nullable
  private String name;
  
  @Nullable
  private String version;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Browser() {}
  
  Browser(@NotNull Browser paramBrowser) {
    this.name = paramBrowser.name;
    this.version = paramBrowser.version;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramBrowser.unknown);
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
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Browser browser = (Browser)paramObject;
    return (Objects.equals(this.name, browser.name) && Objects.equals(this.version, browser.version));
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
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.version != null)
      paramObjectWriter.name("version").value(this.version); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String NAME = "name";
    
    public static final String VERSION = "version";
  }
  
  public static final class Deserializer implements JsonDeserializer<Browser> {
    @NotNull
    public Browser deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Browser browser = new Browser();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            browser.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "version":
            browser.version = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      browser.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return browser;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Browser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */