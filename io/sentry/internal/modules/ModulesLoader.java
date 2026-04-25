package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public abstract class ModulesLoader implements IModulesLoader {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  public static final String EXTERNAL_MODULES_FILENAME = "sentry-external-modules.txt";
  
  @NotNull
  protected final ILogger logger;
  
  @Nullable
  private Map<String, String> cachedModules = null;
  
  public ModulesLoader(@NotNull ILogger paramILogger) {
    this.logger = paramILogger;
  }
  
  @Nullable
  public Map<String, String> getOrLoadModules() {
    if (this.cachedModules != null)
      return this.cachedModules; 
    this.cachedModules = loadModules();
    return this.cachedModules;
  }
  
  protected abstract Map<String, String> loadModules();
  
  protected Map<String, String> parseStream(@NotNull InputStream paramInputStream) {
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, UTF_8));
      try {
        for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
          int i = str.lastIndexOf(':');
          String str1 = str.substring(0, i);
          String str2 = str.substring(i + 1);
          treeMap.put(str1, str2);
        } 
        this.logger.log(SentryLevel.DEBUG, "Extracted %d modules from resources.", new Object[] { Integer.valueOf(treeMap.size()) });
        bufferedReader.close();
      } catch (Throwable throwable) {
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, "Error extracting modules.", iOException);
    } catch (RuntimeException runtimeException) {
      this.logger.log(SentryLevel.ERROR, runtimeException, "%s file is malformed.", new Object[] { "sentry-external-modules.txt" });
    } 
    return (Map)treeMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\modules\ModulesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */