package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FilesystemPropertiesLoader implements PropertiesLoader {
  @NotNull
  private final String filePath;
  
  @NotNull
  private final ILogger logger;
  
  public FilesystemPropertiesLoader(@NotNull String paramString, @NotNull ILogger paramILogger) {
    this.filePath = paramString;
    this.logger = paramILogger;
  }
  
  @Nullable
  public Properties load() {
    try {
      File file = new File(this.filePath);
      if (file.isFile() && file.canRead()) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
          Properties properties1 = new Properties();
          properties1.load(bufferedInputStream);
          Properties properties2 = properties1;
          bufferedInputStream.close();
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
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, iOException, "Failed to load Sentry configuration from file: %s", new Object[] { this.filePath });
      return null;
    } 
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\FilesystemPropertiesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */