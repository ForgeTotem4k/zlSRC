package pro.gravit.utils.command;

import java.util.Map;

public interface CommandCategory {
  void registerCommand(String paramString, Command paramCommand);
  
  Command unregisterCommand(String paramString);
  
  Command findCommand(String paramString);
  
  Map<String, Command> commandsMap();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\CommandCategory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */