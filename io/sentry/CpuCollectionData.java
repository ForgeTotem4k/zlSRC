package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class CpuCollectionData {
  final long timestampMillis;
  
  final double cpuUsagePercentage;
  
  public CpuCollectionData(long paramLong, double paramDouble) {
    this.timestampMillis = paramLong;
    this.cpuUsagePercentage = paramDouble;
  }
  
  public long getTimestampMillis() {
    return this.timestampMillis;
  }
  
  public double getCpuUsagePercentage() {
    return this.cpuUsagePercentage;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CpuCollectionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */