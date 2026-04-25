package pro.gravit.utils.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jline.reader.Candidate;
import pro.gravit.utils.helper.VerifyHelper;

public abstract class Command {
  public final Map<String, Command> childCommands = new HashMap<>();
  
  public Command() {}
  
  public Command(Map<String, Command> paramMap) {}
  
  protected static String parseUsername(String paramString) throws CommandException {
    try {
      return VerifyHelper.verifyUsername(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CommandException(illegalArgumentException.getMessage());
    } 
  }
  
  protected static UUID parseUUID(String paramString) throws CommandException {
    try {
      return UUID.fromString(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CommandException(String.format("Invalid UUID: '%s'", new Object[] { paramString }));
    } 
  }
  
  public abstract String getArgsDescription();
  
  public abstract String getUsageDescription();
  
  public Candidate buildCandidate(CommandHandler.Category paramCategory, String paramString) {
    return new Candidate(paramString);
  }
  
  public List<Candidate> complete(List<String> paramList, int paramInt, String paramString) {
    if (paramInt == 0) {
      ArrayList<Candidate> arrayList = new ArrayList();
      this.childCommands.forEach((paramString2, paramCommand) -> {
            if (paramString2.startsWith(paramString1))
              paramList.add(new Candidate(paramString2)); 
          });
      return arrayList;
    } 
    Command command = this.childCommands.get(paramList.get(0));
    return (command == null) ? new ArrayList<>() : command.complete(paramList.subList(1, paramList.size()), paramInt - 1, paramString);
  }
  
  public void invokeSubcommands(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 1);
    Command command = this.childCommands.get(paramVarArgs[0]);
    if (command == null)
      throw new CommandException(String.format("Unknown sub command: '%s'", new Object[] { paramVarArgs[0] })); 
    command.invoke(Arrays.<String>copyOfRange(paramVarArgs, 1, paramVarArgs.length));
  }
  
  public abstract void invoke(String... paramVarArgs) throws Exception;
  
  protected final void verifyArgs(String[] paramArrayOfString, int paramInt) throws CommandException {
    if (paramArrayOfString.length < paramInt)
      throw new CommandException("Command usage: " + getArgsDescription()); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\Command.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */