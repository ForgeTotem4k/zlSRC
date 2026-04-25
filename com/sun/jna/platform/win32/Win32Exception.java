package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Win32Exception extends LastErrorException {
  private static final long serialVersionUID = 1L;
  
  private WinNT.HRESULT _hr;
  
  private static Method addSuppressedMethod = null;
  
  public WinNT.HRESULT getHR() {
    return this._hr;
  }
  
  public Win32Exception(int paramInt) {
    this(paramInt, W32Errors.HRESULT_FROM_WIN32(paramInt));
  }
  
  public Win32Exception(WinNT.HRESULT paramHRESULT) {
    this(W32Errors.HRESULT_CODE(paramHRESULT.intValue()), paramHRESULT);
  }
  
  protected Win32Exception(int paramInt, WinNT.HRESULT paramHRESULT) {
    this(paramInt, paramHRESULT, Kernel32Util.formatMessage(paramHRESULT));
  }
  
  protected Win32Exception(int paramInt, WinNT.HRESULT paramHRESULT, String paramString) {
    super(paramInt, paramString);
    this._hr = paramHRESULT;
  }
  
  void addSuppressedReflected(Throwable paramThrowable) {
    if (addSuppressedMethod == null)
      return; 
    try {
      addSuppressedMethod.invoke(this, new Object[] { paramThrowable });
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException illegalAccessException) {
      throw new RuntimeException("Failed to call addSuppressedMethod", illegalAccessException);
    } 
  }
  
  static {
    try {
      addSuppressedMethod = Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class });
    } catch (NoSuchMethodException noSuchMethodException) {
    
    } catch (SecurityException securityException) {
      Logger.getLogger(Win32Exception.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", securityException);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Win32Exception.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */