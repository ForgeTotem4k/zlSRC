package pro.gravit.utils.command;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.VerifyHelper;

public class BaseCommandCategory implements CommandCategory {
  private final Map<String, Command> commands = new ConcurrentHashMap<>(32);
  
  public void registerCommand(String paramString, Command paramCommand) {
    VerifyHelper.verifyIDName(paramString);
    VerifyHelper.putIfAbsent(this.commands, paramString.toLowerCase(), Objects.<Command>requireNonNull(paramCommand, "command"), String.format("Command has been already registered: '%s'", new Object[] { paramString.toLowerCase() }));
  }
  
  public Command unregisterCommand(String paramString) {
    return this.commands.remove(paramString);
  }
  
  public Command findCommand(String paramString) {
    return this.commands.get(paramString);
  }
  
  public Map<String, Command> commandsMap() {
    return this.commands;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\BaseCommandCategory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */