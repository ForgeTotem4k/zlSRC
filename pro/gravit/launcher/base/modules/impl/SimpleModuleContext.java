package pro.gravit.launcher.base.modules.impl;

import java.net.URL;
import pro.gravit.launcher.base.modules.LauncherModulesContext;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.base.modules.ModulesConfigManager;

public class SimpleModuleContext implements LauncherModulesContext {
  public final LauncherModulesManager modulesManager;
  
  public final ModulesConfigManager configManager;
  
  public SimpleModuleContext(LauncherModulesManager paramLauncherModulesManager, ModulesConfigManager paramModulesConfigManager) {
    this.modulesManager = paramLauncherModulesManager;
    this.configManager = paramModulesConfigManager;
  }
  
  public void addURL(URL paramURL) {
    LauncherModulesManager launcherModulesManager = this.modulesManager;
    if (launcherModulesManager instanceof SimpleModuleManager) {
      SimpleModuleManager simpleModuleManager = (SimpleModuleManager)launcherModulesManager;
      simpleModuleManager.addUrlToClassLoader(paramURL);
    } else {
      throw new UnsupportedOperationException();
    } 
  }
  
  public LauncherModulesManager getModulesManager() {
    return this.modulesManager;
  }
  
  public ModulesConfigManager getModulesConfigManager() {
    return this.configManager;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\impl\SimpleModuleContext.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */