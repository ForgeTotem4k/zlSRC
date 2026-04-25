package pro.gravit.launcher.gui.stage;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.StageStyle;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.AbstractStage;

public class ConsoleStage extends AbstractStage {
  public ConsoleStage(JavaFXApplication paramJavaFXApplication) {
    super(paramJavaFXApplication, paramJavaFXApplication.newStage());
    this.stage.initStyle(StageStyle.TRANSPARENT);
    this.stage.setResizable(true);
    this.scene.setFill((Paint)Color.TRANSPARENT);
    this.stage.setTitle("%s Launcher Console".formatted(new Object[] { paramJavaFXApplication.config.projectName }));
    this.stage.setResizable(false);
    setClipRadius(10.0D, 10.0D);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\stage\ConsoleStage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */