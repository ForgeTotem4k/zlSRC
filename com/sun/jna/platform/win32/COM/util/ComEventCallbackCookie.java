package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.WinDef;

public class ComEventCallbackCookie implements IComEventCallbackCookie {
  WinDef.DWORD value;
  
  public ComEventCallbackCookie(WinDef.DWORD paramDWORD) {
    this.value = paramDWORD;
  }
  
  public WinDef.DWORD getValue() {
    return this.value;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\ComEventCallbackCookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */