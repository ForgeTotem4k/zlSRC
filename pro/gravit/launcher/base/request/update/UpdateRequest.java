package pro.gravit.launcher.base.request.update;

import pro.gravit.launcher.base.events.request.UpdateRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public final class UpdateRequest extends Request<UpdateRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final String dirName;
  
  public UpdateRequest(String paramString) {
    this.dirName = paramString;
  }
  
  public String getType() {
    return "update";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\update\UpdateRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */