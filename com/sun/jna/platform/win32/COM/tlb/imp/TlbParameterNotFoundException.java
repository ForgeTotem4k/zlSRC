package com.sun.jna.platform.win32.COM.tlb.imp;

public class TlbParameterNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public TlbParameterNotFoundException() {}
  
  public TlbParameterNotFoundException(String paramString) {
    super(paramString);
  }
  
  public TlbParameterNotFoundException(Throwable paramThrowable) {
    super(paramThrowable);
  }
  
  public TlbParameterNotFoundException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbParameterNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */