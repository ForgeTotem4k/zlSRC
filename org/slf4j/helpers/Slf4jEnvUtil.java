package org.slf4j.helpers;

public class Slf4jEnvUtil {
  public static String slf4jVersion() {
    Package package_ = Slf4jEnvUtil.class.getPackage();
    return (package_ == null) ? null : package_.getImplementationVersion();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\Slf4jEnvUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */