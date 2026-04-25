package pro.gravit.launcher.base.modules.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.request.RequestService;

public class OfflineModeEvent extends LauncherModule.Event {
  public RequestService service;
  
  public OfflineModeEvent(RequestService paramRequestService) {
    this.service = paramRequestService;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\events\OfflineModeEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */