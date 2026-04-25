package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.utils.command.Command;

public class ShowFxmlCommand extends Command {
  public String getArgsDescription() {
    return "[fxmlPath]";
  }
  
  public String getUsageDescription() {
    return "show any fxml without initialize";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 1);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\ShowFxmlCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */