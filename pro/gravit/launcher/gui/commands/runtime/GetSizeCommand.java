package pro.gravit.launcher.gui.commands.runtime;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.stage.PrimaryStage;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class GetSizeCommand extends Command {
  private final JavaFXApplication application;
  
  public GetSizeCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return null;
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    PrimaryStage primaryStage = this.application.getMainStage();
    Stage stage = primaryStage.getStage();
    LogHelper.info("Stage: H: %f W: %f", new Object[] { Double.valueOf(stage.getHeight()), Double.valueOf(stage.getWidth()) });
    Scene scene = stage.getScene();
    LogHelper.info("Scene: H: %f W: %f", new Object[] { Double.valueOf(scene.getHeight()), Double.valueOf(scene.getWidth()) });
    Pane pane1 = (Pane)scene.getRoot();
    LogHelper.info("StackPane: H: %f W: %f", new Object[] { Double.valueOf(pane1.getHeight()), Double.valueOf(pane1.getWidth()) });
    Pane pane2 = (Pane)pane1.getChildren().get(0);
    LogHelper.info("Layout: H: %f W: %f", new Object[] { Double.valueOf(pane2.getHeight()), Double.valueOf(pane2.getWidth()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\GetSizeCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */