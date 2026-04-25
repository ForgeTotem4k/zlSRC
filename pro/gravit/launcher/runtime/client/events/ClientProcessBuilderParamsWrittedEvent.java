package pro.gravit.launcher.runtime.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.runtime.client.ClientLauncherProcess;

public class ClientProcessBuilderParamsWrittedEvent extends LauncherModule.Event {
  public final ClientLauncherProcess process;
  
  public ClientProcessBuilderParamsWrittedEvent(ClientLauncherProcess paramClientLauncherProcess) {
    this.process = paramClientLauncherProcess;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\events\ClientProcessBuilderParamsWrittedEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */