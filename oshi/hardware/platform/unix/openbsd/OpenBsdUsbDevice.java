package oshi.hardware.platform.unix.openbsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ExecutingCommand;

@Immutable
public class OpenBsdUsbDevice extends AbstractUsbDevice {
  public OpenBsdUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = getUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<OpenBsdUsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list) {
      arrayList.add(new OpenBsdUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
      addDevicesToList((List)arrayList, usbDevice.getConnectedDevices());
    } 
    return (List)arrayList;
  }
  
  private static List<UsbDevice> getUsbDevices() {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    HashMap<Object, Object> hashMap4 = new HashMap<>();
    HashMap<Object, Object> hashMap5 = new HashMap<>();
    HashMap<Object, Object> hashMap6 = new HashMap<>();
    ArrayList<String> arrayList = new ArrayList();
    String str1 = "";
    String str2 = "";
    for (String str : ExecutingCommand.runNative("usbdevs -v")) {
      if (str.startsWith("Controller ")) {
        str2 = str.substring(11);
        continue;
      } 
      if (str.startsWith("addr ")) {
        if (str.indexOf(':') == 7 && str.indexOf(',') >= 18) {
          str1 = str2 + str.substring(0, 7);
          String[] arrayOfString = str.substring(8).trim().split(",");
          if (arrayOfString.length > 1) {
            String str3 = arrayOfString[0].trim();
            int i = str3.indexOf(':');
            int j = str3.indexOf(' ');
            if (i >= 0 && j >= 0) {
              hashMap3.put(str1, str3.substring(0, i));
              hashMap4.put(str1, str3.substring(i + 1, j));
              hashMap2.put(str1, str3.substring(j + 1));
            } 
            hashMap1.put(str1, arrayOfString[1].trim());
            ((List<String>)hashMap6.computeIfAbsent(str2, paramString -> new ArrayList())).add(str1);
            if (!str2.contains("addr")) {
              str2 = str1;
              arrayList.add(str2);
            } 
          } 
        } 
        continue;
      } 
      if (!str1.isEmpty()) {
        int i = str.indexOf("iSerial ");
        if (i >= 0)
          hashMap5.put(str1, str.substring(i + 8).trim()); 
        str1 = "";
      } 
    } 
    ArrayList<OpenBsdUsbDevice> arrayList1 = new ArrayList();
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
  
  private static OpenBsdUsbDevice getDeviceAndChildren(String paramString1, String paramString2, String paramString3, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, String> paramMap3, Map<String, String> paramMap4, Map<String, String> paramMap5, Map<String, List<String>> paramMap) {
    String str1 = paramMap3.getOrDefault(paramString1, paramString2);
    String str2 = paramMap4.getOrDefault(paramString1, paramString3);
    List list = paramMap.getOrDefault(paramString1, new ArrayList<>());
    ArrayList<OpenBsdUsbDevice> arrayList = new ArrayList();
    for (String str : list)
      arrayList.add(getDeviceAndChildren(str, str1, str2, paramMap1, paramMap2, paramMap3, paramMap4, paramMap5, paramMap)); 
    Collections.sort(arrayList);
    return new OpenBsdUsbDevice(paramMap1.getOrDefault(paramString1, str1 + ":" + str2), paramMap2.getOrDefault(paramString1, ""), str1, str2, paramMap5.getOrDefault(paramString1, ""), paramString1, (List)arrayList);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */