package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface IPerformanceSnapshotCollector extends IPerformanceCollector {
  void setup();
  
  void collect(@NotNull PerformanceCollectionData paramPerformanceCollectionData);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IPerformanceSnapshotCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */