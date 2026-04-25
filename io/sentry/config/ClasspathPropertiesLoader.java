package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ClasspathPropertiesLoader implements PropertiesLoader {
  @NotNull
  private final String fileName;
  
  @NotNull
  private final ClassLoader classLoader;
  
  @NotNull
  private final ILogger logger;
  
  public ClasspathPropertiesLoader(@NotNull String paramString, @Nullable ClassLoader paramClassLoader, @NotNull ILogger paramILogger) {
    this.fileName = paramString;
    this.classLoader = ClassLoaderUtils.classLoaderOrDefault(paramClassLoader);
    this.logger = paramILogger;
  }
  
  public ClasspathPropertiesLoader(@NotNull ILogger paramILogger) {
    this("sentry.properties", ClasspathPropertiesLoader.class.getClassLoader(), paramILogger);
  }
  
  @Nullable
  public Properties load() {
    try {
      InputStream inputStream = this.classLoader.getResourceAsStream(this.fileName);
      try {
        if (inputStream != null) {
          BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
          try {
            Properties properties1 = new Properties();
            properties1.load(bufferedInputStream);
            Properties properties2 = properties1;
            bufferedInputStream.close();
            if (inputStream != null)
              inputStream.close(); 
            return properties2;
          } catch (Throwable throwable) {
            try {
              bufferedInputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } 
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
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, iOException, "Failed to load Sentry configuration from classpath resource: %s", new Object[] { this.fileName });
      return null;
    } 
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\ClasspathPropertiesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */