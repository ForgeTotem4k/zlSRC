package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;

@Immutable
public abstract class AbstractSoundCard implements SoundCard {
  private String kernelVersion;
  
  private String name;
  
  private String codec;
  
  protected AbstractSoundCard(String paramString1, String paramString2, String paramString3) {
    this.kernelVersion = paramString1;
    this.name = paramString2;
    this.codec = paramString3;
  }
  
  public String getDriverVersion() {
    return this.kernelVersion;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getCodec() {
    return this.codec;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SoundCard@");
    stringBuilder.append(Integer.toHexString(hashCode()));
    stringBuilder.append(" [name=");
    stringBuilder.append(this.name);
    stringBuilder.append(", kernelVersion=");
    stringBuilder.append(this.kernelVersion);
    stringBuilder.append(", codec=");
    stringBuilder.append(this.codec);
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */