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
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Mechanism implements JsonUnknown, JsonSerializable {
  @Nullable
  private final transient Thread thread;
  
  @Nullable
  private String type;
  
  @Nullable
  private String description;
  
  @Nullable
  private String helpLink;
  
  @Nullable
  private Boolean handled;
  
  @Nullable
  private Map<String, Object> meta;
  
  @Nullable
  private Map<String, Object> data;
  
  @Nullable
  private Boolean synthetic;
  
  @Nullable
  private Integer exceptionId;
  
  @Nullable
  private Integer parentId;
  
  @Nullable
  private Boolean exceptionGroup;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Mechanism() {
    this(null);
  }
  
  public Mechanism(@Nullable Thread paramThread) {
    this.thread = paramThread;
  }
  
  @Nullable
  public String getType() {
    return this.type;
  }
  
  public void setType(@Nullable String paramString) {
    this.type = paramString;
  }
  
  @Nullable
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(@Nullable String paramString) {
    this.description = paramString;
  }
  
  @Nullable
  public String getHelpLink() {
    return this.helpLink;
  }
  
  public void setHelpLink(@Nullable String paramString) {
    this.helpLink = paramString;
  }
  
  @Nullable
  public Boolean isHandled() {
    return this.handled;
  }
  
  public void setHandled(@Nullable Boolean paramBoolean) {
    this.handled = paramBoolean;
  }
  
  @Nullable
  public Map<String, Object> getMeta() {
    return this.meta;
  }
  
  public void setMeta(@Nullable Map<String, Object> paramMap) {
    this.meta = CollectionUtils.newHashMap(paramMap);
  }
  
  @Nullable
  public Map<String, Object> getData() {
    return this.data;
  }
  
  public void setData(@Nullable Map<String, Object> paramMap) {
    this.data = CollectionUtils.newHashMap(paramMap);
  }
  
  @Nullable
  Thread getThread() {
    return this.thread;
  }
  
  @Nullable
  public Boolean getSynthetic() {
    return this.synthetic;
  }
  
  public void setSynthetic(@Nullable Boolean paramBoolean) {
    this.synthetic = paramBoolean;
  }
  
  @Nullable
  public Integer getExceptionId() {
    return this.exceptionId;
  }
  
  public void setExceptionId(@Nullable Integer paramInteger) {
    this.exceptionId = paramInteger;
  }
  
  @Nullable
  public Integer getParentId() {
    return this.parentId;
  }
  
  public void setParentId(@Nullable Integer paramInteger) {
    this.parentId = paramInteger;
  }
  
  @Nullable
  public Boolean isExceptionGroup() {
    return this.exceptionGroup;
  }
  
  public void setExceptionGroup(@Nullable Boolean paramBoolean) {
    this.exceptionGroup = paramBoolean;
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
    if (this.type != null)
      paramObjectWriter.name("type").value(this.type); 
    if (this.description != null)
      paramObjectWriter.name("description").value(this.description); 
    if (this.helpLink != null)
      paramObjectWriter.name("help_link").value(this.helpLink); 
    if (this.handled != null)
      paramObjectWriter.name("handled").value(this.handled); 
    if (this.meta != null)
      paramObjectWriter.name("meta").value(paramILogger, this.meta); 
    if (this.data != null)
      paramObjectWriter.name("data").value(paramILogger, this.data); 
    if (this.synthetic != null)
      paramObjectWriter.name("synthetic").value(this.synthetic); 
    if (this.exceptionId != null)
      paramObjectWriter.name("exception_id").value(paramILogger, this.exceptionId); 
    if (this.parentId != null)
      paramObjectWriter.name("parent_id").value(paramILogger, this.parentId); 
    if (this.exceptionGroup != null)
      paramObjectWriter.name("is_exception_group").value(this.exceptionGroup); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TYPE = "type";
    
    public static final String DESCRIPTION = "description";
    
    public static final String HELP_LINK = "help_link";
    
    public static final String HANDLED = "handled";
    
    public static final String META = "meta";
    
    public static final String DATA = "data";
    
    public static final String SYNTHETIC = "synthetic";
    
    public static final String EXCEPTION_ID = "exception_id";
    
    public static final String PARENT_ID = "parent_id";
    
    public static final String IS_EXCEPTION_GROUP = "is_exception_group";
  }
  
  public static final class Deserializer implements JsonDeserializer<Mechanism> {
    @NotNull
    public Mechanism deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      Mechanism mechanism = new Mechanism();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "type":
            mechanism.type = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "description":
            mechanism.description = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "help_link":
            mechanism.helpLink = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "handled":
            mechanism.handled = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "meta":
            mechanism.meta = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "data":
            mechanism.data = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "synthetic":
            mechanism.synthetic = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "exception_id":
            mechanism.exceptionId = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "parent_id":
            mechanism.parentId = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "is_exception_group":
            mechanism.exceptionGroup = param1JsonObjectReader.nextBooleanOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      mechanism.setUnknown((Map)hashMap);
      return mechanism;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Mechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */