package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class TypeInfo extends Unknown implements ITypeInfo {
  public TypeInfo() {}
  
  public TypeInfo(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT GetTypeAttr(PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetTypeComp(PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetFuncDesc(WinDef.UINT paramUINT, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), paramUINT, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetVarDesc(WinDef.UINT paramUINT, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), paramUINT, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetNames(OaIdl.MEMBERID paramMEMBERID, WTypes.BSTR[] paramArrayOfBSTR, WinDef.UINT paramUINT, WinDef.UINTByReference paramUINTByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] { getPointer(), paramMEMBERID, paramArrayOfBSTR, paramUINT, paramUINTByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetRefTypeOfImplType(WinDef.UINT paramUINT, OaIdl.HREFTYPEByReference paramHREFTYPEByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(8, new Object[] { getPointer(), paramUINT, paramHREFTYPEByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetImplTypeFlags(WinDef.UINT paramUINT, IntByReference paramIntByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(9, new Object[] { getPointer(), paramUINT, paramIntByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetIDsOfNames(WTypes.LPOLESTR[] paramArrayOfLPOLESTR, WinDef.UINT paramUINT, OaIdl.MEMBERID[] paramArrayOfMEMBERID) {
    return (WinNT.HRESULT)_invokeNativeObject(10, new Object[] { getPointer(), paramArrayOfLPOLESTR, paramUINT, paramArrayOfMEMBERID }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT Invoke(WinDef.PVOID paramPVOID, OaIdl.MEMBERID paramMEMBERID, WinDef.WORD paramWORD, OleAuto.DISPPARAMS.ByReference paramByReference, Variant.VARIANT.ByReference paramByReference1, OaIdl.EXCEPINFO.ByReference paramByReference2, WinDef.UINTByReference paramUINTByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(11, new Object[] { getPointer(), paramPVOID, paramMEMBERID, paramWORD, paramByReference, paramByReference1, paramByReference2, paramUINTByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetDocumentation(OaIdl.MEMBERID paramMEMBERID, WTypes.BSTRByReference paramBSTRByReference1, WTypes.BSTRByReference paramBSTRByReference2, WinDef.DWORDByReference paramDWORDByReference, WTypes.BSTRByReference paramBSTRByReference3) {
    return (WinNT.HRESULT)_invokeNativeObject(12, new Object[] { getPointer(), paramMEMBERID, paramBSTRByReference1, paramBSTRByReference2, paramDWORDByReference, paramBSTRByReference3 }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetDllEntry(OaIdl.MEMBERID paramMEMBERID, OaIdl.INVOKEKIND paramINVOKEKIND, WTypes.BSTRByReference paramBSTRByReference1, WTypes.BSTRByReference paramBSTRByReference2, WinDef.WORDByReference paramWORDByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(13, new Object[] { getPointer(), paramMEMBERID, paramINVOKEKIND, paramBSTRByReference1, paramBSTRByReference2, paramWORDByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetRefTypeInfo(OaIdl.HREFTYPE paramHREFTYPE, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(14, new Object[] { getPointer(), paramHREFTYPE, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT AddressOfMember(OaIdl.MEMBERID paramMEMBERID, OaIdl.INVOKEKIND paramINVOKEKIND, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(15, new Object[] { getPointer(), paramMEMBERID, paramINVOKEKIND, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT CreateInstance(IUnknown paramIUnknown, Guid.REFIID paramREFIID, PointerByReference paramPointerByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(16, new Object[] { getPointer(), paramIUnknown, paramREFIID, paramPointerByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetMops(OaIdl.MEMBERID paramMEMBERID, WTypes.BSTRByReference paramBSTRByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(17, new Object[] { getPointer(), paramMEMBERID, paramBSTRByReference }, WinNT.HRESULT.class);
  }
  
  public WinNT.HRESULT GetContainingTypeLib(PointerByReference paramPointerByReference, WinDef.UINTByReference paramUINTByReference) {
    return (WinNT.HRESULT)_invokeNativeObject(18, new Object[] { getPointer(), paramPointerByReference, paramUINTByReference }, WinNT.HRESULT.class);
  }
  
  public void ReleaseTypeAttr(OaIdl.TYPEATTR paramTYPEATTR) {
    _invokeNativeVoid(19, new Object[] { getPointer(), paramTYPEATTR });
  }
  
  public void ReleaseFuncDesc(OaIdl.FUNCDESC paramFUNCDESC) {
    _invokeNativeVoid(20, new Object[] { getPointer(), paramFUNCDESC });
  }
  
  public void ReleaseVarDesc(OaIdl.VARDESC paramVARDESC) {
    _invokeNativeVoid(21, new Object[] { getPointer(), paramVARDESC });
  }
  
  public static class ByReference extends TypeInfo implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */