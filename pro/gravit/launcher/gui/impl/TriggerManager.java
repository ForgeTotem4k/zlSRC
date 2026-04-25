package pro.gravit.launcher.gui.impl;

import java.util.Locale;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTriggerContext;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.utils.helper.JavaHelper;

public class TriggerManager {
  private final JavaFXApplication application;
  
  public TriggerManager(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public void process(ClientProfile paramClientProfile, OptionalView paramOptionalView) {
    TriggerManagerContext triggerManagerContext = new TriggerManagerContext(paramClientProfile);
    for (OptionalFile optionalFile : paramOptionalView.all) {
      if (optionalFile.limited)
        if (!this.application.authService.checkPermission("launcher.runtime.optionals.%s.%s.show".formatted(new Object[] { paramClientProfile.getUUID(), optionalFile.name.toLowerCase(Locale.ROOT) }))) {
          paramOptionalView.disable(optionalFile, null);
          optionalFile.visible = false;
        } else {
          optionalFile.visible = true;
        }  
      if (optionalFile.triggersList == null)
        continue; 
      boolean bool = false;
      byte b1 = 0;
      byte b2 = 0;
      for (OptionalTrigger optionalTrigger : optionalFile.triggersList) {
        if (optionalTrigger.required)
          bool = true; 
        if (optionalTrigger.check(optionalFile, triggerManagerContext)) {
          b1++;
          continue;
        } 
        b2++;
      } 
      if (bool) {
        if (b2 == 0) {
          paramOptionalView.enable(optionalFile, true, null);
          continue;
        } 
        paramOptionalView.disable(optionalFile, null);
        continue;
      } 
      if (b1 > 0)
        paramOptionalView.enable(optionalFile, false, null); 
    } 
  }
  
  private class TriggerManagerContext implements OptionalTriggerContext {
    private final ClientProfile profile;
    
    private TriggerManagerContext(ClientProfile param1ClientProfile) {
      this.profile = param1ClientProfile;
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\TriggerManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */