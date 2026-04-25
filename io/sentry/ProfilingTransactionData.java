package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ProfilingTransactionData implements JsonUnknown, JsonSerializable {
  @NotNull
  private String id;
  
  @NotNull
  private String traceId;
  
  @NotNull
  private String name;
  
  @NotNull
  private Long relativeStartNs;
  
  @Nullable
  private Long relativeEndNs;
  
  @NotNull
  private Long relativeStartCpuMs;
  
  @Nullable
  private Long relativeEndCpuMs;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public ProfilingTransactionData() {
    this(NoOpTransaction.getInstance(), Long.valueOf(0L), Long.valueOf(0L));
  }
  
  public ProfilingTransactionData(@NotNull ITransaction paramITransaction, @NotNull Long paramLong1, @NotNull Long paramLong2) {
    this.id = paramITransaction.getEventId().toString();
    this.traceId = paramITransaction.getSpanContext().getTraceId().toString();
    this.name = paramITransaction.getName();
    this.relativeStartNs = paramLong1;
    this.relativeStartCpuMs = paramLong2;
  }
  
  public void notifyFinish(@NotNull Long paramLong1, @NotNull Long paramLong2, @NotNull Long paramLong3, @NotNull Long paramLong4) {
    if (this.relativeEndNs == null) {
      this.relativeEndNs = Long.valueOf(paramLong1.longValue() - paramLong2.longValue());
      this.relativeStartNs = Long.valueOf(this.relativeStartNs.longValue() - paramLong2.longValue());
      this.relativeEndCpuMs = Long.valueOf(paramLong3.longValue() - paramLong4.longValue());
      this.relativeStartCpuMs = Long.valueOf(this.relativeStartCpuMs.longValue() - paramLong4.longValue());
    } 
  }
  
  @NotNull
  public String getId() {
    return this.id;
  }
  
  @NotNull
  public String getTraceId() {
    return this.traceId;
  }
  
  @NotNull
  public String getName() {
    return this.name;
  }
  
  @NotNull
  public Long getRelativeStartNs() {
    return this.relativeStartNs;
  }
  
  @Nullable
  public Long getRelativeEndNs() {
    return this.relativeEndNs;
  }
  
  @Nullable
  public Long getRelativeEndCpuMs() {
    return this.relativeEndCpuMs;
  }
  
  @NotNull
  public Long getRelativeStartCpuMs() {
    return this.relativeStartCpuMs;
  }
  
  public void setId(@NotNull String paramString) {
    this.id = paramString;
  }
  
  public void setTraceId(@NotNull String paramString) {
    this.traceId = paramString;
  }
  
  public void setName(@NotNull String paramString) {
    this.name = paramString;
  }
  
  public void setRelativeStartNs(@NotNull Long paramLong) {
    this.relativeStartNs = paramLong;
  }
  
  public void setRelativeEndNs(@Nullable Long paramLong) {
    this.relativeEndNs = paramLong;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ProfilingTransactionData profilingTransactionData = (ProfilingTransactionData)paramObject;
    return (this.id.equals(profilingTransactionData.id) && this.traceId.equals(profilingTransactionData.traceId) && this.name.equals(profilingTransactionData.name) && this.relativeStartNs.equals(profilingTransactionData.relativeStartNs) && this.relativeStartCpuMs.equals(profilingTransactionData.relativeStartCpuMs) && Objects.equals(this.relativeEndCpuMs, profilingTransactionData.relativeEndCpuMs) && Objects.equals(this.relativeEndNs, profilingTransactionData.relativeEndNs) && Objects.equals(this.unknown, profilingTransactionData.unknown));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.id, this.traceId, this.name, this.relativeStartNs, this.relativeEndNs, this.relativeStartCpuMs, this.relativeEndCpuMs, this.unknown });
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("id").value(paramILogger, this.id);
    paramObjectWriter.name("trace_id").value(paramILogger, this.traceId);
    paramObjectWriter.name("name").value(paramILogger, this.name);
    paramObjectWriter.name("relative_start_ns").value(paramILogger, this.relativeStartNs);
    paramObjectWriter.name("relative_end_ns").value(paramILogger, this.relativeEndNs);
    paramObjectWriter.name("relative_cpu_start_ms").value(paramILogger, this.relativeStartCpuMs);
    paramObjectWriter.name("relative_cpu_end_ms").value(paramILogger, this.relativeEndCpuMs);
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public static final class JsonKeys {
    public static final String ID = "id";
    
    public static final String TRACE_ID = "trace_id";
    
    public static final String NAME = "name";
    
    public static final String START_NS = "relative_start_ns";
    
    public static final String END_NS = "relative_end_ns";
    
    public static final String START_CPU_MS = "relative_cpu_start_ms";
    
    public static final String END_CPU_MS = "relative_cpu_end_ms";
  }
  
  public static final class Deserializer implements JsonDeserializer<ProfilingTransactionData> {
    @NotNull
    public ProfilingTransactionData deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      ProfilingTransactionData profilingTransactionData = new ProfilingTransactionData();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str2;
        String str3;
        String str4;
        Long long_1;
        Long long_2;
        Long long_3;
        Long long_4;
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "id":
            str2 = param1JsonObjectReader.nextStringOrNull();
            if (str2 != null)
              profilingTransactionData.id = str2; 
            continue;
          case "trace_id":
            str3 = param1JsonObjectReader.nextStringOrNull();
            if (str3 != null)
              profilingTransactionData.traceId = str3; 
            continue;
          case "name":
            str4 = param1JsonObjectReader.nextStringOrNull();
            if (str4 != null)
              profilingTransactionData.name = str4; 
            continue;
          case "relative_start_ns":
            long_1 = param1JsonObjectReader.nextLongOrNull();
            if (long_1 != null)
              profilingTransactionData.relativeStartNs = long_1; 
            continue;
          case "relative_end_ns":
            long_2 = param1JsonObjectReader.nextLongOrNull();
            if (long_2 != null)
              profilingTransactionData.relativeEndNs = long_2; 
            continue;
          case "relative_cpu_start_ms":
            long_3 = param1JsonObjectReader.nextLongOrNull();
            if (long_3 != null)
              profilingTransactionData.relativeStartCpuMs = long_3; 
            continue;
          case "relative_cpu_end_ms":
            long_4 = param1JsonObjectReader.nextLongOrNull();
            if (long_4 != null)
              profilingTransactionData.relativeEndCpuMs = long_4; 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str1);
      } 
      profilingTransactionData.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return profilingTransactionData;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ProfilingTransactionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */