package pro.gravit.launcher.runtime.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.runtime.client.ClientLauncherProcess;

public class ClientProcessBuilderCreateEvent extends LauncherModule.Event {
  public final ClientLauncherProcess processBuilder;
  
  public ClientProcessBuilderCreateEvent(ClientLauncherProcess paramClientLauncherProcess) {
    this.processBuilder = paramClientLauncherProcess;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\events\ClientProcessBuilderCreateEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */