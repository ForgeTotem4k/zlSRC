package io.sentry;

import io.sentry.protocol.App;
import io.sentry.protocol.Browser;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Device;
import io.sentry.protocol.Gpu;
import io.sentry.protocol.OperatingSystem;
import io.sentry.protocol.Response;
import io.sentry.protocol.SentryRuntime;
import io.sentry.util.HintUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombinedContextsView extends Contexts {
  private static final long serialVersionUID = 3585992094653318439L;
  
  @NotNull
  private final Contexts globalContexts;
  
  @NotNull
  private final Contexts isolationContexts;
  
  @NotNull
  private final Contexts currentContexts;
  
  @NotNull
  private final ScopeType defaultScopeType;
  
  public CombinedContextsView(@NotNull Contexts paramContexts1, @NotNull Contexts paramContexts2, @NotNull Contexts paramContexts3, @NotNull ScopeType paramScopeType) {
    this.globalContexts = paramContexts1;
    this.isolationContexts = paramContexts2;
    this.currentContexts = paramContexts3;
    this.defaultScopeType = paramScopeType;
  }
  
  @Nullable
  public SpanContext getTrace() {
    SpanContext spanContext1 = this.currentContexts.getTrace();
    if (spanContext1 != null)
      return spanContext1; 
    SpanContext spanContext2 = this.isolationContexts.getTrace();
    return (spanContext2 != null) ? spanContext2 : this.globalContexts.getTrace();
  }
  
  public void setTrace(@Nullable SpanContext paramSpanContext) {
    getDefaultContexts().setTrace(paramSpanContext);
  }
  
  @NotNull
  private Contexts getDefaultContexts() {
    switch (this.defaultScopeType) {
      case CURRENT:
        return this.currentContexts;
      case ISOLATION:
        return this.isolationContexts;
      case GLOBAL:
        return this.globalContexts;
    } 
    return this.currentContexts;
  }
  
  @Nullable
  public App getApp() {
    App app1 = this.currentContexts.getApp();
    if (app1 != null)
      return app1; 
    App app2 = this.isolationContexts.getApp();
    return (app2 != null) ? app2 : this.globalContexts.getApp();
  }
  
  public void setApp(@NotNull App paramApp) {
    getDefaultContexts().setApp(paramApp);
  }
  
  @Nullable
  public Browser getBrowser() {
    Browser browser1 = this.currentContexts.getBrowser();
    if (browser1 != null)
      return browser1; 
    Browser browser2 = this.isolationContexts.getBrowser();
    return (browser2 != null) ? browser2 : this.globalContexts.getBrowser();
  }
  
  public void setBrowser(@NotNull Browser paramBrowser) {
    getDefaultContexts().setBrowser(paramBrowser);
  }
  
  @Nullable
  public Device getDevice() {
    Device device1 = this.currentContexts.getDevice();
    if (device1 != null)
      return device1; 
    Device device2 = this.isolationContexts.getDevice();
    return (device2 != null) ? device2 : this.globalContexts.getDevice();
  }
  
  public void setDevice(@NotNull Device paramDevice) {
    getDefaultContexts().setDevice(paramDevice);
  }
  
  @Nullable
  public OperatingSystem getOperatingSystem() {
    OperatingSystem operatingSystem1 = this.currentContexts.getOperatingSystem();
    if (operatingSystem1 != null)
      return operatingSystem1; 
    OperatingSystem operatingSystem2 = this.isolationContexts.getOperatingSystem();
    return (operatingSystem2 != null) ? operatingSystem2 : this.globalContexts.getOperatingSystem();
  }
  
  public void setOperatingSystem(@NotNull OperatingSystem paramOperatingSystem) {
    getDefaultContexts().setOperatingSystem(paramOperatingSystem);
  }
  
  @Nullable
  public SentryRuntime getRuntime() {
    SentryRuntime sentryRuntime1 = this.currentContexts.getRuntime();
    if (sentryRuntime1 != null)
      return sentryRuntime1; 
    SentryRuntime sentryRuntime2 = this.isolationContexts.getRuntime();
    return (sentryRuntime2 != null) ? sentryRuntime2 : this.globalContexts.getRuntime();
  }
  
  public void setRuntime(@NotNull SentryRuntime paramSentryRuntime) {
    getDefaultContexts().setRuntime(paramSentryRuntime);
  }
  
  @Nullable
  public Gpu getGpu() {
    Gpu gpu1 = this.currentContexts.getGpu();
    if (gpu1 != null)
      return gpu1; 
    Gpu gpu2 = this.isolationContexts.getGpu();
    return (gpu2 != null) ? gpu2 : this.globalContexts.getGpu();
  }
  
  public void setGpu(@NotNull Gpu paramGpu) {
    getDefaultContexts().setGpu(paramGpu);
  }
  
  @Nullable
  public Response getResponse() {
    Response response1 = this.currentContexts.getResponse();
    if (response1 != null)
      return response1; 
    Response response2 = this.isolationContexts.getResponse();
    return (response2 != null) ? response2 : this.globalContexts.getResponse();
  }
  
  public void withResponse(HintUtils.SentryConsumer<Response> paramSentryConsumer) {
    if (this.currentContexts.getResponse() != null) {
      this.currentContexts.withResponse(paramSentryConsumer);
    } else if (this.isolationContexts.getResponse() != null) {
      this.isolationContexts.withResponse(paramSentryConsumer);
    } else if (this.globalContexts.getResponse() != null) {
      this.globalContexts.withResponse(paramSentryConsumer);
    } else {
      getDefaultContexts().withResponse(paramSentryConsumer);
    } 
  }
  
  public void setResponse(@NotNull Response paramResponse) {
    getDefaultContexts().setResponse(paramResponse);
  }
  
  public int size() {
    return mergeContexts().size();
  }
  
  public int getSize() {
    return size();
  }
  
  public boolean isEmpty() {
    return (this.globalContexts.isEmpty() && this.isolationContexts.isEmpty() && this.currentContexts.isEmpty());
  }
  
  public boolean containsKey(@NotNull Object paramObject) {
    return (this.globalContexts.containsKey(paramObject) || this.isolationContexts.containsKey(paramObject) || this.currentContexts.containsKey(paramObject));
  }
  
  @Nullable
  public Object get(@NotNull Object paramObject) {
    Object object1 = this.currentContexts.get(paramObject);
    if (object1 != null)
      return object1; 
    Object object2 = this.isolationContexts.get(paramObject);
    return (object2 != null) ? object2 : this.globalContexts.get(paramObject);
  }
  
  @Nullable
  public Object put(@NotNull String paramString, @Nullable Object paramObject) {
    return getDefaultContexts().put(paramString, paramObject);
  }
  
  @Nullable
  public Object remove(@NotNull Object paramObject) {
    return getDefaultContexts().remove(paramObject);
  }
  
  @NotNull
  public Enumeration<String> keys() {
    return mergeContexts().keys();
  }
  
  @NotNull
  public Set<Map.Entry<String, Object>> entrySet() {
    return mergeContexts().entrySet();
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    mergeContexts().serialize(paramObjectWriter, paramILogger);
  }
  
  @NotNull
  private Contexts mergeContexts() {
    Contexts contexts = new Contexts();
    contexts.putAll(this.globalContexts);
    contexts.putAll(this.isolationContexts);
    contexts.putAll(this.currentContexts);
    return contexts;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CombinedContextsView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */