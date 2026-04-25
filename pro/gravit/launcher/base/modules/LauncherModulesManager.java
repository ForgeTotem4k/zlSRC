package pro.gravit.launcher.base.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import pro.gravit.utils.Version;

public interface LauncherModulesManager {
  LauncherModule loadModule(LauncherModule paramLauncherModule);
  
  LauncherModule loadModule(Path paramPath) throws IOException;
  
  LauncherModule getModule(String paramString);
  
  LauncherModule getCoreModule();
  
  default boolean containsModule(String paramString) {
    return (getModule(paramString) != null);
  }
  
  default <T extends LauncherModule> boolean containsModule(Class<? extends T> paramClass) {
    return (getModule(paramClass) != null);
  }
  
  ClassLoader getModuleClassLoader();
  
  ModulesConfigManager getConfigManager();
  
  <T extends LauncherModule> T getModule(Class<? extends T> paramClass);
  
  <T> T getModuleByInterface(Class<T> paramClass);
  
  <T> List<T> getModulesByInterface(Class<T> paramClass);
  
  <T extends LauncherModule> T findModule(Class<? extends T> paramClass, Predicate<Version> paramPredicate);
  
  LauncherModule findModule(String paramString, Predicate<Version> paramPredicate);
  
  <T extends LauncherModule.Event> void invokeEvent(T paramT);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModulesManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */