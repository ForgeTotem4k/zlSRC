package oshi.driver.windows.perfmon;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class PhysicalDisk {
  public static Pair<List<String>, Map<PhysicalDiskProperty, List<Long>>> queryDiskCounters() {
    return PerfmonDisabled.PERF_DISK_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(PhysicalDiskProperty.class, "PhysicalDisk", "Win32_PerfRawData_PerfDisk_PhysicalDisk WHERE Name!=\"_Total\"");
  }
  
  public enum PhysicalDiskProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^_Total"),
    DISKREADSPERSEC("Disk Reads/sec"),
    DISKREADBYTESPERSEC("Disk Read Bytes/sec"),
    DISKWRITESPERSEC("Disk Writes/sec"),
    DISKWRITEBYTESPERSEC("Disk Write Bytes/sec"),
    CURRENTDISKQUEUELENGTH("Current Disk Queue Length"),
    PERCENTDISKTIME("% Disk Time");
    
    private final String counter;
    
    PhysicalDiskProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\PhysicalDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */