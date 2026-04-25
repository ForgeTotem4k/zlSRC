package org.slf4j;

public class LoggerFactoryFriend {
  public static void reset() {
    LoggerFactory.reset();
  }
  
  public static void setDetectLoggerNameMismatch(boolean paramBoolean) {
    LoggerFactory.DETECT_LOGGER_NAME_MISMATCH = paramBoolean;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\LoggerFactoryFriend.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */