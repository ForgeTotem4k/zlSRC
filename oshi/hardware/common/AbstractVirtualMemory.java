package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractVirtualMemory implements VirtualMemory {
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Swap Used/Avail: ");
    stringBuilder.append(FormatUtil.formatBytes(getSwapUsed()));
    stringBuilder.append("/");
    stringBuilder.append(FormatUtil.formatBytes(getSwapTotal()));
    stringBuilder.append(", Virtual Memory In Use/Max=");
    stringBuilder.append(FormatUtil.formatBytes(getVirtualInUse()));
    stringBuilder.append("/");
    stringBuilder.append(FormatUtil.formatBytes(getVirtualMax()));
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */