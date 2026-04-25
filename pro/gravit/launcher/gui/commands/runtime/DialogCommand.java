package pro.gravit.launcher.gui.commands.runtime;

import pro.gravit.launcher.gui.impl.MessageManager;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class DialogCommand extends Command {
  private final MessageManager messageManager;
  
  public DialogCommand(MessageManager paramMessageManager) {
    this.messageManager = paramMessageManager;
  }
  
  public String getArgsDescription() {
    return "[header] [message] (dialog/dialogApply/dialogTextInput) (launcher/native/default)";
  }
  
  public String getUsageDescription() {
    return "show test dialog";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 3);
    boolean bool = (paramVarArgs.length <= 3 || paramVarArgs[3].equals("launcher")) ? true : false;
    String str1 = paramVarArgs[0];
    String str2 = paramVarArgs[1];
    String str3 = paramVarArgs[2];
    switch (str3) {
      case "dialog":
        this.messageManager.showDialog(str1, str2, () -> LogHelper.info("Dialog apply callback"), () -> LogHelper.info("Dialog cancel callback"), bool);
        break;
      case "dialogApply":
        this.messageManager.showApplyDialog(str1, str2, () -> LogHelper.info("Dialog apply callback"), () -> LogHelper.info("Dialog deny callback"), () -> LogHelper.info("Dialog close callback"), bool);
        break;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\DialogCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */