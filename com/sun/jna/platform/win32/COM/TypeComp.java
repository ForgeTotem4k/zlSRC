package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class TypeComp extends Unknown {
  public TypeComp() {}
  
  public TypeComp(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT Bind(WString paramWString, WinDef.ULONG paramULONG, WinDef.WORD paramWORD, PointerByReference paramPointerByReference, OaIdl.DESCKIND.ByReference paramByReference, OaIdl.BINDPTR.ByReference paramByReference1) {
    return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), paramWString, paramULONG, paramWORD, paramPointerByReference, paramByReference, paramByReference1 }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT BindType(WString paramWString, WinDef.ULONG paramULONG, PointerByReference paramPointerByReference1, PointerByReference paramPointerByReference2) {
    return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), paramWString, paramULONG, paramPointerByReference1, paramPointerByReference2 }, WinNT.HRESULT.class);
  }
  
  public static class ByReference extends TypeComp implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\TypeComp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */