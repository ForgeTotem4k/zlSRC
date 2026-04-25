package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class MacVirtualMemory extends AbstractVirtualMemory {
  private static final Logger LOG = LoggerFactory.getLogger(MacVirtualMemory.class);
  
  private final MacGlobalMemory global;
  
  private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(MacVirtualMemory::querySwapUsage, Memoizer.defaultExpiration());
  
  private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(MacVirtualMemory::queryVmStat, Memoizer.defaultExpiration());
  
  MacVirtualMemory(MacGlobalMemory paramMacGlobalMemory) {
    this.global = paramMacGlobalMemory;
  }
  
  public long getSwapUsed() {
    return ((Long)((Pair)this.usedTotal.get()).getA()).longValue();
  }
  
  public long getSwapTotal() {
    return ((Long)((Pair)this.usedTotal.get()).getB()).longValue();
  }
  
  public long getVirtualMax() {
    return this.global.getTotal() + getSwapTotal();
  }
  
  public long getVirtualInUse() {
    return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
  }
  
  public long getSwapPagesIn() {
    return ((Long)((Pair)this.inOut.get()).getA()).longValue();
  }
  
  public long getSwapPagesOut() {
    return ((Long)((Pair)this.inOut.get()).getB()).longValue();
  }
  
  private static Pair<Long, Long> querySwapUsage() {
    long l1 = 0L;
    long l2 = 0L;
    Struct.CloseableXswUsage closeableXswUsage = new Struct.CloseableXswUsage();
    try {
      if (SysctlUtil.sysctl("vm.swapusage", (Structure)closeableXswUsage)) {
        l1 = closeableXswUsage.xsu_used;
        l2 = closeableXswUsage.xsu_total;
      } 
      closeableXswUsage.close();
    } catch (Throwable throwable) {
      try {
        closeableXswUsage.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  private static Pair<Long, Long> queryVmStat() {
    long l1 = 0L;
    long l2 = 0L;
    Struct.CloseableVMStatistics closeableVMStatistics = new Struct.CloseableVMStatistics();
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(closeableVMStatistics.size() / SystemB.INT_SIZE);
      try {
        if (0 == SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, (Structure)closeableVMStatistics, (IntByReference)closeableIntByReference)) {
          l1 = ParseUtil.unsignedIntToLong(closeableVMStatistics.pageins);
          l2 = ParseUtil.unsignedIntToLong(closeableVMStatistics.pageouts);
        } else {
          LOG.error("Failed to get host VM info. Error code: {}", Integer.valueOf(Native.getLastError()));
        } 
        closeableIntByReference.close();
      } catch (Throwable throwable) {
        try {
          closeableIntByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      closeableVMStatistics.close();
    } catch (Throwable throwable) {
      try {
        closeableVMStatistics.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */