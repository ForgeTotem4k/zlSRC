package io.sentry;

import io.sentry.util.Objects;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class HostnameCache {
  private static final long HOSTNAME_CACHE_DURATION = TimeUnit.HOURS.toMillis(5L);
  
  private static final long GET_HOSTNAME_TIMEOUT = TimeUnit.SECONDS.toMillis(1L);
  
  @Nullable
  private static HostnameCache INSTANCE;
  
  private final long cacheDuration;
  
  @Nullable
  private volatile String hostname;
  
  private volatile long expirationTimestamp;
  
  @NotNull
  private final AtomicBoolean updateRunning = new AtomicBoolean(false);
  
  @NotNull
  private final Callable<InetAddress> getLocalhost;
  
  @NotNull
  private final ExecutorService executorService = Executors.newSingleThreadExecutor(new HostnameCacheThreadFactory());
  
  @NotNull
  static HostnameCache getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HostnameCache(); 
    return INSTANCE;
  }
  
  private HostnameCache() {
    this(HOSTNAME_CACHE_DURATION);
  }
  
  HostnameCache(long paramLong) {
    this(paramLong, () -> InetAddress.getLocalHost());
  }
  
  HostnameCache(long paramLong, @NotNull Callable<InetAddress> paramCallable) {
    this.cacheDuration = paramLong;
    this.getLocalhost = (Callable<InetAddress>)Objects.requireNonNull(paramCallable, "getLocalhost is required");
    updateCache();
  }
  
  void close() {
    this.executorService.shutdown();
  }
  
  boolean isClosed() {
    return this.executorService.isShutdown();
  }
  
  @Nullable
  String getHostname() {
    if (this.expirationTimestamp < System.currentTimeMillis() && this.updateRunning.compareAndSet(false, true))
      updateCache(); 
    return this.hostname;
  }
  
  private void updateCache() {
    Callable<?> callable = () -> {
        try {
          this.hostname = ((InetAddress)this.getLocalhost.call()).getCanonicalHostName();
          this.expirationTimestamp = System.currentTimeMillis() + this.cacheDuration;
        } finally {
          this.updateRunning.set(false);
        } 
        return null;
      };
    try {
      Future<?> future = this.executorService.submit(callable);
      future.get(GET_HOSTNAME_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      handleCacheUpdateFailure();
    } catch (ExecutionException|java.util.concurrent.TimeoutException|RuntimeException executionException) {
      handleCacheUpdateFailure();
    } 
  }
  
  private void handleCacheUpdateFailure() {
    this.expirationTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1L);
  }
  
  private static final class HostnameCacheThreadFactory implements ThreadFactory {
    private int cnt;
    
    private HostnameCacheThreadFactory() {}
    
    @NotNull
    public Thread newThread(@NotNull Runnable param1Runnable) {
      Thread thread = new Thread(param1Runnable, "SentryHostnameCache-" + this.cnt++);
      thread.setDaemon(true);
      return thread;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\HostnameCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */