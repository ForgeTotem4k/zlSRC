package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpLogger implements ILogger {
  private static final NoOpLogger instance = new NoOpLogger();
  
  public static NoOpLogger getInstance() {
    return instance;
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Object... paramVarArgs) {}
  
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Throwable paramThrowable) {}
  
  public void log(@NotNull SentryLevel paramSentryLevel, @Nullable Throwable paramThrowable, @NotNull String paramString, @Nullable Object... paramVarArgs) {}
  
  public boolean isEnabled(@Nullable SentryLevel paramSentryLevel) {
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */