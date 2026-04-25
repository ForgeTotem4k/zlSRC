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

public final class Gpu implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "gpu";
  
  @Nullable
  private String name;
  
  @Nullable
  private Integer id;
  
  @Nullable
  private String vendorId;
  
  @Nullable
  private String vendorName;
  
  @Nullable
  private Integer memorySize;
  
  @Nullable
  private String apiType;
  
  @Nullable
  private Boolean multiThreadedRendering;
  
  @Nullable
  private String version;
  
  @Nullable
  private String npotSupport;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Gpu() {}
  
  Gpu(@NotNull Gpu paramGpu) {
    this.name = paramGpu.name;
    this.id = paramGpu.id;
    this.vendorId = paramGpu.vendorId;
    this.vendorName = paramGpu.vendorName;
    this.memorySize = paramGpu.memorySize;
    this.apiType = paramGpu.apiType;
    this.multiThreadedRendering = paramGpu.multiThreadedRendering;
    this.version = paramGpu.version;
    this.npotSupport = paramGpu.npotSupport;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramGpu.unknown);
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public Integer getId() {
    return this.id;
  }
  
  public void setId(Integer paramInteger) {
    this.id = paramInteger;
  }
  
  @Nullable
  public String getVendorId() {
    return this.vendorId;
  }
  
  public void setVendorId(@Nullable String paramString) {
    this.vendorId = paramString;
  }
  
  @Nullable
  public String getVendorName() {
    return this.vendorName;
  }
  
  public void setVendorName(@Nullable String paramString) {
    this.vendorName = paramString;
  }
  
  @Nullable
  public Integer getMemorySize() {
    return this.memorySize;
  }
  
  public void setMemorySize(@Nullable Integer paramInteger) {
    this.memorySize = paramInteger;
  }
  
  @Nullable
  public String getApiType() {
    return this.apiType;
  }
  
  public void setApiType(@Nullable String paramString) {
    this.apiType = paramString;
  }
  
  @Nullable
  public Boolean isMultiThreadedRendering() {
    return this.multiThreadedRendering;
  }
  
  public void setMultiThreadedRendering(@Nullable Boolean paramBoolean) {
    this.multiThreadedRendering = paramBoolean;
  }
  
  @Nullable
  public String getVersion() {
    return this.version;
  }
  
  public void setVersion(@Nullable String paramString) {
    this.version = paramString;
  }
  
  @Nullable
  public String getNpotSupport() {
    return this.npotSupport;
  }
  
  public void setNpotSupport(@Nullable String paramString) {
    this.npotSupport = paramString;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Gpu gpu = (Gpu)paramObject;
    return (Objects.equals(this.name, gpu.name) && Objects.equals(this.id, gpu.id) && Objects.equals(this.vendorId, gpu.vendorId) && Objects.equals(this.vendorName, gpu.vendorName) && Objects.equals(this.memorySize, gpu.memorySize) && Objects.equals(this.apiType, gpu.apiType) && Objects.equals(this.multiThreadedRendering, gpu.multiThreadedRendering) && Objects.equals(this.version, gpu.version) && Objects.equals(this.npotSupport, gpu.npotSupport));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.id, this.vendorId, this.vendorName, this.memorySize, this.apiType, this.multiThreadedRendering, this.version, this.npotSupport });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.id != null)
      paramObjectWriter.name("id").value(this.id); 
    if (this.vendorId != null)
      paramObjectWriter.name("vendor_id").value(this.vendorId); 
    if (this.vendorName != null)
      paramObjectWriter.name("vendor_name").value(this.vendorName); 
    if (this.memorySize != null)
      paramObjectWriter.name("memory_size").value(this.memorySize); 
    if (this.apiType != null)
      paramObjectWriter.name("api_type").value(this.apiType); 
    if (this.multiThreadedRendering != null)
      paramObjectWriter.name("multi_threaded_rendering").value(this.multiThreadedRendering); 
    if (this.version != null)
      paramObjectWriter.name("version").value(this.version); 
    if (this.npotSupport != null)
      paramObjectWriter.name("npot_support").value(this.npotSupport); 
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
    
    public static final String ID = "id";
    
    public static final String VENDOR_ID = "vendor_id";
    
    public static final String VENDOR_NAME = "vendor_name";
    
    public static final String MEMORY_SIZE = "memory_size";
    
    public static final String API_TYPE = "api_type";
    
    public static final String MULTI_THREADED_RENDERING = "multi_threaded_rendering";
    
    public static final String VERSION = "version";
    
    public static final String NPOT_SUPPORT = "npot_support";
  }
  
  public static final class Deserializer implements JsonDeserializer<Gpu> {
    @NotNull
    public Gpu deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Gpu gpu = new Gpu();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            gpu.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "id":
            gpu.id = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "vendor_id":
            gpu.vendorId = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "vendor_name":
            gpu.vendorName = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "memory_size":
            gpu.memorySize = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "api_type":
            gpu.apiType = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "multi_threaded_rendering":
            gpu.multiThreadedRendering = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "version":
            gpu.version = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "npot_support":
            gpu.npotSupport = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      gpu.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return gpu;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Gpu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */