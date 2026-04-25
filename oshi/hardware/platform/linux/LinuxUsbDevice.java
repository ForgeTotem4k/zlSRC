package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.software.os.linux.LinuxOperatingSystem;

@Immutable
public class LinuxUsbDevice extends AbstractUsbDevice {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxUsbDevice.class);
  
  private static final String SUBSYSTEM_USB = "usb";
  
  private static final String DEVTYPE_USB_DEVICE = "usb_device";
  
  private static final String ATTR_PRODUCT = "product";
  
  private static final String ATTR_MANUFACTURER = "manufacturer";
  
  private static final String ATTR_VENDOR_ID = "idVendor";
  
  private static final String ATTR_PRODUCT_ID = "idProduct";
  
  private static final String ATTR_SERIAL = "serial";
  
  public LinuxUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = getUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<LinuxUsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list) {
      arrayList.add(new LinuxUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
      addDevicesToList((List)arrayList, usbDevice.getConnectedDevices());
    } 
    return (List)arrayList;
  }
  
  private static List<UsbDevice> getUsbDevices() {
    if (!LinuxOperatingSystem.HAS_UDEV) {
      LOG.warn("USB Device information requires libudev, which is not present.");
      return Collections.emptyList();
    } 
    ArrayList<String> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    HashMap<Object, Object> hashMap4 = new HashMap<>();
    HashMap<Object, Object> hashMap5 = new HashMap<>();
    HashMap<Object, Object> hashMap6 = new HashMap<>();
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("usb");
        udevEnumerate.scanDevices();
        for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
          String str = udevListEntry.getName();
          Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(str);
          if (udevDevice != null)
            try {
              if ("usb_device".equals(udevDevice.getDevtype())) {
                String str1 = udevDevice.getSysattrValue("product");
                if (str1 != null)
                  hashMap1.put(str, str1); 
                str1 = udevDevice.getSysattrValue("manufacturer");
                if (str1 != null)
                  hashMap2.put(str, str1); 
                str1 = udevDevice.getSysattrValue("idVendor");
                if (str1 != null)
                  hashMap3.put(str, str1); 
                str1 = udevDevice.getSysattrValue("idProduct");
                if (str1 != null)
                  hashMap4.put(str, str1); 
                str1 = udevDevice.getSysattrValue("serial");
                if (str1 != null)
                  hashMap5.put(str, str1); 
                Udev.UdevDevice udevDevice1 = udevDevice.getParentWithSubsystemDevtype("usb", "usb_device");
                if (udevDevice1 == null) {
                  arrayList.add(str);
                } else {
                  String str2 = udevDevice1.getSyspath();
                  ((List<String>)hashMap6.computeIfAbsent(str2, paramString -> new ArrayList())).add(str);
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
    ArrayList<LinuxUsbDevice> arrayList1 = new ArrayList();
    for (String str : arrayList)
      arrayList1.add(getDeviceAndChildren(str, "0000", "0000", (Map)hashMap1, (Map)hashMap2, (Map)hashMap3, (Map)hashMap4, (Map)hashMap5, (Map)hashMap6)); 
    return (List)arrayList1;
  }
  
  private static void addDevicesToList(List<UsbDevice> paramList1, List<UsbDevice> paramList2) {
    for (UsbDevice usbDevice : paramList2) {
      paramList1.add(usbDevice);
      addDevicesToList(paramList1, usbDevice.getConnectedDevices());
    } 
  }
  
  private static LinuxUsbDevice getDeviceAndChildren(String paramString1, String paramString2, String paramString3, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, String> paramMap3, Map<String, String> paramMap4, Map<String, String> paramMap5, Map<String, List<String>> paramMap) {
    String str1 = paramMap3.getOrDefault(paramString1, paramString2);
    String str2 = paramMap4.getOrDefault(paramString1, paramString3);
    List list = paramMap.getOrDefault(paramString1, new ArrayList<>());
    ArrayList<LinuxUsbDevice> arrayList = new ArrayList();
    for (String str : list)
      arrayList.add(getDeviceAndChildren(str, str1, str2, paramMap1, paramMap2, paramMap3, paramMap4, paramMap5, paramMap)); 
    Collections.sort(arrayList);
    return new LinuxUsbDevice(paramMap1.getOrDefault(paramString1, str1 + ":" + str2), paramMap2.getOrDefault(paramString1, ""), str1, str2, paramMap5.getOrDefault(paramString1, ""), paramString1, (List)arrayList);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */