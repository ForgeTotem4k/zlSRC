package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatConfig {
  private static final Perfstat PERF = Perfstat.INSTANCE;
  
  public static Perfstat.perfstat_partition_config_t queryConfig() {
    Perfstat.perfstat_partition_config_t perfstat_partition_config_t = new Perfstat.perfstat_partition_config_t();
    int i = PERF.perfstat_partition_config(null, perfstat_partition_config_t, perfstat_partition_config_t.size(), 1);
    return (i > 0) ? perfstat_partition_config_t : new Perfstat.perfstat_partition_config_t();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\perfstat\PerfstatConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */