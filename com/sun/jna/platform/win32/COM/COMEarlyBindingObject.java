package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class COMEarlyBindingObject extends COMBindingBaseObject implements IDispatch {
  public COMEarlyBindingObject(Guid.CLSID paramCLSID, boolean paramBoolean, int paramInt) {
    super(paramCLSID, paramBoolean, paramInt);
  }
  
  protected String getStringProperty(OaIdl.DISPID paramDISPID) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramDISPID);
    return byReference.getValue().toString();
  }
  
  protected void setProperty(OaIdl.DISPID paramDISPID, boolean paramBoolean) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramDISPID, new Variant.VARIANT(paramBoolean));
  }
  
  public WinNT.HRESULT QueryInterface(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference) {
    return getIDispatch().QueryInterface(paramREFIID, paramPointerByReference);
  }
  
  public int AddRef() {
    return getIDispatch().AddRef();
  }
  
  public int Release() {
    return getIDispatch().Release();
  }
  
  public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference paramUINTByReference) {
    return getIDispatch().GetTypeInfoCount(paramUINTByReference);
  }
  
  public WinNT.HRESULT GetTypeInfo(WinDef.UINT paramUINT, WinDef.LCID paramLCID, PointerByReference paramPointerByReference) {
    return getIDispatch().GetTypeInfo(paramUINT, paramLCID, paramPointerByReference);
  }
  
  public WinNT.HRESULT GetIDsOfNames(Guid.REFIID paramREFIID, WString[] paramArrayOfWString, int paramInt, WinDef.LCID paramLCID, OaIdl.DISPIDByReference paramDISPIDByReference) {
    return getIDispatch().GetIDsOfNames(paramREFIID, paramArrayOfWString, paramInt, paramLCID, paramDISPIDByReference);
  }
  
  public WinNT.HRESULT Invoke(OaIdl.DISPID paramDISPID, Guid.REFIID paramREFIID, WinDef.LCID paramLCID, WinDef.WORD paramWORD, OleAuto.DISPPARAMS.ByReference paramByReference, Variant.VARIANT.ByReference paramByReference1, OaIdl.EXCEPINFO.ByReference paramByReference2, IntByReference paramIntByReference) {
    return getIDispatch().Invoke(paramDISPID, paramREFIID, paramLCID, paramWORD, paramByReference, paramByReference1, paramByReference2, paramIntByReference);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMEarlyBindingObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */