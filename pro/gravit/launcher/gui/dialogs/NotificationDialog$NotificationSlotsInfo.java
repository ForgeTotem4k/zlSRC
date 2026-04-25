package pro.gravit.launcher.gui.dialogs;

import java.util.LinkedList;

class NotificationSlotsInfo {
  private final LinkedList<NotificationDialog.NotificationSlot> stack = new LinkedList<>();
  
  double add(NotificationDialog.NotificationSlot paramNotificationSlot) {
    double d = 0.0D;
    for (NotificationDialog.NotificationSlot notificationSlot : this.stack)
      d += notificationSlot.size; 
    this.stack.add(paramNotificationSlot);
    return d;
  }
  
  void remove(NotificationDialog.NotificationSlot paramNotificationSlot) {
    boolean bool = false;
    for (NotificationDialog.NotificationSlot notificationSlot : this.stack) {
      if (bool) {
        notificationSlot.onScroll.accept(Double.valueOf(paramNotificationSlot.size));
        continue;
      } 
      if (paramNotificationSlot == notificationSlot)
        bool = true; 
    } 
    this.stack.remove(paramNotificationSlot);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\dialogs\NotificationDialog$NotificationSlotsInfo.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */