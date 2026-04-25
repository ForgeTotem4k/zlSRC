package pro.gravit.utils.command;

public class Category {
  public final CommandCategory category;
  
  public final String name;
  
  public String description;
  
  public Category(CommandCategory paramCommandCategory, String paramString) {
    this.category = paramCommandCategory;
    this.name = paramString;
  }
  
  public Category(CommandCategory paramCommandCategory, String paramString1, String paramString2) {
    this.category = paramCommandCategory;
    this.name = paramString1;
    this.description = paramString2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\CommandHandler$Category.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */