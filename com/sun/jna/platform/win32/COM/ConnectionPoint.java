package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class ConnectionPoint extends Unknown implements IConnectionPoint {
  public ConnectionPoint(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT GetConnectionInterface(Guid.IID paramIID) {
    byte b = 3;
    return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), paramIID }, WinNT.HRESULT.class);
  }
  
  void GetConnectionPointContainer() {
    byte b = 4;
  }
  
  public WinNT.HRESULT Advise(IUnknownCallback paramIUnknownCallback, WinDef.DWORDByReference paramDWORDByReference) {
    byte b = 5;
    return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), paramIUnknownCallback.getPointer(), paramDWORDByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT Unadvise(WinDef.DWORD paramDWORD) {
    byte b = 6;
    return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), paramDWORD }, WinNT.HRESULT.class);
  }
  
  void EnumConnections() {
    byte b = 7;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\ConnectionPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */