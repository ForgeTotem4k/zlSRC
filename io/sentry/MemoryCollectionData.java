package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MemoryCollectionData {
  final long timestampMillis;
  
  final long usedHeapMemory;
  
  final long usedNativeMemory;
  
  public MemoryCollectionData(long paramLong1, long paramLong2, long paramLong3) {
    this.timestampMillis = paramLong1;
    this.usedHeapMemory = paramLong2;
    this.usedNativeMemory = paramLong3;
  }
  
  public MemoryCollectionData(long paramLong1, long paramLong2) {
    this(paramLong1, paramLong2, -1L);
  }
  
  public long getTimestampMillis() {
    return this.timestampMillis;
  }
  
  public long getUsedHeapMemory() {
    return this.usedHeapMemory;
  }
  
  public long getUsedNativeMemory() {
    return this.usedNativeMemory;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MemoryCollectionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */