package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class MonitorContexts extends ConcurrentHashMap<String, Object> implements JsonSerializable {
  private static final long serialVersionUID = 3987329379811822556L;
  
  public MonitorContexts() {}
  
  public MonitorContexts(@NotNull MonitorContexts paramMonitorContexts) {
    for (Map.Entry<String, Object> entry : paramMonitorContexts.entrySet()) {
      if (entry != null) {
        Object object = entry.getValue();
        if ("trace".equals(entry.getKey()) && object instanceof SpanContext) {
          setTrace(new SpanContext((SpanContext)object));
          continue;
        } 
        put((String)entry.getKey(), object);
      } 
    } 
  }
  
  @Nullable
  private <T> T toContextType(@NotNull String paramString, @NotNull Class<T> paramClass) {
    Object object = get(paramString);
    return paramClass.isInstance(object) ? paramClass.cast(object) : null;
  }
  
  @Nullable
  public SpanContext getTrace() {
    return toContextType("trace", SpanContext.class);
  }
  
  public void setTrace(@Nullable SpanContext paramSpanContext) {
    Objects.requireNonNull(paramSpanContext, "traceContext is required");
    put("trace", paramSpanContext);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    ArrayList<String> arrayList = Collections.list(keys());
    Collections.sort(arrayList);
    for (String str : arrayList) {
      Object object = get(str);
      if (object != null)
        paramObjectWriter.name(str).value(paramILogger, object); 
    } 
    paramObjectWriter.endObject();
  }
  
  public static final class Deserializer implements JsonDeserializer<MonitorContexts> {
    @NotNull
    public MonitorContexts deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      MonitorContexts monitorContexts = new MonitorContexts();
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "trace":
            monitorContexts.setTrace((new SpanContext.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
        } 
        Object object = param1JsonObjectReader.nextObjectOrNull();
        if (object != null)
          monitorContexts.put(str, object); 
      } 
      param1JsonObjectReader.endObject();
      return monitorContexts;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MonitorContexts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */