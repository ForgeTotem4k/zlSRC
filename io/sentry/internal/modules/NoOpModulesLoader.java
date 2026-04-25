package io.sentry.internal.modules;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class NoOpModulesLoader implements IModulesLoader {
  private static final NoOpModulesLoader instance = new NoOpModulesLoader();
  
  public static NoOpModulesLoader getInstance() {
    return instance;
  }
  
  @Nullable
  public Map<String, String> getOrLoadModules() {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\modules\NoOpModulesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */