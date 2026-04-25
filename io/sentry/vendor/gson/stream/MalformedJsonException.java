package io.sentry.vendor.gson.stream;

import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MalformedJsonException extends IOException {
  private static final long serialVersionUID = 1L;
  
  public MalformedJsonException(String paramString) {
    super(paramString);
  }
  
  public MalformedJsonException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public MalformedJsonException(Throwable paramThrowable) {
    initCause(paramThrowable);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\vendor\gson\stream\MalformedJsonException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */