package io.sentry;

import io.sentry.metrics.CounterMetric;
import io.sentry.metrics.DistributionMetric;
import io.sentry.metrics.EncodedMetrics;
import io.sentry.metrics.GaugeMetric;
import io.sentry.metrics.IMetricsClient;
import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.metrics.Metric;
import io.sentry.metrics.MetricType;
import io.sentry.metrics.MetricsHelper;
import io.sentry.metrics.SetMetric;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class MetricsAggregator implements IMetricsAggregator, Runnable, Closeable {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  @NotNull
  private final ILogger logger;
  
  @NotNull
  private final IMetricsClient client;
  
  @NotNull
  private final SentryDateProvider dateProvider;
  
  @Nullable
  private final SentryOptions.BeforeEmitMetricCallback beforeEmitCallback;
  
  @NotNull
  private volatile ISentryExecutorService executorService;
  
  private volatile boolean isClosed = false;
  
  private volatile boolean flushScheduled = false;
  
  @NotNull
  private final NavigableMap<Long, Map<String, Metric>> buckets = new ConcurrentSkipListMap<>();
  
  @NotNull
  private final AtomicInteger totalBucketsWeight = new AtomicInteger();
  
  private final int maxWeight;
  
  public MetricsAggregator(@NotNull SentryOptions paramSentryOptions, @NotNull IMetricsClient paramIMetricsClient) {
    this(paramIMetricsClient, paramSentryOptions.getLogger(), paramSentryOptions.getDateProvider(), 100000, paramSentryOptions.getBeforeEmitMetricCallback(), NoOpSentryExecutorService.getInstance());
  }
  
  @TestOnly
  public MetricsAggregator(@NotNull IMetricsClient paramIMetricsClient, @NotNull ILogger paramILogger, @NotNull SentryDateProvider paramSentryDateProvider, int paramInt, @Nullable SentryOptions.BeforeEmitMetricCallback paramBeforeEmitMetricCallback, @NotNull ISentryExecutorService paramISentryExecutorService) {
    this.client = paramIMetricsClient;
    this.logger = paramILogger;
    this.dateProvider = paramSentryDateProvider;
    this.maxWeight = paramInt;
    this.beforeEmitCallback = paramBeforeEmitMetricCallback;
    this.executorService = paramISentryExecutorService;
  }
  
  public void increment(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    add(MetricType.Counter, paramString, paramDouble, paramMeasurementUnit, paramMap, paramLong, paramLocalMetricsAggregator);
  }
  
  public void gauge(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    add(MetricType.Gauge, paramString, paramDouble, paramMeasurementUnit, paramMap, paramLong, paramLocalMetricsAggregator);
  }
  
  public void distribution(@NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    add(MetricType.Distribution, paramString, paramDouble, paramMeasurementUnit, paramMap, paramLong, paramLocalMetricsAggregator);
  }
  
  public void set(@NotNull String paramString, int paramInt, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    add(MetricType.Set, paramString, paramInt, paramMeasurementUnit, paramMap, paramLong, paramLocalMetricsAggregator);
  }
  
  public void set(@NotNull String paramString1, @NotNull String paramString2, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    byte[] arrayOfByte = paramString2.getBytes(UTF8);
    CRC32 cRC32 = new CRC32();
    cRC32.update(arrayOfByte, 0, arrayOfByte.length);
    int i = (int)cRC32.getValue();
    add(MetricType.Set, paramString1, i, paramMeasurementUnit, paramMap, paramLong, paramLocalMetricsAggregator);
  }
  
  private void add(@NotNull MetricType paramMetricType, @NotNull String paramString, double paramDouble, @Nullable MeasurementUnit paramMeasurementUnit, @Nullable Map<String, String> paramMap, long paramLong, @Nullable LocalMetricsAggregator paramLocalMetricsAggregator) {
    int i;
    if (this.isClosed)
      return; 
    if (this.beforeEmitCallback != null)
      try {
        if (!this.beforeEmitCallback.execute(paramString, paramMap))
          return; 
      } catch (Throwable throwable) {
        this.logger.log(SentryLevel.ERROR, "The beforeEmit callback threw an exception.", throwable);
      }  
    long l = MetricsHelper.getTimeBucketKey(paramLong);
    Map<String, Metric> map = getOrAddTimeBucket(l);
    String str = MetricsHelper.getMetricBucketKey(paramMetricType, paramString, paramMeasurementUnit, paramMap);
    synchronized (map) {
      Metric metric = map.get(str);
      if (metric != null) {
        int j = metric.getWeight();
        metric.add(paramDouble);
        i = metric.getWeight() - j;
      } else {
        CounterMetric counterMetric;
        GaugeMetric gaugeMetric;
        DistributionMetric distributionMetric;
        SetMetric setMetric;
        switch (paramMetricType) {
          case Counter:
            counterMetric = new CounterMetric(paramString, paramDouble, paramMeasurementUnit, paramMap);
            break;
          case Gauge:
            gaugeMetric = new GaugeMetric(paramString, paramDouble, paramMeasurementUnit, paramMap);
            break;
          case Distribution:
            distributionMetric = new DistributionMetric(paramString, paramDouble, paramMeasurementUnit, paramMap);
            break;
          case Set:
            setMetric = new SetMetric(paramString, paramMeasurementUnit, paramMap);
            setMetric.add((int)paramDouble);
            break;
          default:
            throw new IllegalArgumentException("Unknown MetricType: " + paramMetricType.name());
        } 
        i = setMetric.getWeight();
        map.put(str, setMetric);
      } 
      this.totalBucketsWeight.addAndGet(i);
    } 
    if (paramLocalMetricsAggregator != null) {
      double d = (paramMetricType == MetricType.Set) ? i : paramDouble;
      paramLocalMetricsAggregator.add(str, paramMetricType, paramString, d, paramMeasurementUnit, paramMap);
    } 
    boolean bool = isOverWeight();
    if (!this.isClosed && (bool || !this.flushScheduled))
      synchronized (this) {
        if (!this.isClosed) {
          if (this.executorService instanceof NoOpSentryExecutorService)
            this.executorService = new SentryExecutorService(); 
          this.flushScheduled = true;
          long l1 = bool ? 0L : 5000L;
          this.executorService.schedule(this, l1);
        } 
      }  
  }
  
  public void flush(boolean paramBoolean) {
    if (!paramBoolean && isOverWeight()) {
      this.logger.log(SentryLevel.INFO, "Metrics: total weight exceeded, flushing all buckets", new Object[0]);
      paramBoolean = true;
    } 
    this.flushScheduled = false;
    Set<Long> set = getFlushableBuckets(paramBoolean);
    if (set.isEmpty()) {
      this.logger.log(SentryLevel.DEBUG, "Metrics: nothing to flush", new Object[0]);
      return;
    } 
    this.logger.log(SentryLevel.DEBUG, "Metrics: flushing " + set.size() + " buckets", new Object[0]);
    HashMap<Object, Object> hashMap = new HashMap<>();
    int i = 0;
    Iterator<Long> iterator = set.iterator();
    while (iterator.hasNext()) {
      long l = ((Long)iterator.next()).longValue();
      Map<String, Metric> map = this.buckets.remove(Long.valueOf(l));
      if (map != null)
        synchronized (map) {
          int j = getBucketWeight(map);
          this.totalBucketsWeight.addAndGet(-j);
          i += map.size();
          hashMap.put(Long.valueOf(l), map);
        }  
    } 
    if (i == 0) {
      this.logger.log(SentryLevel.DEBUG, "Metrics: only empty buckets found", new Object[0]);
      return;
    } 
    this.logger.log(SentryLevel.DEBUG, "Metrics: capturing metrics", new Object[0]);
    this.client.captureMetrics(new EncodedMetrics(hashMap));
  }
  
  private boolean isOverWeight() {
    int i = this.buckets.size() + this.totalBucketsWeight.get();
    return (i >= this.maxWeight);
  }
  
  private static int getBucketWeight(@NotNull Map<String, Metric> paramMap) {
    int i = 0;
    for (Metric metric : paramMap.values())
      i += metric.getWeight(); 
    return i;
  }
  
  @NotNull
  private Set<Long> getFlushableBuckets(boolean paramBoolean) {
    if (paramBoolean)
      return this.buckets.keySet(); 
    long l1 = MetricsHelper.getCutoffTimestampMs(nowMillis());
    long l2 = MetricsHelper.getTimeBucketKey(l1);
    return this.buckets.headMap(Long.valueOf(l2), true).keySet();
  }
  
  @NotNull
  private Map<String, Metric> getOrAddTimeBucket(long paramLong) {
    Map<Object, Object> map = (Map)this.buckets.get(Long.valueOf(paramLong));
    if (map == null)
      synchronized (this.buckets) {
        map = (Map)this.buckets.get(Long.valueOf(paramLong));
        if (map == null) {
          map = new HashMap<>();
          this.buckets.put(Long.valueOf(paramLong), map);
        } 
      }  
    return (Map)map;
  }
  
  public void close() throws IOException {
    synchronized (this) {
      this.isClosed = true;
      this.executorService.close(0L);
    } 
    flush(true);
  }
  
  public void run() {
    flush(false);
    synchronized (this) {
      if (!this.isClosed && !this.buckets.isEmpty())
        this.executorService.schedule(this, 5000L); 
    } 
  }
  
  private long nowMillis() {
    return TimeUnit.NANOSECONDS.toMillis(this.dateProvider.now().nanoTimestamp());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MetricsAggregator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */