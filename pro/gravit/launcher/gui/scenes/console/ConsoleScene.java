package pro.gravit.launcher.gui.scenes.console;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.utils.helper.LogHelper;

public class ConsoleScene extends AbstractScene {
  private static final long MAX_LENGTH = 16384L;
  
  private static final int REMOVE_LENGTH = 1024;
  
  private TextField commandLine;
  
  private TextArea output;
  
  public ConsoleScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/console/console.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.output = (TextArea)LookupHelper.lookup((Node)this.layout, new String[] { "#output" });
    this.commandLine = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#commandInput" });
    LogHelper.addOutput(this::append, LogHelper.OutputTypes.PLAIN);
    this.commandLine.setOnAction(this::send);
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#send" })).setOnAction(this::send);
  }
  
  public void reset() {
    this.output.clear();
    this.commandLine.clear();
    this.commandLine.getStyleClass().removeAll((Object[])new String[] { "InputError" });
  }
  
  public String getName() {
    return "console";
  }
  
  private void send(ActionEvent paramActionEvent) {
    String str = this.commandLine.getText();
    this.commandLine.clear();
    try {
      ConsoleManager.handler.evalNative(str, false);
      this.commandLine.getStyleClass().removeAll((Object[])new String[] { "InputError" });
    } catch (Exception exception) {
      LogHelper.error(exception);
      this.commandLine.getStyleClass().add("InputError");
    } 
  }
  
  private void append(String paramString) {
    this.contextHelper.runInFxThread(() -> {
          if (this.output.lengthProperty().get() > 16384L)
            this.output.deleteText(0, 1024); 
          this.output.appendText(paramString.concat("\n"));
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\console\ConsoleScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */