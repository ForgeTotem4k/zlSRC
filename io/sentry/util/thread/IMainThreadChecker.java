package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface IMainThreadChecker {
  boolean isMainThread(long paramLong);
  
  boolean isMainThread(@NotNull Thread paramThread);
  
  boolean isMainThread();
  
  boolean isMainThread(@NotNull SentryThread paramSentryThread);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\thread\IMainThreadChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */