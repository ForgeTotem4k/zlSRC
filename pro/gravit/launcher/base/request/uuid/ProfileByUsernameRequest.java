package pro.gravit.launcher.base.request.uuid;

import pro.gravit.launcher.base.events.request.ProfileByUsernameRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public final class ProfileByUsernameRequest extends Request<ProfileByUsernameRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final String username;
  
  public ProfileByUsernameRequest(String paramString) {
    this.username = paramString;
  }
  
  public String getType() {
    return "profileByUsername";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\uuid\ProfileByUsernameRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */