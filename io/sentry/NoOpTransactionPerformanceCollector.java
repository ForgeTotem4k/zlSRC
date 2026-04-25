package io.sentry;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpTransactionPerformanceCollector implements TransactionPerformanceCollector {
  private static final NoOpTransactionPerformanceCollector instance = new NoOpTransactionPerformanceCollector();
  
  public static NoOpTransactionPerformanceCollector getInstance() {
    return instance;
  }
  
  public void start(@NotNull ITransaction paramITransaction) {}
  
  public void onSpanStarted(@NotNull ISpan paramISpan) {}
  
  public void onSpanFinished(@NotNull ISpan paramISpan) {}
  
  @Nullable
  public List<PerformanceCollectionData> stop(@NotNull ITransaction paramITransaction) {
    return null;
  }
  
  public void close() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpTransactionPerformanceCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */