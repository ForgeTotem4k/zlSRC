package oshi.hardware.platform.mac;

import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;

@Immutable
public class MacUsbDevice extends AbstractUsbDevice {
  private static final CoreFoundation CF = CoreFoundation.INSTANCE;
  
  private static final String IOUSB = "IOUSB";
  
  private static final String IOSERVICE = "IOService";
  
  public MacUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = getUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<UsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list)
      addDevicesToList(arrayList, usbDevice.getConnectedDevices()); 
    return arrayList;
  }
  
  private static List<UsbDevice> getUsbDevices() {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    HashMap<Object, Object> hashMap4 = new HashMap<>();
    HashMap<Object, Object> hashMap5 = new HashMap<>();
    HashMap<Object, Object> hashMap6 = new HashMap<>();
    ArrayList<Long> arrayList = new ArrayList();
    IOKit.IORegistryEntry iORegistryEntry = IOKitUtil.getRoot();
    IOKit.IOIterator iOIterator = iORegistryEntry.getChildIterator("IOUSB");
    if (iOIterator != null) {
      CoreFoundation.CFStringRef cFStringRef1 = CoreFoundation.CFStringRef.createCFString("locationID");
      CoreFoundation.CFStringRef cFStringRef2 = CoreFoundation.CFStringRef.createCFString("IOPropertyMatch");
      for (IOKit.IORegistryEntry iORegistryEntry1 = iOIterator.next(); iORegistryEntry1 != null; iORegistryEntry1 = iOIterator.next()) {
        long l = 0L;
        IOKit.IORegistryEntry iORegistryEntry2 = iORegistryEntry1.getParentEntry("IOService");
        if (iORegistryEntry2 != null) {
          l = iORegistryEntry2.getRegistryEntryID();
          hashMap1.put(Long.valueOf(l), iORegistryEntry2.getName());
          CoreFoundation.CFTypeRef cFTypeRef = iORegistryEntry2.createCFProperty(cFStringRef1);
          if (cFTypeRef != null) {
            getControllerIdByLocation(l, cFTypeRef, cFStringRef1, cFStringRef2, (Map)hashMap3, (Map)hashMap4);
            cFTypeRef.release();
          } 
          iORegistryEntry2.release();
        } 
        arrayList.add(Long.valueOf(l));
        addDeviceAndChildrenToMaps(iORegistryEntry1, l, (Map)hashMap1, (Map)hashMap2, (Map)hashMap3, (Map)hashMap4, (Map)hashMap5, (Map)hashMap6);
        iORegistryEntry1.release();
      } 
      cFStringRef1.release();
      cFStringRef2.release();
      iOIterator.release();
    } 
    iORegistryEntry.release();
    ArrayList<MacUsbDevice> arrayList1 = new ArrayList();
    for (Long long_ : arrayList)
      arrayList1.add(getDeviceAndChildren(long_, "0000", "0000", (Map)hashMap1, (Map)hashMap2, (Map)hashMap3, (Map)hashMap4, (Map)hashMap5, (Map)hashMap6)); 
    return (List)arrayList1;
  }
  
  private static void addDeviceAndChildrenToMaps(IOKit.IORegistryEntry paramIORegistryEntry, long paramLong, Map<Long, String> paramMap1, Map<Long, String> paramMap2, Map<Long, String> paramMap3, Map<Long, String> paramMap4, Map<Long, String> paramMap5, Map<Long, List<Long>> paramMap) {
    long l = paramIORegistryEntry.getRegistryEntryID();
    ((List<Long>)paramMap.computeIfAbsent(Long.valueOf(paramLong), paramLong -> new ArrayList())).add(Long.valueOf(l));
    paramMap1.put(Long.valueOf(l), paramIORegistryEntry.getName().trim());
    String str1 = paramIORegistryEntry.getStringProperty("USB Vendor Name");
    if (str1 != null)
      paramMap2.put(Long.valueOf(l), str1.trim()); 
    Long long_1 = paramIORegistryEntry.getLongProperty("idVendor");
    if (long_1 != null)
      paramMap3.put(Long.valueOf(l), String.format(Locale.ROOT, "%04x", new Object[] { Long.valueOf(0xFFFFL & long_1.longValue()) })); 
    Long long_2 = paramIORegistryEntry.getLongProperty("idProduct");
    if (long_2 != null)
      paramMap4.put(Long.valueOf(l), String.format(Locale.ROOT, "%04x", new Object[] { Long.valueOf(0xFFFFL & long_2.longValue()) })); 
    String str2 = paramIORegistryEntry.getStringProperty("USB Serial Number");
    if (str2 != null)
      paramMap5.put(Long.valueOf(l), str2.trim()); 
    IOKit.IOIterator iOIterator = paramIORegistryEntry.getChildIterator("IOUSB");
    for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
      addDeviceAndChildrenToMaps(iORegistryEntry, l, paramMap1, paramMap2, paramMap3, paramMap4, paramMap5, paramMap);
      iORegistryEntry.release();
    } 
    iOIterator.release();
  }
  
  private static void addDevicesToList(List<UsbDevice> paramList1, List<UsbDevice> paramList2) {
    for (UsbDevice usbDevice : paramList2) {
      paramList1.add(new MacUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
      addDevicesToList(paramList1, usbDevice.getConnectedDevices());
    } 
  }
  
  private static void getControllerIdByLocation(long paramLong, CoreFoundation.CFTypeRef paramCFTypeRef, CoreFoundation.CFStringRef paramCFStringRef1, CoreFoundation.CFStringRef paramCFStringRef2, Map<Long, String> paramMap1, Map<Long, String> paramMap2) {
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef1 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
    cFMutableDictionaryRef1.setValue((PointerType)paramCFStringRef1, (PointerType)paramCFTypeRef);
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef2 = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
    cFMutableDictionaryRef2.setValue((PointerType)paramCFStringRef2, (PointerType)cFMutableDictionaryRef1);
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)cFMutableDictionaryRef2);
    cFMutableDictionaryRef1.release();
    boolean bool = false;
    if (iOIterator != null) {
      for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null && !bool; iORegistryEntry = iOIterator.next()) {
        IOKit.IORegistryEntry iORegistryEntry1 = iORegistryEntry.getParentEntry("IOService");
        if (iORegistryEntry1 != null) {
          byte[] arrayOfByte1 = iORegistryEntry1.getByteArrayProperty("vendor-id");
          if (arrayOfByte1 != null && arrayOfByte1.length >= 2) {
            paramMap1.put(Long.valueOf(paramLong), String.format(Locale.ROOT, "%02x%02x", new Object[] { Byte.valueOf(arrayOfByte1[1]), Byte.valueOf(arrayOfByte1[0]) }));
            bool = true;
          } 
          byte[] arrayOfByte2 = iORegistryEntry1.getByteArrayProperty("device-id");
          if (arrayOfByte2 != null && arrayOfByte2.length >= 2) {
            paramMap2.put(Long.valueOf(paramLong), String.format(Locale.ROOT, "%02x%02x", new Object[] { Byte.valueOf(arrayOfByte2[1]), Byte.valueOf(arrayOfByte2[0]) }));
            bool = true;
          } 
          iORegistryEntry1.release();
        } 
        iORegistryEntry.release();
      } 
      iOIterator.release();
    } 
  }
  
  private static MacUsbDevice getDeviceAndChildren(Long paramLong, String paramString1, String paramString2, Map<Long, String> paramMap1, Map<Long, String> paramMap2, Map<Long, String> paramMap3, Map<Long, String> paramMap4, Map<Long, String> paramMap5, Map<Long, List<Long>> paramMap) {
    String str1 = paramMap3.getOrDefault(paramLong, paramString1);
    String str2 = paramMap4.getOrDefault(paramLong, paramString2);
    List list = paramMap.getOrDefault(paramLong, new ArrayList<>());
    ArrayList<MacUsbDevice> arrayList = new ArrayList();
    for (Long long_ : list)
      arrayList.add(getDeviceAndChildren(long_, str1, str2, paramMap1, paramMap2, paramMap3, paramMap4, paramMap5, paramMap)); 
    Collections.sort(arrayList);
    return new MacUsbDevice(paramMap1.getOrDefault(paramLong, str1 + ":" + str2), paramMap2.getOrDefault(paramLong, ""), str1, str2, paramMap5.getOrDefault(paramLong, ""), "0x" + Long.toHexString(paramLong.longValue()), (List)arrayList);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */