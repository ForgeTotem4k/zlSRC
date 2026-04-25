package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatMemory {
  private static final Perfstat PERF = Perfstat.INSTANCE;
  
  public static Perfstat.perfstat_memory_total_t queryMemoryTotal() {
    Perfstat.perfstat_memory_total_t perfstat_memory_total_t = new Perfstat.perfstat_memory_total_t();
    int i = PERF.perfstat_memory_total(null, perfstat_memory_total_t, perfstat_memory_total_t.size(), 1);
    return (i > 0) ? perfstat_memory_total_t : new Perfstat.perfstat_memory_total_t();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\perfstat\PerfstatMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */