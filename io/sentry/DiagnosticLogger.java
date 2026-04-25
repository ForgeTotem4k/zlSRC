package io.sentry;

import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class DiagnosticLogger implements ILogger {
  @NotNull
  private final SentryOptions options;
  
  @Nullable
  private final ILogger logger;
  
  public DiagnosticLogger(@NotNull SentryOptions paramSentryOptions, @Nullable ILogger paramILogger) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required.");
    this.logger = paramILogger;
  }
  
  public boolean isEnabled(@Nullable SentryLevel paramSentryLevel) {
    SentryLevel sentryLevel = this.options.getDiagnosticLevel();
    return (paramSentryLevel == null) ? false : ((this.options.isDebug() && paramSentryLevel.ordinal() >= sentryLevel.ordinal()));
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Object... paramVarArgs) {
    if (this.logger != null && isEnabled(paramSentryLevel))
      this.logger.log(paramSentryLevel, paramString, paramVarArgs); 
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Throwable paramThrowable) {
    if (this.logger != null && isEnabled(paramSentryLevel))
      this.logger.log(paramSentryLevel, paramString, paramThrowable); 
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @Nullable Throwable paramThrowable, @NotNull String paramString, @Nullable Object... paramVarArgs) {
    if (this.logger != null && isEnabled(paramSentryLevel))
      this.logger.log(paramSentryLevel, paramThrowable, paramString, paramVarArgs); 
  }
  
  @TestOnly
  @Nullable
  public ILogger getLogger() {
    return this.logger;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DiagnosticLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */