package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class PerformanceCollectionData {
  @Nullable
  private MemoryCollectionData memoryData = null;
  
  @Nullable
  private CpuCollectionData cpuData = null;
  
  public void addMemoryData(@Nullable MemoryCollectionData paramMemoryCollectionData) {
    if (paramMemoryCollectionData != null)
      this.memoryData = paramMemoryCollectionData; 
  }
  
  public void addCpuData(@Nullable CpuCollectionData paramCpuCollectionData) {
    if (paramCpuCollectionData != null)
      this.cpuData = paramCpuCollectionData; 
  }
  
  @Nullable
  public CpuCollectionData getCpuData() {
    return this.cpuData;
  }
  
  @Nullable
  public MemoryCollectionData getMemoryData() {
    return this.memoryData;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\PerformanceCollectionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */