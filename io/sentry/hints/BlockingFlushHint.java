package io.sentry.hints;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public abstract class BlockingFlushHint implements DiskFlushNotification, Flushable {
  private final CountDownLatch latch;
  
  private final long flushTimeoutMillis;
  
  @NotNull
  private final ILogger logger;
  
  public BlockingFlushHint(long paramLong, @NotNull ILogger paramILogger) {
    this.flushTimeoutMillis = paramLong;
    this.latch = new CountDownLatch(1);
    this.logger = paramILogger;
  }
  
  public boolean waitFlush() {
    try {
      return this.latch.await(this.flushTimeoutMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      this.logger.log(SentryLevel.ERROR, "Exception while awaiting for flush in BlockingFlushHint", interruptedException);
      return false;
    } 
  }
  
  public void markFlushed() {
    this.latch.countDown();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\hints\BlockingFlushHint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */