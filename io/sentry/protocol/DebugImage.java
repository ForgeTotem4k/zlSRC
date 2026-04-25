package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugImage implements JsonUnknown, JsonSerializable {
  public static final String PROGUARD = "proguard";
  
  public static final String JVM = "jvm";
  
  @Nullable
  private String uuid;
  
  @Nullable
  private String type;
  
  @Nullable
  private String debugId;
  
  @Nullable
  private String debugFile;
  
  @Nullable
  private String codeId;
  
  @Nullable
  private String codeFile;
  
  @Nullable
  private String imageAddr;
  
  @Nullable
  private Long imageSize;
  
  @Nullable
  private String arch;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public String getUuid() {
    return this.uuid;
  }
  
  public void setUuid(@Nullable String paramString) {
    this.uuid = paramString;
  }
  
  @Nullable
  public String getType() {
    return this.type;
  }
  
  public void setType(@Nullable String paramString) {
    this.type = paramString;
  }
  
  @Nullable
  public String getDebugId() {
    return this.debugId;
  }
  
  public void setDebugId(@Nullable String paramString) {
    this.debugId = paramString;
  }
  
  @Nullable
  public String getDebugFile() {
    return this.debugFile;
  }
  
  public void setDebugFile(@Nullable String paramString) {
    this.debugFile = paramString;
  }
  
  @Nullable
  public String getCodeFile() {
    return this.codeFile;
  }
  
  public void setCodeFile(@Nullable String paramString) {
    this.codeFile = paramString;
  }
  
  @Nullable
  public String getImageAddr() {
    return this.imageAddr;
  }
  
  public void setImageAddr(@Nullable String paramString) {
    this.imageAddr = paramString;
  }
  
  @Nullable
  public Long getImageSize() {
    return this.imageSize;
  }
  
  public void setImageSize(@Nullable Long paramLong) {
    this.imageSize = paramLong;
  }
  
  public void setImageSize(long paramLong) {
    this.imageSize = Long.valueOf(paramLong);
  }
  
  @Nullable
  public String getArch() {
    return this.arch;
  }
  
  public void setArch(@Nullable String paramString) {
    this.arch = paramString;
  }
  
  @Nullable
  public String getCodeId() {
    return this.codeId;
  }
  
  public void setCodeId(@Nullable String paramString) {
    this.codeId = paramString;
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
    if (this.uuid != null)
      paramObjectWriter.name("uuid").value(this.uuid); 
    if (this.type != null)
      paramObjectWriter.name("type").value(this.type); 
    if (this.debugId != null)
      paramObjectWriter.name("debug_id").value(this.debugId); 
    if (this.debugFile != null)
      paramObjectWriter.name("debug_file").value(this.debugFile); 
    if (this.codeId != null)
      paramObjectWriter.name("code_id").value(this.codeId); 
    if (this.codeFile != null)
      paramObjectWriter.name("code_file").value(this.codeFile); 
    if (this.imageAddr != null)
      paramObjectWriter.name("image_addr").value(this.imageAddr); 
    if (this.imageSize != null)
      paramObjectWriter.name("image_size").value(this.imageSize); 
    if (this.arch != null)
      paramObjectWriter.name("arch").value(this.arch); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String UUID = "uuid";
    
    public static final String TYPE = "type";
    
    public static final String DEBUG_ID = "debug_id";
    
    public static final String DEBUG_FILE = "debug_file";
    
    public static final String CODE_ID = "code_id";
    
    public static final String CODE_FILE = "code_file";
    
    public static final String IMAGE_ADDR = "image_addr";
    
    public static final String IMAGE_SIZE = "image_size";
    
    public static final String ARCH = "arch";
  }
  
  public static final class Deserializer implements JsonDeserializer<DebugImage> {
    @NotNull
    public DebugImage deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      DebugImage debugImage = new DebugImage();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "uuid":
            debugImage.uuid = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "type":
            debugImage.type = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "debug_id":
            debugImage.debugId = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "debug_file":
            debugImage.debugFile = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "code_id":
            debugImage.codeId = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "code_file":
            debugImage.codeFile = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "image_addr":
            debugImage.imageAddr = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "image_size":
            debugImage.imageSize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "arch":
            debugImage.arch = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      debugImage.setUnknown((Map)hashMap);
      return debugImage;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\DebugImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */