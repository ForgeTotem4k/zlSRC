package oshi.hardware.platform.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;

@ThreadSafe
final class AixVirtualMemory extends AbstractVirtualMemory {
  private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem;
  
  private static final long PAGESIZE = 4096L;
  
  AixVirtualMemory(Supplier<Perfstat.perfstat_memory_total_t> paramSupplier) {
    this.perfstatMem = paramSupplier;
  }
  
  public long getSwapUsed() {
    Perfstat.perfstat_memory_total_t perfstat_memory_total_t = this.perfstatMem.get();
    return (perfstat_memory_total_t.pgsp_total - perfstat_memory_total_t.pgsp_free) * 4096L;
  }
  
  public long getSwapTotal() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgsp_total * 4096L;
  }
  
  public long getVirtualMax() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_total * 4096L;
  }
  
  public long getVirtualInUse() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_active * 4096L;
  }
  
  public long getSwapPagesIn() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspins;
  }
  
  public long getSwapPagesOut() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspouts;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */