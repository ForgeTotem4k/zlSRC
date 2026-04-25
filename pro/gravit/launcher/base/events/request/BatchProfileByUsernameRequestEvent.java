package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class BatchProfileByUsernameRequestEvent extends RequestEvent {
  @LauncherNetworkAPI
  public String error;
  
  @LauncherNetworkAPI
  public PlayerProfile[] playerProfiles;
  
  public BatchProfileByUsernameRequestEvent(PlayerProfile[] paramArrayOfPlayerProfile) {
    this.playerProfiles = paramArrayOfPlayerProfile;
  }
  
  public BatchProfileByUsernameRequestEvent() {}
  
  public String getType() {
    return "batchProfileByUsername";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\BatchProfileByUsernameRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */