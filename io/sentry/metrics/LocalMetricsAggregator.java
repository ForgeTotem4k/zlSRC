package io.sentry.metrics;

import io.sentry.MeasurementUnit;
import io.sentry.protocol.MetricSummary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class LocalMetricsAggregator {
  @NotNull
  private final Map<String, Map<String, GaugeMetric>> buckets = new HashMap<>();
  
  public void add(@NotNull String paramString1, @NotNull MetricType paramMetricType, @NotNull String paramString2, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    String str = MetricsHelper.getExportKey(paramMetricType, paramString2, paramMeasurementUnit);
    synchronized (this.buckets) {
      Map<Object, Object> map = (Map)this.buckets.get(str);
      if (map == null) {
        map = new HashMap<>();
        this.buckets.put(str, map);
      } 
      GaugeMetric gaugeMetric = (GaugeMetric)map.get(paramString1);
      if (gaugeMetric == null) {
        gaugeMetric = new GaugeMetric(paramString2, paramDouble, paramMeasurementUnit, paramMap);
        map.put(paramString1, gaugeMetric);
      } else {
        gaugeMetric.add(paramDouble);
      } 
    } 
  }
  
  @NotNull
  public Map<String, List<MetricSummary>> getSummaries() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    synchronized (this.buckets) {
      for (Map.Entry<String, Map<String, GaugeMetric>> entry : this.buckets.entrySet()) {
        String str = Objects.<String>requireNonNull((String)entry.getKey());
        ArrayList<MetricSummary> arrayList = new ArrayList();
        for (GaugeMetric gaugeMetric : ((Map)entry.getValue()).values())
          arrayList.add(new MetricSummary(gaugeMetric.getMin(), gaugeMetric.getMax(), gaugeMetric.getSum(), gaugeMetric.getCount(), gaugeMetric.getTags())); 
        hashMap.put(str, arrayList);
      } 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\LocalMetricsAggregator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */