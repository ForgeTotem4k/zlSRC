package pro.gravit.launcher.runtime.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;

public class ClientPreGuiPhase extends LauncherModule.Event {
  public RuntimeProvider runtimeProvider;
  
  public ClientPreGuiPhase(RuntimeProvider paramRuntimeProvider) {
    this.runtimeProvider = paramRuntimeProvider;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\events\ClientPreGuiPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */