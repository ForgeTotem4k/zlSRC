package pro.gravit.launcher.base.events.request;

import java.util.List;
import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class ProfilesRequestEvent extends RequestEvent {
  private static final UUID uuid = UUID.fromString("2f26fbdf-598a-46dd-92fc-1699c0e173b1");
  
  @LauncherNetworkAPI
  public List<ClientProfile> profiles;
  
  public ProfilesRequestEvent(List<ClientProfile> paramList) {
    this.profiles = paramList;
  }
  
  public ProfilesRequestEvent() {}
  
  public String getType() {
    return "profiles";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\ProfilesRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */