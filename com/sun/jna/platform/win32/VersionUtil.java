package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class VersionUtil {
  public static VerRsrc.VS_FIXEDFILEINFO getFileVersionInfo(String paramString) {
    IntByReference intByReference1 = new IntByReference();
    int i = Version.INSTANCE.GetFileVersionInfoSize(paramString, intByReference1);
    if (i == 0)
      throw new Win32Exception(Native.getLastError()); 
    Memory memory = new Memory(i);
    PointerByReference pointerByReference = new PointerByReference();
    if (!Version.INSTANCE.GetFileVersionInfo(paramString, 0, i, (Pointer)memory))
      throw new Win32Exception(Native.getLastError()); 
    IntByReference intByReference2 = new IntByReference();
    if (!Version.INSTANCE.VerQueryValue((Pointer)memory, "\\", pointerByReference, intByReference2))
      throw new UnsupportedOperationException("Unable to extract version info from the file: \"" + paramString + "\""); 
    VerRsrc.VS_FIXEDFILEINFO vS_FIXEDFILEINFO = new VerRsrc.VS_FIXEDFILEINFO(pointerByReference.getValue());
    vS_FIXEDFILEINFO.read();
    return vS_FIXEDFILEINFO;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\VersionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */