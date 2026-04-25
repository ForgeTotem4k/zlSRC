package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class JavaMemoryCollector implements IPerformanceSnapshotCollector {
  @NotNull
  private final Runtime runtime = Runtime.getRuntime();
  
  public void setup() {}
  
  public void collect(@NotNull PerformanceCollectionData paramPerformanceCollectionData) {
    long l1 = System.currentTimeMillis();
    long l2 = this.runtime.totalMemory() - this.runtime.freeMemory();
    MemoryCollectionData memoryCollectionData = new MemoryCollectionData(l1, l2);
    paramPerformanceCollectionData.addMemoryData(memoryCollectionData);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JavaMemoryCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */