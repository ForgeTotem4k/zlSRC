package pro.gravit.launcher.gui.impl;

import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTriggerContext;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.utils.helper.JavaHelper;

class TriggerManagerContext implements OptionalTriggerContext {
  private final ClientProfile profile;
  
  private TriggerManagerContext(ClientProfile paramClientProfile) {
    this.profile = paramClientProfile;
  }
  
  public ClientProfile getProfile() {
    return this.profile;
  }
  
  public String getUsername() {
    return TriggerManager.this.application.authService.getUsername();
  }
  
  public JavaHelper.JavaVersion getJavaVersion() {
    RuntimeSettings.ProfileSettings profileSettings = TriggerManager.this.application.getProfileSettings(this.profile);
    for (JavaHelper.JavaVersion javaVersion : TriggerManager.this.application.javaService.javaVersions) {
      if (profileSettings.javaPath != null && profileSettings.javaPath.equals(javaVersion.jvmDir.toString()))
        return javaVersion; 
    } 
    return JavaHelper.JavaVersion.getCurrentJavaVersion();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\TriggerManager$TriggerManagerContext.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */