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

public final class OperatingSystem implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "os";
  
  @Nullable
  private String name;
  
  @Nullable
  private String version;
  
  @Nullable
  private String rawDescription;
  
  @Nullable
  private String build;
  
  @Nullable
  private String kernelVersion;
  
  @Nullable
  private Boolean rooted;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public OperatingSystem() {}
  
  OperatingSystem(@NotNull OperatingSystem paramOperatingSystem) {
    this.name = paramOperatingSystem.name;
    this.version = paramOperatingSystem.version;
    this.rawDescription = paramOperatingSystem.rawDescription;
    this.build = paramOperatingSystem.build;
    this.kernelVersion = paramOperatingSystem.kernelVersion;
    this.rooted = paramOperatingSystem.rooted;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramOperatingSystem.unknown);
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
  
  @Nullable
  public String getBuild() {
    return this.build;
  }
  
  public void setBuild(@Nullable String paramString) {
    this.build = paramString;
  }
  
  @Nullable
  public String getKernelVersion() {
    return this.kernelVersion;
  }
  
  public void setKernelVersion(@Nullable String paramString) {
    this.kernelVersion = paramString;
  }
  
  @Nullable
  public Boolean isRooted() {
    return this.rooted;
  }
  
  public void setRooted(@Nullable Boolean paramBoolean) {
    this.rooted = paramBoolean;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    OperatingSystem operatingSystem = (OperatingSystem)paramObject;
    return (Objects.equals(this.name, operatingSystem.name) && Objects.equals(this.version, operatingSystem.version) && Objects.equals(this.rawDescription, operatingSystem.rawDescription) && Objects.equals(this.build, operatingSystem.build) && Objects.equals(this.kernelVersion, operatingSystem.kernelVersion) && Objects.equals(this.rooted, operatingSystem.rooted));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.version, this.rawDescription, this.build, this.kernelVersion, this.rooted });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.version != null)
      paramObjectWriter.name("version").value(this.version); 
    if (this.rawDescription != null)
      paramObjectWriter.name("raw_description").value(this.rawDescription); 
    if (this.build != null)
      paramObjectWriter.name("build").value(this.build); 
    if (this.kernelVersion != null)
      paramObjectWriter.name("kernel_version").value(this.kernelVersion); 
    if (this.rooted != null)
      paramObjectWriter.name("rooted").value(this.rooted); 
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
    
    public static final String BUILD = "build";
    
    public static final String KERNEL_VERSION = "kernel_version";
    
    public static final String ROOTED = "rooted";
  }
  
  public static final class Deserializer implements JsonDeserializer<OperatingSystem> {
    @NotNull
    public OperatingSystem deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      OperatingSystem operatingSystem = new OperatingSystem();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            operatingSystem.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "version":
            operatingSystem.version = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "raw_description":
            operatingSystem.rawDescription = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "build":
            operatingSystem.build = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "kernel_version":
            operatingSystem.kernelVersion = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "rooted":
            operatingSystem.rooted = param1JsonObjectReader.nextBooleanOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      operatingSystem.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return operatingSystem;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\OperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */