package pro.gravit.launcher.base.events;

import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class NotificationEvent implements WebSocketEvent {
  @LauncherNetworkAPI
  public final String head;
  
  @LauncherNetworkAPI
  public final String message;
  
  @LauncherNetworkAPI
  public final NotificationType icon;
  
  public NotificationEvent(String paramString1, String paramString2) {
    this.head = paramString1;
    this.message = paramString2;
    this.icon = NotificationType.INFO;
  }
  
  public NotificationEvent(String paramString1, String paramString2, NotificationType paramNotificationType) {
    this.head = paramString1;
    this.message = paramString2;
    this.icon = paramNotificationType;
  }
  
  public String getType() {
    return "notification";
  }
  
  public enum NotificationType {
    INFO, WARN, ERROR, OTHER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\NotificationEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */