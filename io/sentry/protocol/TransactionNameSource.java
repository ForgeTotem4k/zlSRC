package io.sentry.protocol;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum TransactionNameSource {
  CUSTOM, URL, ROUTE, VIEW, COMPONENT, TASK;
  
  public String apiName() {
    return name().toLowerCase(Locale.ROOT);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\TransactionNameSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */