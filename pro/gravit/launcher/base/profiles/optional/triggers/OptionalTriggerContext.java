package pro.gravit.launcher.base.profiles.optional.triggers;

import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.utils.helper.JavaHelper;

public interface OptionalTriggerContext {
  ClientProfile getProfile();
  
  String getUsername();
  
  JavaHelper.JavaVersion getJavaVersion();
  
  default ClientPermissions getPermissions() {
    return ClientPermissions.DEFAULT;
  }
  
  default PlayerProfile getPlayerProfile() {
    return null;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\triggers\OptionalTriggerContext.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */