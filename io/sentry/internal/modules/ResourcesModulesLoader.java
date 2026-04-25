package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ResourcesModulesLoader extends ModulesLoader {
  @NotNull
  private final ClassLoader classLoader;
  
  public ResourcesModulesLoader(@NotNull ILogger paramILogger) {
    this(paramILogger, ResourcesModulesLoader.class.getClassLoader());
  }
  
  ResourcesModulesLoader(@NotNull ILogger paramILogger, @Nullable ClassLoader paramClassLoader) {
    super(paramILogger);
    this.classLoader = ClassLoaderUtils.classLoaderOrDefault(paramClassLoader);
  }
  
  protected Map<String, String> loadModules() {
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    try {
      InputStream inputStream = this.classLoader.getResourceAsStream("sentry-external-modules.txt");
      try {
        if (inputStream == null) {
          this.logger.log(SentryLevel.INFO, "%s file was not found.", new Object[] { "sentry-external-modules.txt" });
          TreeMap<Object, Object> treeMap1 = treeMap;
          if (inputStream != null)
            inputStream.close(); 
          return (Map)treeMap1;
        } 
        Map<String, String> map = parseStream(inputStream);
        if (inputStream != null)
          inputStream.close(); 
        return map;
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (SecurityException securityException) {
      this.logger.log(SentryLevel.INFO, "Access to resources denied.", securityException);
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.INFO, "Access to resources failed.", iOException);
    } 
    return (Map)treeMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\modules\ResourcesModulesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */