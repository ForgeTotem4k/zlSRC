package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class COMBindingBaseObject extends COMInvoker {
  public static final WinDef.LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
  
  public static final WinDef.LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE.GetSystemDefaultLCID();
  
  private IUnknown iUnknown;
  
  private IDispatch iDispatch;
  
  private PointerByReference pDispatch = new PointerByReference();
  
  private PointerByReference pUnknown = new PointerByReference();
  
  public COMBindingBaseObject(IDispatch paramIDispatch) {
    this.iDispatch = paramIDispatch;
  }
  
  public COMBindingBaseObject(Guid.CLSID paramCLSID, boolean paramBoolean) {
    this(paramCLSID, paramBoolean, 21);
  }
  
  public COMBindingBaseObject(Guid.CLSID paramCLSID, boolean paramBoolean, int paramInt) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    init(paramBoolean, (Guid.GUID)paramCLSID, paramInt);
  }
  
  public COMBindingBaseObject(String paramString, boolean paramBoolean, int paramInt) throws COMException {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    Guid.CLSID.ByReference byReference = new Guid.CLSID.ByReference();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CLSIDFromProgID(paramString, byReference);
    COMUtils.checkRC(hRESULT);
    init(paramBoolean, (Guid.GUID)byReference, paramInt);
  }
  
  public COMBindingBaseObject(String paramString, boolean paramBoolean) throws COMException {
    this(paramString, paramBoolean, 21);
  }
  
  private void init(boolean paramBoolean, Guid.GUID paramGUID, int paramInt) throws COMException {
    WinNT.HRESULT hRESULT;
    if (paramBoolean) {
      hRESULT = OleAuto.INSTANCE.GetActiveObject(paramGUID, null, this.pUnknown);
      if (COMUtils.SUCCEEDED(hRESULT)) {
        this.iUnknown = new Unknown(this.pUnknown.getValue());
        hRESULT = this.iUnknown.QueryInterface(new Guid.REFIID(IDispatch.IID_IDISPATCH), this.pDispatch);
      } else {
        hRESULT = Ole32.INSTANCE.CoCreateInstance(paramGUID, null, paramInt, (Guid.GUID)IDispatch.IID_IDISPATCH, this.pDispatch);
      } 
    } else {
      hRESULT = Ole32.INSTANCE.CoCreateInstance(paramGUID, null, paramInt, (Guid.GUID)IDispatch.IID_IDISPATCH, this.pDispatch);
    } 
    COMUtils.checkRC(hRESULT);
    this.iDispatch = new Dispatch(this.pDispatch.getValue());
  }
  
  public IDispatch getIDispatch() {
    return this.iDispatch;
  }
  
  public PointerByReference getIDispatchPointer() {
    return this.pDispatch;
  }
  
  public IUnknown getIUnknown() {
    return this.iUnknown;
  }
  
  public PointerByReference getIUnknownPointer() {
    return this.pUnknown;
  }
  
  public void release() {
    if (this.iDispatch != null)
      this.iDispatch.Release(); 
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    WString[] arrayOfWString = { new WString(paramString) };
    OaIdl.DISPIDByReference dISPIDByReference = new OaIdl.DISPIDByReference();
    WinNT.HRESULT hRESULT = this.iDispatch.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), arrayOfWString, 1, LOCALE_USER_DEFAULT, dISPIDByReference);
    COMUtils.checkRC(hRESULT);
    return oleMethod(paramInt, paramByReference, dISPIDByReference.getValue(), paramArrayOfVARIANT);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    int j;
    int i = 0;
    Variant.VARIANT[] arrayOfVARIANT = null;
    OleAuto.DISPPARAMS.ByReference byReference = new OleAuto.DISPPARAMS.ByReference();
    OaIdl.EXCEPINFO.ByReference byReference1 = new OaIdl.EXCEPINFO.ByReference();
    IntByReference intByReference = new IntByReference();
    if (paramArrayOfVARIANT != null && paramArrayOfVARIANT.length > 0) {
      i = paramArrayOfVARIANT.length;
      arrayOfVARIANT = new Variant.VARIANT[i];
      j = i;
      for (byte b = 0; b < i; b++)
        arrayOfVARIANT[b] = paramArrayOfVARIANT[--j]; 
    } 
    if (paramInt == 4)
      byReference.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT }); 
    if (i > 0) {
      byReference.setArgs(arrayOfVARIANT);
      byReference.write();
    } 
    if (paramInt == 1 || paramInt == 2) {
      j = 3;
    } else {
      j = paramInt;
    } 
    WinNT.HRESULT hRESULT = this.iDispatch.Invoke(paramDISPID, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD(j), byReference, paramByReference, byReference1, intByReference);
    COMUtils.checkRC(hRESULT, (OaIdl.EXCEPINFO)byReference1, intByReference);
    return hRESULT;
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramString, new Variant.VARIANT[] { paramVARIANT });
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramDISPID, new Variant.VARIANT[] { paramVARIANT });
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString) throws COMException {
    return oleMethod(paramInt, paramByReference, paramString, (Variant.VARIANT[])null);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID) throws COMException {
    return oleMethod(paramInt, paramByReference, paramDISPID, (Variant.VARIANT[])null);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    if (paramIDispatch == null)
      throw new COMException("pDisp (IDispatch) parameter is null!"); 
    WString[] arrayOfWString = { new WString(paramString) };
    OaIdl.DISPIDByReference dISPIDByReference = new OaIdl.DISPIDByReference();
    WinNT.HRESULT hRESULT = paramIDispatch.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), arrayOfWString, 1, LOCALE_USER_DEFAULT, dISPIDByReference);
    COMUtils.checkRC(hRESULT);
    return oleMethod(paramInt, paramByReference, paramIDispatch, dISPIDByReference.getValue(), paramArrayOfVARIANT);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    int j;
    if (paramIDispatch == null)
      throw new COMException("pDisp (IDispatch) parameter is null!"); 
    int i = 0;
    Variant.VARIANT[] arrayOfVARIANT = null;
    OleAuto.DISPPARAMS.ByReference byReference = new OleAuto.DISPPARAMS.ByReference();
    OaIdl.EXCEPINFO.ByReference byReference1 = new OaIdl.EXCEPINFO.ByReference();
    IntByReference intByReference = new IntByReference();
    if (paramArrayOfVARIANT != null && paramArrayOfVARIANT.length > 0) {
      i = paramArrayOfVARIANT.length;
      arrayOfVARIANT = new Variant.VARIANT[i];
      j = i;
      for (byte b = 0; b < i; b++)
        arrayOfVARIANT[b] = paramArrayOfVARIANT[--j]; 
    } 
    if (paramInt == 4)
      byReference.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT }); 
    if (i > 0) {
      byReference.setArgs(arrayOfVARIANT);
      byReference.write();
    } 
    if (paramInt == 1 || paramInt == 2) {
      j = 3;
    } else {
      j = paramInt;
    } 
    WinNT.HRESULT hRESULT = paramIDispatch.Invoke(paramDISPID, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD(j), byReference, paramByReference, byReference1, intByReference);
    COMUtils.checkRC(hRESULT, (OaIdl.EXCEPINFO)byReference1, intByReference);
    return hRESULT;
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramString, new Variant.VARIANT[] { paramVARIANT });
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramDISPID, new Variant.VARIANT[] { paramVARIANT });
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramString, (Variant.VARIANT[])null);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramDISPID, (Variant.VARIANT[])null);
  }
  
  @Deprecated
  protected void checkFailed(WinNT.HRESULT paramHRESULT) {
    COMUtils.checkRC(paramHRESULT);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMBindingBaseObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */