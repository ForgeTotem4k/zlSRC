package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.Arrays;

public abstract class PsapiUtil {
  public static int[] enumProcesses() {
    byte b = 0;
    int[] arrayOfInt = null;
    IntByReference intByReference = new IntByReference();
    while (true) {
      b += true;
      arrayOfInt = new int[b];
      if (!Psapi.INSTANCE.EnumProcesses(arrayOfInt, b * 4, intByReference))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      if (b != intByReference.getValue() / 4)
        return Arrays.copyOf(arrayOfInt, intByReference.getValue() / 4); 
    } 
  }
  
  public static String GetProcessImageFileName(WinNT.HANDLE paramHANDLE) {
    char c = 'ࠀ';
    while (true) {
      char[] arrayOfChar = new char[c];
      int i = Psapi.INSTANCE.GetProcessImageFileName(paramHANDLE, arrayOfChar, arrayOfChar.length);
      if (i == 0) {
        if (Native.getLastError() != 122)
          throw new Win32Exception(Native.getLastError()); 
        c += 'ࠀ';
        continue;
      } 
      return Native.toString(arrayOfChar);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\PsapiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */