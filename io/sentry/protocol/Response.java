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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Response implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "response";
  
  @Nullable
  private String cookies;
  
  @Nullable
  private Map<String, String> headers;
  
  @Nullable
  private Integer statusCode;
  
  @Nullable
  private Long bodySize;
  
  @Nullable
  private Object data;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Response() {}
  
  public Response(@NotNull Response paramResponse) {
    this.cookies = paramResponse.cookies;
    this.headers = CollectionUtils.newConcurrentHashMap(paramResponse.headers);
    this.unknown = CollectionUtils.newConcurrentHashMap(paramResponse.unknown);
    this.statusCode = paramResponse.statusCode;
    this.bodySize = paramResponse.bodySize;
    this.data = paramResponse.data;
  }
  
  @Nullable
  public String getCookies() {
    return this.cookies;
  }
  
  public void setCookies(@Nullable String paramString) {
    this.cookies = paramString;
  }
  
  @Nullable
  public Map<String, String> getHeaders() {
    return this.headers;
  }
  
  public void setHeaders(@Nullable Map<String, String> paramMap) {
    this.headers = CollectionUtils.newConcurrentHashMap(paramMap);
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  @Nullable
  public Integer getStatusCode() {
    return this.statusCode;
  }
  
  public void setStatusCode(@Nullable Integer paramInteger) {
    this.statusCode = paramInteger;
  }
  
  @Nullable
  public Long getBodySize() {
    return this.bodySize;
  }
  
  public void setBodySize(@Nullable Long paramLong) {
    this.bodySize = paramLong;
  }
  
  @Nullable
  public Object getData() {
    return this.data;
  }
  
  public void setData(@Nullable Object paramObject) {
    this.data = paramObject;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.cookies != null)
      paramObjectWriter.name("cookies").value(this.cookies); 
    if (this.headers != null)
      paramObjectWriter.name("headers").value(paramILogger, this.headers); 
    if (this.statusCode != null)
      paramObjectWriter.name("status_code").value(paramILogger, this.statusCode); 
    if (this.bodySize != null)
      paramObjectWriter.name("body_size").value(paramILogger, this.bodySize); 
    if (this.data != null)
      paramObjectWriter.name("data").value(paramILogger, this.data); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String COOKIES = "cookies";
    
    public static final String HEADERS = "headers";
    
    public static final String STATUS_CODE = "status_code";
    
    public static final String BODY_SIZE = "body_size";
    
    public static final String DATA = "data";
  }
  
  public static final class Deserializer implements JsonDeserializer<Response> {
    @NotNull
    public Response deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Response response = new Response();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Map map;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "cookies":
            response.cookies = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "headers":
            map = (Map)param1JsonObjectReader.nextObjectOrNull();
            if (map != null)
              response.headers = CollectionUtils.newConcurrentHashMap(map); 
            continue;
          case "status_code":
            response.statusCode = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "body_size":
            response.bodySize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "data":
            response.data = param1JsonObjectReader.nextObjectOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      response.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return response;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Response.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */