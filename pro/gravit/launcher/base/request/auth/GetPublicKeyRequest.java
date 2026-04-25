package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.GetPublicKeyRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class GetPublicKeyRequest extends Request<GetPublicKeyRequestEvent> {
  public String getType() {
    return "getPublicKey";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\GetPublicKeyRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */