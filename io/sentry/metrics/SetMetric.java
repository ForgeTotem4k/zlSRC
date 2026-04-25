package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SetMetric extends Metric {
  @NotNull
  private final Set<Integer> values = new HashSet<>();
  
  public SetMetric(@NotNull String paramString, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    super(MetricType.Set, paramString, paramMeasurementUnit, paramMap);
  }
  
  public void add(double paramDouble) {
    this.values.add(Integer.valueOf((int)paramDouble));
  }
  
  public int getWeight() {
    return this.values.size();
  }
  
  @NotNull
  public Iterable<?> serialize() {
    return this.values;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\SetMetric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */