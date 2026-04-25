package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class ErrorRequestEvent extends RequestEvent {
  public static UUID uuid = UUID.fromString("0af22bc7-aa01-4881-bdbb-dc62b3cdac96");
  
  @LauncherNetworkAPI
  public final String error;
  
  public ErrorRequestEvent(String paramString) {
    this.error = paramString;
  }
  
  public String getType() {
    return "error";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\ErrorRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */