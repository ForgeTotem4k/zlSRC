package com.sun.jna.platform.wince;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

public interface CoreDLL extends WinNT, Library {
  public static final CoreDLL INSTANCE = (CoreDLL)Native.load("coredll", CoreDLL.class, W32APIOptions.UNICODE_OPTIONS);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\wince\CoreDLL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */