package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatDisk {
  private static final Perfstat PERF = Perfstat.INSTANCE;
  
  public static Perfstat.perfstat_disk_t[] queryDiskStats() {
    Perfstat.perfstat_disk_t perfstat_disk_t = new Perfstat.perfstat_disk_t();
    int i = PERF.perfstat_disk(null, null, perfstat_disk_t.size(), 0);
    if (i > 0) {
      Perfstat.perfstat_disk_t[] arrayOfPerfstat_disk_t = (Perfstat.perfstat_disk_t[])perfstat_disk_t.toArray(i);
      Perfstat.perfstat_id_t perfstat_id_t = new Perfstat.perfstat_id_t();
      int j = PERF.perfstat_disk(perfstat_id_t, arrayOfPerfstat_disk_t, perfstat_disk_t.size(), i);
      if (j > 0)
        return arrayOfPerfstat_disk_t; 
    } 
    return new Perfstat.perfstat_disk_t[0];
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\perfstat\PerfstatDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */