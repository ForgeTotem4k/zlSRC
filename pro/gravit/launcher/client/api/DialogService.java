package pro.gravit.launcher.client.api;

import java.util.function.Consumer;
import pro.gravit.launcher.base.events.NotificationEvent;

public class DialogService {
  private static DialogServiceImplementation dialogImpl;
  
  private static DialogServiceNotificationImplementation notificationImpl;
  
  private DialogService() {
    throw new UnsupportedOperationException();
  }
  
  public static void setDialogImpl(DialogServiceImplementation paramDialogServiceImplementation) {
    dialogImpl = paramDialogServiceImplementation;
  }
  
  public static void setNotificationImpl(DialogServiceNotificationImplementation paramDialogServiceNotificationImplementation) {
    notificationImpl = paramDialogServiceNotificationImplementation;
  }
  
  public static boolean isDialogsAvailable() {
    return (dialogImpl != null);
  }
  
  public static boolean isNotificationsAvailable() {
    return (notificationImpl != null);
  }
  
  private static void checkIfAvailable() {
    if (!isDialogsAvailable())
      throw new UnsupportedOperationException("DialogService dialogs implementation not available"); 
  }
  
  public static void createNotification(NotificationEvent.NotificationType paramNotificationType, String paramString1, String paramString2) {
    if (!isNotificationsAvailable())
      throw new UnsupportedOperationException("DialogService notifications implementation not available"); 
    notificationImpl.createNotification(paramNotificationType, paramString1, paramString2);
  }
  
  public static void showDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2) {
    checkIfAvailable();
    dialogImpl.showDialog(paramString1, paramString2, paramRunnable1, paramRunnable2);
  }
  
  public static void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2) {
    checkIfAvailable();
    dialogImpl.showApplyDialog(paramString1, paramString2, paramRunnable1, paramRunnable2);
  }
  
  public static void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, Runnable paramRunnable3) {
    checkIfAvailable();
    dialogImpl.showApplyDialog(paramString1, paramString2, paramRunnable1, paramRunnable2, paramRunnable3);
  }
  
  public static void showTextDialog(String paramString, Consumer<String> paramConsumer, Runnable paramRunnable) {
    checkIfAvailable();
    dialogImpl.showTextDialog(paramString, paramConsumer, paramRunnable);
  }
  
  public static interface DialogServiceImplementation {
    void showDialog(String param1String1, String param1String2, Runnable param1Runnable1, Runnable param1Runnable2);
    
    void showApplyDialog(String param1String1, String param1String2, Runnable param1Runnable1, Runnable param1Runnable2);
    
    void showApplyDialog(String param1String1, String param1String2, Runnable param1Runnable1, Runnable param1Runnable2, Runnable param1Runnable3);
    
    void showTextDialog(String param1String, Consumer<String> param1Consumer, Runnable param1Runnable);
    
    static {
    
    }
  }
  
  public static interface DialogServiceNotificationImplementation {
    void createNotification(NotificationEvent.NotificationType param1NotificationType, String param1String1, String param1String2);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\api\DialogService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */