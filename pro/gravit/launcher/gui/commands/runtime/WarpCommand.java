package pro.gravit.launcher.gui.commands.runtime;

import javafx.event.ActionEvent;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.stage.PrimaryStage;
import pro.gravit.utils.command.Command;

public class WarpCommand extends Command {
  private JavaFXApplication application;
  
  public WarpCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return "[scene/overlay] [name]";
  }
  
  public String getUsageDescription() {
    return "warp to any scene/overlay";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 2);
    if (this.application == null)
      this.application = JavaFXApplication.getInstance(); 
    if (paramVarArgs[0].equals("scene")) {
      AbstractScene abstractScene = (AbstractScene)this.application.gui.getByName(paramVarArgs[1]);
      if (abstractScene == null)
        throw new IllegalArgumentException("Scene %s not found".formatted(new Object[] { paramVarArgs[1] })); 
      PrimaryStage primaryStage = this.application.getMainStage();
      ContextHelper.runInFxThreadStatic(() -> {
            paramPrimaryStage.setScene((AbstractVisualComponent)paramAbstractScene, true);
            if (!paramPrimaryStage.isShowing())
              paramPrimaryStage.show(); 
          });
    } else if (paramVarArgs[0].equals("overlay")) {
      AbstractOverlay abstractOverlay = (AbstractOverlay)this.application.gui.getByName(paramVarArgs[1]);
      if (abstractOverlay == null)
        throw new IllegalArgumentException("Overlay %s not found".formatted(new Object[] { paramVarArgs[1] })); 
      PrimaryStage primaryStage = this.application.getMainStage();
      if (primaryStage.isNullScene())
        throw new IllegalStateException("Please wrap to scene before"); 
      AbstractScene abstractScene = (AbstractScene)primaryStage.getVisualComponent();
      ContextHelper.runInFxThreadStatic(() -> paramAbstractScene.showOverlay(paramAbstractOverlay, ()));
    } else {
      throw new IllegalArgumentException("%s not found".formatted(new Object[] { paramVarArgs[0] }));
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\WarpCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */