package pro.gravit.launcher.gui.dialogs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.helper.PositionHelper;
import pro.gravit.utils.helper.LogHelper;

public class NotificationDialog extends AbstractDialog {
  private static final Map<PositionHelper.PositionInfo, NotificationSlotsInfo> slots = new HashMap<>();
  
  private String header;
  
  private String text;
  
  private Label textHeader;
  
  private Label textDescription;
  
  private PositionHelper.PositionInfo positionInfo;
  
  private NotificationSlot positionSlot;
  
  private double positionOffset;
  
  public NotificationDialog(JavaFXApplication paramJavaFXApplication, String paramString1, String paramString2) {
    super("components/notification.fxml", paramJavaFXApplication);
    LogHelper.info("=== NotificationDialog CREATED ===");
    LogHelper.info("Header: " + paramString1);
    LogHelper.info("Text: " + paramString2);
    this.header = paramString1;
    this.text = paramString2;
  }
  
  public String getName() {
    return "notify";
  }
  
  protected void doInit() {
    LogHelper.info("=== NotificationDialog INIT ===");
    this.textHeader = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#notificationHeading" });
    this.textDescription = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#notificationText" });
    ProgressBar progressBar = new ProgressBar(1.0D);
    progressBar.setPrefWidth(189.0D);
    progressBar.setPrefHeight(4.0D);
    progressBar.setStyle("-fx-accent: #ff4444;");
    VBox vBox = (VBox)LookupHelper.lookup((Node)this.layout, new String[] { "#content" });
    if (vBox != null)
      vBox.getChildren().add(progressBar); 
    Timeline timeline = new Timeline(new KeyFrame[] { new KeyFrame(Duration.seconds(2.5D), new KeyValue[] { new KeyValue((WritableValue)progressBar.progressProperty(), Integer.valueOf(0)) }) });
    timeline.setOnFinished(paramActionEvent -> {
          try {
            close();
          } catch (Throwable throwable) {
            errorHandle(throwable);
          } 
        });
    timeline.play();
    this.layout.setOnMouseClicked(paramMouseEvent -> {
          paramTimeline.stop();
          try {
            close();
          } catch (Throwable throwable) {
            errorHandle(throwable);
          } 
        });
    this.textHeader.setText(this.header);
    this.textDescription.setText(this.text);
    setOnClose(() -> {
          if (this.positionSlot != null) {
            NotificationSlotsInfo notificationSlotsInfo = slots.get(this.positionInfo);
            notificationSlotsInfo.remove(this.positionSlot);
          } 
        });
    this.layout.setVisible(true);
    this.layout.setManaged(true);
  }
  
  public void reset() {
    super.reset();
  }
  
  public void setPosition(PositionHelper.PositionInfo paramPositionInfo, NotificationSlot paramNotificationSlot) {
    if (this.positionInfo != null) {
      NotificationSlotsInfo notificationSlotsInfo1 = slots.get(this.positionInfo);
      notificationSlotsInfo1.remove(paramNotificationSlot);
    } 
    this.positionInfo = paramPositionInfo;
    LogHelper.info("Notification position: %s", new Object[] { paramPositionInfo });
    if (paramPositionInfo == null)
      return; 
    NotificationSlotsInfo notificationSlotsInfo = slots.get(paramPositionInfo);
    if (notificationSlotsInfo == null) {
      notificationSlotsInfo = new NotificationSlotsInfo();
      slots.put(paramPositionInfo, notificationSlotsInfo);
    } 
    this.positionSlot = paramNotificationSlot;
    this.positionOffset = notificationSlotsInfo.add(paramNotificationSlot);
  }
  
  public void setHeader(String paramString) {
    this.header = paramString;
    if (isInit())
      this.textHeader.setText(paramString); 
  }
  
  public void setText(String paramString) {
    this.text = paramString;
    if (isInit())
      this.textDescription.setText(paramString); 
  }
  
  public LookupHelper.Point2D getOutSceneCoords(Rectangle2D paramRectangle2D) {
    if (this.positionInfo == null) {
      LogHelper.info("Notification position: using central");
      return super.getOutSceneCoords(paramRectangle2D);
    } 
    return PositionHelper.calculate(this.positionInfo, this.layout.getPrefWidth(), this.layout.getPrefHeight(), 0.0D, 30.0D + this.positionOffset, paramRectangle2D.getMaxX(), paramRectangle2D.getMaxY());
  }
  
  public LookupHelper.Point2D getSceneCoords(Pane paramPane) {
    return (this.positionInfo == null) ? super.getSceneCoords(paramPane) : PositionHelper.calculate(this.positionInfo, this.layout.getPrefWidth(), this.layout.getPrefHeight(), 0.0D, 30.0D + this.positionOffset, paramPane.getPrefWidth(), paramPane.getPrefHeight());
  }
  
  public void errorHandle(Throwable paramThrowable) {
    LogHelper.error(paramThrowable);
  }
  
  private static class NotificationSlotsInfo {
    private final LinkedList<NotificationDialog.NotificationSlot> stack = new LinkedList<>();
    
    double add(NotificationDialog.NotificationSlot param1NotificationSlot) {
      double d = 0.0D;
      for (NotificationDialog.NotificationSlot notificationSlot : this.stack)
        d += notificationSlot.size; 
      this.stack.add(param1NotificationSlot);
      return d;
    }
    
    void remove(NotificationDialog.NotificationSlot param1NotificationSlot) {
      boolean bool = false;
      for (NotificationDialog.NotificationSlot notificationSlot : this.stack) {
        if (bool) {
          notificationSlot.onScroll.accept(Double.valueOf(param1NotificationSlot.size));
          continue;
        } 
        if (param1NotificationSlot == notificationSlot)
          bool = true; 
      } 
      this.stack.remove(param1NotificationSlot);
    }
  }
  
  public static final class NotificationSlot extends Record {
    private final Consumer<Double> onScroll;
    
    private final double size;
    
    public NotificationSlot(Consumer<Double> param1Consumer, double param1Double) {
      this.onScroll = param1Consumer;
      this.size = param1Double;
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
    
    public final boolean equals(Object param1Object) {
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\dialogs\NotificationDialog.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */