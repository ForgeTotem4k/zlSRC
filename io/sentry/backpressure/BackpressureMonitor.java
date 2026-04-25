package io.sentry.backpressure;

import io.sentry.IScopes;
import io.sentry.ISentryExecutorService;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;

public final class BackpressureMonitor implements IBackpressureMonitor, Runnable {
  static final int MAX_DOWNSAMPLE_FACTOR = 10;
  
  private static final int INITIAL_CHECK_DELAY_IN_MS = 500;
  
  private static final int CHECK_INTERVAL_IN_MS = 10000;
  
  @NotNull
  private final SentryOptions sentryOptions;
  
  @NotNull
  private final IScopes scopes;
  
  private int downsampleFactor = 0;
  
  public BackpressureMonitor(@NotNull SentryOptions paramSentryOptions, @NotNull IScopes paramIScopes) {
    this.sentryOptions = paramSentryOptions;
    this.scopes = paramIScopes;
  }
  
  public void start() {
    reschedule(500);
  }
  
  public void run() {
    checkHealth();
    reschedule(10000);
  }
  
  public int getDownsampleFactor() {
    return this.downsampleFactor;
  }
  
  void checkHealth() {
    if (isHealthy()) {
      if (this.downsampleFactor > 0)
        this.sentryOptions.getLogger().log(SentryLevel.DEBUG, "Health check positive, reverting to normal sampling.", new Object[0]); 
      this.downsampleFactor = 0;
    } else if (this.downsampleFactor < 10) {
      this.downsampleFactor++;
      this.sentryOptions.getLogger().log(SentryLevel.DEBUG, "Health check negative, downsampling with a factor of %d", new Object[] { Integer.valueOf(this.downsampleFactor) });
    } 
  }
  
  private void reschedule(int paramInt) {
    ISentryExecutorService iSentryExecutorService = this.sentryOptions.getExecutorService();
    if (!iSentryExecutorService.isClosed())
      iSentryExecutorService.schedule(this, paramInt); 
  }
  
  private boolean isHealthy() {
    return this.scopes.isHealthy();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\backpressure\BackpressureMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */