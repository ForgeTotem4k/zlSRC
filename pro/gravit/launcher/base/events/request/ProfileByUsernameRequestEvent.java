package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class ProfileByUsernameRequestEvent extends RequestEvent {
  private static final UUID uuid = UUID.fromString("06204302-ff6b-4779-b97d-541e3bc39aa1");
  
  @LauncherNetworkAPI
  public final PlayerProfile playerProfile;
  
  @LauncherNetworkAPI
  public String error;
  
  public ProfileByUsernameRequestEvent(PlayerProfile paramPlayerProfile) {
    this.playerProfile = paramPlayerProfile;
  }
  
  public String getType() {
    return "profileByUsername";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\ProfileByUsernameRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */