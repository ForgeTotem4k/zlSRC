package pro.gravit.utils.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.LogHelper;

public abstract class CommandHandler implements Runnable {
  protected final List<Category> categories = new ArrayList<>();
  
  protected final CommandCategory baseCategory = new BaseCommandCategory();
  
  public CommandHandler() {}
  
  protected CommandHandler(List<Category> paramList, CommandCategory paramCommandCategory) {}
  
  public void eval(String paramString, boolean paramBoolean) {
    LogHelper.info("Command '%s'", new Object[] { paramString });
    try {
      evalNative(paramString, paramBoolean);
    } catch (Exception exception) {
      LogHelper.error(exception);
    } 
  }
  
  public void evalNative(String paramString, boolean paramBoolean) throws Exception {
    String[] arrayOfString = CommonHelper.parseCommand(paramString);
    if (arrayOfString.length > 0)
      arrayOfString[0] = arrayOfString[0].toLowerCase(); 
    eval(arrayOfString, paramBoolean);
  }
  
  public void eval(String[] paramArrayOfString, boolean paramBoolean) throws Exception {
    if (paramArrayOfString.length == 0)
      return; 
    long l1 = System.currentTimeMillis();
    lookup(paramArrayOfString[0]).invoke(Arrays.<String>copyOfRange(paramArrayOfString, 1, paramArrayOfString.length));
    long l2 = System.currentTimeMillis();
    if (paramBoolean && l2 - l1 >= 5000L)
      bell(); 
  }
  
  public Command lookup(String paramString) throws CommandException {
    Command command = findCommand(paramString);
    if (command == null)
      throw new CommandException(String.format("Unknown command: '%s'", new Object[] { paramString })); 
    return command;
  }
  
  public Command findCommand(String paramString) {
    Command command = this.baseCategory.findCommand(paramString);
    if (command == null)
      for (Category category : this.categories) {
        command = category.category.findCommand(paramString);
        if (command != null)
          return command; 
      }  
    return command;
  }
  
  public abstract String readLine() throws IOException;
  
  private void readLoop() throws IOException {
    for (String str = readLine(); str != null; str = readLine())
      eval(str, true); 
  }
  
  public void registerCommand(String paramString, Command paramCommand) {
    this.baseCategory.registerCommand(paramString, paramCommand);
  }
  
  public void registerCategory(Category paramCategory) {
    this.categories.add(paramCategory);
  }
  
  public void unregisterCategory(Category paramCategory) {
    this.categories.remove(paramCategory);
  }
  
  public Category findCategory(String paramString) {
    for (Category category : this.categories) {
      if (category.name.equals(paramString))
        return category; 
    } 
    return null;
  }
  
  public Command unregisterCommand(String paramString) {
    return this.baseCategory.unregisterCommand(paramString);
  }
  
  public void run() {
    try {
      readLoop();
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
  }
  
  public void walk(CommandWalk paramCommandWalk) {
    for (Category category : getCategories()) {
      for (Map.Entry<String, Command> entry : category.category.commandsMap().entrySet())
        paramCommandWalk.walk(category, (String)entry.getKey(), (Command)entry.getValue()); 
    } 
    for (Map.Entry<String, Command> entry : getBaseCategory().commandsMap().entrySet())
      paramCommandWalk.walk(null, (String)entry.getKey(), (Command)entry.getValue()); 
  }
  
  public CommandCategory getBaseCategory() {
    return this.baseCategory;
  }
  
  public List<Category> getCategories() {
    return this.categories;
  }
  
  public abstract void bell();
  
  public abstract void clear() throws IOException;
  
  public static class Category {
    public final CommandCategory category;
    
    public final String name;
    
    public String description;
    
    public Category(CommandCategory param1CommandCategory, String param1String) {
      this.category = param1CommandCategory;
      this.name = param1String;
    }
    
    public Category(CommandCategory param1CommandCategory, String param1String1, String param1String2) {
      this.category = param1CommandCategory;
      this.name = param1String1;
      this.description = param1String2;
    }
  }
  
  @FunctionalInterface
  public static interface CommandWalk {
    void walk(CommandHandler.Category param1Category, String param1String, Command param1Command);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\CommandHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */