package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class SolarisSensors extends AbstractSensors {
  public double queryCpuTemperature() {
    double d = 0.0D;
    for (String str : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c temperature-sensor")) {
      if (str.trim().startsWith("Temperature:")) {
        int i = ParseUtil.parseLastInt(str, 0);
        if (i > d)
          d = i; 
      } 
    } 
    if (d > 1000.0D)
      d /= 1000.0D; 
    return d;
  }
  
  public int[] queryFanSpeeds() {
    ArrayList<Integer> arrayList = new ArrayList();
    for (String str : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan")) {
      if (str.trim().startsWith("Speed:"))
        arrayList.add(Integer.valueOf(ParseUtil.parseLastInt(str, 0))); 
    } 
    int[] arrayOfInt = new int[arrayList.size()];
    for (byte b = 0; b < arrayList.size(); b++)
      arrayOfInt[b] = ((Integer)arrayList.get(b)).intValue(); 
    return arrayOfInt;
  }
  
  public double queryCpuVoltage() {
    double d = 0.0D;
    for (String str : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
      if (str.trim().startsWith("Voltage:")) {
        d = ParseUtil.parseDoubleOrDefault(str.replace("Voltage:", "").trim(), 0.0D);
        break;
      } 
    } 
    return d;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */