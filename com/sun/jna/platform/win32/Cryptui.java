package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Cryptui extends StdCallLibrary {
  public static final Cryptui INSTANCE = (Cryptui)Native.load("Cryptui", Cryptui.class, W32APIOptions.UNICODE_OPTIONS);
  
  WinCrypt.CERT_CONTEXT.ByReference CryptUIDlgSelectCertificateFromStore(WinCrypt.HCERTSTORE paramHCERTSTORE, WinDef.HWND paramHWND, String paramString1, String paramString2, int paramInt1, int paramInt2, PointerType paramPointerType);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Cryptui.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */