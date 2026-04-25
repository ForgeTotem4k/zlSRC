package pro.gravit.launcher.runtime.client;

import com.google.gson.GsonBuilder;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.events.PreGsonPhase;
import pro.gravit.launcher.base.request.websockets.ClientWebSocketService;
import pro.gravit.launcher.core.managers.GsonManager;
import pro.gravit.launcher.start.RuntimeModuleManager;
import pro.gravit.utils.UniversalJsonAdapter;

public class RuntimeGsonManager extends GsonManager {
  private final RuntimeModuleManager moduleManager;
  
  public RuntimeGsonManager(RuntimeModuleManager paramRuntimeModuleManager) {
    this.moduleManager = paramRuntimeModuleManager;
  }
  
  public void registerAdapters(GsonBuilder paramGsonBuilder) {
    super.registerAdapters(paramGsonBuilder);
    paramGsonBuilder.registerTypeAdapter(UserSettings.class, new UniversalJsonAdapter(UserSettings.providers));
    ClientWebSocketService.appendTypeAdapters(paramGsonBuilder);
    this.moduleManager.invokeEvent((LauncherModule.Event)new PreGsonPhase(paramGsonBuilder));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\RuntimeGsonManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */