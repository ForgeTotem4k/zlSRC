package io.sentry;

import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DefaultTransactionPerformanceCollector implements TransactionPerformanceCollector {
  private static final long TRANSACTION_COLLECTION_INTERVAL_MILLIS = 100L;
  
  private static final long TRANSACTION_COLLECTION_TIMEOUT_MILLIS = 30000L;
  
  @NotNull
  private final Object timerLock = new Object();
  
  @Nullable
  private volatile Timer timer = null;
  
  @NotNull
  private final Map<String, List<PerformanceCollectionData>> performanceDataMap = new ConcurrentHashMap<>();
  
  @NotNull
  private final List<IPerformanceSnapshotCollector> snapshotCollectors;
  
  @NotNull
  private final List<IPerformanceContinuousCollector> continuousCollectors;
  
  private final boolean hasNoCollectors;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final AtomicBoolean isStarted = new AtomicBoolean(false);
  
  private long lastCollectionTimestamp = 0L;
  
  public DefaultTransactionPerformanceCollector(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "The options object is required.");
    this.snapshotCollectors = new ArrayList<>();
    this.continuousCollectors = new ArrayList<>();
    List<IPerformanceCollector> list = paramSentryOptions.getPerformanceCollectors();
    for (IPerformanceCollector iPerformanceCollector : list) {
      if (iPerformanceCollector instanceof IPerformanceSnapshotCollector)
        this.snapshotCollectors.add((IPerformanceSnapshotCollector)iPerformanceCollector); 
      if (iPerformanceCollector instanceof IPerformanceContinuousCollector)
        this.continuousCollectors.add((IPerformanceContinuousCollector)iPerformanceCollector); 
    } 
    this.hasNoCollectors = (this.snapshotCollectors.isEmpty() && this.continuousCollectors.isEmpty());
  }
  
  public void start(@NotNull ITransaction paramITransaction) {
    if (this.hasNoCollectors) {
      this.options.getLogger().log(SentryLevel.INFO, "No collector found. Performance stats will not be captured during transactions.", new Object[0]);
      return;
    } 
    for (IPerformanceContinuousCollector iPerformanceContinuousCollector : this.continuousCollectors)
      iPerformanceContinuousCollector.onSpanStarted(paramITransaction); 
    if (!this.performanceDataMap.containsKey(paramITransaction.getEventId().toString())) {
      this.performanceDataMap.put(paramITransaction.getEventId().toString(), new ArrayList<>());
      try {
        this.options.getExecutorService().schedule(() -> stop(paramITransaction), 30000L);
      } catch (RejectedExecutionException rejectedExecutionException) {
        this.options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Performance collector will not be automatically finished. Did you call Sentry.close()?", rejectedExecutionException);
      } 
    } 
    if (!this.isStarted.getAndSet(true))
      synchronized (this.timerLock) {
        if (this.timer == null)
          this.timer = new Timer(true); 
        this.timer.schedule(new TimerTask() {
              public void run() {
                for (IPerformanceSnapshotCollector iPerformanceSnapshotCollector : DefaultTransactionPerformanceCollector.this.snapshotCollectors)
                  iPerformanceSnapshotCollector.setup(); 
              }
            },  0L);
        TimerTask timerTask = new TimerTask() {
            public void run() {
              long l = System.currentTimeMillis();
              if (l - DefaultTransactionPerformanceCollector.this.lastCollectionTimestamp < 10L)
                return; 
              DefaultTransactionPerformanceCollector.this.lastCollectionTimestamp = l;
              PerformanceCollectionData performanceCollectionData = new PerformanceCollectionData();
              for (IPerformanceSnapshotCollector iPerformanceSnapshotCollector : DefaultTransactionPerformanceCollector.this.snapshotCollectors)
                iPerformanceSnapshotCollector.collect(performanceCollectionData); 
              for (List<PerformanceCollectionData> list : (Iterable<List<PerformanceCollectionData>>)DefaultTransactionPerformanceCollector.this.performanceDataMap.values())
                list.add(performanceCollectionData); 
            }
          };
        this.timer.scheduleAtFixedRate(timerTask, 100L, 100L);
      }  
  }
  
  public void onSpanStarted(@NotNull ISpan paramISpan) {
    for (IPerformanceContinuousCollector iPerformanceContinuousCollector : this.continuousCollectors)
      iPerformanceContinuousCollector.onSpanStarted(paramISpan); 
  }
  
  public void onSpanFinished(@NotNull ISpan paramISpan) {
    for (IPerformanceContinuousCollector iPerformanceContinuousCollector : this.continuousCollectors)
      iPerformanceContinuousCollector.onSpanFinished(paramISpan); 
  }
  
  @Nullable
  public List<PerformanceCollectionData> stop(@NotNull ITransaction paramITransaction) {
    this.options.getLogger().log(SentryLevel.DEBUG, "stop collecting performance info for transactions %s (%s)", new Object[] { paramITransaction.getName(), paramITransaction.getSpanContext().getTraceId().toString() });
    List<PerformanceCollectionData> list = this.performanceDataMap.remove(paramITransaction.getEventId().toString());
    for (IPerformanceContinuousCollector iPerformanceContinuousCollector : this.continuousCollectors)
      iPerformanceContinuousCollector.onSpanFinished(paramITransaction); 
    if (this.performanceDataMap.isEmpty())
      close(); 
    return list;
  }
  
  public void close() {
    this.options.getLogger().log(SentryLevel.DEBUG, "stop collecting all performance info for transactions", new Object[0]);
    this.performanceDataMap.clear();
    for (IPerformanceContinuousCollector iPerformanceContinuousCollector : this.continuousCollectors)
      iPerformanceContinuousCollector.clear(); 
    if (this.isStarted.getAndSet(false))
      synchronized (this.timerLock) {
        if (this.timer != null) {
          this.timer.cancel();
          this.timer = null;
        } 
      }  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DefaultTransactionPerformanceCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */