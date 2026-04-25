package io.sentry;

import io.sentry.metrics.LocalMetricsAggregator;
import java.io.Closeable;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMetricsAggregator extends Closeable {
  void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator);
  
  void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator);
  
  void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator);
  
  void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator);
  
  void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator);
  
  void flush(boolean paramBoolean);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IMetricsAggregator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */