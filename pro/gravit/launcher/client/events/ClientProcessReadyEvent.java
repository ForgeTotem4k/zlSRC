package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.events.PostInitPhase;
import pro.gravit.launcher.client.ClientParams;

public class ClientProcessReadyEvent extends PostInitPhase {
  public final ClientParams params;
  
  public ClientProcessReadyEvent(ClientParams paramClientParams) {
    this.params = paramClientParams;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientProcessReadyEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */