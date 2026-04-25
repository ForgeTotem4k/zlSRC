package pro.gravit.launcher.gui.stage;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.utils.helper.LogHelper;

public class PrimaryStage extends AbstractStage {
  public PrimaryStage(JavaFXApplication paramJavaFXApplication, Stage paramStage, String paramString) {
    super(paramJavaFXApplication, paramStage);
    paramStage.setTitle(paramString);
    this.stage.initStyle(StageStyle.TRANSPARENT);
    this.stage.setResizable(true);
    this.scene.setFill((Paint)Color.TRANSPARENT);
    try {
      Image image = new Image(JavaFXApplication.getResourceURL("favicon.png").toString());
      this.stage.getIcons().add(image);
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
    setClipRadius(10.0D, 10.0D);
  }
  
  public void pushBackground(AbstractVisualComponent paramAbstractVisualComponent) {
    this.scenePosition.incrementAndGet();
    addBefore((Node)this.visualComponent.getLayout(), (Node)paramAbstractVisualComponent.getLayout());
  }
  
  public void pullBackground(AbstractVisualComponent paramAbstractVisualComponent) {
    this.scenePosition.decrementAndGet();
    pull((Node)paramAbstractVisualComponent.getLayout());
  }
  
  public void close() {
    Platform.exit();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\stage\PrimaryStage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */