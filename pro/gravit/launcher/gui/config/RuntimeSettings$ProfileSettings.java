package pro.gravit.launcher.gui.config;

import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.gui.service.JavaService;
import pro.gravit.utils.helper.JavaHelper;

public class ProfileSettings {
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
  
  public static ProfileSettings getDefault(JavaService paramJavaService, ClientProfile paramClientProfile) {
    ProfileSettings profileSettings = new ProfileSettings();
    ClientProfile.ProfileDefaultSettings profileDefaultSettings = paramClientProfile.getSettings();
    profileSettings.ram = profileDefaultSettings.ram;
    profileSettings.autoEnter = profileDefaultSettings.autoEnter;
    profileSettings.fullScreen = profileDefaultSettings.fullScreen;
    JavaHelper.JavaVersion javaVersion = paramJavaService.getRecommendJavaVersion(paramClientProfile);
    if (javaVersion != null)
      profileSettings.javaPath = javaVersion.jvmDir.toString(); 
    profileSettings.debugSkipUpdate = false;
    profileSettings.debugSkipFileMonitor = false;
    return profileSettings;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\RuntimeSettings$ProfileSettings.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */