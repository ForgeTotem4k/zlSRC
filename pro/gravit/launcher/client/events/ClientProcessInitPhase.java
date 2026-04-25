package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.events.InitPhase;
import pro.gravit.launcher.client.ClientParams;

public class ClientProcessInitPhase extends InitPhase {
  public final ClientParams params;
  
  public ClientProcessInitPhase(ClientParams paramClientParams) {
    this.params = paramClientParams;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientProcessInitPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */