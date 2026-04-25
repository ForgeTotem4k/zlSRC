package com.sun.jna.platform.win32;

public abstract class W32Errors implements WinError {
  public static final boolean SUCCEEDED(int paramInt) {
    return (paramInt >= 0);
  }
  
  public static final boolean FAILED(int paramInt) {
    return (paramInt < 0);
  }
  
  public static final boolean SUCCEEDED(WinNT.HRESULT paramHRESULT) {
    return (paramHRESULT == null || SUCCEEDED(paramHRESULT.intValue()));
  }
  
  public static final boolean FAILED(WinNT.HRESULT paramHRESULT) {
    return (paramHRESULT != null && FAILED(paramHRESULT.intValue()));
  }
  
  public static final int HRESULT_CODE(int paramInt) {
    return paramInt & 0xFFFF;
  }
  
  public static final int SCODE_CODE(int paramInt) {
    return paramInt & 0xFFFF;
  }
  
  public static final int HRESULT_FACILITY(int paramInt) {
    return (paramInt >>= 16) & 0x1FFF;
  }
  
  public static final int SCODE_FACILITY(short paramShort) {
    return (paramShort = (short)(paramShort >> 16)) & 0x1FFF;
  }
  
  public static short HRESULT_SEVERITY(int paramInt) {
    return (short)((paramInt >>= 31) & 0x1);
  }
  
  public static short SCODE_SEVERITY(short paramShort) {
    return (short)((paramShort = (short)(paramShort >> 31)) & 0x1);
  }
  
  public static int MAKE_HRESULT(short paramShort1, short paramShort2, short paramShort3) {
    return paramShort1 << 31 | paramShort2 << 16 | paramShort3;
  }
  
  public static final int MAKE_SCODE(short paramShort1, short paramShort2, short paramShort3) {
    return paramShort1 << 31 | paramShort2 << 16 | paramShort3;
  }
  
  public static final WinNT.HRESULT HRESULT_FROM_WIN32(int paramInt) {
    int i = 7;
    return new WinNT.HRESULT((paramInt <= 0) ? paramInt : (paramInt & 0xFFFF | (i <<= 16) | Integer.MIN_VALUE));
  }
  
  public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int paramInt) {
    int i = 31;
    return paramInt & 0x8000FFFF | (i <<= 16);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\W32Errors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */