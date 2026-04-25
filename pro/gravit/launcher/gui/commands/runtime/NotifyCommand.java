package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.launcher.gui.impl.MessageManager;
import pro.gravit.utils.command.Command;

public class NotifyCommand extends Command {
  private final MessageManager messageManager;
  
  public NotifyCommand(MessageManager paramMessageManager) {
    this.messageManager = paramMessageManager;
  }
  
  public String getArgsDescription() {
    return "[header] [message] (launcher/native/default)";
  }
  
  public String getUsageDescription() {
    return "show notify message";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 2);
    boolean bool1 = (paramVarArgs.length <= 2 || paramVarArgs[2].equals("default")) ? true : false;
    boolean bool2 = (paramVarArgs.length <= 2 || paramVarArgs[2].equals("launcher")) ? true : false;
    String str1 = paramVarArgs[0];
    String str2 = paramVarArgs[1];
    if (bool1) {
      this.messageManager.createNotification(str1, str2);
    } else {
      this.messageManager.createNotification(str1, str2, bool2);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\NotifyCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */