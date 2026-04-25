package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SentryDateProvider {
  SentryDate now();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */