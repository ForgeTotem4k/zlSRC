package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.MemoryInformation;
import oshi.driver.windows.perfmon.PagingFile;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.jna.Struct;
import oshi.util.Memoizer;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class WindowsVirtualMemory extends AbstractVirtualMemory {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsVirtualMemory.class);
  
  private final WindowsGlobalMemory global;
  
  private final Supplier<Long> used = Memoizer.memoize(WindowsVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
  
  private final Supplier<Triplet<Long, Long, Long>> totalVmaxVused = Memoizer.memoize(WindowsVirtualMemory::querySwapTotalVirtMaxVirtUsed, Memoizer.defaultExpiration());
  
  private final Supplier<Pair<Long, Long>> swapInOut = Memoizer.memoize(WindowsVirtualMemory::queryPageSwaps, Memoizer.defaultExpiration());
  
  WindowsVirtualMemory(WindowsGlobalMemory paramWindowsGlobalMemory) {
    this.global = paramWindowsGlobalMemory;
  }
  
  public long getSwapUsed() {
    return this.global.getPageSize() * ((Long)this.used.get()).longValue();
  }
  
  public long getSwapTotal() {
    return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getA()).longValue();
  }
  
  public long getVirtualMax() {
    return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getB()).longValue();
  }
  
  public long getVirtualInUse() {
    return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getC()).longValue();
  }
  
  public long getSwapPagesIn() {
    return ((Long)((Pair)this.swapInOut.get()).getA()).longValue();
  }
  
  public long getSwapPagesOut() {
    return ((Long)((Pair)this.swapInOut.get()).getB()).longValue();
  }
  
  private static long querySwapUsed() {
    return ((Long)PagingFile.querySwapUsed().getOrDefault(PagingFile.PagingPercentProperty.PERCENTUSAGE, Long.valueOf(0L))).longValue();
  }
  
  private static Triplet<Long, Long, Long> querySwapTotalVirtMaxVirtUsed() {
    Struct.CloseablePerformanceInformation closeablePerformanceInformation = new Struct.CloseablePerformanceInformation();
    try {
      if (!Psapi.INSTANCE.GetPerformanceInfo((Psapi.PERFORMANCE_INFORMATION)closeablePerformanceInformation, closeablePerformanceInformation.size())) {
        LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
        Triplet<Long, Long, Long> triplet1 = new Triplet(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(0L));
        closeablePerformanceInformation.close();
        return triplet1;
      } 
      Triplet<Long, Long, Long> triplet = new Triplet(Long.valueOf(closeablePerformanceInformation.CommitLimit.longValue() - closeablePerformanceInformation.PhysicalTotal.longValue()), Long.valueOf(closeablePerformanceInformation.CommitLimit.longValue()), Long.valueOf(closeablePerformanceInformation.CommitTotal.longValue()));
      closeablePerformanceInformation.close();
      return triplet;
    } catch (Throwable throwable) {
      try {
        closeablePerformanceInformation.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  private static Pair<Long, Long> queryPageSwaps() {
    Map map = MemoryInformation.queryPageSwaps();
    return new Pair(map.getOrDefault(MemoryInformation.PageSwapProperty.PAGESINPUTPERSEC, Long.valueOf(0L)), map.getOrDefault(MemoryInformation.PageSwapProperty.PAGESOUTPUTPERSEC, Long.valueOf(0L)));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */