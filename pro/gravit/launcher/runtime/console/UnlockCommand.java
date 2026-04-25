package pro.gravit.launcher.runtime.console;

import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.launcher.runtime.managers.SettingsManager;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class UnlockCommand extends Command {
  public String getArgsDescription() {
    return "[key]";
  }
  
  public String getUsageDescription() {
    return "Unlock console commands";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 1);
    if (ConsoleManager.checkUnlockKey(paramVarArgs[0])) {
      LogHelper.info("Unlock successful");
      if (!ConsoleManager.unlock()) {
        LogHelper.error("Console unlock canceled");
        return;
      } 
      LogHelper.info("Write unlock key");
      SettingsManager.settings.consoleUnlockKey = paramVarArgs[0];
    } else {
      LogHelper.error("Unlock key incorrect");
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\UnlockCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */