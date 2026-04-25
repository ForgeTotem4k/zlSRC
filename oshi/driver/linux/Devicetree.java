package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.platform.linux.SysPath;

@ThreadSafe
public final class Devicetree {
  public static String queryModel() {
    String str = FileUtil.getStringFromFile(SysPath.MODEL);
    return !str.isEmpty() ? str.replace("Machine: ", "") : null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Devicetree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */