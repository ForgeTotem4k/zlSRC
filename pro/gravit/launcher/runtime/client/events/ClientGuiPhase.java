package pro.gravit.launcher.runtime.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;

public class ClientGuiPhase extends LauncherModule.Event {
  public final RuntimeProvider runtimeProvider;
  
  public ClientGuiPhase(RuntimeProvider paramRuntimeProvider) {
    this.runtimeProvider = paramRuntimeProvider;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\events\ClientGuiPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */