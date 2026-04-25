package io.sentry.transport;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryLevel;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class QueuedThreadPoolExecutor extends ThreadPoolExecutor {
  private final int maxQueueSize;
  
  @Nullable
  private SentryDate lastRejectTimestamp = null;
  
  @NotNull
  private final ILogger logger;
  
  @NotNull
  private final SentryDateProvider dateProvider;
  
  @NotNull
  private final ReusableCountLatch unfinishedTasksCount = new ReusableCountLatch();
  
  private static final long RECENT_THRESHOLD = DateUtils.millisToNanos(2000L);
  
  public QueuedThreadPoolExecutor(int paramInt1, int paramInt2, @NotNull ThreadFactory paramThreadFactory, @NotNull RejectedExecutionHandler paramRejectedExecutionHandler, @NotNull ILogger paramILogger, @NotNull SentryDateProvider paramSentryDateProvider) {
    super(paramInt1, paramInt1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), paramThreadFactory, paramRejectedExecutionHandler);
    this.maxQueueSize = paramInt2;
    this.logger = paramILogger;
    this.dateProvider = paramSentryDateProvider;
  }
  
  public Future<?> submit(@NotNull Runnable paramRunnable) {
    if (isSchedulingAllowed()) {
      this.unfinishedTasksCount.increment();
      return super.submit(paramRunnable);
    } 
    this.lastRejectTimestamp = this.dateProvider.now();
    this.logger.log(SentryLevel.WARNING, "Submit cancelled", new Object[0]);
    return new CancelledFuture();
  }
  
  protected void afterExecute(@NotNull Runnable paramRunnable, @Nullable Throwable paramThrowable) {
    try {
      super.afterExecute(paramRunnable, paramThrowable);
    } finally {
      this.unfinishedTasksCount.decrement();
    } 
  }
  
  void waitTillIdle(long paramLong) {
    try {
      this.unfinishedTasksCount.waitTillZero(paramLong, TimeUnit.MILLISECONDS);
    } catch (InterruptedException interruptedException) {
      this.logger.log(SentryLevel.ERROR, "Failed to wait till idle", interruptedException);
      Thread.currentThread().interrupt();
    } 
  }
  
  public boolean isSchedulingAllowed() {
    return (this.unfinishedTasksCount.getCount() < this.maxQueueSize);
  }
  
  public boolean didRejectRecently() {
    SentryDate sentryDate = this.lastRejectTimestamp;
    if (sentryDate == null)
      return false; 
    long l = this.dateProvider.now().diff(sentryDate);
    return (l < RECENT_THRESHOLD);
  }
  
  static final class CancelledFuture<T> implements Future<T> {
    public boolean cancel(boolean param1Boolean) {
      return true;
    }
    
    public boolean isCancelled() {
      return true;
    }
    
    public boolean isDone() {
      return true;
    }
    
    public T get() {
      throw new CancellationException();
    }
    
    public T get(long param1Long, @NotNull TimeUnit param1TimeUnit) {
      throw new CancellationException();
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\QueuedThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */