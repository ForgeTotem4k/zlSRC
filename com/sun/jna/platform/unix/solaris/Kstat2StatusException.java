package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Native;

public class Kstat2StatusException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  private final int kstat2Status;
  
  public Kstat2StatusException(int paramInt) {
    this(paramInt, formatMessage(paramInt));
  }
  
  protected Kstat2StatusException(int paramInt, String paramString) {
    super(paramString);
    this.kstat2Status = paramInt;
  }
  
  public int getKstat2Status() {
    return this.kstat2Status;
  }
  
  private static String formatMessage(int paramInt) {
    String str = Kstat2.INSTANCE.kstat2_status_string(paramInt);
    if (paramInt == 10)
      str = str + " (errno=" + Native.getLastError() + ")"; 
    return "Kstat2Status error code " + paramInt + ": " + str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\solaris\Kstat2StatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */