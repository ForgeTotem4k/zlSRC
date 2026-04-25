package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UserFeedback implements JsonUnknown, JsonSerializable {
  private final SentryId eventId;
  
  @Nullable
  private String name;
  
  @Nullable
  private String email;
  
  @Nullable
  private String comments;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public UserFeedback(SentryId paramSentryId) {
    this(paramSentryId, null, null, null);
  }
  
  public UserFeedback(SentryId paramSentryId, @Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3) {
    this.eventId = paramSentryId;
    this.name = paramString1;
    this.email = paramString2;
    this.comments = paramString3;
  }
  
  public SentryId getEventId() {
    return this.eventId;
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public String getEmail() {
    return this.email;
  }
  
  public void setEmail(@Nullable String paramString) {
    this.email = paramString;
  }
  
  @Nullable
  public String getComments() {
    return this.comments;
  }
  
  public void setComments(@Nullable String paramString) {
    this.comments = paramString;
  }
  
  public String toString() {
    return "UserFeedback{eventId=" + this.eventId + ", name='" + this.name + '\'' + ", email='" + this.email + '\'' + ", comments='" + this.comments + '\'' + '}';
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
    paramObjectWriter.name("event_id");
    this.eventId.serialize(paramObjectWriter, paramILogger);
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.email != null)
      paramObjectWriter.name("email").value(this.email); 
    if (this.comments != null)
      paramObjectWriter.name("comments").value(this.comments); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String EVENT_ID = "event_id";
    
    public static final String NAME = "name";
    
    public static final String EMAIL = "email";
    
    public static final String COMMENTS = "comments";
  }
  
  public static final class Deserializer implements JsonDeserializer<UserFeedback> {
    @NotNull
    public UserFeedback deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryId sentryId = null;
      String str1 = null;
      String str2 = null;
      String str3 = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "event_id":
            sentryId = (new SentryId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "name":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "email":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "comments":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (sentryId == null) {
        String str = "Missing required field \"event_id\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      UserFeedback userFeedback = new UserFeedback(sentryId, str1, str2, str3);
      userFeedback.setUnknown((Map)hashMap);
      return userFeedback;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\UserFeedback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */