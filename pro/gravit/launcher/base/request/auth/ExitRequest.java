package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.ExitRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class ExitRequest extends Request<ExitRequestEvent> {
  public final boolean exitAll = false;
  
  public final String username = null;
  
  public ExitRequest() {}
  
  public ExitRequest(boolean paramBoolean) {}
  
  public ExitRequest(boolean paramBoolean, String paramString) {}
  
  public String getType() {
    return "exit";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\ExitRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */