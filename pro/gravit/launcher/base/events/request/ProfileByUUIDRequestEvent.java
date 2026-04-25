package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class ProfileByUUIDRequestEvent extends RequestEvent {
  private static final UUID uuid = UUID.fromString("b9014cf3-4b95-4d38-8c5f-867f190a18a0");
  
  @LauncherNetworkAPI
  public String error;
  
  @LauncherNetworkAPI
  public PlayerProfile playerProfile;
  
  public ProfileByUUIDRequestEvent(PlayerProfile paramPlayerProfile) {
    this.playerProfile = paramPlayerProfile;
  }
  
  public ProfileByUUIDRequestEvent() {}
  
  public String getType() {
    return "profileByUUID";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\ProfileByUUIDRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */