package io.sentry.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InvalidSentryTraceHeaderException extends Exception {
  private static final long serialVersionUID = -8353316997083420940L;
  
  @NotNull
  private final String sentryTraceHeader;
  
  public InvalidSentryTraceHeaderException(@NotNull String paramString) {
    this(paramString, null);
  }
  
  public InvalidSentryTraceHeaderException(@NotNull String paramString, @Nullable Throwable paramThrowable) {
    super("sentry-trace header does not conform to expected format: " + paramString, paramThrowable);
    this.sentryTraceHeader = paramString;
  }
  
  @NotNull
  public String getSentryTraceHeader() {
    return this.sentryTraceHeader;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\exception\InvalidSentryTraceHeaderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */