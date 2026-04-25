package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
final class FreeBsdGlobalMemory extends AbstractGlobalMemory {
  private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
  
  private final Supplier<Long> total = Memoizer.memoize(FreeBsdGlobalMemory::queryPhysMem);
  
  private final Supplier<Long> pageSize = Memoizer.memoize(FreeBsdGlobalMemory::queryPageSize);
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  public long getAvailable() {
    return ((Long)this.available.get()).longValue();
  }
  
  public long getTotal() {
    return ((Long)this.total.get()).longValue();
  }
  
  public long getPageSize() {
    return ((Long)this.pageSize.get()).longValue();
  }
  
  public VirtualMemory getVirtualMemory() {
    return this.vm.get();
  }
  
  private long queryVmStats() {
    int i = BsdSysctlUtil.sysctl("vm.stats.vm.v_inactive_count", 0);
    int j = BsdSysctlUtil.sysctl("vm.stats.vm.v_free_count", 0);
    return (i + j) * getPageSize();
  }
  
  private static long queryPhysMem() {
    return BsdSysctlUtil.sysctl("hw.physmem", 0L);
  }
  
  private static long queryPageSize() {
    return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysconf PAGESIZE"), 4096L);
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new FreeBsdVirtualMemory(this);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */