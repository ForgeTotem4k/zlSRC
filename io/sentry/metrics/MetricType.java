package io.sentry.metrics;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public enum MetricType {
  Counter("c"),
  Gauge("g"),
  Distribution("d"),
  Set("s");
  
  @NotNull
  final String statsdCode;
  
  MetricType(String paramString1) {
    this.statsdCode = paramString1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\MetricType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */