package io.sentry;

import io.sentry.util.Platform;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class SentryAutoDateProvider implements SentryDateProvider {
  @NotNull
  private final SentryDateProvider dateProvider;
  
  public SentryAutoDateProvider() {
    if (checkInstantAvailabilityAndPrecision()) {
      this.dateProvider = new SentryInstantDateProvider();
    } else {
      this.dateProvider = new SentryNanotimeDateProvider();
    } 
  }
  
  @NotNull
  public SentryDate now() {
    return this.dateProvider.now();
  }
  
  private static boolean checkInstantAvailabilityAndPrecision() {
    return (Platform.isJvm() && Platform.isJavaNinePlus());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryAutoDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */