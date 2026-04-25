package pro.gravit.utils.command;

public final class CommandException extends Exception {
  public CommandException(String paramString) {
    super(paramString, (Throwable)null, false, false);
  }
  
  public CommandException(Throwable paramThrowable) {
    super(paramThrowable);
  }
  
  public String toString() {
    return getMessage();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\CommandException.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */