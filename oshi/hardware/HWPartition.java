package oshi.hardware;

import oshi.annotation.concurrent.Immutable;
import oshi.util.FormatUtil;

@Immutable
public class HWPartition {
  private final String identification;
  
  private final String name;
  
  private final String type;
  
  private final String uuid;
  
  private final long size;
  
  private final int major;
  
  private final int minor;
  
  private final String mountPoint;
  
  public HWPartition(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong, int paramInt1, int paramInt2, String paramString5) {
    this.identification = paramString1;
    this.name = paramString2;
    this.type = paramString3;
    this.uuid = paramString4;
    this.size = paramLong;
    this.major = paramInt1;
    this.minor = paramInt2;
    this.mountPoint = paramString5;
  }
  
  public String getIdentification() {
    return this.identification;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getUuid() {
    return this.uuid;
  }
  
  public long getSize() {
    return this.size;
  }
  
  public int getMajor() {
    return this.major;
  }
  
  public int getMinor() {
    return this.minor;
  }
  
  public String getMountPoint() {
    return this.mountPoint;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getIdentification()).append(": ");
    stringBuilder.append(getName()).append(" ");
    stringBuilder.append("(").append(getType()).append(") ");
    stringBuilder.append("Maj:Min=").append(getMajor()).append(":").append(getMinor()).append(", ");
    stringBuilder.append("size: ").append(FormatUtil.formatBytesDecimal(getSize()));
    stringBuilder.append(getMountPoint().isEmpty() ? "" : (" @ " + getMountPoint()));
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\HWPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */