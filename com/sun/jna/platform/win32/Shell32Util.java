package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public abstract class Shell32Util {
  public static String getFolderPath(WinDef.HWND paramHWND, int paramInt, WinDef.DWORD paramDWORD) {
    char[] arrayOfChar = new char[260];
    WinNT.HRESULT hRESULT = Shell32.INSTANCE.SHGetFolderPath(paramHWND, paramInt, null, paramDWORD, arrayOfChar);
    if (!hRESULT.equals(W32Errors.S_OK))
      throw new Win32Exception(hRESULT); 
    return Native.toString(arrayOfChar);
  }
  
  public static String getFolderPath(int paramInt) {
    return getFolderPath(null, paramInt, ShlObj.SHGFP_TYPE_CURRENT);
  }
  
  public static String getKnownFolderPath(Guid.GUID paramGUID) throws Win32Exception {
    int i = ShlObj.KNOWN_FOLDER_FLAG.NONE.getFlag();
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HANDLE hANDLE = null;
    WinNT.HRESULT hRESULT = Shell32.INSTANCE.SHGetKnownFolderPath(paramGUID, i, hANDLE, pointerByReference);
    if (!W32Errors.SUCCEEDED(hRESULT.intValue()))
      throw new Win32Exception(hRESULT); 
    String str = pointerByReference.getValue().getWideString(0L);
    Ole32.INSTANCE.CoTaskMemFree(pointerByReference.getValue());
    return str;
  }
  
  public static final String getSpecialFolderPath(int paramInt, boolean paramBoolean) {
    char[] arrayOfChar = new char[260];
    if (!Shell32.INSTANCE.SHGetSpecialFolderPath(null, arrayOfChar, paramInt, paramBoolean))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static final String[] CommandLineToArgv(String paramString) {
    WString wString = new WString(paramString);
    IntByReference intByReference = new IntByReference();
    Pointer pointer = Shell32.INSTANCE.CommandLineToArgvW(wString, intByReference);
    if (pointer != null)
      try {
        return pointer.getWideStringArray(0L, intByReference.getValue());
      } finally {
        Kernel32.INSTANCE.LocalFree(pointer);
      }  
    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Shell32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */