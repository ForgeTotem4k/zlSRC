package pro.gravit.utils.command.basic;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.fusesource.jansi.Ansi;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.command.CommandException;
import pro.gravit.utils.command.CommandHandler;
import pro.gravit.utils.helper.LogHelper;

public final class HelpCommand extends Command {
  private final CommandHandler handler;
  
  public HelpCommand(CommandHandler paramCommandHandler) {
    this.handler = paramCommandHandler;
  }
  
  public static void printCommand(String paramString, Command paramCommand) {
    String str = paramCommand.getArgsDescription();
    Supplier supplier1 = () -> String.format("%s %s - %s", new Object[] { paramString1, (paramString2 == null) ? "[nothing]" : paramString2, paramCommand.getUsageDescription() });
    Supplier supplier2 = () -> {
        Ansi ansi = new Ansi();
        ansi.fgBright(Ansi.Color.GREEN);
        ansi.a(paramString1 + " ");
        ansi.fgBright(Ansi.Color.CYAN);
        ansi.a((paramString2 == null) ? "[nothing]" : paramString2);
        ansi.reset();
        ansi.a(" - ");
        ansi.fgBright(Ansi.Color.YELLOW);
        ansi.a(paramCommand.getUsageDescription());
        ansi.reset();
        return ansi.toString();
      };
    LogHelper.logJAnsi(LogHelper.Level.INFO, supplier1, supplier2, true);
  }
  
  public static void printSubCommandsHelp(String paramString, Command paramCommand) {
    paramCommand.childCommands.forEach((paramString2, paramCommand) -> printCommand(paramString1.concat(" ").concat(paramString2), paramCommand));
  }
  
  public static void printSubCommandsHelp(String paramString, String[] paramArrayOfString, Command paramCommand) throws CommandException {
    if (paramArrayOfString.length == 0) {
      printSubCommandsHelp(paramString, paramCommand);
    } else {
      Command command = (Command)paramCommand.childCommands.get(paramArrayOfString[0]);
      if (command == null)
        throw new CommandException(String.format("Unknown sub command: '%s'", new Object[] { paramArrayOfString[0] })); 
      printSubCommandsHelp(paramString.concat(" ").concat(paramArrayOfString[0]), Arrays.<String>copyOfRange(paramArrayOfString, 1, paramArrayOfString.length), command);
    } 
  }
  
  private static void printCategory(String paramString1, String paramString2) {
    if (paramString2 != null) {
      LogHelper.info("Category: %s - %s", new Object[] { paramString1, paramString2 });
    } else {
      LogHelper.info("Category: %s", new Object[] { paramString1 });
    } 
  }
  
  public String getArgsDescription() {
    return "[command name]";
  }
  
  public String getUsageDescription() {
    return "Print command usage";
  }
  
  public void invoke(String... paramVarArgs) throws CommandException {
    if (paramVarArgs.length < 1) {
      printCommands();
      return;
    } 
    if (paramVarArgs.length == 1)
      printCommand(paramVarArgs[0]); 
    printSubCommandsHelp(paramVarArgs[0], Arrays.<String>copyOfRange(paramVarArgs, 1, paramVarArgs.length), this.handler.lookup(paramVarArgs[0]));
  }
  
  private void printCommand(String paramString) throws CommandException {
    printCommand(paramString, this.handler.lookup(paramString));
  }
  
  private void printCommands() {
    for (CommandHandler.Category category : this.handler.getCategories()) {
      printCategory(category.name, category.description);
      for (Map.Entry entry : category.category.commandsMap().entrySet())
        printCommand((String)entry.getKey(), (Command)entry.getValue()); 
    } 
    printCategory("Base", null);
    for (Map.Entry entry : this.handler.getBaseCategory().commandsMap().entrySet())
      printCommand((String)entry.getKey(), (Command)entry.getValue()); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\basic\HelpCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */