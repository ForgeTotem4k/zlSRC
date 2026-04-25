package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class LogUtils {
  public static void logNotInstanceOf(@NotNull Class<?> paramClass, @Nullable Object paramObject, @NotNull ILogger paramILogger) {
    paramILogger.log(SentryLevel.DEBUG, "%s is not %s", new Object[] { (paramObject != null) ? paramObject.getClass().getCanonicalName() : "Hint", paramClass.getCanonicalName() });
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\LogUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */