package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadClass {
  @Nullable
  public Class<?> loadClass(@NotNull String paramString, @Nullable ILogger paramILogger) {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramILogger != null)
        paramILogger.log(SentryLevel.DEBUG, "Class not available:" + paramString, classNotFoundException); 
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      if (paramILogger != null)
        paramILogger.log(SentryLevel.ERROR, "Failed to load (UnsatisfiedLinkError) " + paramString, unsatisfiedLinkError); 
    } catch (Throwable throwable) {
      if (paramILogger != null)
        paramILogger.log(SentryLevel.ERROR, "Failed to initialize " + paramString, throwable); 
    } 
    return null;
  }
  
  public boolean isClassAvailable(@NotNull String paramString, @Nullable ILogger paramILogger) {
    return (loadClass(paramString, paramILogger) != null);
  }
  
  public boolean isClassAvailable(@NotNull String paramString, @Nullable SentryOptions paramSentryOptions) {
    return isClassAvailable(paramString, (paramSentryOptions != null) ? paramSentryOptions.getLogger() : null);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\LoadClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */