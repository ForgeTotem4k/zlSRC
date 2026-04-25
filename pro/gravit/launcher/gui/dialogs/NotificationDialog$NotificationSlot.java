package pro.gravit.launcher.gui.dialogs;

import java.util.function.Consumer;

public final class NotificationSlot extends Record {
  private final Consumer<Double> onScroll;
  
  private final double size;
  
  public NotificationSlot(Consumer<Double> paramConsumer, double paramDouble) {
    this.onScroll = paramConsumer;
    this.size = paramDouble;
  }
  
  public final String toString() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/dialogs/NotificationDialog$NotificationSlot;)Ljava/lang/String;
    //   6: areturn
  }
  
  public final int hashCode() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/dialogs/NotificationDialog$NotificationSlot;)I
    //   6: ireturn
  }
  
  public final boolean equals(Object paramObject) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/dialogs/NotificationDialog$NotificationSlot;Ljava/lang/Object;)Z
    //   7: ireturn
  }
  
  public Consumer<Double> onScroll() {
    return this.onScroll;
  }
  
  public double size() {
    return this.size;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\dialogs\NotificationDialog$NotificationSlot.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */