package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.disk.Iostat;
import oshi.driver.unix.solaris.disk.Lshal;
import oshi.driver.unix.solaris.disk.Prtvtoc;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class SolarisHWDiskStore extends AbstractHWDiskStore {
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private SolarisHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
    super(paramString1, paramString2, paramString3, paramLong);
  }
  
  public long getReads() {
    return this.reads;
  }
  
  public long getReadBytes() {
    return this.readBytes;
  }
  
  public long getWrites() {
    return this.writes;
  }
  
  public long getWriteBytes() {
    return this.writeBytes;
  }
  
  public long getCurrentQueueLength() {
    return this.currentQueueLength;
  }
  
  public long getTransferTime() {
    return this.transferTime;
  }
  
  public long getTimeStamp() {
    return this.timeStamp;
  }
  
  public List<HWPartition> getPartitions() {
    return this.partitionList;
  }
  
  public boolean updateAttributes() {
    this.timeStamp = System.currentTimeMillis();
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return updateAttributes2(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup(null, 0, getName());
      if (kstat != null && kstatChain.read(kstat)) {
        LibKstat.KstatIO kstatIO = new LibKstat.KstatIO(kstat.ks_data);
        this.reads = kstatIO.reads;
        this.writes = kstatIO.writes;
        this.readBytes = kstatIO.nread;
        this.writeBytes = kstatIO.nwritten;
        this.currentQueueLength = kstatIO.wcnt + kstatIO.rcnt;
        this.transferTime = kstatIO.rtime / 1000000L;
        this.timeStamp = kstat.ks_snaptime / 1000000L;
        boolean bool = true;
        if (kstatChain != null)
          kstatChain.close(); 
        return bool;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return false;
  }
  
  private boolean updateAttributes2() {
    String str1 = getName();
    String str2 = str1;
    String str3 = "";
    for (byte b = 0; b < str1.length(); b++) {
      if (str1.charAt(b) >= '0' && str1.charAt(b) <= '9') {
        str2 = str1.substring(0, b);
        str3 = str1.substring(b);
        break;
      } 
    } 
    Object[] arrayOfObject = KstatUtil.queryKstat2("kstat:/disk/" + str2 + "/" + getName() + "/0", new String[] { "reads", "writes", "nread", "nwritten", "wcnt", "rcnt", "rtime", "snaptime" });
    if (arrayOfObject[arrayOfObject.length - 1] == null)
      arrayOfObject = KstatUtil.queryKstat2("kstat:/disk/" + str2 + "/" + str3 + "/io", new String[] { "reads", "writes", "nread", "nwritten", "wcnt", "rcnt", "rtime", "snaptime" }); 
    if (arrayOfObject[arrayOfObject.length - 1] == null)
      return false; 
    this.reads = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
    this.writes = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
    this.readBytes = (arrayOfObject[2] == null) ? 0L : ((Long)arrayOfObject[2]).longValue();
    this.writeBytes = (arrayOfObject[3] == null) ? 0L : ((Long)arrayOfObject[3]).longValue();
    this.currentQueueLength = (arrayOfObject[4] == null) ? 0L : ((Long)arrayOfObject[4]).longValue();
    this.currentQueueLength += (arrayOfObject[5] == null) ? 0L : ((Long)arrayOfObject[5]).longValue();
    this.transferTime = (arrayOfObject[6] == null) ? 0L : (((Long)arrayOfObject[6]).longValue() / 1000000L);
    this.timeStamp = ((Long)arrayOfObject[7]).longValue() / 1000000L;
    return true;
  }
  
  public static List<HWDiskStore> getDisks() {
    Map map1 = Iostat.queryPartitionToMountMap();
    Map map2 = Lshal.queryDiskToMajorMap();
    Map map3 = Iostat.queryDeviceStrings(map1.keySet());
    ArrayList<SolarisHWDiskStore> arrayList = new ArrayList();
    for (Map.Entry entry : map3.entrySet()) {
      String str = (String)entry.getKey();
      Quintet quintet = (Quintet)entry.getValue();
      arrayList.add(createStore(str, (String)quintet.getA(), (String)quintet.getB(), (String)quintet.getC(), (String)quintet.getD(), ((Long)quintet.getE()).longValue(), (String)map1.getOrDefault(str, ""), ((Integer)map2.getOrDefault(str, Integer.valueOf(0))).intValue()));
    } 
    return (List)arrayList;
  }
  
  private static SolarisHWDiskStore createStore(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, long paramLong, String paramString6, int paramInt) {
    SolarisHWDiskStore solarisHWDiskStore = new SolarisHWDiskStore(paramString1, paramString2.isEmpty() ? (paramString3 + " " + paramString4).trim() : paramString2, paramString5, paramLong);
    solarisHWDiskStore.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)Prtvtoc.queryPartitions(paramString6, paramInt).stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
    solarisHWDiskStore.updateAttributes();
    return solarisHWDiskStore;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */