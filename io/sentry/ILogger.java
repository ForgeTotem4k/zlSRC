package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILogger {
  void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Object... paramVarArgs);
  
  void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Throwable paramThrowable);
  
  void log(@NotNull SentryLevel paramSentryLevel, @Nullable Throwable paramThrowable, @NotNull String paramString, @Nullable Object... paramVarArgs);
  
  boolean isEnabled(@Nullable SentryLevel paramSentryLevel);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ILogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */