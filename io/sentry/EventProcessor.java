package io.sentry;

import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventProcessor {
  @Nullable
  default SentryEvent process(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    return paramSentryEvent;
  }
  
  @Nullable
  default SentryTransaction process(@NotNull SentryTransaction paramSentryTransaction, @NotNull Hint paramHint) {
    return paramSentryTransaction;
  }
  
  @Nullable
  default Long getOrder() {
    return null;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\EventProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */