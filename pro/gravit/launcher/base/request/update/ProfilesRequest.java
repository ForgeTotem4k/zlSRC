package pro.gravit.launcher.base.request.update;

import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;

public final class ProfilesRequest extends Request<ProfilesRequestEvent> implements WebSocketRequest {
  public String getType() {
    return "profiles";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\update\ProfilesRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */