package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Guid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.DeviceTree;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ParseUtil;
import oshi.util.tuples.Quintet;
import oshi.util.tuples.Triplet;

@Immutable
public class WindowsUsbDevice extends AbstractUsbDevice {
  private static final Guid.GUID GUID_DEVINTERFACE_USB_HOST_CONTROLLER = new Guid.GUID("{3ABF6F2D-71C4-462A-8A92-1E6861E6AF27}");
  
  public WindowsUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramList);
  }
  
  public static List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    List<UsbDevice> list = queryUsbDevices();
    if (paramBoolean)
      return list; 
    ArrayList<UsbDevice> arrayList = new ArrayList();
    for (UsbDevice usbDevice : list)
      addDevicesToList(arrayList, usbDevice.getConnectedDevices()); 
    return arrayList;
  }
  
  private static void addDevicesToList(List<UsbDevice> paramList1, List<UsbDevice> paramList2) {
    for (UsbDevice usbDevice : paramList2) {
      paramList1.add(new WindowsUsbDevice(usbDevice.getName(), usbDevice.getVendor(), usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getSerialNumber(), usbDevice.getUniqueDeviceId(), Collections.emptyList()));
      addDevicesToList(paramList1, usbDevice.getConnectedDevices());
    } 
  }
  
  private static List<UsbDevice> queryUsbDevices() {
    Quintet quintet = DeviceTree.queryDeviceTree(GUID_DEVINTERFACE_USB_HOST_CONTROLLER);
    Map<Integer, Integer> map = (Map)quintet.getB();
    Map<Integer, String> map1 = (Map)quintet.getC();
    Map<Integer, String> map2 = (Map)quintet.getD();
    Map<Integer, String> map3 = (Map)quintet.getE();
    ArrayList<WindowsUsbDevice> arrayList = new ArrayList();
    for (Integer integer : quintet.getA()) {
      WindowsUsbDevice windowsUsbDevice = queryDeviceAndChildren(integer, map, map1, map2, map3, "0000", "0000", "");
      if (windowsUsbDevice != null)
        arrayList.add(windowsUsbDevice); 
    } 
    return (List)arrayList;
  }
  
  private static WindowsUsbDevice queryDeviceAndChildren(Integer paramInteger, Map<Integer, Integer> paramMap, Map<Integer, String> paramMap1, Map<Integer, String> paramMap2, Map<Integer, String> paramMap3, String paramString1, String paramString2, String paramString3) {
    String str1 = paramString1;
    String str2 = paramString2;
    String str3 = paramString3;
    Triplet triplet = ParseUtil.parseDeviceIdToVendorProductSerial(paramMap2.get(paramInteger));
    if (triplet != null) {
      str1 = (String)triplet.getA();
      str2 = (String)triplet.getB();
      str3 = (String)triplet.getC();
      if (str3.isEmpty() && str1.equals(paramString1) && str2.equals(paramString2))
        str3 = paramString3; 
    } 
    Set set = (Set)paramMap.entrySet().stream().filter(paramEntry -> ((Integer)paramEntry.getValue()).equals(paramInteger)).map(Map.Entry::getKey).collect(Collectors.toSet());
    ArrayList<WindowsUsbDevice> arrayList = new ArrayList();
    for (Integer integer : set) {
      WindowsUsbDevice windowsUsbDevice = queryDeviceAndChildren(integer, paramMap, paramMap1, paramMap2, paramMap3, str1, str2, str3);
      if (windowsUsbDevice != null)
        arrayList.add(windowsUsbDevice); 
    } 
    Collections.sort(arrayList);
    if (paramMap1.containsKey(paramInteger)) {
      String str4 = paramMap1.get(paramInteger);
      if (str4.isEmpty())
        str4 = str1 + ":" + str2; 
      String str5 = paramMap2.get(paramInteger);
      String str6 = paramMap3.get(paramInteger);
      return new WindowsUsbDevice(str4, str6, str1, str2, str3, str5, (List)arrayList);
    } 
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */