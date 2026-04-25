package oshi.hardware.platform.unix.openbsd;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.openbsd.disk.Disklabel;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;
import oshi.util.tuples.Quartet;

@ThreadSafe
public final class OpenBsdHWDiskStore extends AbstractHWDiskStore {
  private final Supplier<List<String>> iostat = Memoizer.memoize(OpenBsdHWDiskStore::querySystatIostat, Memoizer.defaultExpiration());
  
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private OpenBsdHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
    super(paramString1, paramString2, paramString3, paramLong);
  }
  
  public static List<HWDiskStore> getDisks() {
    ArrayList<OpenBsdHWDiskStore> arrayList = new ArrayList();
    List list = null;
    String[] arrayOfString = OpenBsdSysctlUtil.sysctl("hw.disknames", "").split(",");
    for (String str2 : arrayOfString) {
      String str1 = str2.split(":")[0];
      Quartet quartet = Disklabel.getDiskParams(str1);
      String str3 = (String)quartet.getA();
      long l = ((Long)quartet.getC()).longValue();
      if (l <= 1L) {
        if (list == null)
          list = ExecutingCommand.runNative("dmesg"); 
        Pattern pattern1 = Pattern.compile(str1 + " at .*<(.+)>.*");
        Pattern pattern2 = Pattern.compile(str1 + ":.* (\\d+)MB, (?:(\\d+) bytes\\/sector, )?(?:(\\d+) sectors).*");
        for (String str : list) {
          Matcher matcher = pattern1.matcher(str);
          if (matcher.matches())
            str3 = matcher.group(1); 
          matcher = pattern2.matcher(str);
          if (matcher.matches()) {
            long l1 = ParseUtil.parseLongOrDefault(matcher.group(3), 0L);
            long l2 = ParseUtil.parseLongOrDefault(matcher.group(2), 0L);
            if (l2 == 0L && l1 > 0L) {
              l = ParseUtil.parseLongOrDefault(matcher.group(1), 0L) << 20L;
              l2 = l / l1;
              l2 = Long.highestOneBit(l2 + l2 >> 1L);
            } 
            l = l2 * l1;
            break;
          } 
        } 
      } 
      OpenBsdHWDiskStore openBsdHWDiskStore = new OpenBsdHWDiskStore(str1, str3, (String)quartet.getB(), l);
      openBsdHWDiskStore.partitionList = (List<HWPartition>)quartet.getD();
      openBsdHWDiskStore.updateAttributes();
      arrayList.add(openBsdHWDiskStore);
    } 
    return (List)arrayList;
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
    long l = System.currentTimeMillis();
    boolean bool = false;
    for (String str : this.iostat.get()) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length < 7 && arrayOfString[0].equals(getName())) {
        bool = true;
        this.readBytes = ParseUtil.parseMultipliedToLongs(arrayOfString[1]);
        this.writeBytes = ParseUtil.parseMultipliedToLongs(arrayOfString[2]);
        this.reads = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[3], 0.0D);
        this.writes = (long)ParseUtil.parseDoubleOrDefault(arrayOfString[4], 0.0D);
        this.transferTime = (long)(ParseUtil.parseDoubleOrDefault(arrayOfString[5], 0.0D) * 1000.0D);
        this.timeStamp = l;
      } 
    } 
    return bool;
  }
  
  private static List<String> querySystatIostat() {
    return ExecutingCommand.runNative("systat -ab iostat");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */