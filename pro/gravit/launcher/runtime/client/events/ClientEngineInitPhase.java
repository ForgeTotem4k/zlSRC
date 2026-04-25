package pro.gravit.launcher.runtime.client.events;

import pro.gravit.launcher.base.modules.events.InitPhase;
import pro.gravit.launcher.runtime.LauncherEngine;

public class ClientEngineInitPhase extends InitPhase {
  public final LauncherEngine engine;
  
  public ClientEngineInitPhase(LauncherEngine paramLauncherEngine) {
    this.engine = paramLauncherEngine;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\events\ClientEngineInitPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */