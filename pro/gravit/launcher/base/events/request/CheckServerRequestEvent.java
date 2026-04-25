package pro.gravit.launcher.base.events.request;

import java.util.Map;
import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class CheckServerRequestEvent extends RequestEvent {
  private static final UUID _uuid = UUID.fromString("8801d07c-51ba-4059-b61d-fe1f1510b28a");
  
  @LauncherNetworkAPI
  public UUID uuid;
  
  @LauncherNetworkAPI
  public PlayerProfile playerProfile;
  
  @LauncherNetworkAPI
  public String sessionId;
  
  @LauncherNetworkAPI
  public String hardwareId;
  
  @LauncherNetworkAPI
  public Map<String, String> sessionProperties;
  
  public CheckServerRequestEvent(PlayerProfile paramPlayerProfile) {
    this.playerProfile = paramPlayerProfile;
  }
  
  public CheckServerRequestEvent() {}
  
  public String getType() {
    return "checkServer";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\CheckServerRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */