package io.sentry.metrics;

import io.sentry.IMetricsAggregator;
import io.sentry.ISpan;
import io.sentry.MeasurementUnit;
import io.sentry.SentryDate;
import io.sentry.SentryNanotimeDate;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MetricsApi {
  @NotNull
  private final IMetricsInterface aggregator;
  
  public MetricsApi(@NotNull IMetricsInterface paramIMetricsInterface) {
    this.aggregator = paramIMetricsInterface;
  }
  
  public void increment(@NotNull String paramString) {
    increment(paramString, 1.0D, null, null, null);
  }
  
  public void increment(@NotNull String paramString, double paramDouble) {
    increment(paramString, paramDouble, null, null, null);
  }
  
  public void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit) {
    increment(paramString, paramDouble, paramMeasurementUnit, null, null);
  }
  
  public void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    increment(paramString, paramDouble, paramMeasurementUnit, paramMap, null);
  }
  
  public void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, @Nullable Long paramLong) {
    long l = (paramLong != null) ? paramLong.longValue() : System.currentTimeMillis();
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    LocalMetricsAggregator localMetricsAggregator = this.aggregator.getLocalMetricsAggregator();
    this.aggregator.getMetricsAggregator().increment(paramString, paramDouble, paramMeasurementUnit, map, l, localMetricsAggregator);
  }
  
  public void gauge(@NotNull String paramString, double paramDouble) {
    gauge(paramString, paramDouble, null, null, null);
  }
  
  public void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit) {
    gauge(paramString, paramDouble, paramMeasurementUnit, null, null);
  }
  
  public void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    gauge(paramString, paramDouble, paramMeasurementUnit, paramMap, null);
  }
  
  public void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, @Nullable Long paramLong) {
    long l = (paramLong != null) ? paramLong.longValue() : System.currentTimeMillis();
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    LocalMetricsAggregator localMetricsAggregator = this.aggregator.getLocalMetricsAggregator();
    this.aggregator.getMetricsAggregator().gauge(paramString, paramDouble, paramMeasurementUnit, map, l, localMetricsAggregator);
  }
  
  public void distribution(@NotNull String paramString, double paramDouble) {
    distribution(paramString, paramDouble, null, null, null);
  }
  
  public void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit) {
    distribution(paramString, paramDouble, paramMeasurementUnit, null, null);
  }
  
  public void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    distribution(paramString, paramDouble, paramMeasurementUnit, paramMap, null);
  }
  
  public void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, @Nullable Long paramLong) {
    long l = (paramLong != null) ? paramLong.longValue() : System.currentTimeMillis();
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    LocalMetricsAggregator localMetricsAggregator = this.aggregator.getLocalMetricsAggregator();
    this.aggregator.getMetricsAggregator().distribution(paramString, paramDouble, paramMeasurementUnit, map, l, localMetricsAggregator);
  }
  
  public void set(@NotNull String paramString, int paramInt) {
    set(paramString, paramInt, (MeasurementUnit)null, (Map<String, String>)null, (Long)null);
  }
  
  public void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit) {
    set(paramString, paramInt, paramMeasurementUnit, (Map<String, String>)null, (Long)null);
  }
  
  public void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    set(paramString, paramInt, paramMeasurementUnit, paramMap, (Long)null);
  }
  
  public void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, @Nullable Long paramLong) {
    long l = (paramLong != null) ? paramLong.longValue() : System.currentTimeMillis();
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    LocalMetricsAggregator localMetricsAggregator = this.aggregator.getLocalMetricsAggregator();
    this.aggregator.getMetricsAggregator().set(paramString, paramInt, paramMeasurementUnit, map, l, localMetricsAggregator);
  }
  
  public void set(@NotNull String paramString1, @NotNull String paramString2) {
    set(paramString1, paramString2, (MeasurementUnit)null, (Map<String, String>)null, (Long)null);
  }
  
  public void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit) {
    set(paramString1, paramString2, paramMeasurementUnit, (Map<String, String>)null, (Long)null);
  }
  
  public void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap) {
    set(paramString1, paramString2, paramMeasurementUnit, paramMap, (Long)null);
  }
  
  public void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, @Nullable Long paramLong) {
    long l = (paramLong != null) ? paramLong.longValue() : System.currentTimeMillis();
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    LocalMetricsAggregator localMetricsAggregator = this.aggregator.getLocalMetricsAggregator();
    this.aggregator.getMetricsAggregator().set(paramString1, paramString2, paramMeasurementUnit, map, l, localMetricsAggregator);
  }
  
  public void timing(@NotNull String paramString, @NotNull Runnable paramRunnable) {
    timing(paramString, paramRunnable, null, null);
  }
  
  public void timing(@NotNull String paramString, @NotNull Runnable paramRunnable, @NotNull MeasurementUnit.Duration paramDuration) {
    timing(paramString, paramRunnable, paramDuration, null);
  }
  
  public void timing(@NotNull String paramString, @NotNull Runnable paramRunnable, @Nullable MeasurementUnit.Duration paramDuration, @Nullable Map<String, String> paramMap) {
    MeasurementUnit.Duration duration = (paramDuration != null) ? paramDuration : MeasurementUnit.Duration.SECOND;
    Map<String, String> map = MetricsHelper.mergeTags(paramMap, this.aggregator.getDefaultTagsForMetrics());
    ISpan iSpan = this.aggregator.startSpanForMetric("metric.timing", paramString);
    LocalMetricsAggregator localMetricsAggregator = (iSpan != null) ? iSpan.getLocalMetricsAggregator() : this.aggregator.getLocalMetricsAggregator();
    if (iSpan != null && paramMap != null)
      for (Map.Entry<String, String> entry : paramMap.entrySet())
        iSpan.setTag((String)entry.getKey(), (String)entry.getValue());  
    long l1 = System.currentTimeMillis();
    long l2 = System.nanoTime();
    try {
      paramRunnable.run();
    } finally {
      long l;
      if (iSpan != null) {
        iSpan.finish();
        SentryDate sentryDate = (SentryDate)((iSpan.getFinishDate() != null) ? iSpan.getFinishDate() : new SentryNanotimeDate());
        l = sentryDate.diff(iSpan.getStartDate());
      } else {
        l = System.nanoTime() - l2;
      } 
      double d = MetricsHelper.convertNanosTo(duration, l);
      this.aggregator.getMetricsAggregator().distribution(paramString, d, (MeasurementUnit)duration, map, l1, localMetricsAggregator);
    } 
  }
  
  @Internal
  public static interface IMetricsInterface {
    @NotNull
    IMetricsAggregator getMetricsAggregator();
    
    @Nullable
    LocalMetricsAggregator getLocalMetricsAggregator();
    
    @NotNull
    Map<String, String> getDefaultTagsForMetrics();
    
    @Nullable
    ISpan startSpanForMetric(@NotNull String param1String1, @NotNull String param1String2);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\MetricsApi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */