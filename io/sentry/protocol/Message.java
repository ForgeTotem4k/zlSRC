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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Message implements JsonUnknown, JsonSerializable {
  @Nullable
  private String formatted;
  
  @Nullable
  private String message;
  
  @Nullable
  private List<String> params;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  public String getFormatted() {
    return this.formatted;
  }
  
  public void setFormatted(@Nullable String paramString) {
    this.formatted = paramString;
  }
  
  @Nullable
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(@Nullable String paramString) {
    this.message = paramString;
  }
  
  @Nullable
  public List<String> getParams() {
    return this.params;
  }
  
  public void setParams(@Nullable List<String> paramList) {
    this.params = CollectionUtils.newArrayList(paramList);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.formatted != null)
      paramObjectWriter.name("formatted").value(this.formatted); 
    if (this.message != null)
      paramObjectWriter.name("message").value(this.message); 
    if (this.params != null && !this.params.isEmpty())
      paramObjectWriter.name("params").value(paramILogger, this.params); 
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
    public static final String FORMATTED = "formatted";
    
    public static final String MESSAGE = "message";
    
    public static final String PARAMS = "params";
  }
  
  public static final class Deserializer implements JsonDeserializer<Message> {
    @NotNull
    public Message deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Message message = new Message();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List list;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "formatted":
            message.formatted = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "message":
            message.message = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "params":
            list = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list != null)
              message.params = list; 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      message.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return message;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */