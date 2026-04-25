package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.WinNT;

public class COMException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  private final WinNT.HRESULT hresult = null;
  
  public COMException() {
    this("", (Throwable)null);
  }
  
  public COMException(String paramString) {
    this(paramString, (Throwable)null);
  }
  
  public COMException(Throwable paramThrowable) {
    this((String)null, paramThrowable);
  }
  
  public COMException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public COMException(String paramString, WinNT.HRESULT paramHRESULT) {
    super(paramString);
  }
  
  public WinNT.HRESULT getHresult() {
    return this.hresult;
  }
  
  public boolean matchesErrorCode(int paramInt) {
    return (this.hresult != null && this.hresult.intValue() == paramInt);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */