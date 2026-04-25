package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.utils.command.Command;

public class WebViewCommand extends Command {
  private final JavaFXApplication application;
  
  public WebViewCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return null;
  }
  
  public void invoke(String... paramVarArgs) throws Exception {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\WebViewCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */