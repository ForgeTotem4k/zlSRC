package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class TypeLib extends Unknown implements ITypeLib {
  public TypeLib() {}
  
  public TypeLib(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinDef.UINT GetTypeInfoCount() {
    return (WinDef.UINT)_invokeNativeObject(3, new Object[] { getPointer() }, WinDef.UINT.class);
  }
  
  public WinNT.HRESULT GetTypeInfo(WinDef.UINT paramUINT, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), paramUINT, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetTypeInfoType(WinDef.UINT paramUINT, OaIdl.TYPEKIND.ByReference paramByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), paramUINT, paramByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetTypeInfoOfGuid(Guid.GUID paramGUID, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), paramGUID, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetLibAttr(PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] { getPointer(), paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetTypeComp(PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(8, new Object[] { getPointer(), paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetDocumentation(int paramInt, WTypes.BSTRByReference paramBSTRByReference1, WTypes.BSTRByReference paramBSTRByReference2, WinDef.DWORDByReference paramDWORDByReference, WTypes.BSTRByReference paramBSTRByReference3) {
    return (WinNT.HRESULT)_invokeNativeObject(9, new Object[] { getPointer(), Integer.valueOf(paramInt), paramBSTRByReference1, paramBSTRByReference2, paramDWORDByReference, paramBSTRByReference3 }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT IsName(WTypes.LPOLESTR paramLPOLESTR, WinDef.ULONG paramULONG, WinDef.BOOLByReference paramBOOLByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(10, new Object[] { getPointer(), paramLPOLESTR, paramULONG, paramBOOLByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT FindName(WTypes.LPOLESTR paramLPOLESTR, WinDef.ULONG paramULONG, Pointer[] paramArrayOfPointer, OaIdl.MEMBERID[] paramArrayOfMEMBERID, WinDef.USHORTByReference paramUSHORTByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(11, new Object[] { getPointer(), paramLPOLESTR, paramULONG, paramArrayOfPointer, paramArrayOfMEMBERID, paramUSHORTByReference }, WinNT.HRESULT.class);
  }
  
  public void ReleaseTLibAttr(OaIdl.TLIBATTR paramTLIBATTR) {
    _invokeNativeObject(12, new Object[] { getPointer(), paramTLIBATTR.getPointer() }, WinNT.HRESULT.class);
  }
  
  public static class ByReference extends TypeLib implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\TypeLib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */