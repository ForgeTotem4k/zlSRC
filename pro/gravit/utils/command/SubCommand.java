package pro.gravit.utils.command;

public abstract class SubCommand extends Command {
  private String defaultArgs;
  
  private String defaultUsage;
  
  public SubCommand() {}
  
  public SubCommand(String paramString1, String paramString2) {
    this.defaultArgs = paramString1;
    this.defaultUsage = paramString2;
  }
  
  public String getArgsDescription() {
    return this.defaultArgs;
  }
  
  public String getUsageDescription() {
    return this.defaultUsage;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\SubCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */