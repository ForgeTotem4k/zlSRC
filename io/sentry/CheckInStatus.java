package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

@Experimental
public enum CheckInStatus {
  IN_PROGRESS, OK, ERROR;
  
  @NotNull
  public String apiName() {
    return name().toLowerCase(Locale.ROOT);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CheckInStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */