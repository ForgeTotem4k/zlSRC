package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class CounterMetric extends Metric {
  private double value;
  
  public CounterMetric(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    super(MetricType.Counter, paramString, paramMeasurementUnit, paramMap);
    this.value = paramDouble;
  }
  
  public double getValue() {
    return this.value;
  }
  
  public void add(double paramDouble) {
    this.value += paramDouble;
  }
  
  public int getWeight() {
    return 1;
  }
  
  @NotNull
  public Iterable<?> serialize() {
    return Collections.singletonList(Double.valueOf(this.value));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\CounterMetric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */