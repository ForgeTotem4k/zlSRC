package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.disk.Fsstat;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.platform.mac.CFUtil;

@ThreadSafe
public final class MacHWDiskStore extends AbstractHWDiskStore {
  private static final CoreFoundation CF = CoreFoundation.INSTANCE;
  
  private static final DiskArbitration DA = DiskArbitration.INSTANCE;
  
  private static final Logger LOG = LoggerFactory.getLogger(MacHWDiskStore.class);
  
  private long reads = 0L;
  
  private long readBytes = 0L;
  
  private long writes = 0L;
  
  private long writeBytes = 0L;
  
  private long currentQueueLength = 0L;
  
  private long transferTime = 0L;
  
  private long timeStamp = 0L;
  
  private List<HWPartition> partitionList;
  
  private MacHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong, DiskArbitration.DASessionRef paramDASessionRef, Map<String, String> paramMap, Map<CFKey, CoreFoundation.CFStringRef> paramMap1) {
    super(paramString1, paramString2, paramString3, paramLong);
    updateDiskStats(paramDASessionRef, paramMap, paramMap1);
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
    DiskArbitration.DASessionRef dASessionRef = DA.DASessionCreate(CF.CFAllocatorGetDefault());
    if (dASessionRef == null) {
      LOG.error("Unable to open session to DiskArbitration framework.");
      return false;
    } 
    Map<CFKey, CoreFoundation.CFStringRef> map = mapCFKeys();
    boolean bool = updateDiskStats(dASessionRef, Fsstat.queryPartitionToMountMap(), map);
    dASessionRef.release();
    for (CoreFoundation.CFTypeRef cFTypeRef : map.values())
      cFTypeRef.release(); 
    return bool;
  }
  
  private boolean updateDiskStats(DiskArbitration.DASessionRef paramDASessionRef, Map<String, String> paramMap, Map<CFKey, CoreFoundation.CFStringRef> paramMap1) {
    String str = getName();
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = IOKitUtil.getBSDNameMatchingDict(str);
    if (cFMutableDictionaryRef != null) {
      IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)cFMutableDictionaryRef);
      if (iOIterator != null) {
        IOKit.IORegistryEntry iORegistryEntry = iOIterator.next();
        if (iORegistryEntry != null) {
          if (iORegistryEntry.conformsTo("IOMedia")) {
            IOKit.IORegistryEntry iORegistryEntry1 = iORegistryEntry.getParentEntry("IOService");
            if (iORegistryEntry1 != null && (iORegistryEntry1.conformsTo("IOBlockStorageDriver") || iORegistryEntry1.conformsTo("AppleAPFSContainerScheme"))) {
              CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef3 = iORegistryEntry1.createCFProperties();
              Pointer pointer1 = cFMutableDictionaryRef3.getValue((PointerType)paramMap1.get(CFKey.STATISTICS));
              CoreFoundation.CFDictionaryRef cFDictionaryRef = new CoreFoundation.CFDictionaryRef(pointer1);
              this.timeStamp = System.currentTimeMillis();
              pointer1 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.READ_OPS));
              CoreFoundation.CFNumberRef cFNumberRef1 = new CoreFoundation.CFNumberRef(pointer1);
              this.reads = cFNumberRef1.longValue();
              pointer1 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.READ_BYTES));
              cFNumberRef1.setPointer(pointer1);
              this.readBytes = cFNumberRef1.longValue();
              pointer1 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.WRITE_OPS));
              cFNumberRef1.setPointer(pointer1);
              this.writes = cFNumberRef1.longValue();
              pointer1 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.WRITE_BYTES));
              cFNumberRef1.setPointer(pointer1);
              this.writeBytes = cFNumberRef1.longValue();
              Pointer pointer2 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.READ_TIME));
              Pointer pointer3 = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.WRITE_TIME));
              if (pointer2 != null && pointer3 != null) {
                cFNumberRef1.setPointer(pointer2);
                long l = cFNumberRef1.longValue();
                cFNumberRef1.setPointer(pointer3);
                l += cFNumberRef1.longValue();
                this.transferTime = l / 1000000L;
              } 
              cFMutableDictionaryRef3.release();
            } else {
              LOG.debug("Unable to find block storage driver properties for {}", str);
            } 
            ArrayList<HWPartition> arrayList = new ArrayList();
            CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef1 = iORegistryEntry.createCFProperties();
            Pointer pointer = cFMutableDictionaryRef1.getValue((PointerType)paramMap1.get(CFKey.BSD_UNIT));
            CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(pointer);
            pointer = cFMutableDictionaryRef1.getValue((PointerType)paramMap1.get(CFKey.LEAF));
            CoreFoundation.CFBooleanRef cFBooleanRef = new CoreFoundation.CFBooleanRef(pointer);
            CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef2 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
            cFMutableDictionaryRef2.setValue((PointerType)paramMap1.get(CFKey.BSD_UNIT), (PointerType)cFNumberRef);
            cFMutableDictionaryRef2.setValue((PointerType)paramMap1.get(CFKey.WHOLE), (PointerType)cFBooleanRef);
            cFMutableDictionaryRef = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
            cFMutableDictionaryRef.setValue((PointerType)paramMap1.get(CFKey.IO_PROPERTY_MATCH), (PointerType)cFMutableDictionaryRef2);
            IOKit.IOIterator iOIterator1 = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)cFMutableDictionaryRef);
            cFMutableDictionaryRef1.release();
            cFMutableDictionaryRef2.release();
            if (iOIterator1 != null) {
              for (IOKit.IORegistryEntry iORegistryEntry2 = IOKit.INSTANCE.IOIteratorNext(iOIterator1); iORegistryEntry2 != null; iORegistryEntry2 = IOKit.INSTANCE.IOIteratorNext(iOIterator1)) {
                String str1 = iORegistryEntry2.getStringProperty("BSD Name");
                String str2 = str1;
                String str3 = "";
                DiskArbitration.DADiskRef dADiskRef = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), paramDASessionRef, str1);
                if (dADiskRef != null) {
                  CoreFoundation.CFDictionaryRef cFDictionaryRef = DA.DADiskCopyDescription(dADiskRef);
                  if (cFDictionaryRef != null) {
                    pointer = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.DA_MEDIA_NAME));
                    str3 = CFUtil.cfPointerToString(pointer);
                    pointer = cFDictionaryRef.getValue((PointerType)paramMap1.get(CFKey.DA_VOLUME_NAME));
                    if (pointer == null) {
                      str2 = str3;
                    } else {
                      str2 = CFUtil.cfPointerToString(pointer);
                    } 
                    cFDictionaryRef.release();
                  } 
                  dADiskRef.release();
                } 
                String str4 = paramMap.getOrDefault(str1, "");
                Long long_ = iORegistryEntry2.getLongProperty("Size");
                Integer integer1 = iORegistryEntry2.getIntegerProperty("BSD Major");
                Integer integer2 = iORegistryEntry2.getIntegerProperty("BSD Minor");
                String str5 = iORegistryEntry2.getStringProperty("UUID");
                arrayList.add(new HWPartition(str1, str2, str3, (str5 == null) ? "unknown" : str5, (long_ == null) ? 0L : long_.longValue(), (integer1 == null) ? 0 : integer1.intValue(), (integer2 == null) ? 0 : integer2.intValue(), str4));
                iORegistryEntry2.release();
              } 
              iOIterator1.release();
            } 
            this.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)arrayList.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
            if (iORegistryEntry1 != null)
              iORegistryEntry1.release(); 
          } else {
            LOG.error("Unable to find IOMedia device or parent for {}", str);
          } 
          iORegistryEntry.release();
        } 
        iOIterator.release();
        return true;
      } 
    } 
    return false;
  }
  
  public static List<HWDiskStore> getDisks() {
    Map<String, String> map = Fsstat.queryPartitionToMountMap();
    Map<CFKey, CoreFoundation.CFStringRef> map1 = mapCFKeys();
    ArrayList<MacHWDiskStore> arrayList = new ArrayList();
    DiskArbitration.DASessionRef dASessionRef = DA.DASessionCreate(CF.CFAllocatorGetDefault());
    if (dASessionRef == null) {
      LOG.error("Unable to open session to DiskArbitration framework.");
      return Collections.emptyList();
    } 
    ArrayList<String> arrayList1 = new ArrayList();
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices("IOMedia");
    if (iOIterator != null) {
      for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
        Boolean bool = iORegistryEntry.getBooleanProperty("Whole");
        if (bool != null && bool.booleanValue()) {
          DiskArbitration.DADiskRef dADiskRef = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), dASessionRef, (IOKit.IOObject)iORegistryEntry);
          arrayList1.add(DA.DADiskGetBSDName(dADiskRef));
          dADiskRef.release();
        } 
        iORegistryEntry.release();
      } 
      iOIterator.release();
    } 
    for (String str1 : arrayList1) {
      String str2 = "";
      String str3 = "";
      long l = 0L;
      String str4 = "/dev/" + str1;
      DiskArbitration.DADiskRef dADiskRef = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), dASessionRef, str4);
      if (dADiskRef != null) {
        CoreFoundation.CFDictionaryRef cFDictionaryRef = DA.DADiskCopyDescription(dADiskRef);
        if (cFDictionaryRef != null) {
          Pointer pointer = cFDictionaryRef.getValue((PointerType)map1.get(CFKey.DA_DEVICE_MODEL));
          str2 = CFUtil.cfPointerToString(pointer);
          pointer = cFDictionaryRef.getValue((PointerType)map1.get(CFKey.DA_MEDIA_SIZE));
          CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(pointer);
          l = cFNumberRef.longValue();
          cFDictionaryRef.release();
          if (!"Disk Image".equals(str2)) {
            CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(str2);
            CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef1 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
            cFMutableDictionaryRef1.setValue((PointerType)map1.get(CFKey.MODEL), (PointerType)cFStringRef);
            CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef2 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
            cFMutableDictionaryRef2.setValue((PointerType)map1.get(CFKey.IO_PROPERTY_MATCH), (PointerType)cFMutableDictionaryRef1);
            IOKit.IOIterator iOIterator1 = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)cFMutableDictionaryRef2);
            cFStringRef.release();
            cFMutableDictionaryRef1.release();
            if (iOIterator1 != null) {
              for (IOKit.IORegistryEntry iORegistryEntry = iOIterator1.next(); iORegistryEntry != null; iORegistryEntry = iOIterator1.next()) {
                str3 = iORegistryEntry.getStringProperty("Serial Number");
                iORegistryEntry.release();
                if (str3 != null)
                  break; 
                iORegistryEntry.release();
              } 
              iOIterator1.release();
            } 
            if (str3 == null)
              str3 = ""; 
          } 
        } 
        dADiskRef.release();
        if (l <= 0L)
          continue; 
        MacHWDiskStore macHWDiskStore = new MacHWDiskStore(str1, str2.trim(), str3.trim(), l, dASessionRef, map, map1);
        arrayList.add(macHWDiskStore);
      } 
    } 
    dASessionRef.release();
    for (CoreFoundation.CFTypeRef cFTypeRef : map1.values())
      cFTypeRef.release(); 
    return (List)arrayList;
  }
  
  private static Map<CFKey, CoreFoundation.CFStringRef> mapCFKeys() {
    EnumMap<CFKey, Object> enumMap = new EnumMap<>(CFKey.class);
    for (CFKey cFKey : CFKey.values())
      enumMap.put(cFKey, CoreFoundation.CFStringRef.createCFString(cFKey.getKey())); 
    return (Map)enumMap;
  }
  
  private enum CFKey {
    IO_PROPERTY_MATCH("IOPropertyMatch"),
    STATISTICS("Statistics"),
    READ_OPS("Operations (Read)"),
    READ_BYTES("Bytes (Read)"),
    READ_TIME("Total Time (Read)"),
    WRITE_OPS("Operations (Write)"),
    WRITE_BYTES("Bytes (Write)"),
    WRITE_TIME("Total Time (Write)"),
    BSD_UNIT("BSD Unit"),
    LEAF("Leaf"),
    WHOLE("Whole"),
    DA_MEDIA_NAME("DAMediaName"),
    DA_VOLUME_NAME("DAVolumeName"),
    DA_MEDIA_SIZE("DAMediaSize"),
    DA_DEVICE_MODEL("DADeviceModel"),
    MODEL("Model");
    
    private final String key;
    
    CFKey(String param1String1) {
      this.key = param1String1;
    }
    
    public String getKey() {
      return this.key;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */