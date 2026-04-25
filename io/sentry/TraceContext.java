package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class TraceContext implements JsonUnknown, JsonSerializable {
  @NotNull
  private final SentryId traceId;
  
  @NotNull
  private final String publicKey;
  
  @Nullable
  private final String release;
  
  @Nullable
  private final String environment;
  
  @Nullable
  private final String userId;
  
  @Nullable
  private final String transaction;
  
  @Nullable
  private final String sampleRate;
  
  @Nullable
  private final String sampled;
  
  @Nullable
  private Map<String, Object> unknown;
  
  TraceContext(@NotNull SentryId paramSentryId, @NotNull String paramString) {
    this(paramSentryId, paramString, null, null, null, null, null, null);
  }
  
  TraceContext(@NotNull SentryId paramSentryId, @NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3, @Nullable String paramString4, @Nullable String paramString5, @Nullable String paramString6, @Nullable String paramString7) {
    this.traceId = paramSentryId;
    this.publicKey = paramString1;
    this.release = paramString2;
    this.environment = paramString3;
    this.userId = paramString4;
    this.transaction = paramString5;
    this.sampleRate = paramString6;
    this.sampled = paramString7;
  }
  
  @Nullable
  private static String getUserId(@NotNull SentryOptions paramSentryOptions, @Nullable User paramUser) {
    return (paramSentryOptions.isSendDefaultPii() && paramUser != null) ? paramUser.getId() : null;
  }
  
  @NotNull
  public SentryId getTraceId() {
    return this.traceId;
  }
  
  @NotNull
  public String getPublicKey() {
    return this.publicKey;
  }
  
  @Nullable
  public String getRelease() {
    return this.release;
  }
  
  @Nullable
  public String getEnvironment() {
    return this.environment;
  }
  
  @Nullable
  public String getUserId() {
    return this.userId;
  }
  
  @Nullable
  public String getTransaction() {
    return this.transaction;
  }
  
  @Nullable
  public String getSampleRate() {
    return this.sampleRate;
  }
  
  @Nullable
  public String getSampled() {
    return this.sampled;
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
    paramObjectWriter.name("trace_id").value(paramILogger, this.traceId);
    paramObjectWriter.name("public_key").value(this.publicKey);
    if (this.release != null)
      paramObjectWriter.name("release").value(this.release); 
    if (this.environment != null)
      paramObjectWriter.name("environment").value(this.environment); 
    if (this.userId != null)
      paramObjectWriter.name("user_id").value(this.userId); 
    if (this.transaction != null)
      paramObjectWriter.name("transaction").value(this.transaction); 
    if (this.sampleRate != null)
      paramObjectWriter.name("sample_rate").value(this.sampleRate); 
    if (this.sampled != null)
      paramObjectWriter.name("sampled").value(this.sampled); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TRACE_ID = "trace_id";
    
    public static final String PUBLIC_KEY = "public_key";
    
    public static final String RELEASE = "release";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String USER = "user";
    
    public static final String USER_ID = "user_id";
    
    public static final String TRANSACTION = "transaction";
    
    public static final String SAMPLE_RATE = "sample_rate";
    
    public static final String SAMPLED = "sampled";
  }
  
  public static final class Deserializer implements JsonDeserializer<TraceContext> {
    @NotNull
    public TraceContext deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryId sentryId = null;
      String str1 = null;
      String str2 = null;
      String str3 = null;
      TraceContext.TraceContextUser traceContextUser = null;
      String str4 = null;
      String str5 = null;
      String str6 = null;
      String str7 = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "trace_id":
            sentryId = (new SentryId.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            continue;
          case "public_key":
            str1 = param1JsonObjectReader.nextString();
            continue;
          case "release":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "environment":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "user":
            traceContextUser = param1JsonObjectReader.<TraceContext.TraceContextUser>nextOrNull(param1ILogger, new TraceContext.TraceContextUser.Deserializer());
            continue;
          case "user_id":
            str4 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "transaction":
            str5 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "sample_rate":
            str6 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "sampled":
            str7 = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str);
      } 
      if (sentryId == null)
        throw missingRequiredFieldException("trace_id", param1ILogger); 
      if (str1 == null)
        throw missingRequiredFieldException("public_key", param1ILogger); 
      if (traceContextUser != null && str4 == null)
        str4 = traceContextUser.getId(); 
      TraceContext traceContext = new TraceContext(sentryId, str1, str2, str3, str4, str5, str6, str7);
      traceContext.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return traceContext;
    }
    
    private Exception missingRequiredFieldException(String param1String, ILogger param1ILogger) {
      String str = "Missing required field \"" + param1String + "\"";
      IllegalStateException illegalStateException = new IllegalStateException(str);
      param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
      return illegalStateException;
    }
    
    static {
    
    }
  }
  
  @Deprecated
  private static final class TraceContextUser implements JsonUnknown {
    @Nullable
    private final String id;
    
    @Nullable
    private Map<String, Object> unknown;
    
    private TraceContextUser(@Nullable String param1String) {
      this.id = param1String;
    }
    
    @Nullable
    public String getId() {
      return this.id;
    }
    
    @Nullable
    public Map<String, Object> getUnknown() {
      return this.unknown;
    }
    
    public void setUnknown(@Nullable Map<String, Object> param1Map) {
      this.unknown = param1Map;
    }
    
    public static final class Deserializer implements JsonDeserializer<TraceContextUser> {
      @NotNull
      public TraceContext.TraceContextUser deserialize(@NotNull JsonObjectReader param2JsonObjectReader, @NotNull ILogger param2ILogger) throws Exception {
        param2JsonObjectReader.beginObject();
        String str = null;
        ConcurrentHashMap<Object, Object> concurrentHashMap = null;
        while (param2JsonObjectReader.peek() == JsonToken.NAME) {
          String str1 = param2JsonObjectReader.nextName();
          if (str1.equals("id")) {
            str = param2JsonObjectReader.nextStringOrNull();
            continue;
          } 
          if (concurrentHashMap == null)
            concurrentHashMap = new ConcurrentHashMap<>(); 
          param2JsonObjectReader.nextUnknown(param2ILogger, (Map)concurrentHashMap, str1);
        } 
        TraceContext.TraceContextUser traceContextUser = new TraceContext.TraceContextUser(str);
        traceContextUser.setUnknown((Map)concurrentHashMap);
        param2JsonObjectReader.endObject();
        return traceContextUser;
      }
      
      static {
      
      }
    }
    
    public static final class JsonKeys {
      public static final String ID = "id";
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TraceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */