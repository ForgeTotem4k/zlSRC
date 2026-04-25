package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.PhysicalDisk;
import oshi.driver.windows.wmi.Win32DiskDrive;
import oshi.driver.windows.wmi.Win32DiskDriveToDiskPartition;
import oshi.driver.windows.wmi.Win32DiskPartition;
import oshi.driver.windows.wmi.Win32LogicalDiskToPartition;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class WindowsHWDiskStore extends AbstractHWDiskStore {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsHWDiskStore.class);
  
  private static final String PHYSICALDRIVE_PREFIX = "\\\\.\\PHYSICALDRIVE";
  
  private static final Pattern DEVICE_ID = Pattern.compile(".*\\.DeviceID=\"(.*)\"");
  
  private static final int GUID_BUFSIZE = 100;
  
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private WindowsHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
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
    String str = null;
    List<HWPartition> list = getPartitions();
    if (!list.isEmpty()) {
      str = Integer.toString(((HWPartition)list.get(0)).getMajor());
    } else if (getName().startsWith("\\\\.\\PHYSICALDRIVE")) {
      str = getName().substring("\\\\.\\PHYSICALDRIVE".length(), getName().length());
    } else {
      LOG.warn("Couldn't match index for {}", getName());
      return false;
    } 
    DiskStats diskStats = queryReadWriteStats(str);
    if (diskStats.readMap.containsKey(str)) {
      this.reads = ((Long)diskStats.readMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.readBytes = ((Long)diskStats.readByteMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.writes = ((Long)diskStats.writeMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.writeBytes = ((Long)diskStats.writeByteMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.currentQueueLength = ((Long)diskStats.queueLengthMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.transferTime = ((Long)diskStats.diskTimeMap.getOrDefault(str, Long.valueOf(0L))).longValue();
      this.timeStamp = diskStats.timeStamp;
      return true;
    } 
    return false;
  }
  
  public static List<HWDiskStore> getDisks() {
    WmiQueryHandler wmiQueryHandler = Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance());
    boolean bool = false;
    try {
      bool = wmiQueryHandler.initCOM();
      ArrayList<WindowsHWDiskStore> arrayList = new ArrayList();
      DiskStats diskStats = queryReadWriteStats(null);
      PartitionMaps partitionMaps = queryPartitionMaps(wmiQueryHandler);
      WbemcliUtil.WmiResult wmiResult = Win32DiskDrive.queryDiskDrive(wmiQueryHandler);
      for (byte b = 0; b < wmiResult.getResultCount(); b++) {
        WindowsHWDiskStore windowsHWDiskStore = new WindowsHWDiskStore(WmiUtil.getString(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.NAME, b), String.format(Locale.ROOT, "%s %s", new Object[] { WmiUtil.getString(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.MODEL, b), WmiUtil.getString(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.MANUFACTURER, b) }).trim(), ParseUtil.hexStringToString(WmiUtil.getString(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.SERIALNUMBER, b)), WmiUtil.getUint64(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.SIZE, b));
        String str = Integer.toString(WmiUtil.getUint32(wmiResult, (Enum)Win32DiskDrive.DiskDriveProperty.INDEX, b));
        windowsHWDiskStore.reads = ((Long)diskStats.readMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.readBytes = ((Long)diskStats.readByteMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.writes = ((Long)diskStats.writeMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.writeBytes = ((Long)diskStats.writeByteMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.currentQueueLength = ((Long)diskStats.queueLengthMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.transferTime = ((Long)diskStats.diskTimeMap.getOrDefault(str, Long.valueOf(0L))).longValue();
        windowsHWDiskStore.timeStamp = diskStats.timeStamp;
        ArrayList arrayList1 = new ArrayList();
        List list = (List)partitionMaps.driveToPartitionMap.get(windowsHWDiskStore.getName());
        if (list != null && !list.isEmpty())
          for (String str1 : list) {
            if (partitionMaps.partitionMap.containsKey(str1))
              arrayList1.addAll((Collection)partitionMaps.partitionMap.get(str1)); 
          }  
        windowsHWDiskStore.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)arrayList1.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
        arrayList.add(windowsHWDiskStore);
      } 
      return (List)arrayList;
    } catch (COMException cOMException) {
      LOG.warn("COM exception: {}", cOMException.getMessage());
      return (List)Collections.emptyList();
    } finally {
      if (bool)
        wmiQueryHandler.unInitCOM(); 
    } 
  }
  
  private static DiskStats queryReadWriteStats(String paramString) {
    DiskStats diskStats = new DiskStats();
    Pair pair = PhysicalDisk.queryDiskCounters();
    List<String> list = (List)pair.getA();
    Map map = (Map)pair.getB();
    diskStats.timeStamp = System.currentTimeMillis();
    List<Long> list1 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKREADSPERSEC);
    List<Long> list2 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKREADBYTESPERSEC);
    List<Long> list3 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITESPERSEC);
    List<Long> list4 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITEBYTESPERSEC);
    List<Long> list5 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.CURRENTDISKQUEUELENGTH);
    List<Long> list6 = (List)map.get(PhysicalDisk.PhysicalDiskProperty.PERCENTDISKTIME);
    if (list.isEmpty() || list1 == null || list2 == null || list3 == null || list4 == null || list5 == null || list6 == null)
      return diskStats; 
    for (byte b = 0; b < list.size(); b++) {
      String str = getIndexFromName(list.get(b));
      if (paramString == null || paramString.equals(str)) {
        diskStats.readMap.put(str, list1.get(b));
        diskStats.readByteMap.put(str, list2.get(b));
        diskStats.writeMap.put(str, list3.get(b));
        diskStats.writeByteMap.put(str, list4.get(b));
        diskStats.queueLengthMap.put(str, list5.get(b));
        diskStats.diskTimeMap.put(str, Long.valueOf(((Long)list6.get(b)).longValue() / 10000L));
      } 
    } 
    return diskStats;
  }
  
  private static PartitionMaps queryPartitionMaps(WmiQueryHandler paramWmiQueryHandler) {
    PartitionMaps partitionMaps = new PartitionMaps();
    WbemcliUtil.WmiResult wmiResult1 = Win32DiskDriveToDiskPartition.queryDriveToPartition(paramWmiQueryHandler);
    for (byte b1 = 0; b1 < wmiResult1.getResultCount(); b1++) {
      Matcher matcher1 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult1, (Enum)Win32DiskDriveToDiskPartition.DriveToPartitionProperty.ANTECEDENT, b1));
      Matcher matcher2 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult1, (Enum)Win32DiskDriveToDiskPartition.DriveToPartitionProperty.DEPENDENT, b1));
      if (matcher1.matches() && matcher2.matches())
        ((List<String>)partitionMaps.driveToPartitionMap.computeIfAbsent(matcher1.group(1).replace("\\\\", "\\"), paramString -> new ArrayList())).add(matcher2.group(1)); 
    } 
    WbemcliUtil.WmiResult wmiResult2 = Win32LogicalDiskToPartition.queryDiskToPartition(paramWmiQueryHandler);
    for (byte b2 = 0; b2 < wmiResult2.getResultCount(); b2++) {
      Matcher matcher1 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult2, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.ANTECEDENT, b2));
      Matcher matcher2 = DEVICE_ID.matcher(WmiUtil.getRefString(wmiResult2, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.DEPENDENT, b2));
      long l = WmiUtil.getUint64(wmiResult2, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.ENDINGADDRESS, b2) - WmiUtil.getUint64(wmiResult2, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.STARTINGADDRESS, b2) + 1L;
      if (matcher1.matches() && matcher2.matches())
        if (partitionMaps.partitionToLogicalDriveMap.containsKey(matcher1.group(1))) {
          ((List<Pair>)partitionMaps.partitionToLogicalDriveMap.get(matcher1.group(1))).add(new Pair(matcher2.group(1) + "\\", Long.valueOf(l)));
        } else {
          ArrayList<Pair> arrayList = new ArrayList();
          arrayList.add(new Pair(matcher2.group(1) + "\\", Long.valueOf(l)));
          partitionMaps.partitionToLogicalDriveMap.put(matcher1.group(1), arrayList);
        }  
    } 
    WbemcliUtil.WmiResult wmiResult3 = Win32DiskPartition.queryPartition(paramWmiQueryHandler);
    for (byte b3 = 0; b3 < wmiResult3.getResultCount(); b3++) {
      String str = WmiUtil.getString(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.DEVICEID, b3);
      List<Pair> list = (List)partitionMaps.partitionToLogicalDriveMap.get(str);
      if (list != null)
        for (byte b = 0; b < list.size(); b++) {
          Pair pair = list.get(b);
          if (pair != null && !((String)pair.getA()).isEmpty()) {
            char[] arrayOfChar = new char[100];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint((String)pair.getA(), arrayOfChar, 100);
            String str1 = ParseUtil.parseUuidOrDefault((new String(arrayOfChar)).trim(), "");
            HWPartition hWPartition = new HWPartition(WmiUtil.getString(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.NAME, b3), WmiUtil.getString(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.TYPE, b3), WmiUtil.getString(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.DESCRIPTION, b3), str1, ((Long)pair.getB()).longValue(), WmiUtil.getUint32(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.DISKINDEX, b3), WmiUtil.getUint32(wmiResult3, (Enum)Win32DiskPartition.DiskPartitionProperty.INDEX, b3), (String)pair.getA());
            if (partitionMaps.partitionMap.containsKey(str)) {
              ((List<HWPartition>)partitionMaps.partitionMap.get(str)).add(hWPartition);
            } else {
              ArrayList<HWPartition> arrayList = new ArrayList();
              arrayList.add(hWPartition);
              partitionMaps.partitionMap.put(str, arrayList);
            } 
          } 
        }  
    } 
    return partitionMaps;
  }
  
  private static String getIndexFromName(String paramString) {
    return paramString.isEmpty() ? paramString : paramString.split("\\s")[0];
  }
  
  private static final class DiskStats {
    private final Map<String, Long> readMap = new HashMap<>();
    
    private final Map<String, Long> readByteMap = new HashMap<>();
    
    private final Map<String, Long> writeMap = new HashMap<>();
    
    private final Map<String, Long> writeByteMap = new HashMap<>();
    
    private final Map<String, Long> queueLengthMap = new HashMap<>();
    
    private final Map<String, Long> diskTimeMap = new HashMap<>();
    
    private long timeStamp;
    
    private DiskStats() {}
  }
  
  private static final class PartitionMaps {
    private final Map<String, List<String>> driveToPartitionMap = new HashMap<>();
    
    private final Map<String, List<Pair<String, Long>>> partitionToLogicalDriveMap = new HashMap<>();
    
    private final Map<String, List<HWPartition>> partitionMap = new HashMap<>();
    
    private PartitionMaps() {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */