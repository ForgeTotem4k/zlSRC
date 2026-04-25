package org.slf4j.event;

public enum Level {
  ERROR(40, "ERROR"),
  WARN(30, "WARN"),
  INFO(20, "INFO"),
  DEBUG(10, "DEBUG"),
  TRACE(0, "TRACE");
  
  private final int levelInt;
  
  private final String levelStr;
  
  Level(int paramInt1, String paramString1) {
    this.levelInt = paramInt1;
    this.levelStr = paramString1;
  }
  
  public int toInt() {
    return this.levelInt;
  }
  
  public static Level intToLevel(int paramInt) {
    switch (paramInt) {
      case 0:
        return TRACE;
      case 10:
        return DEBUG;
      case 20:
        return INFO;
      case 30:
        return WARN;
      case 40:
        return ERROR;
    } 
    throw new IllegalArgumentException("Level integer [" + paramInt + "] not recognized.");
  }
  
  public String toString() {
    return this.levelStr;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\event\Level.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */