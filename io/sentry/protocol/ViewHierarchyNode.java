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

public final class ViewHierarchyNode implements JsonUnknown, JsonSerializable {
  @Nullable
  private String renderingSystem;
  
  @Nullable
  private String type;
  
  @Nullable
  private String identifier;
  
  @Nullable
  private String tag;
  
  @Nullable
  private Double width;
  
  @Nullable
  private Double height;
  
  @Nullable
  private Double x;
  
  @Nullable
  private Double y;
  
  @Nullable
  private String visibility;
  
  @Nullable
  private Double alpha;
  
  @Nullable
  private List<ViewHierarchyNode> children;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public void setRenderingSystem(String paramString) {
    this.renderingSystem = paramString;
  }
  
  public void setType(String paramString) {
    this.type = paramString;
  }
  
  public void setIdentifier(@Nullable String paramString) {
    this.identifier = paramString;
  }
  
  public void setTag(@Nullable String paramString) {
    this.tag = paramString;
  }
  
  public void setWidth(@Nullable Double paramDouble) {
    this.width = paramDouble;
  }
  
  public void setHeight(@Nullable Double paramDouble) {
    this.height = paramDouble;
  }
  
  public void setX(@Nullable Double paramDouble) {
    this.x = paramDouble;
  }
  
  public void setY(@Nullable Double paramDouble) {
    this.y = paramDouble;
  }
  
  public void setVisibility(@Nullable String paramString) {
    this.visibility = paramString;
  }
  
  public void setAlpha(@Nullable Double paramDouble) {
    this.alpha = paramDouble;
  }
  
  public void setChildren(@Nullable List<ViewHierarchyNode> paramList) {
    this.children = paramList;
  }
  
  @Nullable
  public String getRenderingSystem() {
    return this.renderingSystem;
  }
  
  @Nullable
  public String getType() {
    return this.type;
  }
  
  @Nullable
  public String getIdentifier() {
    return this.identifier;
  }
  
  @Nullable
  public String getTag() {
    return this.tag;
  }
  
  @Nullable
  public Double getWidth() {
    return this.width;
  }
  
  @Nullable
  public Double getHeight() {
    return this.height;
  }
  
  @Nullable
  public Double getX() {
    return this.x;
  }
  
  @Nullable
  public Double getY() {
    return this.y;
  }
  
  @Nullable
  public String getVisibility() {
    return this.visibility;
  }
  
  @Nullable
  public Double getAlpha() {
    return this.alpha;
  }
  
  @Nullable
  public List<ViewHierarchyNode> getChildren() {
    return this.children;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.renderingSystem != null)
      paramObjectWriter.name("rendering_system").value(this.renderingSystem); 
    if (this.type != null)
      paramObjectWriter.name("type").value(this.type); 
    if (this.identifier != null)
      paramObjectWriter.name("identifier").value(this.identifier); 
    if (this.tag != null)
      paramObjectWriter.name("tag").value(this.tag); 
    if (this.width != null)
      paramObjectWriter.name("width").value(this.width); 
    if (this.height != null)
      paramObjectWriter.name("height").value(this.height); 
    if (this.x != null)
      paramObjectWriter.name("x").value(this.x); 
    if (this.y != null)
      paramObjectWriter.name("y").value(this.y); 
    if (this.visibility != null)
      paramObjectWriter.name("visibility").value(this.visibility); 
    if (this.alpha != null)
      paramObjectWriter.name("alpha").value(this.alpha); 
    if (this.children != null && !this.children.isEmpty())
      paramObjectWriter.name("children").value(paramILogger, this.children); 
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
    
    public static final String TYPE = "type";
    
    public static final String IDENTIFIER = "identifier";
    
    public static final String TAG = "tag";
    
    public static final String WIDTH = "width";
    
    public static final String HEIGHT = "height";
    
    public static final String X = "x";
    
    public static final String Y = "y";
    
    public static final String VISIBILITY = "visibility";
    
    public static final String ALPHA = "alpha";
    
    public static final String CHILDREN = "children";
  }
  
  public static final class Deserializer implements JsonDeserializer<ViewHierarchyNode> {
    @NotNull
    public ViewHierarchyNode deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      HashMap<Object, Object> hashMap = null;
      ViewHierarchyNode viewHierarchyNode = new ViewHierarchyNode();
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "rendering_system":
            viewHierarchyNode.renderingSystem = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "type":
            viewHierarchyNode.type = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "identifier":
            viewHierarchyNode.identifier = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "tag":
            viewHierarchyNode.tag = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "width":
            viewHierarchyNode.width = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "height":
            viewHierarchyNode.height = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "x":
            viewHierarchyNode.x = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "y":
            viewHierarchyNode.y = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "visibility":
            viewHierarchyNode.visibility = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "alpha":
            viewHierarchyNode.alpha = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "children":
            viewHierarchyNode.children = param1JsonObjectReader.nextListOrNull(param1ILogger, this);
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      viewHierarchyNode.setUnknown((Map)hashMap);
      return viewHierarchyNode;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\ViewHierarchyNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */