package pro.gravit.utils.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;

public class StdCommandHandler extends CommandHandler {
  private final BufferedReader reader;
  
  public StdCommandHandler(boolean paramBoolean) {
    this.reader = paramBoolean ? IOHelper.newReader(System.in) : null;
  }
  
  public StdCommandHandler(InputStream paramInputStream) {
    this.reader = IOHelper.newReader(paramInputStream);
  }
  
  public StdCommandHandler(BufferedReader paramBufferedReader) {
    this.reader = paramBufferedReader;
  }
  
  protected StdCommandHandler(List<CommandHandler.Category> paramList, CommandCategory paramCommandCategory, boolean paramBoolean) {
    super(paramList, paramCommandCategory);
    this.reader = paramBoolean ? IOHelper.newReader(System.in) : null;
  }
  
  protected StdCommandHandler(List<CommandHandler.Category> paramList, CommandCategory paramCommandCategory, InputStream paramInputStream) {
    super(paramList, paramCommandCategory);
    this.reader = IOHelper.newReader(paramInputStream);
  }
  
  protected StdCommandHandler(List<CommandHandler.Category> paramList, CommandCategory paramCommandCategory, BufferedReader paramBufferedReader) {
    super(paramList, paramCommandCategory);
    this.reader = paramBufferedReader;
  }
  
  public void bell() {}
  
  public void clear() throws IOException {
    System.out.flush();
    if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
      try {
        (new ProcessBuilder(new String[] { "cmd", "/c", "cls" })).inheritIO().start().waitFor();
      } catch (InterruptedException interruptedException) {
        throw new IOException(interruptedException);
      } 
    } else {
      System.out.print("\033[H\033[2J");
      System.out.flush();
    } 
  }
  
  public String readLine() throws IOException {
    return (this.reader == null) ? null : this.reader.readLine();
  }
  
  public StdCommandHandler ofHandler(CommandHandler paramCommandHandler, boolean paramBoolean) {
    return new StdCommandHandler(paramCommandHandler.categories, paramCommandHandler.baseCategory, paramBoolean);
  }
  
  public StdCommandHandler ofHandler(CommandHandler paramCommandHandler, InputStream paramInputStream) {
    return new StdCommandHandler(paramCommandHandler.categories, paramCommandHandler.baseCategory, paramInputStream);
  }
  
  public StdCommandHandler ofHandler(CommandHandler paramCommandHandler, BufferedReader paramBufferedReader) {
    return new StdCommandHandler(paramCommandHandler.categories, paramCommandHandler.baseCategory, paramBufferedReader);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\StdCommandHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */