package pro.gravit.launcher.base.modules.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import pro.gravit.launcher.base.config.SimpleConfigurable;
import pro.gravit.launcher.base.modules.ModulesConfigManager;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class SimpleModulesConfigManager implements ModulesConfigManager {
  public final Path configDir;
  
  public SimpleModulesConfigManager(Path paramPath) {
    this.configDir = paramPath;
  }
  
  public Path getModuleConfig(String paramString) {
    return getModuleConfig(paramString, "Config");
  }
  
  public Path getModuleConfig(String paramString1, String paramString2) {
    return getModuleConfigDir(paramString1).resolve(paramString2.concat(".json"));
  }
  
  public Path getModuleConfigDir(String paramString) {
    if (!IOHelper.isDir(this.configDir))
      try {
        Files.createDirectories(this.configDir, (FileAttribute<?>[])new FileAttribute[0]);
      } catch (IOException iOException) {
        LogHelper.error(iOException);
      }  
    return this.configDir.resolve(paramString);
  }
  
  public <T> SimpleConfigurable<T> getConfigurable(Class<T> paramClass, Path paramPath) {
    return new SimpleConfigurable(paramClass, paramPath);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\impl\SimpleModulesConfigManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */