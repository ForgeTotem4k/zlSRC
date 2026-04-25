package pro.gravit.utils.helper;

public enum Level {
  DEV("DEV"),
  DEBUG("DEBUG"),
  INFO("INFO"),
  WARNING("WARN"),
  ERROR("ERROR");
  
  public final String name;
  
  Level(String paramString1) {
    this.name = paramString1;
  }
  
  public String toString() {
    return this.name;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\LogHelper$Level.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */