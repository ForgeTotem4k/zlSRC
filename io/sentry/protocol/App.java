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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class App implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "app";
  
  @Nullable
  private String appIdentifier;
  
  @Nullable
  private Date appStartTime;
  
  @Nullable
  private String deviceAppHash;
  
  @Nullable
  private String buildType;
  
  @Nullable
  private String appName;
  
  @Nullable
  private String appVersion;
  
  @Nullable
  private String appBuild;
  
  @Nullable
  private Map<String, String> permissions;
  
  @Nullable
  private List<String> viewNames;
  
  @Nullable
  private String startType;
  
  @Nullable
  private Boolean inForeground;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public App() {}
  
  App(@NotNull App paramApp) {
    this.appBuild = paramApp.appBuild;
    this.appIdentifier = paramApp.appIdentifier;
    this.appName = paramApp.appName;
    this.appStartTime = paramApp.appStartTime;
    this.appVersion = paramApp.appVersion;
    this.buildType = paramApp.buildType;
    this.deviceAppHash = paramApp.deviceAppHash;
    this.permissions = CollectionUtils.newConcurrentHashMap(paramApp.permissions);
    this.inForeground = paramApp.inForeground;
    this.viewNames = CollectionUtils.newArrayList(paramApp.viewNames);
    this.startType = paramApp.startType;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramApp.unknown);
  }
  
  @Nullable
  public String getAppIdentifier() {
    return this.appIdentifier;
  }
  
  public void setAppIdentifier(@Nullable String paramString) {
    this.appIdentifier = paramString;
  }
  
  @Nullable
  public Date getAppStartTime() {
    Date date = this.appStartTime;
    return (date != null) ? (Date)date.clone() : null;
  }
  
  public void setAppStartTime(@Nullable Date paramDate) {
    this.appStartTime = paramDate;
  }
  
  @Nullable
  public String getDeviceAppHash() {
    return this.deviceAppHash;
  }
  
  public void setDeviceAppHash(@Nullable String paramString) {
    this.deviceAppHash = paramString;
  }
  
  @Nullable
  public String getBuildType() {
    return this.buildType;
  }
  
  public void setBuildType(@Nullable String paramString) {
    this.buildType = paramString;
  }
  
  @Nullable
  public String getAppName() {
    return this.appName;
  }
  
  public void setAppName(@Nullable String paramString) {
    this.appName = paramString;
  }
  
  @Nullable
  public String getAppVersion() {
    return this.appVersion;
  }
  
  public void setAppVersion(@Nullable String paramString) {
    this.appVersion = paramString;
  }
  
  @Nullable
  public String getAppBuild() {
    return this.appBuild;
  }
  
  public void setAppBuild(@Nullable String paramString) {
    this.appBuild = paramString;
  }
  
  @Nullable
  public Map<String, String> getPermissions() {
    return this.permissions;
  }
  
  public void setPermissions(@Nullable Map<String, String> paramMap) {
    this.permissions = paramMap;
  }
  
  @Nullable
  public Boolean getInForeground() {
    return this.inForeground;
  }
  
  public void setInForeground(@Nullable Boolean paramBoolean) {
    this.inForeground = paramBoolean;
  }
  
  @Nullable
  public List<String> getViewNames() {
    return this.viewNames;
  }
  
  public void setViewNames(@Nullable List<String> paramList) {
    this.viewNames = paramList;
  }
  
  @Nullable
  public String getStartType() {
    return this.startType;
  }
  
  public void setStartType(@Nullable String paramString) {
    this.startType = paramString;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    App app = (App)paramObject;
    return (Objects.equals(this.appIdentifier, app.appIdentifier) && Objects.equals(this.appStartTime, app.appStartTime) && Objects.equals(this.deviceAppHash, app.deviceAppHash) && Objects.equals(this.buildType, app.buildType) && Objects.equals(this.appName, app.appName) && Objects.equals(this.appVersion, app.appVersion) && Objects.equals(this.appBuild, app.appBuild) && Objects.equals(this.permissions, app.permissions) && Objects.equals(this.inForeground, app.inForeground) && Objects.equals(this.viewNames, app.viewNames) && Objects.equals(this.startType, app.startType));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { 
          this.appIdentifier, this.appStartTime, this.deviceAppHash, this.buildType, this.appName, this.appVersion, this.appBuild, this.permissions, this.inForeground, this.viewNames, 
          this.startType });
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
    if (this.appIdentifier != null)
      paramObjectWriter.name("app_identifier").value(this.appIdentifier); 
    if (this.appStartTime != null)
      paramObjectWriter.name("app_start_time").value(paramILogger, this.appStartTime); 
    if (this.deviceAppHash != null)
      paramObjectWriter.name("device_app_hash").value(this.deviceAppHash); 
    if (this.buildType != null)
      paramObjectWriter.name("build_type").value(this.buildType); 
    if (this.appName != null)
      paramObjectWriter.name("app_name").value(this.appName); 
    if (this.appVersion != null)
      paramObjectWriter.name("app_version").value(this.appVersion); 
    if (this.appBuild != null)
      paramObjectWriter.name("app_build").value(this.appBuild); 
    if (this.permissions != null && !this.permissions.isEmpty())
      paramObjectWriter.name("permissions").value(paramILogger, this.permissions); 
    if (this.inForeground != null)
      paramObjectWriter.name("in_foreground").value(this.inForeground); 
    if (this.viewNames != null)
      paramObjectWriter.name("view_names").value(paramILogger, this.viewNames); 
    if (this.startType != null)
      paramObjectWriter.name("start_type").value(this.startType); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String APP_IDENTIFIER = "app_identifier";
    
    public static final String APP_START_TIME = "app_start_time";
    
    public static final String DEVICE_APP_HASH = "device_app_hash";
    
    public static final String BUILD_TYPE = "build_type";
    
    public static final String APP_NAME = "app_name";
    
    public static final String APP_VERSION = "app_version";
    
    public static final String APP_BUILD = "app_build";
    
    public static final String APP_PERMISSIONS = "permissions";
    
    public static final String IN_FOREGROUND = "in_foreground";
    
    public static final String VIEW_NAMES = "view_names";
    
    public static final String START_TYPE = "start_type";
  }
  
  public static final class Deserializer implements JsonDeserializer<App> {
    @NotNull
    public App deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      App app = new App();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List<String> list;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "app_identifier":
            app.appIdentifier = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "app_start_time":
            app.appStartTime = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            continue;
          case "device_app_hash":
            app.deviceAppHash = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "build_type":
            app.buildType = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "app_name":
            app.appName = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "app_version":
            app.appVersion = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "app_build":
            app.appBuild = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "permissions":
            app.permissions = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "in_foreground":
            app.inForeground = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "view_names":
            list = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list != null)
              app.setViewNames(list); 
            continue;
          case "start_type":
            app.startType = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      app.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return app;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\App.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */