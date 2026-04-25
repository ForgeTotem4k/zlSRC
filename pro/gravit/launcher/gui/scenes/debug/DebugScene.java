package pro.gravit.launcher.gui.scenes.debug;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.service.LaunchService;
import pro.gravit.utils.helper.LogHelper;

public class DebugScene extends AbstractScene {
  private ProcessLogOutput processLogOutput;
  
  private LaunchService.ClientInstance clientInstance;
  
  public DebugScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/debug/debug.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.processLogOutput = new ProcessLogOutput((TextArea)LookupHelper.lookup((Node)this.layout, new String[] { "#output" }));
    LookupHelper.lookupIfPossible((Node)this.header, new String[] { "#controls", "#kill" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#version" }).ifPresent(paramLabel -> paramLabel.setText(JavaRuntimeModule.getMiniLauncherInfo()));
    LookupHelper.lookupIfPossible((Node)this.header, new String[] { "#controls", "#copy" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    ((ButtonBase)LookupHelper.lookup((Node)this.header, new String[] { "#back" })).setOnAction(paramActionEvent -> {
          if (this.clientInstance != null)
            this.clientInstance.unregisterListener(this.processLogOutput); 
          try {
            switchToBackScene();
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
  }
  
  public void reset() {
    this.processLogOutput.clear();
  }
  
  public void onClientInstance(LaunchService.ClientInstance paramClientInstance) {
    this.clientInstance = paramClientInstance;
    this.clientInstance.registerListener(this.processLogOutput);
    this.clientInstance.getOnWriteParamsFuture().thenAccept(paramVoid -> this.processLogOutput.append("[START] Write param successful\n")).exceptionally(paramThrowable -> {
          errorHandle(paramThrowable);
          return null;
        });
    this.clientInstance.start().thenAccept(paramInteger -> this.processLogOutput.append(String.format("[START] Process exit with code %d", new Object[] { paramInteger }))).exceptionally(paramThrowable -> {
          errorHandle(paramThrowable);
          return null;
        });
  }
  
  public void append(String paramString) {
    this.processLogOutput.append(paramString);
  }
  
  public void errorHandle(Throwable paramThrowable) {
    if (!(paramThrowable instanceof java.io.EOFException) && LogHelper.isDebugEnabled())
      this.processLogOutput.append(paramThrowable.toString()); 
  }
  
  public String getName() {
    return "debug";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\debug\DebugScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */