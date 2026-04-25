package io.sentry.internal.debugmeta;

import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoOpDebugMetaLoader implements IDebugMetaLoader {
  private static final NoOpDebugMetaLoader instance = new NoOpDebugMetaLoader();
  
  public static NoOpDebugMetaLoader getInstance() {
    return instance;
  }
  
  @Nullable
  public List<Properties> loadDebugMeta() {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\debugmeta\NoOpDebugMetaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */