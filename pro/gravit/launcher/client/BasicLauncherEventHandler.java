package pro.gravit.launcher.client;

import pro.gravit.launcher.base.events.ExtendedTokenRequestEvent;
import pro.gravit.launcher.base.events.NotificationEvent;
import pro.gravit.launcher.base.events.request.SecurityReportRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.client.api.DialogService;
import pro.gravit.utils.helper.LogHelper;

public class BasicLauncherEventHandler implements RequestService.EventHandler {
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> boolean eventHandle(T paramT) {
    if (paramT instanceof SecurityReportRequestEvent) {
      SecurityReportRequestEvent securityReportRequestEvent = (SecurityReportRequestEvent)paramT;
      if (securityReportRequestEvent.action == SecurityReportRequestEvent.ReportAction.TOKEN_EXPIRED)
        try {
          Request.restore();
        } catch (Exception exception) {
          LogHelper.error(exception);
        }  
    } else if (paramT instanceof ExtendedTokenRequestEvent) {
      ExtendedTokenRequestEvent extendedTokenRequestEvent = (ExtendedTokenRequestEvent)paramT;
      String str = extendedTokenRequestEvent.getExtendedToken();
      if (str != null)
        Request.addExtendedToken(extendedTokenRequestEvent.getExtendedTokenName(), new Request.ExtendedToken(extendedTokenRequestEvent.getExtendedToken(), extendedTokenRequestEvent.getExtendedTokenExpire())); 
    } else if (paramT instanceof NotificationEvent) {
      NotificationEvent notificationEvent = (NotificationEvent)paramT;
      if (DialogService.isNotificationsAvailable())
        DialogService.createNotification(notificationEvent.icon, notificationEvent.head, notificationEvent.message); 
    } 
    return false;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\BasicLauncherEventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */