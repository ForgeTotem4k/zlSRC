package io.sentry.metrics;

import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface IMetricsClient {
  @NotNull
  SentryId captureMetrics(@NotNull EncodedMetrics paramEncodedMetrics);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\IMetricsClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */