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

public final class Request implements JsonUnknown, JsonSerializable {
  @Nullable
  private String url;
  
  @Nullable
  private String method;
  
  @Nullable
  private String queryString;
  
  @Nullable
  private Object data;
  
  @Nullable
  private String cookies;
  
  @Nullable
  private Map<String, String> headers;
  
  @Nullable
  private Map<String, String> env;
  
  @Nullable
  private Long bodySize;
  
  @Nullable
  private Map<String, String> other;
  
  @Nullable
  private String fragment;
  
  @Nullable
  private String apiTarget;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Request() {}
  
  public Request(@NotNull Request paramRequest) {
    this.url = paramRequest.url;
    this.cookies = paramRequest.cookies;
    this.method = paramRequest.method;
    this.queryString = paramRequest.queryString;
    this.headers = CollectionUtils.newConcurrentHashMap(paramRequest.headers);
    this.env = CollectionUtils.newConcurrentHashMap(paramRequest.env);
    this.other = CollectionUtils.newConcurrentHashMap(paramRequest.other);
    this.unknown = CollectionUtils.newConcurrentHashMap(paramRequest.unknown);
    this.data = paramRequest.data;
    this.fragment = paramRequest.fragment;
    this.bodySize = paramRequest.bodySize;
    this.apiTarget = paramRequest.apiTarget;
  }
  
  @Nullable
  public String getUrl() {
    return this.url;
  }
  
  public void setUrl(@Nullable String paramString) {
    this.url = paramString;
  }
  
  @Nullable
  public String getMethod() {
    return this.method;
  }
  
  public void setMethod(@Nullable String paramString) {
    this.method = paramString;
  }
  
  @Nullable
  public String getQueryString() {
    return this.queryString;
  }
  
  public void setQueryString(@Nullable String paramString) {
    this.queryString = paramString;
  }
  
  @Nullable
  public Object getData() {
    return this.data;
  }
  
  public void setData(@Nullable Object paramObject) {
    this.data = paramObject;
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
  public Map<String, String> getEnvs() {
    return this.env;
  }
  
  public void setEnvs(@Nullable Map<String, String> paramMap) {
    this.env = CollectionUtils.newConcurrentHashMap(paramMap);
  }
  
  @Nullable
  public Map<String, String> getOthers() {
    return this.other;
  }
  
  public void setOthers(@Nullable Map<String, String> paramMap) {
    this.other = CollectionUtils.newConcurrentHashMap(paramMap);
  }
  
  @Nullable
  public String getFragment() {
    return this.fragment;
  }
  
  public void setFragment(@Nullable String paramString) {
    this.fragment = paramString;
  }
  
  @Nullable
  public Long getBodySize() {
    return this.bodySize;
  }
  
  public void setBodySize(@Nullable Long paramLong) {
    this.bodySize = paramLong;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Request request = (Request)paramObject;
    return (Objects.equals(this.url, request.url) && Objects.equals(this.method, request.method) && Objects.equals(this.queryString, request.queryString) && Objects.equals(this.cookies, request.cookies) && Objects.equals(this.headers, request.headers) && Objects.equals(this.env, request.env) && Objects.equals(this.bodySize, request.bodySize) && Objects.equals(this.fragment, request.fragment) && Objects.equals(this.apiTarget, request.apiTarget));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.url, this.method, this.queryString, this.cookies, this.headers, this.env, this.bodySize, this.fragment, this.apiTarget });
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  @Nullable
  public String getApiTarget() {
    return this.apiTarget;
  }
  
  public void setApiTarget(@Nullable String paramString) {
    this.apiTarget = paramString;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.url != null)
      paramObjectWriter.name("url").value(this.url); 
    if (this.method != null)
      paramObjectWriter.name("method").value(this.method); 
    if (this.queryString != null)
      paramObjectWriter.name("query_string").value(this.queryString); 
    if (this.data != null)
      paramObjectWriter.name("data").value(paramILogger, this.data); 
    if (this.cookies != null)
      paramObjectWriter.name("cookies").value(this.cookies); 
    if (this.headers != null)
      paramObjectWriter.name("headers").value(paramILogger, this.headers); 
    if (this.env != null)
      paramObjectWriter.name("env").value(paramILogger, this.env); 
    if (this.other != null)
      paramObjectWriter.name("other").value(paramILogger, this.other); 
    if (this.fragment != null)
      paramObjectWriter.name("fragment").value(paramILogger, this.fragment); 
    if (this.bodySize != null)
      paramObjectWriter.name("body_size").value(paramILogger, this.bodySize); 
    if (this.apiTarget != null)
      paramObjectWriter.name("api_target").value(paramILogger, this.apiTarget); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String URL = "url";
    
    public static final String METHOD = "method";
    
    public static final String QUERY_STRING = "query_string";
    
    public static final String DATA = "data";
    
    public static final String COOKIES = "cookies";
    
    public static final String HEADERS = "headers";
    
    public static final String ENV = "env";
    
    public static final String OTHER = "other";
    
    public static final String FRAGMENT = "fragment";
    
    public static final String BODY_SIZE = "body_size";
    
    public static final String API_TARGET = "api_target";
  }
  
  public static final class Deserializer implements JsonDeserializer<Request> {
    @NotNull
    public Request deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Request request = new Request();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Map map1;
        Map map2;
        Map map3;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "url":
            request.url = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "method":
            request.method = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "query_string":
            request.queryString = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "data":
            request.data = param1JsonObjectReader.nextObjectOrNull();
            continue;
          case "cookies":
            request.cookies = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "headers":
            map1 = (Map)param1JsonObjectReader.nextObjectOrNull();
            if (map1 != null)
              request.headers = CollectionUtils.newConcurrentHashMap(map1); 
            continue;
          case "env":
            map2 = (Map)param1JsonObjectReader.nextObjectOrNull();
            if (map2 != null)
              request.env = CollectionUtils.newConcurrentHashMap(map2); 
            continue;
          case "other":
            map3 = (Map)param1JsonObjectReader.nextObjectOrNull();
            if (map3 != null)
              request.other = CollectionUtils.newConcurrentHashMap(map3); 
            continue;
          case "fragment":
            request.fragment = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "body_size":
            request.bodySize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "api_target":
            request.apiTarget = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      request.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return request;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */