package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class Unknown extends COMInvoker implements IUnknown {
  public Unknown() {}
  
  public Unknown(Pointer paramPointer) {
    setPointer(paramPointer);
  }
  
  public WinNT.HRESULT QueryInterface(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(0, new Object[] { getPointer(), paramREFIID, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public int AddRef() {
    return _invokeNativeInt(1, new Object[] { getPointer() });
  }
  
  public int Release() {
    return _invokeNativeInt(2, new Object[] { getPointer() });
  }
  
  public static class ByReference extends Unknown implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\Unknown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */