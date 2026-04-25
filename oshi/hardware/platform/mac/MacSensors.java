package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit;
import java.util.Locale;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.mac.SmcUtil;

@ThreadSafe
final class MacSensors extends AbstractSensors {
  private int numFans = 0;
  
  public double queryCpuTemperature() {
    IOKit.IOConnect iOConnect = SmcUtil.smcOpen();
    double d = SmcUtil.smcGetFloat(iOConnect, "TC0P");
    SmcUtil.smcClose(iOConnect);
    return (d > 0.0D) ? d : 0.0D;
  }
  
  public int[] queryFanSpeeds() {
    IOKit.IOConnect iOConnect = SmcUtil.smcOpen();
    if (this.numFans == 0)
      this.numFans = (int)SmcUtil.smcGetLong(iOConnect, "FNum"); 
    int[] arrayOfInt = new int[this.numFans];
    for (byte b = 0; b < this.numFans; b++) {
      arrayOfInt[b] = (int)SmcUtil.smcGetFloat(iOConnect, String.format(Locale.ROOT, "F%dAc", new Object[] { Integer.valueOf(b) }));
    } 
    SmcUtil.smcClose(iOConnect);
    return arrayOfInt;
  }
  
  public double queryCpuVoltage() {
    IOKit.IOConnect iOConnect = SmcUtil.smcOpen();
    double d = SmcUtil.smcGetFloat(iOConnect, "VC0C") / 1000.0D;
    SmcUtil.smcClose(iOConnect);
    return d;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */