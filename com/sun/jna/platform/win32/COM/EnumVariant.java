package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class EnumVariant extends Unknown implements IEnumVariant {
  public static final Guid.IID IID = new Guid.IID("{00020404-0000-0000-C000-000000000046}");
  
  public static final Guid.REFIID REFIID = new Guid.REFIID(IID);
  
  public EnumVariant() {}
  
  public EnumVariant(Pointer paramPointer) {
    setPointer(paramPointer);
  }
  
  public Variant.VARIANT[] Next(int paramInt) {
    Variant.VARIANT[] arrayOfVARIANT1 = new Variant.VARIANT[paramInt];
    IntByReference intByReference = new IntByReference();
    WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), Integer.valueOf(arrayOfVARIANT1.length), arrayOfVARIANT1, intByReference }, WinNT.HRESULT.class);
    COMUtils.checkRC(hRESULT);
    Variant.VARIANT[] arrayOfVARIANT2 = new Variant.VARIANT[intByReference.getValue()];
    System.arraycopy(arrayOfVARIANT1, 0, arrayOfVARIANT2, 0, intByReference.getValue());
    return arrayOfVARIANT2;
  }
  
  public void Skip(int paramInt) {
    WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), Integer.valueOf(paramInt) }, WinNT.HRESULT.class);
    COMUtils.checkRC(hRESULT);
  }
  
  public void Reset() {
    WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer() }, WinNT.HRESULT.class);
    COMUtils.checkRC(hRESULT);
  }
  
  public EnumVariant Clone() {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), pointerByReference }, WinNT.HRESULT.class);
    COMUtils.checkRC(hRESULT);
    return new EnumVariant(pointerByReference.getValue());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\EnumVariant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */