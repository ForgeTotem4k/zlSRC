package io.sentry.metrics;

import io.sentry.IMetricsAggregator;
import io.sentry.ISpan;
import io.sentry.MeasurementUnit;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoopMetricsAggregator implements IMetricsAggregator, MetricsApi.IMetricsInterface {
  private static final NoopMetricsAggregator instance = new NoopMetricsAggregator();
  
  public static NoopMetricsAggregator getInstance() {
    return instance;
  }
  
  public void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {}
  
  public void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {}
  
  public void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {}
  
  public void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {}
  
  public void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {}
  
  public void flush(boolean paramBoolean) {}
  
  public void close() throws IOException {}
  
  @NotNull
  public IMetricsAggregator getMetricsAggregator() {
    return this;
  }
  
  @Nullable
  public LocalMetricsAggregator getLocalMetricsAggregator() {
    return null;
  }
  
  @NotNull
  public Map<String, String> getDefaultTagsForMetrics() {
    return Collections.emptyMap();
  }
  
  @Nullable
  public ISpan startSpanForMetric(@NotNull String paramString1, @NotNull String paramString2) {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\NoopMetricsAggregator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */