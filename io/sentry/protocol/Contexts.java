package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.ObjectWriter;
import io.sentry.SpanContext;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Contexts implements JsonSerializable {
  private static final long serialVersionUID = 252445813254943011L;
  
  @NotNull
  private final ConcurrentHashMap<String, Object> internalStorage = new ConcurrentHashMap<>();
  
  @NotNull
  private final Object responseLock = new Object();
  
  public Contexts() {}
  
  public Contexts(@NotNull Contexts paramContexts) {
    for (Map.Entry<String, Object> entry : paramContexts.entrySet()) {
      if (entry != null) {
        Object object = entry.getValue();
        if ("app".equals(entry.getKey()) && object instanceof App) {
          setApp(new App((App)object));
          continue;
        } 
        if ("browser".equals(entry.getKey()) && object instanceof Browser) {
          setBrowser(new Browser((Browser)object));
          continue;
        } 
        if ("device".equals(entry.getKey()) && object instanceof Device) {
          setDevice(new Device((Device)object));
          continue;
        } 
        if ("os".equals(entry.getKey()) && object instanceof OperatingSystem) {
          setOperatingSystem(new OperatingSystem((OperatingSystem)object));
          continue;
        } 
        if ("runtime".equals(entry.getKey()) && object instanceof SentryRuntime) {
          setRuntime(new SentryRuntime((SentryRuntime)object));
          continue;
        } 
        if ("gpu".equals(entry.getKey()) && object instanceof Gpu) {
          setGpu(new Gpu((Gpu)object));
          continue;
        } 
        if ("trace".equals(entry.getKey()) && object instanceof SpanContext) {
          setTrace(new SpanContext((SpanContext)object));
          continue;
        } 
        if ("response".equals(entry.getKey()) && object instanceof Response) {
          setResponse(new Response((Response)object));
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
  
  @Nullable
  public App getApp() {
    return toContextType("app", App.class);
  }
  
  public void setApp(@NotNull App paramApp) {
    put("app", paramApp);
  }
  
  @Nullable
  public Browser getBrowser() {
    return toContextType("browser", Browser.class);
  }
  
  public void setBrowser(@NotNull Browser paramBrowser) {
    put("browser", paramBrowser);
  }
  
  @Nullable
  public Device getDevice() {
    return toContextType("device", Device.class);
  }
  
  public void setDevice(@NotNull Device paramDevice) {
    put("device", paramDevice);
  }
  
  @Nullable
  public OperatingSystem getOperatingSystem() {
    return toContextType("os", OperatingSystem.class);
  }
  
  public void setOperatingSystem(@NotNull OperatingSystem paramOperatingSystem) {
    put("os", paramOperatingSystem);
  }
  
  @Nullable
  public SentryRuntime getRuntime() {
    return toContextType("runtime", SentryRuntime.class);
  }
  
  public void setRuntime(@NotNull SentryRuntime paramSentryRuntime) {
    put("runtime", paramSentryRuntime);
  }
  
  @Nullable
  public Gpu getGpu() {
    return toContextType("gpu", Gpu.class);
  }
  
  public void setGpu(@NotNull Gpu paramGpu) {
    put("gpu", paramGpu);
  }
  
  @Nullable
  public Response getResponse() {
    return toContextType("response", Response.class);
  }
  
  public void withResponse(HintUtils.SentryConsumer<Response> paramSentryConsumer) {
    synchronized (this.responseLock) {
      Response response = getResponse();
      if (response != null) {
        paramSentryConsumer.accept(response);
      } else {
        Response response1 = new Response();
        setResponse(response1);
        paramSentryConsumer.accept(response1);
      } 
    } 
  }
  
  public void setResponse(@NotNull Response paramResponse) {
    synchronized (this.responseLock) {
      put("response", paramResponse);
    } 
  }
  
  public int size() {
    return this.internalStorage.size();
  }
  
  public int getSize() {
    return size();
  }
  
  public boolean isEmpty() {
    return this.internalStorage.isEmpty();
  }
  
  public boolean containsKey(@NotNull Object paramObject) {
    return this.internalStorage.containsKey(paramObject);
  }
  
  @Nullable
  public Object get(@NotNull Object paramObject) {
    return this.internalStorage.get(paramObject);
  }
  
  @Nullable
  public Object put(@NotNull String paramString, @Nullable Object paramObject) {
    return this.internalStorage.put(paramString, paramObject);
  }
  
  @Nullable
  public Object set(@NotNull String paramString, @Nullable Object paramObject) {
    return put(paramString, paramObject);
  }
  
  @Nullable
  public Object remove(@NotNull Object paramObject) {
    return this.internalStorage.remove(paramObject);
  }
  
  @NotNull
  public Enumeration<String> keys() {
    return this.internalStorage.keys();
  }
  
  @NotNull
  public Set<Map.Entry<String, Object>> entrySet() {
    return this.internalStorage.entrySet();
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap) {
    this.internalStorage.putAll(paramMap);
  }
  
  public void putAll(@NotNull Contexts paramContexts) {
    this.internalStorage.putAll(paramContexts.internalStorage);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Contexts) {
      Contexts contexts = (Contexts)paramObject;
      return this.internalStorage.equals(contexts.internalStorage);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.internalStorage.hashCode();
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
  
  public static final class Deserializer implements JsonDeserializer<Contexts> {
    @NotNull
    public Contexts deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      Contexts contexts = new Contexts();
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "app":
            contexts.setApp((new App.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "browser":
            contexts.setBrowser((new Browser.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "device":
            contexts.setDevice((new Device.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "gpu":
            contexts.setGpu((new Gpu.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "os":
            contexts.setOperatingSystem((new OperatingSystem.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "runtime":
            contexts.setRuntime((new SentryRuntime.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "trace":
            contexts.setTrace((new SpanContext.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
          case "response":
            contexts.setResponse((new Response.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger));
            continue;
        } 
        Object object = param1JsonObjectReader.nextObjectOrNull();
        if (object != null)
          contexts.put(str, object); 
      } 
      param1JsonObjectReader.endObject();
      return contexts;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Contexts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */