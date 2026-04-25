package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.utils.command.Command;

public class ReloadCommand extends Command {
  private final JavaFXApplication application;
  
  public ReloadCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return "[]";
  }
  
  public String getUsageDescription() {
    return "reload ui";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    this.application.gui.reload();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\ReloadCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */