package pro.gravit.utils.command.basic;

import pro.gravit.utils.command.Command;
import pro.gravit.utils.command.CommandHandler;
import pro.gravit.utils.helper.LogHelper;

public final class ClearCommand extends Command {
  private final CommandHandler handler;
  
  public ClearCommand(CommandHandler paramCommandHandler) {
    this.handler = paramCommandHandler;
  }
  
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return "Clear terminal";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    this.handler.clear();
    LogHelper.subInfo("Terminal cleared");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\basic\ClearCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */