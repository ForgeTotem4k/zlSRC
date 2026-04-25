package com.sun.jna.platform.win32;

public class VersionHelpers {
  public static boolean IsWindowsVersionOrGreater(int paramInt1, int paramInt2, int paramInt3) {
    WinNT.OSVERSIONINFOEX oSVERSIONINFOEX = new WinNT.OSVERSIONINFOEX();
    oSVERSIONINFOEX.dwOSVersionInfoSize = new WinDef.DWORD(oSVERSIONINFOEX.size());
    oSVERSIONINFOEX.dwMajorVersion = new WinDef.DWORD(paramInt1);
    oSVERSIONINFOEX.dwMinorVersion = new WinDef.DWORD(paramInt2);
    oSVERSIONINFOEX.wServicePackMajor = new WinDef.WORD(paramInt3);
    long l = 0L;
    l = Kernel32.INSTANCE.VerSetConditionMask(l, 2, (byte)3);
    l = Kernel32.INSTANCE.VerSetConditionMask(l, 1, (byte)3);
    l = Kernel32.INSTANCE.VerSetConditionMask(l, 32, (byte)3);
    return Kernel32.INSTANCE.VerifyVersionInfoW(oSVERSIONINFOEX, 35, l);
  }
  
  public static boolean IsWindowsXPOrGreater() {
    return IsWindowsVersionOrGreater(5, 1, 0);
  }
  
  public static boolean IsWindowsXPSP1OrGreater() {
    return IsWindowsVersionOrGreater(5, 1, 1);
  }
  
  public static boolean IsWindowsXPSP2OrGreater() {
    return IsWindowsVersionOrGreater(5, 1, 2);
  }
  
  public static boolean IsWindowsXPSP3OrGreater() {
    return IsWindowsVersionOrGreater(5, 1, 3);
  }
  
  public static boolean IsWindowsVistaOrGreater() {
    return IsWindowsVersionOrGreater(6, 0, 0);
  }
  
  public static boolean IsWindowsVistaSP1OrGreater() {
    return IsWindowsVersionOrGreater(6, 0, 1);
  }
  
  public static boolean IsWindowsVistaSP2OrGreater() {
    return IsWindowsVersionOrGreater(6, 0, 2);
  }
  
  public static boolean IsWindows7OrGreater() {
    return IsWindowsVersionOrGreater(6, 1, 0);
  }
  
  public static boolean IsWindows7SP1OrGreater() {
    return IsWindowsVersionOrGreater(6, 1, 1);
  }
  
  public static boolean IsWindows8OrGreater() {
    return IsWindowsVersionOrGreater(6, 2, 0);
  }
  
  public static boolean IsWindows8Point1OrGreater() {
    return IsWindowsVersionOrGreater(6, 3, 0);
  }
  
  public static boolean IsWindows10OrGreater() {
    return IsWindowsVersionOrGreater(10, 0, 0);
  }
  
  public static boolean IsWindowsServer() {
    WinNT.OSVERSIONINFOEX oSVERSIONINFOEX = new WinNT.OSVERSIONINFOEX();
    oSVERSIONINFOEX.dwOSVersionInfoSize = new WinDef.DWORD(oSVERSIONINFOEX.size());
    oSVERSIONINFOEX.wProductType = 1;
    long l = Kernel32.INSTANCE.VerSetConditionMask(0L, 128, (byte)1);
    return !Kernel32.INSTANCE.VerifyVersionInfoW(oSVERSIONINFOEX, 128, l);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\VersionHelpers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */