package io.sentry.exception;

import org.jetbrains.annotations.Nullable;

public final class SentryHttpClientException extends Exception {
  private static final long serialVersionUID = 348162238030337390L;
  
  public SentryHttpClientException(@Nullable String paramString) {
    super(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\exception\SentryHttpClientException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */