package pro.gravit.launchermodules.sentryl;

import io.sentry.IScope;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.ExitRequestEvent;
import pro.gravit.launcher.base.request.RequestService;

public class SentryEventHandler implements RequestService.EventHandler {
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> boolean eventHandle(T paramT) {
    if (paramT instanceof AuthRequestEvent) {
      AuthRequestEvent authRequestEvent = (AuthRequestEvent)paramT;
      if (authRequestEvent.playerProfile == null)
        return false; 
      SentryModule.currentScopes.configureScope(paramIScope -> paramIScope.setUser(SentryModule.makeSentryUser(paramAuthRequestEvent.playerProfile)));
    } 
    if (paramT instanceof ExitRequestEvent) {
      ExitRequestEvent exitRequestEvent = (ExitRequestEvent)paramT;
      if (exitRequestEvent.reason == ExitRequestEvent.ExitReason.NO_EXIT)
        return false; 
      SentryModule.currentScopes.configureScope(paramIScope -> paramIScope.setUser(null));
    } 
    return false;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentryl\SentryEventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */