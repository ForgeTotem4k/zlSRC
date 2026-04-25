package pro.gravit.launcher.base.request.management;

import pro.gravit.launcher.base.events.request.GetConnectUUIDRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class GetConnectUUIDRequest extends Request<GetConnectUUIDRequestEvent> {
  public String getType() {
    return "getConnectUUID";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\management\GetConnectUUIDRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */