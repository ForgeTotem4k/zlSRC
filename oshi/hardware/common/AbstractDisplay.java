package oshi.hardware.common;

import java.util.Arrays;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.util.EdidUtil;

@Immutable
public abstract class AbstractDisplay implements Display {
  private final byte[] edid;
  
  protected AbstractDisplay(byte[] paramArrayOfbyte) {
    this.edid = Arrays.copyOf(paramArrayOfbyte, paramArrayOfbyte.length);
  }
  
  public byte[] getEdid() {
    return Arrays.copyOf(this.edid, this.edid.length);
  }
  
  public String toString() {
    return EdidUtil.toString(this.edid);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */