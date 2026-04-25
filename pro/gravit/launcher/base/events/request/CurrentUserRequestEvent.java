package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;

public class CurrentUserRequestEvent extends RequestEvent {
  public final UserInfo userInfo;
  
  public CurrentUserRequestEvent(UserInfo paramUserInfo) {
    this.userInfo = paramUserInfo;
  }
  
  public String getType() {
    return "currentUser";
  }
  
  public static class UserInfo {
    public ClientPermissions permissions;
    
    public String accessToken;
    
    public PlayerProfile playerProfile;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\CurrentUserRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */