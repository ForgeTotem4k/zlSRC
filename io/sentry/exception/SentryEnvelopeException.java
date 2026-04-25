package io.sentry.exception;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryEnvelopeException extends Exception {
  private static final long serialVersionUID = -8307801916948173232L;
  
  public SentryEnvelopeException(@Nullable String paramString) {
    super(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\exception\SentryEnvelopeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */