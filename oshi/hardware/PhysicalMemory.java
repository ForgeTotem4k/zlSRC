package oshi.hardware;

import oshi.annotation.concurrent.Immutable;
import oshi.util.FormatUtil;

@Immutable
public class PhysicalMemory {
  private final String bankLabel;
  
  private final long capacity;
  
  private final long clockSpeed;
  
  private final String manufacturer;
  
  private final String memoryType;
  
  private final String partNumber;
  
  private final String serialNumber;
  
  public PhysicalMemory(String paramString1, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, String paramString5) {
    this.bankLabel = paramString1;
    this.capacity = paramLong1;
    this.clockSpeed = paramLong2;
    this.manufacturer = paramString2;
    this.memoryType = paramString3;
    this.partNumber = paramString4;
    this.serialNumber = paramString5;
  }
  
  public String getBankLabel() {
    return this.bankLabel;
  }
  
  public long getCapacity() {
    return this.capacity;
  }
  
  public long getClockSpeed() {
    return this.clockSpeed;
  }
  
  public String getManufacturer() {
    return this.manufacturer;
  }
  
  public String getMemoryType() {
    return this.memoryType;
  }
  
  public String getPartNumber() {
    return this.partNumber;
  }
  
  public String getSerialNumber() {
    return this.serialNumber;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Bank label: " + getBankLabel());
    stringBuilder.append(", Capacity: " + FormatUtil.formatBytes(getCapacity()));
    stringBuilder.append(", Clock speed: " + FormatUtil.formatHertz(getClockSpeed()));
    stringBuilder.append(", Manufacturer: " + getManufacturer());
    stringBuilder.append(", Memory type: " + getMemoryType());
    stringBuilder.append(", Part number: " + getPartNumber());
    stringBuilder.append(", Serial number: " + getSerialNumber());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\PhysicalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */