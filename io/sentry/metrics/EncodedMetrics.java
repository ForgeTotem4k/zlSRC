package io.sentry.metrics;

import java.nio.charset.Charset;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class EncodedMetrics {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  private final Map<Long, Map<String, Metric>> buckets;
  
  public EncodedMetrics(@NotNull Map<Long, Map<String, Metric>> paramMap) {
    this.buckets = paramMap;
  }
  
  public byte[] encodeToStatsd() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<Long, Map<String, Metric>> entry : this.buckets.entrySet())
      MetricsHelper.encodeMetrics(((Long)entry.getKey()).longValue(), ((Map)entry.getValue()).values(), stringBuilder); 
    return stringBuilder.toString().getBytes(UTF8);
  }
  
  @TestOnly
  Map<Long, Map<String, Metric>> getBuckets() {
    return this.buckets;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\metrics\EncodedMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */