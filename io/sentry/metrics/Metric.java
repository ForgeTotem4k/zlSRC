package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public abstract class Metric {
  @NotNull
  private final MetricType type;
  
  @NotNull
  private final String key;
  
  @Nullable
  private final MeasurementUnit unit;
  
  @Nullable
  private final Map<String, String> tags;
  
  public Metric(@NotNull MetricType paramMetricType, @NotNull String paramString, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    this.type = paramMetricType;
    this.key = paramString;
    this.unit = paramMeasurementUnit;
    this.tags = paramMap;
  }
  
  public abstract void add(double paramDouble);
  
  @NotNull
  public MetricType getType() {
    return this.type;
  }
  
  public abstract int getWeight();
  
  @NotNull
  public String getKey() {
    return this.key;
  }
  
  @Nullable
  public MeasurementUnit getUnit() {
    return this.unit;
  }
  
  @Nullable
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  @NotNull
  public abstract Iterable<?> serialize();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\Metric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */