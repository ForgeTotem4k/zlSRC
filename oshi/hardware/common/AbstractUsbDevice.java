package oshi.hardware.common;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;

@Immutable
public abstract class AbstractUsbDevice implements UsbDevice {
  private final String name;
  
  private final String vendor;
  
  private final String vendorId;
  
  private final String productId;
  
  private final String serialNumber;
  
  private final String uniqueDeviceId;
  
  private final List<UsbDevice> connectedDevices;
  
  protected AbstractUsbDevice(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, List<UsbDevice> paramList) {
    this.name = paramString1;
    this.vendor = paramString2;
    this.vendorId = paramString3;
    this.productId = paramString4;
    this.serialNumber = paramString5;
    this.uniqueDeviceId = paramString6;
    this.connectedDevices = Collections.unmodifiableList(paramList);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getVendor() {
    return this.vendor;
  }
  
  public String getVendorId() {
    return this.vendorId;
  }
  
  public String getProductId() {
    return this.productId;
  }
  
  public String getSerialNumber() {
    return this.serialNumber;
  }
  
  public String getUniqueDeviceId() {
    return this.uniqueDeviceId;
  }
  
  public List<UsbDevice> getConnectedDevices() {
    return this.connectedDevices;
  }
  
  public int compareTo(UsbDevice paramUsbDevice) {
    return getName().compareTo(paramUsbDevice.getName());
  }
  
  public String toString() {
    return indentUsb(this, 1);
  }
  
  private static String indentUsb(UsbDevice paramUsbDevice, int paramInt) {
    String str = (paramInt > 4) ? String.format(Locale.ROOT, "%%%ds|-- ", new Object[] { Integer.valueOf(paramInt - 4) }) : String.format(Locale.ROOT, "%%%ds", new Object[] { Integer.valueOf(paramInt) });
    StringBuilder stringBuilder = new StringBuilder(String.format(Locale.ROOT, str, new Object[] { "" }));
    stringBuilder.append(paramUsbDevice.getName());
    if (!paramUsbDevice.getVendor().isEmpty())
      stringBuilder.append(" (").append(paramUsbDevice.getVendor()).append(')'); 
    if (!paramUsbDevice.getSerialNumber().isEmpty())
      stringBuilder.append(" [s/n: ").append(paramUsbDevice.getSerialNumber()).append(']'); 
    for (UsbDevice usbDevice : paramUsbDevice.getConnectedDevices())
      stringBuilder.append('\n').append(indentUsb(usbDevice, paramInt + 4)); 
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */