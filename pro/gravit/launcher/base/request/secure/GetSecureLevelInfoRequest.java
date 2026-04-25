package pro.gravit.launcher.base.request.secure;

import pro.gravit.launcher.base.events.request.GetSecureLevelInfoRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class GetSecureLevelInfoRequest extends Request<GetSecureLevelInfoRequestEvent> {
  public String getType() {
    return "getSecureLevelInfo";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\secure\GetSecureLevelInfoRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */