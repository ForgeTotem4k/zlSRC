package pro.gravit.launcher.client;

import com.google.gson.GsonBuilder;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.events.PreGsonPhase;
import pro.gravit.launcher.base.request.websockets.ClientWebSocketService;
import pro.gravit.launcher.core.managers.GsonManager;

public class ClientGsonManager extends GsonManager {
  private final ClientModuleManager moduleManager;
  
  public ClientGsonManager(ClientModuleManager paramClientModuleManager) {
    this.moduleManager = paramClientModuleManager;
  }
  
  public void registerAdapters(GsonBuilder paramGsonBuilder) {
    super.registerAdapters(paramGsonBuilder);
    ClientWebSocketService.appendTypeAdapters(paramGsonBuilder);
    this.moduleManager.invokeEvent((LauncherModule.Event)new PreGsonPhase(paramGsonBuilder));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientGsonManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */