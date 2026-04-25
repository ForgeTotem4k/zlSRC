package pro.gravit.launcher.base.modules.impl;

import java.net.URL;
import java.net.URLClassLoader;

public class ModulesClassLoader extends URLClassLoader {
  public ModulesClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader) {
    super("MODULES", paramArrayOfURL, paramClassLoader);
  }
  
  public void addURL(URL paramURL) {
    super.addURL(paramURL);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\impl\SimpleModuleManager$ModulesClassLoader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */