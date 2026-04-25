package pro.gravit.launcher.gui.commands;

import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.commands.runtime.DialogCommand;
import pro.gravit.launcher.gui.commands.runtime.GetSizeCommand;
import pro.gravit.launcher.gui.commands.runtime.InfoCommand;
import pro.gravit.launcher.gui.commands.runtime.NotifyCommand;
import pro.gravit.launcher.gui.commands.runtime.ReloadCommand;
import pro.gravit.launcher.gui.commands.runtime.ThemeCommand;
import pro.gravit.launcher.gui.commands.runtime.WarpCommand;
import pro.gravit.utils.command.Command;

public class RuntimeCommand extends Command {
  private final JavaFXApplication application;
  
  public RuntimeCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
    this.childCommands.put("dialog", new DialogCommand(paramJavaFXApplication.messageManager));
    this.childCommands.put("warp", new WarpCommand(paramJavaFXApplication));
    this.childCommands.put("reload", new ReloadCommand(paramJavaFXApplication));
    this.childCommands.put("notify", new NotifyCommand(paramJavaFXApplication.messageManager));
    this.childCommands.put("theme", new ThemeCommand(paramJavaFXApplication));
    this.childCommands.put("info", new InfoCommand(paramJavaFXApplication));
    this.childCommands.put("getsize", new GetSizeCommand(paramJavaFXApplication));
  }
  
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return null;
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    invokeSubcommands(paramVarArgs);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\RuntimeCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */