package pro.gravit.launcher.gui.service;

import java.util.function.Consumer;
import pro.gravit.launcher.base.events.NotificationEvent;
import pro.gravit.launcher.client.api.DialogService;
import pro.gravit.launcher.gui.impl.MessageManager;

public class RuntimeDialogService implements DialogService.DialogServiceNotificationImplementation, DialogService.DialogServiceImplementation {
  private final MessageManager messageManager;
  
  public RuntimeDialogService(MessageManager paramMessageManager) {
    this.messageManager = paramMessageManager;
  }
  
  public void showDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2) {
    this.messageManager.showDialog(paramString1, paramString2, paramRunnable1, paramRunnable2, false);
  }
  
  public void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2) {
    this.messageManager.showApplyDialog(paramString1, paramString2, paramRunnable1, paramRunnable2, false);
  }
  
  public void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, Runnable paramRunnable3) {
    this.messageManager.showApplyDialog(paramString1, paramString2, paramRunnable1, paramRunnable2, paramRunnable3, false);
  }
  
  public void showTextDialog(String paramString, Consumer<String> paramConsumer, Runnable paramRunnable) {
    throw new UnsupportedOperationException();
  }
  
  public void createNotification(NotificationEvent.NotificationType paramNotificationType, String paramString1, String paramString2) {
    this.messageManager.createNotification(paramString1, paramString2);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\RuntimeDialogService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */