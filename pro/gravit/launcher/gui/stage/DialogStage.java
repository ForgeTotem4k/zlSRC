package pro.gravit.launcher.gui.stage;

import java.io.IOException;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.dialogs.AbstractDialog;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.utils.helper.LogHelper;

public class DialogStage extends AbstractStage {
  public DialogStage(JavaFXApplication paramJavaFXApplication, String paramString, AbstractDialog paramAbstractDialog) throws Exception {
    super(paramJavaFXApplication, paramJavaFXApplication.newStage());
    this.stage.setTitle(paramString);
    this.stage.initStyle(StageStyle.TRANSPARENT);
    this.stage.setResizable(false);
    this.stage.setOnCloseRequest(Event::consume);
    this.scene.setFill((Paint)Color.TRANSPARENT);
    try {
      Image image = new Image(JavaFXApplication.getResourceURL("favicon.png").toString());
      this.stage.getIcons().add(image);
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
    setClipRadius(10.0D, 10.0D);
    setScene((AbstractVisualComponent)paramAbstractDialog, true);
    enableMouseDrag((Node)paramAbstractDialog.getLayout());
    Screen screen = Screen.getPrimary();
    Rectangle2D rectangle2D = screen.getVisualBounds();
    if (rectangle2D.getMaxX() == 0.0D || rectangle2D.getMaxY() == 0.0D)
      rectangle2D = screen.getBounds(); 
    LogHelper.info("Bounds: X: %f Y: %f", new Object[] { Double.valueOf(rectangle2D.getMaxX()), Double.valueOf(rectangle2D.getMaxY()) });
    LookupHelper.Point2D point2D = paramAbstractDialog.getOutSceneCoords(rectangle2D);
    this.stage.setX(point2D.x);
    this.stage.setY(point2D.y);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\stage\DialogStage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */