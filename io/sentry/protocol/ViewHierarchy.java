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
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ViewHierarchy implements JsonUnknown, JsonSerializable {
  @Nullable
  private final String renderingSystem;
  
  @Nullable
  private final List<ViewHierarchyNode> windows;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public ViewHierarchy(@Nullable String paramString, @Nullable List<ViewHierarchyNode> paramList) {
    this.renderingSystem = paramString;
    this.windows = paramList;
  }
  
  @Nullable
  public String getRenderingSystem() {
    return this.renderingSystem;
  }
  
  @Nullable
  public List<ViewHierarchyNode> getWindows() {
    return this.windows;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.renderingSystem != null)
      paramObjectWriter.name("rendering_system").value(this.renderingSystem); 
    if (this.windows != null)
      paramObjectWriter.name("windows").value(paramILogger, this.windows); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
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
    public static final String RENDERING_SYSTEM = "rendering_system";
    
    public static final String WINDOWS = "windows";
  }
  
  public static final class Deserializer implements JsonDeserializer<ViewHierarchy> {
    @NotNull
    public ViewHierarchy deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      String str = null;
      List<ViewHierarchyNode> list = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "rendering_system":
            str = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "windows":
            list = param1JsonObjectReader.nextListOrNull(param1ILogger, new ViewHierarchyNode.Deserializer());
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str1);
      } 
      param1JsonObjectReader.endObject();
      ViewHierarchy viewHierarchy = new ViewHierarchy(str, list);
      viewHierarchy.setUnknown((Map)hashMap);
      return viewHierarchy;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\ViewHierarchy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */