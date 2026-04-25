package pro.gravit.launcher.gui.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.gui.service.JavaService;
import pro.gravit.launcher.gui.utils.SystemTheme;
import pro.gravit.launcher.runtime.client.UserSettings;
import pro.gravit.utils.helper.JavaHelper;

public class RuntimeSettings extends UserSettings {
  public static final LAUNCHER_LOCALE DEFAULT_LOCALE = LAUNCHER_LOCALE.RUSSIAN;
  
  public transient Path updatesDir;
  
  @LauncherNetworkAPI
  public String login;
  
  @LauncherNetworkAPI
  public AuthRequest.AuthPasswordInterface password;
  
  @LauncherNetworkAPI
  public boolean autoAuth;
  
  @LauncherNetworkAPI
  public GetAvailabilityAuthRequestEvent.AuthAvailability lastAuth;
  
  @LauncherNetworkAPI
  public String updatesDirPath;
  
  @LauncherNetworkAPI
  public UUID lastProfile;
  
  @LauncherNetworkAPI
  public volatile LAUNCHER_LOCALE locale;
  
  @LauncherNetworkAPI
  public String oauthAccessToken;
  
  @LauncherNetworkAPI
  public String oauthRefreshToken;
  
  @LauncherNetworkAPI
  public long oauthExpire;
  
  @LauncherNetworkAPI
  public volatile LAUNCHER_THEME theme = LAUNCHER_THEME.COMMON;
  
  @LauncherNetworkAPI
  public Map<UUID, ProfileSettings> profileSettings = new HashMap<>();
  
  @LauncherNetworkAPI
  public List<ClientProfile> profiles;
  
  @LauncherNetworkAPI
  public GlobalSettings globalSettings = new GlobalSettings();
  
  @LauncherNetworkAPI
  public boolean isFirstLaunch = true;
  
  public static RuntimeSettings getDefault(GuiModuleConfig paramGuiModuleConfig) {
    RuntimeSettings runtimeSettings = new RuntimeSettings();
    runtimeSettings.autoAuth = false;
    runtimeSettings.locale = (paramGuiModuleConfig.locale == null) ? LAUNCHER_LOCALE.RUSSIAN : LAUNCHER_LOCALE.valueOf(paramGuiModuleConfig.locale);
    try {
      runtimeSettings.theme = SystemTheme.getSystemTheme();
    } catch (Throwable throwable) {
      runtimeSettings.theme = LAUNCHER_THEME.COMMON;
    } 
    return runtimeSettings;
  }
  
  public void apply() {
    if (this.updatesDirPath != null)
      this.updatesDir = Paths.get(this.updatesDirPath, new String[0]); 
  }
  
  public AuthRequest.AuthPasswordInterface paw() {
    return null;
  }
  
  public enum LAUNCHER_THEME {
    COMMON(null, "default"),
    DARK("dark", "dark");
    
    public final String name;
    
    public final String displayName;
    
    LAUNCHER_THEME(String param1String1, String param1String2) {
      this.name = param1String1;
      this.displayName = param1String2;
    }
  }
  
  public static class GlobalSettings {
    @LauncherNetworkAPI
    public boolean prismVSync = true;
    
    @LauncherNetworkAPI
    public boolean debugAllClients = false;
    
    @LauncherNetworkAPI
    public int ram = 2048;
  }
  
  public enum LAUNCHER_LOCALE {
    RUSSIAN("ru", "Русский"),
    BELARUSIAN("be", "Беларуская"),
    UKRAINIAN("uk", "Українська"),
    POLISH("pl", "Polska"),
    ENGLISH("en", "English");
    
    public final String name;
    
    public final String displayName;
    
    LAUNCHER_LOCALE(String param1String1, String param1String2) {
      this.name = param1String1;
      this.displayName = param1String2;
    }
  }
  
  public static class ProfileSettingsView {
    private final transient RuntimeSettings.ProfileSettings settings;
    
    public int ram;
    
    public boolean debug;
    
    public boolean fullScreen;
    
    public boolean autoEnter;
    
    public String javaPath;
    
    public boolean waylandSupport;
    
    public boolean debugSkipUpdate;
    
    public boolean debugSkipFileMonitor;
    
    public ProfileSettingsView(RuntimeSettings.ProfileSettings param1ProfileSettings) {
      this.ram = param1ProfileSettings.ram;
      this.debug = param1ProfileSettings.debug;
      this.fullScreen = param1ProfileSettings.fullScreen;
      this.autoEnter = param1ProfileSettings.autoEnter;
      this.javaPath = param1ProfileSettings.javaPath;
      this.waylandSupport = param1ProfileSettings.waylandSupport;
      this.debugSkipUpdate = param1ProfileSettings.debugSkipUpdate;
      this.debugSkipFileMonitor = param1ProfileSettings.debugSkipFileMonitor;
      this.settings = param1ProfileSettings;
    }
    
    public void apply() {
      this.settings.ram = this.ram;
      this.settings.debug = this.debug;
      this.settings.autoEnter = this.autoEnter;
      this.settings.fullScreen = this.fullScreen;
      this.settings.javaPath = this.javaPath;
      this.settings.waylandSupport = this.waylandSupport;
      this.settings.debugSkipUpdate = this.debugSkipUpdate;
      this.settings.debugSkipFileMonitor = this.debugSkipFileMonitor;
    }
  }
  
  public static class ProfileSettings {
    @LauncherNetworkAPI
    public int ram;
    
    @LauncherNetworkAPI
    public boolean debug;
    
    @LauncherNetworkAPI
    public boolean fullScreen;
    
    @LauncherNetworkAPI
    public boolean autoEnter;
    
    @LauncherNetworkAPI
    public String javaPath;
    
    @LauncherNetworkAPI
    public boolean waylandSupport;
    
    @LauncherNetworkAPI
    public boolean debugSkipUpdate;
    
    @LauncherNetworkAPI
    public boolean debugSkipFileMonitor;
    
    public static ProfileSettings getDefault(JavaService param1JavaService, ClientProfile param1ClientProfile) {
      ProfileSettings profileSettings = new ProfileSettings();
      ClientProfile.ProfileDefaultSettings profileDefaultSettings = param1ClientProfile.getSettings();
      profileSettings.ram = profileDefaultSettings.ram;
      profileSettings.autoEnter = profileDefaultSettings.autoEnter;
      profileSettings.fullScreen = profileDefaultSettings.fullScreen;
      JavaHelper.JavaVersion javaVersion = param1JavaService.getRecommendJavaVersion(param1ClientProfile);
      if (javaVersion != null)
        profileSettings.javaPath = javaVersion.jvmDir.toString(); 
      profileSettings.debugSkipUpdate = false;
      profileSettings.debugSkipFileMonitor = false;
      return profileSettings;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\RuntimeSettings.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */