package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatCpu {
  private static final Perfstat PERF = Perfstat.INSTANCE;
  
  public static Perfstat.perfstat_cpu_total_t queryCpuTotal() {
    Perfstat.perfstat_cpu_total_t perfstat_cpu_total_t = new Perfstat.perfstat_cpu_total_t();
    int i = PERF.perfstat_cpu_total(null, perfstat_cpu_total_t, perfstat_cpu_total_t.size(), 1);
    return (i > 0) ? perfstat_cpu_total_t : new Perfstat.perfstat_cpu_total_t();
  }
  
  public static Perfstat.perfstat_cpu_t[] queryCpu() {
    Perfstat.perfstat_cpu_t perfstat_cpu_t = new Perfstat.perfstat_cpu_t();
    int i = PERF.perfstat_cpu(null, null, perfstat_cpu_t.size(), 0);
    if (i > 0) {
      Perfstat.perfstat_cpu_t[] arrayOfPerfstat_cpu_t = (Perfstat.perfstat_cpu_t[])perfstat_cpu_t.toArray(i);
      Perfstat.perfstat_id_t perfstat_id_t = new Perfstat.perfstat_id_t();
      int j = PERF.perfstat_cpu(perfstat_id_t, arrayOfPerfstat_cpu_t, perfstat_cpu_t.size(), i);
      if (j > 0)
        return arrayOfPerfstat_cpu_t; 
    } 
    return new Perfstat.perfstat_cpu_t[0];
  }
  
  public static long queryCpuAffinityMask() {
    int i = (queryCpuTotal()).ncpus;
    return (i < 63) ? ((1L << i) - 1L) : ((i == 63) ? Long.MAX_VALUE : -1L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\perfstat\PerfstatCpu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */