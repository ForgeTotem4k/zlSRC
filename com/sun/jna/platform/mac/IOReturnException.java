package com.sun.jna.platform.mac;

public class IOReturnException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  private int ioReturn;
  
  public IOReturnException(int paramInt) {
    this(paramInt, formatMessage(paramInt));
  }
  
  protected IOReturnException(int paramInt, String paramString) {
    super(paramString);
    this.ioReturn = paramInt;
  }
  
  public int getIOReturnCode() {
    return this.ioReturn;
  }
  
  public static int getSystem(int paramInt) {
    return paramInt >> 26 & 0x3F;
  }
  
  public static int getSubSystem(int paramInt) {
    return paramInt >> 14 & 0xFFF;
  }
  
  public static int getCode(int paramInt) {
    return paramInt & 0x3FFF;
  }
  
  private static String formatMessage(int paramInt) {
    return "IOReturn error code: " + paramInt + " (system=" + getSystem(paramInt) + ", subSystem=" + getSubSystem(paramInt) + ", code=" + getCode(paramInt) + ")";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\mac\IOReturnException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */