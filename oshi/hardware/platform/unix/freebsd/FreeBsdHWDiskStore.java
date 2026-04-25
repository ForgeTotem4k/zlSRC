package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.freebsd.disk.GeomDiskList;
import oshi.driver.unix.freebsd.disk.GeomPartList;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class FreeBsdHWDiskStore extends AbstractHWDiskStore {
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private FreeBsdHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
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
    List list = ExecutingCommand.runNative("iostat -Ix " + getName());
    long l = System.currentTimeMillis();
    boolean bool = false;
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length < 7 || !arrayOfString[0].equals(getName()))
        continue; 
      bool = true;
      this.reads = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D);
      this.writes = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[2], 0.0D);
      this.readBytes = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[3], 0.0D) * 1024.0D);
      this.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[4], 0.0D) * 1024.0D);
      this.currentQueueLength = ParseUtil.parseLongOrDefault(arrayOfString[5], 0L);
      this.transferTime = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[6], 0.0D) * 1000.0D);
      this.timeStamp = l;
    } 
    return bool;
  }
  
  public static List<HWDiskStore> getDisks() {
    ArrayList<FreeBsdHWDiskStore> arrayList = new ArrayList();
    Map map1 = GeomPartList.queryPartitions();
    Map map2 = GeomDiskList.queryDisks();
    List<String> list = Arrays.asList(ParseUtil.whitespaces.split(BsdSysctlUtil.sysctl("kern.disks", "")));
    List list1 = ExecutingCommand.runNative("iostat -Ix");
    long l = System.currentTimeMillis();
    for (String str : list1) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 6 && list.contains(arrayOfString[0])) {
        Triplet triplet = (Triplet)map2.get(arrayOfString[0]);
        FreeBsdHWDiskStore freeBsdHWDiskStore = (triplet == null) ? new FreeBsdHWDiskStore(arrayOfString[0], "unknown", "unknown", 0L) : new FreeBsdHWDiskStore(arrayOfString[0], (String)triplet.getA(), (String)triplet.getB(), ((Long)triplet.getC()).longValue());
        freeBsdHWDiskStore.reads = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D);
        freeBsdHWDiskStore.writes = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[2], 0.0D);
        freeBsdHWDiskStore.readBytes = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[3], 0.0D) * 1024.0D);
        freeBsdHWDiskStore.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[4], 0.0D) * 1024.0D);
        freeBsdHWDiskStore.currentQueueLength = ParseUtil.parseLongOrDefault(arrayOfString[5], 0L);
        freeBsdHWDiskStore.transferTime = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[6], 0.0D) * 1000.0D);
        freeBsdHWDiskStore.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)((List)map1.getOrDefault(arrayOfString[0], Collections.emptyList())).stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
        freeBsdHWDiskStore.timeStamp = l;
        arrayList.add(freeBsdHWDiskStore);
      } 
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */