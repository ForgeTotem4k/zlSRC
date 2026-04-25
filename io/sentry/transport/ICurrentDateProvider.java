package io.sentry.transport;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ICurrentDateProvider {
  long getCurrentTimeMillis();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\ICurrentDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */