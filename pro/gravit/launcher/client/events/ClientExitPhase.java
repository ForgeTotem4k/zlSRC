package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.events.ClosePhase;

public class ClientExitPhase extends ClosePhase {
  public final int code;
  
  public ClientExitPhase(int paramInt) {
    this.code = paramInt;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientExitPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */