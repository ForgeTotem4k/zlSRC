package io.sentry.internal.modules;

import io.sentry.ILogger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Experimental
@Internal
public final class CompositeModulesLoader extends ModulesLoader {
  private final List<IModulesLoader> loaders;
  
  public CompositeModulesLoader(@NotNull List<IModulesLoader> paramList, @NotNull ILogger paramILogger) {
    super(paramILogger);
    this.loaders = paramList;
  }
  
  protected Map<String, String> loadModules() {
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    for (IModulesLoader iModulesLoader : this.loaders) {
      Map<String, String> map = iModulesLoader.getOrLoadModules();
      if (map != null)
        treeMap.putAll(map); 
    } 
    return (Map)treeMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\modules\CompositeModulesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */