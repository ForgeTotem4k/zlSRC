package oshi.software.os.windows;

import java.util.List;
import oshi.driver.windows.registry.InstalledAppsData;
import oshi.software.os.ApplicationInfo;

public final class WindowsInstalledApps {
  public static List<ApplicationInfo> queryInstalledApps() {
    return InstalledAppsData.queryInstalledApps();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsInstalledApps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */