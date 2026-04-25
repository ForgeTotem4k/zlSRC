package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractHWDiskStore implements HWDiskStore {
  private final String name;
  
  private final String model;
  
  private final String serial;
  
  private final long size;
  
  protected AbstractHWDiskStore(String paramString1, String paramString2, String paramString3, long paramLong) {
    this.name = paramString1;
    this.model = paramString2;
    this.serial = paramString3;
    this.size = paramLong;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getModel() {
    return this.model;
  }
  
  public String getSerial() {
    return this.serial;
  }
  
  public long getSize() {
    return this.size;
  }
  
  public String toString() {
    boolean bool = (getReads() > 0L || getWrites() > 0L) ? true : false;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getName()).append(": ");
    stringBuilder.append("(model: ").append(getModel());
    stringBuilder.append(" - S/N: ").append(getSerial()).append(") ");
    stringBuilder.append("size: ").append((getSize() > 0L) ? FormatUtil.formatBytesDecimal(getSize()) : "?").append(", ");
    stringBuilder.append("reads: ").append(bool ? Long.valueOf(getReads()) : "?");
    stringBuilder.append(" (").append(bool ? FormatUtil.formatBytes(getReadBytes()) : "?").append("), ");
    stringBuilder.append("writes: ").append(bool ? Long.valueOf(getWrites()) : "?");
    stringBuilder.append(" (").append(bool ? FormatUtil.formatBytes(getWriteBytes()) : "?").append("), ");
    stringBuilder.append("xfer: ").append(bool ? Long.valueOf(getTransferTime()) : "?");
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */