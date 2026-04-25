package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.CurrentUserRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class CurrentUserRequest extends Request<CurrentUserRequestEvent> {
  public String getType() {
    return "currentUser";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\CurrentUserRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */