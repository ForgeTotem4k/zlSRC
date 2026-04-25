package oshi.driver.linux.proc;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class UpTime {
  public static double getSystemUptimeSeconds() {
    String str = FileUtil.getStringFromFile(ProcPath.UPTIME);
    int i = str.indexOf(' ');
    return (i < 0) ? 0.0D : ParseUtil.parseDoubleOrDefault(str.substring(0, i), 0.0D);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\UpTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */