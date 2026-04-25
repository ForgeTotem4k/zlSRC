package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;

@Immutable
public abstract class AbstractGraphicsCard implements GraphicsCard {
  private final String name;
  
  private final String deviceId;
  
  private final String vendor;
  
  private final String versionInfo;
  
  private long vram;
  
  protected AbstractGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    this.name = paramString1;
    this.deviceId = paramString2;
    this.vendor = paramString3;
    this.versionInfo = paramString4;
    this.vram = paramLong;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDeviceId() {
    return this.deviceId;
  }
  
  public String getVendor() {
    return this.vendor;
  }
  
  public String getVersionInfo() {
    return this.versionInfo;
  }
  
  public long getVRam() {
    return this.vram;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("GraphicsCard@");
    stringBuilder.append(Integer.toHexString(hashCode()));
    stringBuilder.append(" [name=");
    stringBuilder.append(this.name);
    stringBuilder.append(", deviceId=");
    stringBuilder.append(this.deviceId);
    stringBuilder.append(", vendor=");
    stringBuilder.append(this.vendor);
    stringBuilder.append(", vRam=");
    stringBuilder.append(this.vram);
    stringBuilder.append(", versionInfo=[");
    stringBuilder.append(this.versionInfo);
    stringBuilder.append("]]");
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */