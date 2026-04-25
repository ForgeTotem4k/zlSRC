package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.FetchClientProfileKeyRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class FetchClientProfileKeyRequest extends Request<FetchClientProfileKeyRequestEvent> {
  public String getType() {
    return "clientProfileKey";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\FetchClientProfileKeyRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */