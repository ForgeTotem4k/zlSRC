package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class User implements JsonUnknown, JsonSerializable {
  @Nullable
  private String email;
  
  @Nullable
  private String id;
  
  @Nullable
  private String username;
  
  @Nullable
  private String ipAddress;
  
  @Nullable
  private String name;
  
  @Nullable
  private Geo geo;
  
  @Nullable
  private Map<String, String> data;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public User() {}
  
  public User(@NotNull User paramUser) {
    this.email = paramUser.email;
    this.username = paramUser.username;
    this.id = paramUser.id;
    this.ipAddress = paramUser.ipAddress;
    this.name = paramUser.name;
    this.geo = paramUser.geo;
    this.data = CollectionUtils.newConcurrentHashMap(paramUser.data);
    this.unknown = CollectionUtils.newConcurrentHashMap(paramUser.unknown);
  }
  
  public static User fromMap(@NotNull Map<String, Object> paramMap, @NotNull SentryOptions paramSentryOptions) {
    User user = new User();
    ConcurrentHashMap<Object, Object> concurrentHashMap = null;
    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
      Map map1;
      Map map2;
      Map map3;
      Object object = entry.getValue();
      switch ((String)entry.getKey()) {
        case "email":
          user.email = (object instanceof String) ? (String)object : null;
          continue;
        case "id":
          user.id = (object instanceof String) ? (String)object : null;
          continue;
        case "username":
          user.username = (object instanceof String) ? (String)object : null;
          continue;
        case "ip_address":
          user.ipAddress = (object instanceof String) ? (String)object : null;
          continue;
        case "name":
          user.name = (object instanceof String) ? (String)object : null;
          continue;
        case "geo":
          map1 = (object instanceof Map) ? (Map)object : null;
          if (map1 != null) {
            ConcurrentHashMap<Object, Object> concurrentHashMap1 = new ConcurrentHashMap<>();
            for (Map.Entry entry1 : map1.entrySet()) {
              if (entry1.getKey() instanceof String && entry1.getValue() != null) {
                concurrentHashMap1.put(entry1.getKey(), entry1.getValue());
                continue;
              } 
              paramSentryOptions.getLogger().log(SentryLevel.WARNING, "Invalid key type in gep map.", new Object[0]);
            } 
            user.geo = Geo.fromMap((Map)concurrentHashMap1);
          } 
          continue;
        case "data":
          map2 = (object instanceof Map) ? (Map)object : null;
          if (map2 != null) {
            ConcurrentHashMap<Object, Object> concurrentHashMap1 = new ConcurrentHashMap<>();
            for (Map.Entry entry1 : map2.entrySet()) {
              if (entry1.getKey() instanceof String && entry1.getValue() != null) {
                concurrentHashMap1.put(entry1.getKey(), entry1.getValue().toString());
                continue;
              } 
              paramSentryOptions.getLogger().log(SentryLevel.WARNING, "Invalid key or null value in data map.", new Object[0]);
            } 
            user.data = (Map)concurrentHashMap1;
          } 
          continue;
        case "other":
          map3 = (object instanceof Map) ? (Map)object : null;
          if (map3 != null && (user.data == null || user.data.isEmpty())) {
            ConcurrentHashMap<Object, Object> concurrentHashMap1 = new ConcurrentHashMap<>();
            for (Map.Entry entry1 : map3.entrySet()) {
              if (entry1.getKey() instanceof String && entry1.getValue() != null) {
                concurrentHashMap1.put(entry1.getKey(), entry1.getValue().toString());
                continue;
              } 
              paramSentryOptions.getLogger().log(SentryLevel.WARNING, "Invalid key or null value in other map.", new Object[0]);
            } 
            user.data = (Map)concurrentHashMap1;
          } 
          continue;
      } 
      if (concurrentHashMap == null)
        concurrentHashMap = new ConcurrentHashMap<>(); 
      concurrentHashMap.put(entry.getKey(), entry.getValue());
    } 
    user.unknown = (Map)concurrentHashMap;
    return user;
  }
  
  @Nullable
  public String getEmail() {
    return this.email;
  }
  
  public void setEmail(@Nullable String paramString) {
    this.email = paramString;
  }
  
  @Nullable
  public String getId() {
    return this.id;
  }
  
  public void setId(@Nullable String paramString) {
    this.id = paramString;
  }
  
  @Nullable
  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(@Nullable String paramString) {
    this.username = paramString;
  }
  
  @Nullable
  public String getIpAddress() {
    return this.ipAddress;
  }
  
  public void setIpAddress(@Nullable String paramString) {
    this.ipAddress = paramString;
  }
  
  @Deprecated
  @Nullable
  public Map<String, String> getOthers() {
    return getData();
  }
  
  @Deprecated
  public void setOthers(@Nullable Map<String, String> paramMap) {
    setData(paramMap);
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(@Nullable String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public Geo getGeo() {
    return this.geo;
  }
  
  public void setGeo(@Nullable Geo paramGeo) {
    this.geo = paramGeo;
  }
  
  @Nullable
  public Map<String, String> getData() {
    return this.data;
  }
  
  public void setData(@Nullable Map<String, String> paramMap) {
    this.data = CollectionUtils.newConcurrentHashMap(paramMap);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    User user = (User)paramObject;
    return (Objects.equals(this.email, user.email) && Objects.equals(this.id, user.id) && Objects.equals(this.username, user.username) && Objects.equals(this.ipAddress, user.ipAddress));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.email, this.id, this.username, this.ipAddress });
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
    if (this.email != null)
      paramObjectWriter.name("email").value(this.email); 
    if (this.id != null)
      paramObjectWriter.name("id").value(this.id); 
    if (this.username != null)
      paramObjectWriter.name("username").value(this.username); 
    if (this.ipAddress != null)
      paramObjectWriter.name("ip_address").value(this.ipAddress); 
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.geo != null) {
      paramObjectWriter.name("geo");
      this.geo.serialize(paramObjectWriter, paramILogger);
    } 
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
    public static final String EMAIL = "email";
    
    public static final String ID = "id";
    
    public static final String USERNAME = "username";
    
    public static final String IP_ADDRESS = "ip_address";
    
    public static final String NAME = "name";
    
    public static final String GEO = "geo";
    
    public static final String OTHER = "other";
    
    public static final String DATA = "data";
  }
  
  public static final class Deserializer implements JsonDeserializer<User> {
    @NotNull
    public User deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      User user = new User();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "email":
            user.email = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "id":
            user.id = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "username":
            user.username = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "ip_address":
            user.ipAddress = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "name":
            user.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "geo":
            user.geo = (new Geo.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "data":
            user.data = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "other":
            if (user.data == null || user.data.isEmpty())
              user.data = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull()); 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      user.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return user;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */