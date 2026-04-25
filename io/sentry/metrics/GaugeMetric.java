package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import java.util.Arrays;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class GaugeMetric extends Metric {
  private double last;
  
  private double min;
  
  private double max;
  
  private double sum;
  
  private int count;
  
  public GaugeMetric(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    super(MetricType.Gauge, paramString, paramMeasurementUnit, paramMap);
    this.last = paramDouble;
    this.min = paramDouble;
    this.max = paramDouble;
    this.sum = paramDouble;
    this.count = 1;
  }
  
  public void add(double paramDouble) {
    this.last = paramDouble;
    this.min = Math.min(this.min, paramDouble);
    this.max = Math.max(this.max, paramDouble);
    this.sum += paramDouble;
    this.count++;
  }
  
  public double getLast() {
    return this.last;
  }
  
  public double getMin() {
    return this.min;
  }
  
  public double getMax() {
    return this.max;
  }
  
  public double getSum() {
    return this.sum;
  }
  
  public int getCount() {
    return this.count;
  }
  
  public int getWeight() {
    return 5;
  }
  
  @NotNull
  public Iterable<?> serialize() {
    return Arrays.asList((Object[])new Number[] { Double.valueOf(this.last), Double.valueOf(this.min), Double.valueOf(this.max), Double.valueOf(this.sum), Integer.valueOf(this.count) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\GaugeMetric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */