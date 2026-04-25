package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class ExceptionUtils {
  @NotNull
  public static Throwable findRootCause(@NotNull Throwable paramThrowable) {
    Objects.requireNonNull(paramThrowable, "throwable cannot be null");
    Throwable throwable;
    for (throwable = paramThrowable; throwable.getCause() != null && throwable.getCause() != throwable; throwable = throwable.getCause());
    return throwable;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\ExceptionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */