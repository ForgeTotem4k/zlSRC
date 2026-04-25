package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable extends Unknown implements IRunningObjectTable {
  public RunningObjectTable() {}
  
  public RunningObjectTable(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT Register(WinDef.DWORD paramDWORD, Pointer paramPointer1, Pointer paramPointer2, WinDef.DWORDByReference paramDWORDByReference) {
    byte b = 3;
    return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), paramDWORD, paramPointer1, paramPointer2, paramDWORDByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT Revoke(WinDef.DWORD paramDWORD) {
    byte b = 4;
    return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), paramDWORD }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT IsRunning(Pointer paramPointer) {
    byte b = 5;
    return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), paramPointer }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetObject(Pointer paramPointer, PointerByReference paramPointerByReference) {
    byte b = 6;
    return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), paramPointer, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT NoteChangeTime(WinDef.DWORD paramDWORD, WinBase.FILETIME paramFILETIME) {
    byte b = 7;
    return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] { getPointer(), paramDWORD, paramFILETIME }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetTimeOfLastChange(Pointer paramPointer, WinBase.FILETIME.ByReference paramByReference) {
    byte b = 8;
    return (WinNT.HRESULT)_invokeNativeObject(8, new Object[] { getPointer(), paramPointer, paramByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT EnumRunning(PointerByReference paramPointerByReference) {
    byte b = 9;
    return (WinNT.HRESULT)_invokeNativeObject(9, new Object[] { getPointer(), paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public static class ByReference extends RunningObjectTable implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\RunningObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */