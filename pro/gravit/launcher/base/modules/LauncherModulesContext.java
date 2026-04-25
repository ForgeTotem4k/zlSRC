package pro.gravit.launcher.base.modules;

import java.net.URL;

public interface LauncherModulesContext {
  LauncherModulesManager getModulesManager();
  
  ModulesConfigManager getModulesConfigManager();
  
  void addURL(URL paramURL);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModulesContext.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */