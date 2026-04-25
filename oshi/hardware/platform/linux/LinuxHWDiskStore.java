package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.DevPath;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class LinuxHWDiskStore extends AbstractHWDiskStore {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxHWDiskStore.class);
  
  private static final String BLOCK = "block";
  
  private static final String DISK = "disk";
  
  private static final String PARTITION = "partition";
  
  private static final String STAT = "stat";
  
  private static final String SIZE = "size";
  
  private static final String MINOR = "MINOR";
  
  private static final String MAJOR = "MAJOR";
  
  private static final String ID_FS_TYPE = "ID_FS_TYPE";
  
  private static final String ID_FS_UUID = "ID_FS_UUID";
  
  private static final String ID_MODEL = "ID_MODEL";
  
  private static final String ID_SERIAL_SHORT = "ID_SERIAL_SHORT";
  
  private static final String DM_UUID = "DM_UUID";
  
  private static final String DM_VG_NAME = "DM_VG_NAME";
  
  private static final String DM_LV_NAME = "DM_LV_NAME";
  
  private static final String LOGICAL_VOLUME_GROUP = "Logical Volume Group";
  
  private static final int SECTORSIZE = 512;
  
  private static final int[] UDEV_STAT_ORDERS = new int[(UdevStat.values()).length];
  
  private static final int UDEV_STAT_LENGTH;
  
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList = new ArrayList<>();
  
  private LinuxHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
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
  
  public static List<HWDiskStore> getDisks() {
    return getDisks((LinuxHWDiskStore)null);
  }
  
  private static List<HWDiskStore> getDisks(LinuxHWDiskStore paramLinuxHWDiskStore) {
    if (!LinuxOperatingSystem.HAS_UDEV) {
      LOG.warn("Disk Store information requires libudev, which is not present.");
      return Collections.emptyList();
    } 
    LinuxHWDiskStore linuxHWDiskStore = null;
    ArrayList<LinuxHWDiskStore> arrayList = new ArrayList();
    Map<String, String> map = readMountsMap();
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("block");
        udevEnumerate.scanDevices();
        for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
          String str = udevListEntry.getName();
          Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(str);
          if (udevDevice != null)
            try {
              String str1 = udevDevice.getDevnode();
              if (str1 != null && !str1.startsWith(DevPath.LOOP) && !str1.startsWith(DevPath.RAM))
                if ("disk".equals(udevDevice.getDevtype())) {
                  String str2 = udevDevice.getPropertyValue("ID_MODEL");
                  String str3 = udevDevice.getPropertyValue("ID_SERIAL_SHORT");
                  long l = ParseUtil.parseLongOrDefault(udevDevice.getSysattrValue("size"), 0L) * 512L;
                  if (str1.startsWith(DevPath.DM)) {
                    str2 = "Logical Volume Group";
                    str3 = udevDevice.getPropertyValue("DM_UUID");
                    linuxHWDiskStore = new LinuxHWDiskStore(str1, str2, (str3 == null) ? "unknown" : str3, l);
                    String str4 = udevDevice.getPropertyValue("DM_VG_NAME");
                    String str5 = udevDevice.getPropertyValue("DM_LV_NAME");
                    linuxHWDiskStore.partitionList.add(new HWPartition(getPartitionNameForDmDevice(str4, str5), udevDevice.getSysname(), (udevDevice.getPropertyValue("ID_FS_TYPE") == null) ? "partition" : udevDevice.getPropertyValue("ID_FS_TYPE"), (udevDevice.getPropertyValue("ID_FS_UUID") == null) ? "" : udevDevice.getPropertyValue("ID_FS_UUID"), ParseUtil.parseLongOrDefault(udevDevice.getSysattrValue("size"), 0L) * 512L, ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("MAJOR"), 0), ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("MINOR"), 0), getMountPointOfDmDevice(str4, str5)));
                  } else {
                    linuxHWDiskStore = new LinuxHWDiskStore(str1, (str2 == null) ? "unknown" : str2, (str3 == null) ? "unknown" : str3, l);
                  } 
                  if (paramLinuxHWDiskStore == null) {
                    computeDiskStats(linuxHWDiskStore, udevDevice.getSysattrValue("stat"));
                    arrayList.add(linuxHWDiskStore);
                  } else if (linuxHWDiskStore.getName().equals(paramLinuxHWDiskStore.getName()) && linuxHWDiskStore.getModel().equals(paramLinuxHWDiskStore.getModel()) && linuxHWDiskStore.getSerial().equals(paramLinuxHWDiskStore.getSerial()) && linuxHWDiskStore.getSize() == paramLinuxHWDiskStore.getSize()) {
                    computeDiskStats(paramLinuxHWDiskStore, udevDevice.getSysattrValue("stat"));
                    arrayList.add(paramLinuxHWDiskStore);
                    udevDevice.unref();
                    break;
                  } 
                } else if (paramLinuxHWDiskStore == null && linuxHWDiskStore != null && "partition".equals(udevDevice.getDevtype())) {
                  Udev.UdevDevice udevDevice1 = udevDevice.getParentWithSubsystemDevtype("block", "disk");
                  if (udevDevice1 != null && linuxHWDiskStore.getName().equals(udevDevice1.getDevnode())) {
                    String str2 = udevDevice.getDevnode();
                    linuxHWDiskStore.partitionList.add(new HWPartition(str2, udevDevice.getSysname(), (udevDevice.getPropertyValue("ID_FS_TYPE") == null) ? "partition" : udevDevice.getPropertyValue("ID_FS_TYPE"), (udevDevice.getPropertyValue("ID_FS_UUID") == null) ? "" : udevDevice.getPropertyValue("ID_FS_UUID"), ParseUtil.parseLongOrDefault(udevDevice.getSysattrValue("size"), 0L) * 512L, ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("MAJOR"), 0), ParseUtil.parseIntOrDefault(udevDevice.getPropertyValue("MINOR"), 0), map.getOrDefault(str2, getDependentNamesFromHoldersDirectory(udevDevice.getSysname()))));
                  } 
                }  
            } finally {
              udevDevice.unref();
            }  
        } 
      } finally {
        udevEnumerate.unref();
      } 
    } finally {
      udevContext.unref();
    } 
    for (HWDiskStore hWDiskStore : arrayList)
      ((LinuxHWDiskStore)hWDiskStore).partitionList = Collections.unmodifiableList((List<? extends HWPartition>)hWDiskStore.getPartitions().stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList())); 
    return (List)arrayList;
  }
  
  public boolean updateAttributes() {
    return !getDisks(this).isEmpty();
  }
  
  private static Map<String, String> readMountsMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List list = FileUtil.readFile(ProcPath.MOUNTS);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length < 2 || !arrayOfString[0].startsWith(DevPath.DEV))
        continue; 
      hashMap.put(arrayOfString[0], arrayOfString[1]);
    } 
    return (Map)hashMap;
  }
  
  private static void computeDiskStats(LinuxHWDiskStore paramLinuxHWDiskStore, String paramString) {
    long[] arrayOfLong = ParseUtil.parseStringToLongArray(paramString, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, ' ');
    paramLinuxHWDiskStore.timeStamp = System.currentTimeMillis();
    paramLinuxHWDiskStore.reads = arrayOfLong[UdevStat.READS.ordinal()];
    paramLinuxHWDiskStore.readBytes = arrayOfLong[UdevStat.READ_BYTES.ordinal()] * 512L;
    paramLinuxHWDiskStore.writes = arrayOfLong[UdevStat.WRITES.ordinal()];
    paramLinuxHWDiskStore.writeBytes = arrayOfLong[UdevStat.WRITE_BYTES.ordinal()] * 512L;
    paramLinuxHWDiskStore.currentQueueLength = arrayOfLong[UdevStat.QUEUE_LENGTH.ordinal()];
    paramLinuxHWDiskStore.transferTime = arrayOfLong[UdevStat.ACTIVE_MS.ordinal()];
  }
  
  private static String getPartitionNameForDmDevice(String paramString1, String paramString2) {
    return DevPath.DEV + paramString1 + '/' + paramString2;
  }
  
  private static String getMountPointOfDmDevice(String paramString1, String paramString2) {
    return DevPath.MAPPER + paramString1 + '-' + paramString2;
  }
  
  private static String getDependentNamesFromHoldersDirectory(String paramString) {
    File file = new File(paramString + "/holders");
    File[] arrayOfFile = file.listFiles();
    return (arrayOfFile != null) ? Arrays.<File>stream(arrayOfFile).map(File::getName).collect(Collectors.joining(" ")) : "";
  }
  
  static {
    for (UdevStat udevStat : UdevStat.values())
      UDEV_STAT_ORDERS[udevStat.ordinal()] = udevStat.getOrder(); 
    String str = FileUtil.getStringFromFile(ProcPath.DISKSTATS);
    int i = 11;
    if (!str.isEmpty())
      i = ParseUtil.countStringToLongArray(str, ' '); 
    UDEV_STAT_LENGTH = i;
  }
  
  enum UdevStat {
    READS(0),
    READ_BYTES(2),
    WRITES(4),
    WRITE_BYTES(6),
    QUEUE_LENGTH(8),
    ACTIVE_MS(9);
    
    private int order;
    
    public int getOrder() {
      return this.order;
    }
    
    UdevStat(int param1Int1) {
      this.order = param1Int1;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */