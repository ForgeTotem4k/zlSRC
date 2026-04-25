package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
final class FreeBsdVirtualMemory extends AbstractVirtualMemory {
  private final FreeBsdGlobalMemory global;
  
  private final Supplier<Long> used = Memoizer.memoize(FreeBsdVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
  
  private final Supplier<Long> total = Memoizer.memoize(FreeBsdVirtualMemory::querySwapTotal, Memoizer.defaultExpiration());
  
  private final Supplier<Long> pagesIn = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
  
  private final Supplier<Long> pagesOut = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());
  
  FreeBsdVirtualMemory(FreeBsdGlobalMemory paramFreeBsdGlobalMemory) {
    this.global = paramFreeBsdGlobalMemory;
  }
  
  public long getSwapUsed() {
    return ((Long)this.used.get()).longValue();
  }
  
  public long getSwapTotal() {
    return ((Long)this.total.get()).longValue();
  }
  
  public long getVirtualMax() {
    return this.global.getTotal() + getSwapTotal();
  }
  
  public long getVirtualInUse() {
    return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
  }
  
  public long getSwapPagesIn() {
    return ((Long)this.pagesIn.get()).longValue();
  }
  
  public long getSwapPagesOut() {
    return ((Long)this.pagesOut.get()).longValue();
  }
  
  private static long querySwapUsed() {
    String str = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
    String[] arrayOfString = ParseUtil.whitespaces.split(str);
    return (arrayOfString.length < 5) ? 0L : (ParseUtil.parseLongOrDefault(arrayOfString[2], 0L) << 10L);
  }
  
  private static long querySwapTotal() {
    return BsdSysctlUtil.sysctl("vm.swap_total", 0L);
  }
  
  private static long queryPagesIn() {
    return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsin", 0L);
  }
  
  private static long queryPagesOut() {
    return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsout", 0L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */