package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public abstract class WevtapiUtil {
  public static String EvtGetExtendedStatus() {
    IntByReference intByReference = new IntByReference();
    int i = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, null, intByReference);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference.getValue() == 0)
      return ""; 
    char[] arrayOfChar = new char[intByReference.getValue()];
    i = Wevtapi.INSTANCE.EvtGetExtendedStatus(arrayOfChar.length, arrayOfChar, intByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    return Native.toString(arrayOfChar);
  }
  
  public static Memory EvtRender(Winevt.EVT_HANDLE paramEVT_HANDLE1, Winevt.EVT_HANDLE paramEVT_HANDLE2, int paramInt, IntByReference paramIntByReference) {
    IntByReference intByReference = new IntByReference();
    boolean bool = Wevtapi.INSTANCE.EvtRender(paramEVT_HANDLE1, paramEVT_HANDLE2, paramInt, 0, null, intByReference, paramIntByReference);
    int i = Kernel32.INSTANCE.GetLastError();
    if (!bool && i != 122)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference.getValue());
    bool = Wevtapi.INSTANCE.EvtRender(paramEVT_HANDLE1, paramEVT_HANDLE2, paramInt, (int)memory.size(), (Pointer)memory, intByReference, paramIntByReference);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return memory;
  }
  
  public static String EvtFormatMessage(Winevt.EVT_HANDLE paramEVT_HANDLE1, Winevt.EVT_HANDLE paramEVT_HANDLE2, int paramInt1, int paramInt2, Winevt.EVT_VARIANT[] paramArrayOfEVT_VARIANT, int paramInt3) {
    IntByReference intByReference = new IntByReference();
    boolean bool = Wevtapi.INSTANCE.EvtFormatMessage(paramEVT_HANDLE1, paramEVT_HANDLE2, paramInt1, paramInt2, paramArrayOfEVT_VARIANT, paramInt3, 0, null, intByReference);
    int i = Kernel32.INSTANCE.GetLastError();
    if (!bool && i != 122)
      throw new Win32Exception(i); 
    char[] arrayOfChar = new char[intByReference.getValue()];
    bool = Wevtapi.INSTANCE.EvtFormatMessage(paramEVT_HANDLE1, paramEVT_HANDLE2, paramInt1, paramInt2, paramArrayOfEVT_VARIANT, paramInt3, arrayOfChar.length, arrayOfChar, intByReference);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static Winevt.EVT_VARIANT EvtGetChannelConfigProperty(Winevt.EVT_HANDLE paramEVT_HANDLE, int paramInt) {
    IntByReference intByReference = new IntByReference();
    boolean bool = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(paramEVT_HANDLE, paramInt, 0, 0, null, intByReference);
    int i = Kernel32.INSTANCE.GetLastError();
    if (!bool && i != 122)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference.getValue());
    bool = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(paramEVT_HANDLE, paramInt, 0, (int)memory.size(), (Pointer)memory, intByReference);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Winevt.EVT_VARIANT eVT_VARIANT = new Winevt.EVT_VARIANT((Pointer)memory);
    eVT_VARIANT.read();
    return eVT_VARIANT;
  }
  
  public static String EvtNextPublisherId(Winevt.EVT_HANDLE paramEVT_HANDLE) {
    IntByReference intByReference = new IntByReference();
    boolean bool = Wevtapi.INSTANCE.EvtNextPublisherId(paramEVT_HANDLE, 0, null, intByReference);
    int i = Kernel32.INSTANCE.GetLastError();
    if (!bool && i != 122)
      throw new Win32Exception(i); 
    char[] arrayOfChar = new char[intByReference.getValue()];
    bool = Wevtapi.INSTANCE.EvtNextPublisherId(paramEVT_HANDLE, arrayOfChar.length, arrayOfChar, intByReference);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static Memory EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE paramEVT_HANDLE, int paramInt1, int paramInt2) {
    IntByReference intByReference = new IntByReference();
    boolean bool = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(paramEVT_HANDLE, paramInt1, paramInt2, 0, null, intByReference);
    int i = Kernel32.INSTANCE.GetLastError();
    if (!bool && i != 122)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference.getValue());
    bool = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(paramEVT_HANDLE, paramInt1, paramInt2, (int)memory.size(), (Pointer)memory, intByReference);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return memory;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WevtapiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */