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

public final class SdkInfo implements JsonUnknown, JsonSerializable {
  @Nullable
  private String sdkName;
  
  @Nullable
  private Integer versionMajor;
  
  @Nullable
  private Integer versionMinor;
  
  @Nullable
  private Integer versionPatchlevel;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public String getSdkName() {
    return this.sdkName;
  }
  
  public void setSdkName(@Nullable String paramString) {
    this.sdkName = paramString;
  }
  
  @Nullable
  public Integer getVersionMajor() {
    return this.versionMajor;
  }
  
  public void setVersionMajor(@Nullable Integer paramInteger) {
    this.versionMajor = paramInteger;
  }
  
  @Nullable
  public Integer getVersionMinor() {
    return this.versionMinor;
  }
  
  public void setVersionMinor(@Nullable Integer paramInteger) {
    this.versionMinor = paramInteger;
  }
  
  @Nullable
  public Integer getVersionPatchlevel() {
    return this.versionPatchlevel;
  }
  
  public void setVersionPatchlevel(@Nullable Integer paramInteger) {
    this.versionPatchlevel = paramInteger;
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
    if (this.sdkName != null)
      paramObjectWriter.name("sdk_name").value(this.sdkName); 
    if (this.versionMajor != null)
      paramObjectWriter.name("version_major").value(this.versionMajor); 
    if (this.versionMinor != null)
      paramObjectWriter.name("version_minor").value(this.versionMinor); 
    if (this.versionPatchlevel != null)
      paramObjectWriter.name("version_patchlevel").value(this.versionPatchlevel); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String SDK_NAME = "sdk_name";
    
    public static final String VERSION_MAJOR = "version_major";
    
    public static final String VERSION_MINOR = "version_minor";
    
    public static final String VERSION_PATCHLEVEL = "version_patchlevel";
  }
  
  public static final class Deserializer implements JsonDeserializer<SdkInfo> {
    @NotNull
    public SdkInfo deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SdkInfo sdkInfo = new SdkInfo();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "sdk_name":
            sdkInfo.sdkName = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "version_major":
            sdkInfo.versionMajor = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "version_minor":
            sdkInfo.versionMinor = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "version_patchlevel":
            sdkInfo.versionPatchlevel = param1JsonObjectReader.nextIntegerOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      sdkInfo.setUnknown((Map)hashMap);
      return sdkInfo;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SdkInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */