package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public class SolarisUsbDevice extends AbstractUsbDevice {
  private static final String PCI_TYPE_USB = "000c";
  
  public SolarisUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = getUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<SolarisUsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list) {
      arrayList.add(new SolarisUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
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
    List list = ExecutingCommand.runNative("prtconf -pv");
    if (list.isEmpty())
      return Collections.emptyList(); 
    HashMap<Object, Object> hashMap6 = new HashMap<>();
    String str = "";
    int i = 0;
    ArrayList<String> arrayList = new ArrayList();
    for (String str1 : list) {
      if (str1.contains("Node 0x")) {
        str = str1.replaceFirst("^\\s*", "");
        int j = str1.length() - str.length();
        if (!i)
          i = j; 
        hashMap6.put(Integer.valueOf(j), str);
        if (j > i) {
          ((List<String>)hashMap4.computeIfAbsent(hashMap6.get(Integer.valueOf(j - i)), paramString -> new ArrayList())).add(str);
          continue;
        } 
        arrayList.add(str);
        continue;
      } 
      if (!str.isEmpty()) {
        str1 = str1.trim();
        if (str1.startsWith("model:")) {
          hashMap1.put(str, ParseUtil.getSingleQuoteStringValue(str1));
          continue;
        } 
        if (str1.startsWith("name:")) {
          hashMap1.putIfAbsent(str, ParseUtil.getSingleQuoteStringValue(str1));
          continue;
        } 
        if (str1.startsWith("vendor-id:")) {
          hashMap2.put(str, str1.substring(str1.length() - 4));
          continue;
        } 
        if (str1.startsWith("device-id:")) {
          hashMap3.put(str, str1.substring(str1.length() - 4));
          continue;
        } 
        if (str1.startsWith("class-code:")) {
          hashMap5.putIfAbsent(str, str1.substring(str1.length() - 8, str1.length() - 4));
          continue;
        } 
        if (str1.startsWith("device_type:"))
          hashMap5.putIfAbsent(str, ParseUtil.getSingleQuoteStringValue(str1)); 
      } 
    } 
    ArrayList<SolarisUsbDevice> arrayList1 = new ArrayList();
    for (String str1 : arrayList) {
      if ("000c".equals(hashMap5.getOrDefault(str1, "")) || "usb".equals(hashMap5.getOrDefault(str1, "")))
        arrayList1.add(getDeviceAndChildren(str1, "0000", "0000", (Map)hashMap1, (Map)hashMap2, (Map)hashMap3, (Map)hashMap4)); 
    } 
    return (List)arrayList1;
  }
  
  private static void addDevicesToList(List<UsbDevice> paramList1, List<UsbDevice> paramList2) {
    for (UsbDevice usbDevice : paramList2) {
      paramList1.add(usbDevice);
      addDevicesToList(paramList1, usbDevice.getConnectedDevices());
    } 
  }
  
  private static SolarisUsbDevice getDeviceAndChildren(String paramString1, String paramString2, String paramString3, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, String> paramMap3, Map<String, List<String>> paramMap) {
    String str1 = paramMap2.getOrDefault(paramString1, paramString2);
    String str2 = paramMap3.getOrDefault(paramString1, paramString3);
    List list = paramMap.getOrDefault(paramString1, new ArrayList<>());
    ArrayList<SolarisUsbDevice> arrayList = new ArrayList();
    for (String str : list)
      arrayList.add(getDeviceAndChildren(str, str1, str2, paramMap1, paramMap2, paramMap3, paramMap)); 
    Collections.sort(arrayList);
    return new SolarisUsbDevice(paramMap1.getOrDefault(paramString1, str1 + ":" + str2), "", str1, str2, "", paramString1, (List)arrayList);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */