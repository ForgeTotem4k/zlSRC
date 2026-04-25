package pro.gravit.launcher.base.modules;

import java.nio.file.Path;
import pro.gravit.launcher.base.config.SimpleConfigurable;

public interface ModulesConfigManager {
  Path getModuleConfig(String paramString);
  
  Path getModuleConfig(String paramString1, String paramString2);
  
  Path getModuleConfigDir(String paramString);
  
  <T> SimpleConfigurable<T> getConfigurable(Class<T> paramClass, Path paramPath);
  
  default <T> SimpleConfigurable<T> getConfigurable(Class<T> paramClass, String paramString) {
    return getConfigurable(paramClass, getModuleConfig(paramString));
  }
  
  default <T> SimpleConfigurable<T> getConfigurable(Class<T> paramClass, String paramString1, String paramString2) {
    return getConfigurable(paramClass, getModuleConfig(paramString1, paramString2));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\ModulesConfigManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */