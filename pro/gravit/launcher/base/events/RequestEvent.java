package pro.gravit.launcher.base.events;

import java.util.UUID;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public abstract class RequestEvent implements WebSocketEvent {
  public static final UUID eventUUID = UUID.fromString("fac0e2bd-9820-4449-b191-1d7c9bf781be");
  
  @LauncherNetworkAPI
  public UUID requestUUID;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\RequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */