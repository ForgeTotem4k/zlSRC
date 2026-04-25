package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.utils.command.Command;

public class ThemeCommand extends Command {
  private final JavaFXApplication application;
  
  public ThemeCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return "[theme]";
  }
  
  public String getUsageDescription() {
    return "Change theme and reload";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 1);
    this.application.runtimeSettings.theme = RuntimeSettings.LAUNCHER_THEME.valueOf(paramVarArgs[0]);
    this.application.gui.reload();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\ThemeCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */