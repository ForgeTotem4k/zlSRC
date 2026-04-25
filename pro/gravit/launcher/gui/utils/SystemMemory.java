package pro.gravit.launcher.gui.utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class SystemMemory {
  private static final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
  
  public static long getPhysicalMemorySize() {
    return systemMXBean.getTotalMemorySize();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\SystemMemory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */