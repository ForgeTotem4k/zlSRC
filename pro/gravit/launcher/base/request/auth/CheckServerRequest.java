package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.CheckServerRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.VerifyHelper;

public final class CheckServerRequest extends Request<CheckServerRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final String username;
  
  @LauncherNetworkAPI
  public final String serverID;
  
  @LauncherNetworkAPI
  public boolean needHardware;
  
  @LauncherNetworkAPI
  public boolean needProperties;
  
  public CheckServerRequest(String paramString1, String paramString2) {
    this.username = paramString1;
    this.serverID = VerifyHelper.verifyServerID(paramString2);
  }
  
  public CheckServerRequest(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
    this.username = paramString1;
    this.serverID = VerifyHelper.verifyServerID(paramString2);
    this.needHardware = paramBoolean1;
    this.needProperties = paramBoolean2;
  }
  
  public String getType() {
    return "checkServer";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\CheckServerRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */