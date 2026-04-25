package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DistributionMetric extends Metric {
  private final List<Double> values = new ArrayList<>();
  
  public DistributionMetric(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    super(MetricType.Distribution, paramString, paramMeasurementUnit, paramMap);
    this.values.add(Double.valueOf(paramDouble));
  }
  
  public void add(double paramDouble) {
    this.values.add(Double.valueOf(paramDouble));
  }
  
  public int getWeight() {
    return this.values.size();
  }
  
  @NotNull
  public Iterable<?> serialize() {
    return this.values;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\DistributionMetric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */