package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer extends Unknown implements IConnectionPointContainer {
  public ConnectionPointContainer(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT EnumConnectionPoints() {
    byte b = 3;
    throw new UnsupportedOperationException();
  }
  
  public WinNT.HRESULT FindConnectionPoint(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference) {
    byte b = 4;
    return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), paramREFIID, paramPointerByReference }, WinNT.HRESULT.class);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\ConnectionPointContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */