package pro.gravit.launcher.base.request.auth;

import java.util.UUID;
import pro.gravit.launcher.base.events.request.JoinServerRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.VerifyHelper;

public final class JoinServerRequest extends Request<JoinServerRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final String username;
  
  @LauncherNetworkAPI
  public final UUID uuid;
  
  @LauncherNetworkAPI
  public final String accessToken;
  
  @LauncherNetworkAPI
  public final String serverID;
  
  public JoinServerRequest(String paramString1, String paramString2, String paramString3) {
    this.username = paramString1;
    this.uuid = null;
    this.accessToken = paramString2;
    this.serverID = VerifyHelper.verifyServerID(paramString3);
  }
  
  public JoinServerRequest(UUID paramUUID, String paramString1, String paramString2) {
    this.username = null;
    this.uuid = paramUUID;
    this.accessToken = paramString1;
    this.serverID = paramString2;
  }
  
  public String getType() {
    return "joinServer";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\JoinServerRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */