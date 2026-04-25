package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public class FreeBsdUsbDevice extends AbstractUsbDevice {
  public FreeBsdUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = getUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<FreeBsdUsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list) {
      arrayList.add(new FreeBsdUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
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
    HashMap<Object, Object> hashMap7 = new HashMap<>();
    List list = ExecutingCommand.runNative("lshal");
    if (list.isEmpty())
      return Collections.emptyList(); 
    String str = "";
    ArrayList<String> arrayList = new ArrayList();
    for (String str1 : list) {
      if (str1.startsWith("udi =")) {
        str = ParseUtil.getSingleQuoteStringValue(str1);
        continue;
      } 
      if (!str.isEmpty()) {
        str1 = str1.trim();
        if (!str1.isEmpty()) {
          if (str1.startsWith("freebsd.driver =") && "usbus".equals(ParseUtil.getSingleQuoteStringValue(str1))) {
            arrayList.add(str);
            continue;
          } 
          if (str1.contains(".parent =")) {
            String str2 = ParseUtil.getSingleQuoteStringValue(str1);
            if (str.replace(str2, "").startsWith("_if"))
              continue; 
            hashMap6.put(str, str2);
            ((List<String>)hashMap7.computeIfAbsent(str2, paramString -> new ArrayList())).add(str);
            continue;
          } 
          if (str1.contains(".product =")) {
            hashMap1.put(str, ParseUtil.getSingleQuoteStringValue(str1));
            continue;
          } 
          if (str1.contains(".vendor =")) {
            hashMap2.put(str, ParseUtil.getSingleQuoteStringValue(str1));
            continue;
          } 
          if (str1.contains(".serial =")) {
            String str2 = ParseUtil.getSingleQuoteStringValue(str1);
            hashMap5.put(str, str2.startsWith("0x") ? ParseUtil.hexStringToString(str2.replace("0x", "")) : str2);
            continue;
          } 
          if (str1.contains(".vendor_id =")) {
            hashMap3.put(str, String.format(Locale.ROOT, "%04x", new Object[] { Integer.valueOf(ParseUtil.getFirstIntValue(str1)) }));
            continue;
          } 
          if (str1.contains(".product_id ="))
            hashMap4.put(str, String.format(Locale.ROOT, "%04x", new Object[] { Integer.valueOf(ParseUtil.getFirstIntValue(str1)) })); 
        } 
      } 
    } 
    ArrayList<FreeBsdUsbDevice> arrayList1 = new ArrayList();
    for (String str1 : arrayList) {
      String str2 = (String)hashMap6.get(str1);
      hashMap7.put(str2, hashMap7.get(str1));
      arrayList1.add(getDeviceAndChildren(str2, "0000", "0000", (Map)hashMap1, (Map)hashMap2, (Map)hashMap3, (Map)hashMap4, (Map)hashMap5, (Map)hashMap7));
    } 
    return (List)arrayList1;
  }
  
  private static void addDevicesToList(List<UsbDevice> paramList1, List<UsbDevice> paramList2) {
    for (UsbDevice usbDevice : paramList2) {
      paramList1.add(usbDevice);
      addDevicesToList(paramList1, usbDevice.getConnectedDevices());
    } 
  }
  
  private static FreeBsdUsbDevice getDeviceAndChildren(String paramString1, String paramString2, String paramString3, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, String> paramMap3, Map<String, String> paramMap4, Map<String, String> paramMap5, Map<String, List<String>> paramMap) {
    String str1 = paramMap3.getOrDefault(paramString1, paramString2);
    String str2 = paramMap4.getOrDefault(paramString1, paramString3);
    List list = paramMap.getOrDefault(paramString1, new ArrayList<>());
    ArrayList<FreeBsdUsbDevice> arrayList = new ArrayList();
    for (String str : list)
      arrayList.add(getDeviceAndChildren(str, str1, str2, paramMap1, paramMap2, paramMap3, paramMap4, paramMap5, paramMap)); 
    Collections.sort(arrayList);
    return new FreeBsdUsbDevice(paramMap1.getOrDefault(paramString1, str1 + ":" + str2), paramMap2.getOrDefault(paramString1, ""), str1, str2, paramMap5.getOrDefault(paramString1, ""), paramString1, (List)arrayList);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */