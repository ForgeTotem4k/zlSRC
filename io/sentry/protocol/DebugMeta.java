package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugMeta implements JsonUnknown, JsonSerializable {
  @Nullable
  private SdkInfo sdkInfo;
  
  @Nullable
  private List<DebugImage> images;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public List<DebugImage> getImages() {
    return this.images;
  }
  
  public void setImages(@Nullable List<DebugImage> paramList) {
    this.images = (paramList != null) ? new ArrayList<>(paramList) : null;
  }
  
  @Nullable
  public SdkInfo getSdkInfo() {
    return this.sdkInfo;
  }
  
  public void setSdkInfo(@Nullable SdkInfo paramSdkInfo) {
    this.sdkInfo = paramSdkInfo;
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
    if (this.sdkInfo != null)
      paramObjectWriter.name("sdk_info").value(paramILogger, this.sdkInfo); 
    if (this.images != null)
      paramObjectWriter.name("images").value(paramILogger, this.images); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String SDK_INFO = "sdk_info";
    
    public static final String IMAGES = "images";
  }
  
  public static final class Deserializer implements JsonDeserializer<DebugMeta> {
    @NotNull
    public DebugMeta deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      DebugMeta debugMeta = new DebugMeta();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "sdk_info":
            debugMeta.sdkInfo = (SdkInfo)param1JsonObjectReader.nextOrNull(param1ILogger, new SdkInfo.Deserializer());
            continue;
          case "images":
            debugMeta.images = param1JsonObjectReader.nextListOrNull(param1ILogger, new DebugImage.Deserializer());
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      debugMeta.setUnknown((Map)hashMap);
      return debugMeta;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\DebugMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */