package io.sentry.internal.debugmeta;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import io.sentry.util.DebugMetaPropertiesApplier;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ResourcesDebugMetaLoader implements IDebugMetaLoader {
  @NotNull
  private final ILogger logger;
  
  @NotNull
  private final ClassLoader classLoader;
  
  public ResourcesDebugMetaLoader(@NotNull ILogger paramILogger) {
    this(paramILogger, ResourcesDebugMetaLoader.class.getClassLoader());
  }
  
  ResourcesDebugMetaLoader(@NotNull ILogger paramILogger, @Nullable ClassLoader paramClassLoader) {
    this.logger = paramILogger;
    this.classLoader = ClassLoaderUtils.classLoaderOrDefault(paramClassLoader);
  }
  
  @Nullable
  public List<Properties> loadDebugMeta() {
    ArrayList<Properties> arrayList = new ArrayList();
    try {
      Enumeration<URL> enumeration = this.classLoader.getResources(DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
      while (enumeration.hasMoreElements()) {
        URL uRL = enumeration.nextElement();
        try {
          InputStream inputStream = uRL.openStream();
          try {
            Properties properties = new Properties();
            properties.load(inputStream);
            arrayList.add(properties);
            this.logger.log(SentryLevel.INFO, "Debug Meta Data Properties loaded from %s", new Object[] { uRL });
            if (inputStream != null)
              inputStream.close(); 
          } catch (Throwable throwable) {
            if (inputStream != null)
              try {
                inputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              }  
            throw throwable;
          } 
        } catch (RuntimeException runtimeException) {
          this.logger.log(SentryLevel.ERROR, runtimeException, "%s file is malformed.", new Object[] { uRL });
        } 
      } 
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, iOException, "Failed to load %s", new Object[] { DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME });
    } 
    if (arrayList.isEmpty()) {
      this.logger.log(SentryLevel.INFO, "No %s file was found.", new Object[] { DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME });
      return null;
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\debugmeta\ResourcesDebugMetaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */