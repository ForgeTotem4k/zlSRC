package io.sentry;

import io.sentry.hints.AbnormalExit;
import io.sentry.hints.Cached;
import io.sentry.protocol.DebugImage;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@Internal
public final class MainEventProcessor implements EventProcessor, Closeable {
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final SentryThreadFactory sentryThreadFactory;
  
  @NotNull
  private final SentryExceptionFactory sentryExceptionFactory;
  
  @Nullable
  private volatile HostnameCache hostnameCache = null;
  
  public MainEventProcessor(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "The SentryOptions is required.");
    SentryStackTraceFactory sentryStackTraceFactory = new SentryStackTraceFactory(this.options);
    this.sentryExceptionFactory = new SentryExceptionFactory(sentryStackTraceFactory);
    this.sentryThreadFactory = new SentryThreadFactory(sentryStackTraceFactory, this.options);
  }
  
  MainEventProcessor(@NotNull SentryOptions paramSentryOptions, @NotNull SentryThreadFactory paramSentryThreadFactory, @NotNull SentryExceptionFactory paramSentryExceptionFactory) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "The SentryOptions is required.");
    this.sentryThreadFactory = (SentryThreadFactory)Objects.requireNonNull(paramSentryThreadFactory, "The SentryThreadFactory is required.");
    this.sentryExceptionFactory = (SentryExceptionFactory)Objects.requireNonNull(paramSentryExceptionFactory, "The SentryExceptionFactory is required.");
  }
  
  @NotNull
  public SentryEvent process(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    setCommons(paramSentryEvent);
    setExceptions(paramSentryEvent);
    setDebugMeta(paramSentryEvent);
    setModules(paramSentryEvent);
    if (shouldApplyScopeData(paramSentryEvent, paramHint)) {
      processNonCachedEvent(paramSentryEvent);
      setThreads(paramSentryEvent, paramHint);
    } 
    return paramSentryEvent;
  }
  
  private void setDebugMeta(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    ArrayList<DebugImage> arrayList = new ArrayList();
    if (this.options.getProguardUuid() != null) {
      DebugImage debugImage = new DebugImage();
      debugImage.setType("proguard");
      debugImage.setUuid(this.options.getProguardUuid());
      arrayList.add(debugImage);
    } 
    for (String str : this.options.getBundleIds()) {
      DebugImage debugImage = new DebugImage();
      debugImage.setType("jvm");
      debugImage.setDebugId(str);
      arrayList.add(debugImage);
    } 
    if (!arrayList.isEmpty()) {
      DebugMeta debugMeta = paramSentryBaseEvent.getDebugMeta();
      if (debugMeta == null)
        debugMeta = new DebugMeta(); 
      if (debugMeta.getImages() == null) {
        debugMeta.setImages(arrayList);
      } else {
        debugMeta.getImages().addAll(arrayList);
      } 
      paramSentryBaseEvent.setDebugMeta(debugMeta);
    } 
  }
  
  private void setModules(@NotNull SentryEvent paramSentryEvent) {
    Map<String, String> map1 = this.options.getModulesLoader().getOrLoadModules();
    if (map1 == null)
      return; 
    Map<String, String> map2 = paramSentryEvent.getModules();
    if (map2 == null) {
      paramSentryEvent.setModules(map1);
    } else {
      map2.putAll(map1);
    } 
  }
  
  private boolean shouldApplyScopeData(@NotNull SentryBaseEvent paramSentryBaseEvent, @NotNull Hint paramHint) {
    if (HintUtils.shouldApplyScopeData(paramHint))
      return true; 
    this.options.getLogger().log(SentryLevel.DEBUG, "Event was cached so not applying data relevant to the current app execution/version: %s", new Object[] { paramSentryBaseEvent.getEventId() });
    return false;
  }
  
  private void processNonCachedEvent(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    setRelease(paramSentryBaseEvent);
    setEnvironment(paramSentryBaseEvent);
    setServerName(paramSentryBaseEvent);
    setDist(paramSentryBaseEvent);
    setSdk(paramSentryBaseEvent);
    setTags(paramSentryBaseEvent);
    mergeUser(paramSentryBaseEvent);
  }
  
  @NotNull
  public SentryTransaction process(@NotNull SentryTransaction paramSentryTransaction, @NotNull Hint paramHint) {
    setCommons((SentryBaseEvent)paramSentryTransaction);
    setDebugMeta((SentryBaseEvent)paramSentryTransaction);
    if (shouldApplyScopeData((SentryBaseEvent)paramSentryTransaction, paramHint))
      processNonCachedEvent((SentryBaseEvent)paramSentryTransaction); 
    return paramSentryTransaction;
  }
  
  private void setCommons(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    setPlatform(paramSentryBaseEvent);
  }
  
  private void setPlatform(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getPlatform() == null)
      paramSentryBaseEvent.setPlatform("java"); 
  }
  
  private void setRelease(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getRelease() == null)
      paramSentryBaseEvent.setRelease(this.options.getRelease()); 
  }
  
  private void setEnvironment(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getEnvironment() == null)
      paramSentryBaseEvent.setEnvironment(this.options.getEnvironment()); 
  }
  
  private void setServerName(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getServerName() == null)
      paramSentryBaseEvent.setServerName(this.options.getServerName()); 
    if (this.options.isAttachServerName() && paramSentryBaseEvent.getServerName() == null) {
      ensureHostnameCache();
      if (this.hostnameCache != null)
        paramSentryBaseEvent.setServerName(this.hostnameCache.getHostname()); 
    } 
  }
  
  private void ensureHostnameCache() {
    if (this.hostnameCache == null)
      synchronized (this) {
        if (this.hostnameCache == null)
          this.hostnameCache = HostnameCache.getInstance(); 
      }  
  }
  
  private void setDist(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getDist() == null)
      paramSentryBaseEvent.setDist(this.options.getDist()); 
  }
  
  private void setSdk(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getSdk() == null)
      paramSentryBaseEvent.setSdk(this.options.getSdkVersion()); 
  }
  
  private void setTags(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    if (paramSentryBaseEvent.getTags() == null) {
      paramSentryBaseEvent.setTags(new HashMap<>(this.options.getTags()));
    } else {
      for (Map.Entry<String, String> entry : this.options.getTags().entrySet()) {
        if (!paramSentryBaseEvent.getTags().containsKey(entry.getKey()))
          paramSentryBaseEvent.setTag((String)entry.getKey(), (String)entry.getValue()); 
      } 
    } 
  }
  
  private void mergeUser(@NotNull SentryBaseEvent paramSentryBaseEvent) {
    User user = paramSentryBaseEvent.getUser();
    if (user == null) {
      user = new User();
      paramSentryBaseEvent.setUser(user);
    } 
    if (user.getIpAddress() == null)
      user.setIpAddress("{{auto}}"); 
  }
  
  private void setExceptions(@NotNull SentryEvent paramSentryEvent) {
    Throwable throwable = paramSentryEvent.getThrowableMechanism();
    if (throwable != null)
      paramSentryEvent.setExceptions(this.sentryExceptionFactory.getSentryExceptions(throwable)); 
  }
  
  private void setThreads(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    if (paramSentryEvent.getThreads() == null) {
      ArrayList<Long> arrayList = null;
      List<SentryException> list = paramSentryEvent.getExceptions();
      if (list != null && !list.isEmpty())
        for (SentryException sentryException : list) {
          if (sentryException.getMechanism() != null && sentryException.getThreadId() != null) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(sentryException.getThreadId());
          } 
        }  
      if (this.options.isAttachThreads() || HintUtils.hasType(paramHint, AbnormalExit.class)) {
        Object object = HintUtils.getSentrySdkHint(paramHint);
        boolean bool = false;
        if (object instanceof AbnormalExit)
          bool = ((AbnormalExit)object).ignoreCurrentThread(); 
        paramSentryEvent.setThreads(this.sentryThreadFactory.getCurrentThreads(arrayList, bool));
      } else if (this.options.isAttachStacktrace() && (list == null || list.isEmpty()) && !isCachedHint(paramHint)) {
        paramSentryEvent.setThreads(this.sentryThreadFactory.getCurrentThread());
      } 
    } 
  }
  
  private boolean isCachedHint(@NotNull Hint paramHint) {
    return HintUtils.hasType(paramHint, Cached.class);
  }
  
  public void close() throws IOException {
    if (this.hostnameCache != null)
      this.hostnameCache.close(); 
  }
  
  boolean isClosed() {
    return (this.hostnameCache != null) ? this.hostnameCache.isClosed() : true;
  }
  
  @VisibleForTesting
  @Nullable
  HostnameCache getHostnameCache() {
    return this.hostnameCache;
  }
  
  @Nullable
  public Long getOrder() {
    return Long.valueOf(0L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MainEventProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */