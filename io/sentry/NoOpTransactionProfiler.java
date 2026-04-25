package io.sentry;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpTransactionProfiler implements ITransactionProfiler {
  private static final NoOpTransactionProfiler instance = new NoOpTransactionProfiler();
  
  public static NoOpTransactionProfiler getInstance() {
    return instance;
  }
  
  public void start() {}
  
  public boolean isRunning() {
    return false;
  }
  
  public void bindTransaction(@NotNull ITransaction paramITransaction) {}
  
  @Nullable
  public ProfilingTraceData onTransactionFinish(@NotNull ITransaction paramITransaction, @Nullable List<PerformanceCollectionData> paramList, @NotNull SentryOptions paramSentryOptions) {
    return null;
  }
  
  public void close() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpTransactionProfiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */