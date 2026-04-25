package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class JoinServerRequestEvent extends RequestEvent {
  private static final UUID uuid = UUID.fromString("2a12e7b5-3f4a-4891-a2f9-ea141c8e1995");
  
  @LauncherNetworkAPI
  public final boolean allow;
  
  public JoinServerRequestEvent(boolean paramBoolean) {
    this.allow = paramBoolean;
  }
  
  public String getType() {
    return "joinServer";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\JoinServerRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */