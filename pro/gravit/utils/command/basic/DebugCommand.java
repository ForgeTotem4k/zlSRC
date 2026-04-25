package pro.gravit.utils.command.basic;

import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class DebugCommand extends Command {
  public String getArgsDescription() {
    return "[true/false] [true/false]";
  }
  
  public String getUsageDescription() {
    return null;
  }
  
  public void invoke(String... paramVarArgs) {
    boolean bool1;
    boolean bool2;
    if (paramVarArgs.length >= 1) {
      bool1 = Boolean.parseBoolean(paramVarArgs[0]);
      if (paramVarArgs.length >= 2) {
        bool2 = Boolean.parseBoolean(paramVarArgs[1]);
      } else {
        bool2 = bool1;
      } 
      LogHelper.setDebugEnabled(bool1);
      LogHelper.setStacktraceEnabled(bool2);
    } else {
      bool1 = LogHelper.isDebugEnabled();
      bool2 = LogHelper.isStacktraceEnabled();
    } 
    LogHelper.subInfo("Debug enabled: " + bool1);
    LogHelper.subInfo("Stacktrace enabled: " + bool2);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\basic\DebugCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */