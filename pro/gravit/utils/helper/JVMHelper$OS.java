package pro.gravit.utils.helper;

public enum OS {
  MUSTDIE("mustdie"),
  LINUX("linux"),
  MACOSX("macosx");
  
  public final String name;
  
  OS(String paramString1) {
    this.name = paramString1;
  }
  
  public static OS byName(String paramString) {
    if (paramString.startsWith("Windows"))
      return MUSTDIE; 
    if (paramString.startsWith("Linux"))
      return LINUX; 
    if (paramString.startsWith("Mac OS X"))
      return MACOSX; 
    throw new RuntimeException(String.format("This shit is not yet supported: '%s'", new Object[] { paramString }));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JVMHelper$OS.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */