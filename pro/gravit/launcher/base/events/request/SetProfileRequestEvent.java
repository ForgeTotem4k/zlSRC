package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class SetProfileRequestEvent extends RequestEvent {
  private static final UUID uuid = UUID.fromString("08c0de9e-4364-4152-9066-8354a3a48541");
  
  @LauncherNetworkAPI
  public final ClientProfile newProfile;
  
  public SetProfileRequestEvent(ClientProfile paramClientProfile) {
    this.newProfile = paramClientProfile;
  }
  
  public String getType() {
    return "setProfile";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\SetProfileRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */