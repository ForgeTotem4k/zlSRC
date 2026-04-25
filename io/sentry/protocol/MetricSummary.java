package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MetricSummary implements JsonUnknown, JsonSerializable {
  private double min;
  
  private double max;
  
  private double sum;
  
  private int count;
  
  @Nullable
  private Map<String, String> tags;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public MetricSummary() {}
  
  public MetricSummary(double paramDouble1, double paramDouble2, double paramDouble3, int paramInt, @Nullable Map<String, String> paramMap) {
    this.tags = paramMap;
    this.min = paramDouble1;
    this.max = paramDouble2;
    this.count = paramInt;
    this.sum = paramDouble3;
    this.unknown = null;
  }
  
  public void setTags(@Nullable Map<String, String> paramMap) {
    this.tags = paramMap;
  }
  
  public void setMin(double paramDouble) {
    this.min = paramDouble;
  }
  
  public void setMax(double paramDouble) {
    this.max = paramDouble;
  }
  
  public void setCount(int paramInt) {
    this.count = paramInt;
  }
  
  public void setSum(double paramDouble) {
    this.sum = paramDouble;
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
  
  @Nullable
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("min").value(this.min);
    paramObjectWriter.name("max").value(this.max);
    paramObjectWriter.name("sum").value(this.sum);
    paramObjectWriter.name("count").value(this.count);
    if (this.tags != null) {
      paramObjectWriter.name("tags");
      paramObjectWriter.value(paramILogger, this.tags);
    } 
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TAGS = "tags";
    
    public static final String MIN = "min";
    
    public static final String MAX = "max";
    
    public static final String COUNT = "count";
    
    public static final String SUM = "sum";
  }
  
  public static final class Deserializer implements JsonDeserializer<MetricSummary> {
    @NotNull
    public MetricSummary deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      MetricSummary metricSummary = new MetricSummary();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "tags":
            metricSummary.tags = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "min":
            metricSummary.setMin(param1JsonObjectReader.nextDouble());
            continue;
          case "max":
            metricSummary.setMax(param1JsonObjectReader.nextDouble());
            continue;
          case "sum":
            metricSummary.setSum(param1JsonObjectReader.nextDouble());
            continue;
          case "count":
            metricSummary.setCount(param1JsonObjectReader.nextInt());
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      metricSummary.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return metricSummary;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\MetricSummary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */