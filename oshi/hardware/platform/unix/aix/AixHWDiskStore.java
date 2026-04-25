package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Ls;
import oshi.driver.unix.aix.Lscfg;
import oshi.driver.unix.aix.Lspv;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class AixHWDiskStore extends AbstractHWDiskStore {
  private final Supplier<Perfstat.perfstat_disk_t[]> diskStats;
  
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private AixHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong, Supplier<Perfstat.perfstat_disk_t[]> paramSupplier) {
    super(paramString1, paramString2, paramString3, paramLong);
    this.diskStats = paramSupplier;
  }
  
  public synchronized long getReads() {
    return this.reads;
  }
  
  public synchronized long getReadBytes() {
    return this.readBytes;
  }
  
  public synchronized long getWrites() {
    return this.writes;
  }
  
  public synchronized long getWriteBytes() {
    return this.writeBytes;
  }
  
  public synchronized long getCurrentQueueLength() {
    return this.currentQueueLength;
  }
  
  public synchronized long getTransferTime() {
    return this.transferTime;
  }
  
  public synchronized long getTimeStamp() {
    return this.timeStamp;
  }
  
  public List<HWPartition> getPartitions() {
    return this.partitionList;
  }
  
  public synchronized boolean updateAttributes() {
    long l = System.currentTimeMillis();
    for (Perfstat.perfstat_disk_t perfstat_disk_t : (Perfstat.perfstat_disk_t[])this.diskStats.get()) {
      String str = Native.toString(perfstat_disk_t.name);
      if (str.equals(getName())) {
        long l1 = perfstat_disk_t.rblks + perfstat_disk_t.wblks;
        if (l1 == 0L) {
          this.reads = perfstat_disk_t.xfers;
          this.writes = 0L;
        } else {
          long l2 = Math.round((perfstat_disk_t.xfers * perfstat_disk_t.rblks) / l1);
          long l3 = perfstat_disk_t.xfers - l2;
          if (l2 > this.reads)
            this.reads = l2; 
          if (l3 > this.writes)
            this.writes = l3; 
        } 
        this.readBytes = perfstat_disk_t.rblks * perfstat_disk_t.bsize;
        this.writeBytes = perfstat_disk_t.wblks * perfstat_disk_t.bsize;
        this.currentQueueLength = perfstat_disk_t.qdepth;
        this.transferTime = perfstat_disk_t.time;
        this.timeStamp = l;
        return true;
      } 
    } 
    return false;
  }
  
  public static List<HWDiskStore> getDisks(Supplier<Perfstat.perfstat_disk_t[]> paramSupplier) {
    Map<String, Pair<Integer, Integer>> map = Ls.queryDeviceMajorMinor();
    ArrayList<AixHWDiskStore> arrayList = new ArrayList();
    for (Perfstat.perfstat_disk_t perfstat_disk_t : (Perfstat.perfstat_disk_t[])paramSupplier.get()) {
      String str1 = Native.toString(perfstat_disk_t.name);
      Pair pair = Lscfg.queryModelSerial(str1);
      String str2 = (pair.getA() == null) ? Native.toString(perfstat_disk_t.description) : (String)pair.getA();
      String str3 = (pair.getB() == null) ? "unknown" : (String)pair.getB();
      arrayList.add(createStore(str1, str2, str3, perfstat_disk_t.size << 20L, paramSupplier, map));
    } 
    return (List<HWDiskStore>)arrayList.stream().sorted(Comparator.comparingInt(paramAixHWDiskStore -> paramAixHWDiskStore.getPartitions().isEmpty() ? Integer.MAX_VALUE : ((HWPartition)paramAixHWDiskStore.getPartitions().get(0)).getMajor())).collect(Collectors.toList());
  }
  
  private static AixHWDiskStore createStore(String paramString1, String paramString2, String paramString3, long paramLong, Supplier<Perfstat.perfstat_disk_t[]> paramSupplier, Map<String, Pair<Integer, Integer>> paramMap) {
    AixHWDiskStore aixHWDiskStore = new AixHWDiskStore(paramString1, paramString2.isEmpty() ? "unknown" : paramString2, paramString3, paramLong, paramSupplier);
    aixHWDiskStore.partitionList = Lspv.queryLogicalVolumes(paramString1, paramMap);
    aixHWDiskStore.updateAttributes();
    return aixHWDiskStore;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */