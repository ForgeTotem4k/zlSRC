package io.sentry;

import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.Request;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SentryBaseEvent {
  public static final String DEFAULT_PLATFORM = "java";
  
  @Nullable
  private SentryId eventId;
  
  @NotNull
  private final Contexts contexts = new Contexts();
  
  @Nullable
  private SdkVersion sdk;
  
  @Nullable
  private Request request;
  
  @Nullable
  private Map<String, String> tags;
  
  @Nullable
  private String release;
  
  @Nullable
  private String environment;
  
  @Nullable
  private String platform;
  
  @Nullable
  private User user;
  
  @Nullable
  protected transient Throwable throwable;
  
  @Nullable
  private String serverName;
  
  @Nullable
  private String dist;
  
  @Nullable
  private List<Breadcrumb> breadcrumbs;
  
  @Nullable
  private DebugMeta debugMeta;
  
  @Nullable
  private Map<String, Object> extra;
  
  protected SentryBaseEvent(@NotNull SentryId paramSentryId) {
    this.eventId = paramSentryId;
  }
  
  protected SentryBaseEvent() {
    this(new SentryId());
  }
  
  @Nullable
  public SentryId getEventId() {
    return this.eventId;
  }
  
  public void setEventId(@Nullable SentryId paramSentryId) {
    this.eventId = paramSentryId;
  }
  
  @NotNull
  public Contexts getContexts() {
    return this.contexts;
  }
  
  @Nullable
  public SdkVersion getSdk() {
    return this.sdk;
  }
  
  public void setSdk(@Nullable SdkVersion paramSdkVersion) {
    this.sdk = paramSdkVersion;
  }
  
  @Nullable
  public Request getRequest() {
    return this.request;
  }
  
  public void setRequest(@Nullable Request paramRequest) {
    this.request = paramRequest;
  }
  
  @Nullable
  public Throwable getThrowable() {
    Throwable throwable = this.throwable;
    return (throwable instanceof ExceptionMechanismException) ? ((ExceptionMechanismException)throwable).getThrowable() : throwable;
  }
  
  @Internal
  @Nullable
  public Throwable getThrowableMechanism() {
    return this.throwable;
  }
  
  public void setThrowable(@Nullable Throwable paramThrowable) {
    this.throwable = paramThrowable;
  }
  
  @Internal
  @Nullable
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  public void setTags(@Nullable Map<String, String> paramMap) {
    this.tags = CollectionUtils.newHashMap(paramMap);
  }
  
  public void removeTag(@NotNull String paramString) {
    if (this.tags != null)
      this.tags.remove(paramString); 
  }
  
  @Nullable
  public String getTag(@NotNull String paramString) {
    return (this.tags != null) ? this.tags.get(paramString) : null;
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    if (this.tags == null)
      this.tags = new HashMap<>(); 
    this.tags.put(paramString1, paramString2);
  }
  
  @Nullable
  public String getRelease() {
    return this.release;
  }
  
  public void setRelease(@Nullable String paramString) {
    this.release = paramString;
  }
  
  @Nullable
  public String getEnvironment() {
    return this.environment;
  }
  
  public void setEnvironment(@Nullable String paramString) {
    this.environment = paramString;
  }
  
  @Nullable
  public String getPlatform() {
    return this.platform;
  }
  
  public void setPlatform(@Nullable String paramString) {
    this.platform = paramString;
  }
  
  @Nullable
  public String getServerName() {
    return this.serverName;
  }
  
  public void setServerName(@Nullable String paramString) {
    this.serverName = paramString;
  }
  
  @Nullable
  public String getDist() {
    return this.dist;
  }
  
  public void setDist(@Nullable String paramString) {
    this.dist = paramString;
  }
  
  @Nullable
  public User getUser() {
    return this.user;
  }
  
  public void setUser(@Nullable User paramUser) {
    this.user = paramUser;
  }
  
  @Nullable
  public List<Breadcrumb> getBreadcrumbs() {
    return this.breadcrumbs;
  }
  
  public void setBreadcrumbs(@Nullable List<Breadcrumb> paramList) {
    this.breadcrumbs = CollectionUtils.newArrayList(paramList);
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    if (this.breadcrumbs == null)
      this.breadcrumbs = new ArrayList<>(); 
    this.breadcrumbs.add(paramBreadcrumb);
  }
  
  @Nullable
  public DebugMeta getDebugMeta() {
    return this.debugMeta;
  }
  
  public void setDebugMeta(@Nullable DebugMeta paramDebugMeta) {
    this.debugMeta = paramDebugMeta;
  }
  
  @Nullable
  public Map<String, Object> getExtras() {
    return this.extra;
  }
  
  public void setExtras(@Nullable Map<String, Object> paramMap) {
    this.extra = CollectionUtils.newHashMap(paramMap);
  }
  
  public void setExtra(@NotNull String paramString, @NotNull Object paramObject) {
    if (this.extra == null)
      this.extra = new HashMap<>(); 
    this.extra.put(paramString, paramObject);
  }
  
  public void removeExtra(@NotNull String paramString) {
    if (this.extra != null)
      this.extra.remove(paramString); 
  }
  
  @Nullable
  public Object getExtra(@NotNull String paramString) {
    return (this.extra != null) ? this.extra.get(paramString) : null;
  }
  
  public void addBreadcrumb(@Nullable String paramString) {
    addBreadcrumb(new Breadcrumb(paramString));
  }
  
  public static final class Deserializer {
    public boolean deserializeValue(@NotNull SentryBaseEvent param1SentryBaseEvent, @NotNull String param1String, @NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      Contexts contexts;
      Map map1;
      Map map2;
      switch (param1String) {
        case "event_id":
          param1SentryBaseEvent.eventId = param1JsonObjectReader.<SentryId>nextOrNull(param1ILogger, (JsonDeserializer<SentryId>)new SentryId.Deserializer());
          return true;
        case "contexts":
          contexts = (new Contexts.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
          param1SentryBaseEvent.contexts.putAll(contexts);
          return true;
        case "sdk":
          param1SentryBaseEvent.sdk = param1JsonObjectReader.<SdkVersion>nextOrNull(param1ILogger, (JsonDeserializer<SdkVersion>)new SdkVersion.Deserializer());
          return true;
        case "request":
          param1SentryBaseEvent.request = param1JsonObjectReader.<Request>nextOrNull(param1ILogger, (JsonDeserializer<Request>)new Request.Deserializer());
          return true;
        case "tags":
          map1 = (Map)param1JsonObjectReader.nextObjectOrNull();
          param1SentryBaseEvent.tags = CollectionUtils.newConcurrentHashMap(map1);
          return true;
        case "release":
          param1SentryBaseEvent.release = param1JsonObjectReader.nextStringOrNull();
          return true;
        case "environment":
          param1SentryBaseEvent.environment = param1JsonObjectReader.nextStringOrNull();
          return true;
        case "platform":
          param1SentryBaseEvent.platform = param1JsonObjectReader.nextStringOrNull();
          return true;
        case "user":
          param1SentryBaseEvent.user = param1JsonObjectReader.<User>nextOrNull(param1ILogger, (JsonDeserializer<User>)new User.Deserializer());
          return true;
        case "server_name":
          param1SentryBaseEvent.serverName = param1JsonObjectReader.nextStringOrNull();
          return true;
        case "dist":
          param1SentryBaseEvent.dist = param1JsonObjectReader.nextStringOrNull();
          return true;
        case "breadcrumbs":
          param1SentryBaseEvent.breadcrumbs = param1JsonObjectReader.nextListOrNull(param1ILogger, new Breadcrumb.Deserializer());
          return true;
        case "debug_meta":
          param1SentryBaseEvent.debugMeta = param1JsonObjectReader.<DebugMeta>nextOrNull(param1ILogger, (JsonDeserializer<DebugMeta>)new DebugMeta.Deserializer());
          return true;
        case "extra":
          map2 = (Map)param1JsonObjectReader.nextObjectOrNull();
          param1SentryBaseEvent.extra = CollectionUtils.newConcurrentHashMap(map2);
          return true;
      } 
      return false;
    }
    
    static {
    
    }
  }
  
  public static final class Serializer {
    public void serialize(@NotNull SentryBaseEvent param1SentryBaseEvent, @NotNull ObjectWriter param1ObjectWriter, @NotNull ILogger param1ILogger) throws IOException {
      if (param1SentryBaseEvent.eventId != null)
        param1ObjectWriter.name("event_id").value(param1ILogger, param1SentryBaseEvent.eventId); 
      param1ObjectWriter.name("contexts").value(param1ILogger, param1SentryBaseEvent.contexts);
      if (param1SentryBaseEvent.sdk != null)
        param1ObjectWriter.name("sdk").value(param1ILogger, param1SentryBaseEvent.sdk); 
      if (param1SentryBaseEvent.request != null)
        param1ObjectWriter.name("request").value(param1ILogger, param1SentryBaseEvent.request); 
      if (param1SentryBaseEvent.tags != null && !param1SentryBaseEvent.tags.isEmpty())
        param1ObjectWriter.name("tags").value(param1ILogger, param1SentryBaseEvent.tags); 
      if (param1SentryBaseEvent.release != null)
        param1ObjectWriter.name("release").value(param1SentryBaseEvent.release); 
      if (param1SentryBaseEvent.environment != null)
        param1ObjectWriter.name("environment").value(param1SentryBaseEvent.environment); 
      if (param1SentryBaseEvent.platform != null)
        param1ObjectWriter.name("platform").value(param1SentryBaseEvent.platform); 
      if (param1SentryBaseEvent.user != null)
        param1ObjectWriter.name("user").value(param1ILogger, param1SentryBaseEvent.user); 
      if (param1SentryBaseEvent.serverName != null)
        param1ObjectWriter.name("server_name").value(param1SentryBaseEvent.serverName); 
      if (param1SentryBaseEvent.dist != null)
        param1ObjectWriter.name("dist").value(param1SentryBaseEvent.dist); 
      if (param1SentryBaseEvent.breadcrumbs != null && !param1SentryBaseEvent.breadcrumbs.isEmpty())
        param1ObjectWriter.name("breadcrumbs").value(param1ILogger, param1SentryBaseEvent.breadcrumbs); 
      if (param1SentryBaseEvent.debugMeta != null)
        param1ObjectWriter.name("debug_meta").value(param1ILogger, param1SentryBaseEvent.debugMeta); 
      if (param1SentryBaseEvent.extra != null && !param1SentryBaseEvent.extra.isEmpty())
        param1ObjectWriter.name("extra").value(param1ILogger, param1SentryBaseEvent.extra); 
    }
    
    static {
    
    }
  }
  
  public static final class JsonKeys {
    public static final String EVENT_ID = "event_id";
    
    public static final String CONTEXTS = "contexts";
    
    public static final String SDK = "sdk";
    
    public static final String REQUEST = "request";
    
    public static final String TAGS = "tags";
    
    public static final String RELEASE = "release";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String PLATFORM = "platform";
    
    public static final String USER = "user";
    
    public static final String SERVER_NAME = "server_name";
    
    public static final String DIST = "dist";
    
    public static final String BREADCRUMBS = "breadcrumbs";
    
    public static final String DEBUG_META = "debug_meta";
    
    public static final String EXTRA = "extra";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryBaseEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */