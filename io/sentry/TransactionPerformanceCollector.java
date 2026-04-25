package io.sentry;

import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TransactionPerformanceCollector {
  void start(@NotNull ITransaction paramITransaction);
  
  void onSpanStarted(@NotNull ISpan paramISpan);
  
  void onSpanFinished(@NotNull ISpan paramISpan);
  
  @Nullable
  List<PerformanceCollectionData> stop(@NotNull ITransaction paramITransaction);
  
  @Internal
  void close();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TransactionPerformanceCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */