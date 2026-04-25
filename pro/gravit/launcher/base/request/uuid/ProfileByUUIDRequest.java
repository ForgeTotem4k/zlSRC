package pro.gravit.launcher.base.request.uuid;

import java.util.Objects;
import java.util.UUID;
import pro.gravit.launcher.base.events.request.ProfileByUUIDRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public final class ProfileByUUIDRequest extends Request<ProfileByUUIDRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final UUID uuid;
  
  public ProfileByUUIDRequest(UUID paramUUID) {
    this.uuid = Objects.<UUID>requireNonNull(paramUUID, "uuid");
  }
  
  public String getType() {
    return "profileByUUID";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\uuid\ProfileByUUIDRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */