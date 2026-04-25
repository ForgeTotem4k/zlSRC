package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;

public abstract class NtDllUtil {
  public static String getKeyName(WinReg.HKEY paramHKEY) {
    IntByReference intByReference = new IntByReference();
    int i = NtDll.INSTANCE.ZwQueryKey(paramHKEY, 0, null, 0, intByReference);
    if (i != -1073741789 || intByReference.getValue() <= 0)
      throw new Win32Exception(i); 
    Wdm.KEY_BASIC_INFORMATION kEY_BASIC_INFORMATION = new Wdm.KEY_BASIC_INFORMATION(intByReference.getValue());
    i = NtDll.INSTANCE.ZwQueryKey(paramHKEY, 0, kEY_BASIC_INFORMATION, intByReference.getValue(), intByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    return kEY_BASIC_INFORMATION.getName();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\NtDllUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */