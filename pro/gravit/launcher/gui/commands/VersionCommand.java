package pro.gravit.launcher.gui.commands;

import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class VersionCommand extends Command {
  public String getArgsDescription() {
    return "print version information";
  }
  
  public String getUsageDescription() {
    return "[]";
  }
  
  public void invoke(String... paramVarArgs) {
    LogHelper.info(JavaRuntimeModule.getLauncherInfo());
    LogHelper.info("JDK Path: %s", new Object[] { System.getProperty("java.home", "UNKNOWN") });
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\VersionCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */