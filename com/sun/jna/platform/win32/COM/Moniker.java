package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class Moniker extends Unknown implements IMoniker {
  static final int vTableIdStart = 7;
  
  public Moniker() {}
  
  public Moniker(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public void BindToObject() {
    byte b = 8;
    throw new UnsupportedOperationException();
  }
  
  public void BindToStorage() {
    byte b = 9;
    throw new UnsupportedOperationException();
  }
  
  public void Reduce() {
    byte b = 10;
    throw new UnsupportedOperationException();
  }
  
  public void ComposeWith() {
    byte b = 11;
    throw new UnsupportedOperationException();
  }
  
  public void Enum() {
    byte b = 12;
    throw new UnsupportedOperationException();
  }
  
  public void IsEqual() {
    byte b = 13;
    throw new UnsupportedOperationException();
  }
  
  public void Hash() {
    byte b = 14;
    throw new UnsupportedOperationException();
  }
  
  public void IsRunning() {
    byte b = 15;
    throw new UnsupportedOperationException();
  }
  
  public void GetTimeOfLastChange() {
    byte b = 16;
    throw new UnsupportedOperationException();
  }
  
  public void Inverse() {
    byte b = 17;
    throw new UnsupportedOperationException();
  }
  
  public void CommonPrefixWith() {
    byte b = 18;
    throw new UnsupportedOperationException();
  }
  
  public void RelativePathTo() {
    byte b = 19;
    throw new UnsupportedOperationException();
  }
  
  public String GetDisplayName(Pointer paramPointer1, Pointer paramPointer2) {
    byte b = 20;
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(20, new Object[] { getPointer(), paramPointer1, paramPointer2, pointerByReference }, WinNT.HRESULT.class);
    COMUtils.checkRC(hRESULT);
    Pointer pointer = pointerByReference.getValue();
    if (pointer == null)
      return null; 
    WTypes.LPOLESTR lPOLESTR = new WTypes.LPOLESTR(pointer);
    String str = lPOLESTR.getValue();
    Ole32.INSTANCE.CoTaskMemFree(pointer);
    return str;
  }
  
  public void ParseDisplayName() {
    byte b = 21;
    throw new UnsupportedOperationException();
  }
  
  public void IsSystemMoniker() {
    byte b = 22;
    throw new UnsupportedOperationException();
  }
  
  public boolean IsDirty() {
    throw new UnsupportedOperationException();
  }
  
  public void Load(IStream paramIStream) {
    throw new UnsupportedOperationException();
  }
  
  public void Save(IStream paramIStream) {
    throw new UnsupportedOperationException();
  }
  
  public void GetSizeMax() {
    throw new UnsupportedOperationException();
  }
  
  public Guid.CLSID GetClassID() {
    throw new UnsupportedOperationException();
  }
  
  public static class ByReference extends Moniker implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\Moniker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */