package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.client.ClientParams;

public class ClientProcessLaunchEvent extends LauncherModule.Event {
  public final ClientParams params;
  
  public ClientProcessLaunchEvent(ClientParams paramClientParams) {
    this.params = paramClientParams;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientProcessLaunchEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */