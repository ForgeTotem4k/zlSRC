package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class TypeInfoUtil {
  public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
  
  private ITypeInfo typeInfo;
  
  public TypeInfoUtil(ITypeInfo paramITypeInfo) {
    this.typeInfo = paramITypeInfo;
  }
  
  public OaIdl.TYPEATTR getTypeAttr() {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetTypeAttr(pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new OaIdl.TYPEATTR(pointerByReference.getValue());
  }
  
  public TypeComp getTypeComp() {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetTypeComp(pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new TypeComp(pointerByReference.getValue());
  }
  
  public OaIdl.FUNCDESC getFuncDesc(int paramInt) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetFuncDesc(new WinDef.UINT(paramInt), pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new OaIdl.FUNCDESC(pointerByReference.getValue());
  }
  
  public OaIdl.VARDESC getVarDesc(int paramInt) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetVarDesc(new WinDef.UINT(paramInt), pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new OaIdl.VARDESC(pointerByReference.getValue());
  }
  
  public String[] getNames(OaIdl.MEMBERID paramMEMBERID, int paramInt) {
    WTypes.BSTR[] arrayOfBSTR = new WTypes.BSTR[paramInt];
    WinDef.UINTByReference uINTByReference = new WinDef.UINTByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetNames(paramMEMBERID, arrayOfBSTR, new WinDef.UINT(paramInt), uINTByReference);
    COMUtils.checkRC(hRESULT);
    int i = uINTByReference.getValue().intValue();
    String[] arrayOfString = new String[i];
    for (byte b = 0; b < arrayOfString.length; b++) {
      arrayOfString[b] = arrayOfBSTR[b].getValue();
      OLEAUTO.SysFreeString(arrayOfBSTR[b]);
    } 
    return arrayOfString;
  }
  
  public OaIdl.HREFTYPE getRefTypeOfImplType(int paramInt) {
    OaIdl.HREFTYPEByReference hREFTYPEByReference = new OaIdl.HREFTYPEByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetRefTypeOfImplType(new WinDef.UINT(paramInt), hREFTYPEByReference);
    COMUtils.checkRC(hRESULT);
    return hREFTYPEByReference.getValue();
  }
  
  public int getImplTypeFlags(int paramInt) {
    IntByReference intByReference = new IntByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetImplTypeFlags(new WinDef.UINT(paramInt), intByReference);
    COMUtils.checkRC(hRESULT);
    return intByReference.getValue();
  }
  
  public OaIdl.MEMBERID[] getIDsOfNames(WTypes.LPOLESTR[] paramArrayOfLPOLESTR, int paramInt) {
    OaIdl.MEMBERID[] arrayOfMEMBERID = new OaIdl.MEMBERID[paramInt];
    WinNT.HRESULT hRESULT = this.typeInfo.GetIDsOfNames(paramArrayOfLPOLESTR, new WinDef.UINT(paramInt), arrayOfMEMBERID);
    COMUtils.checkRC(hRESULT);
    return arrayOfMEMBERID;
  }
  
  public Invoke Invoke(WinDef.PVOID paramPVOID, OaIdl.MEMBERID paramMEMBERID, WinDef.WORD paramWORD, OleAuto.DISPPARAMS.ByReference paramByReference) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    OaIdl.EXCEPINFO.ByReference byReference1 = new OaIdl.EXCEPINFO.ByReference();
    WinDef.UINTByReference uINTByReference = new WinDef.UINTByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.Invoke(paramPVOID, paramMEMBERID, paramWORD, paramByReference, byReference, byReference1, uINTByReference);
    COMUtils.checkRC(hRESULT);
    return new Invoke(byReference, byReference1, uINTByReference.getValue().intValue());
  }
  
  public TypeInfoDoc getDocumentation(OaIdl.MEMBERID paramMEMBERID) {
    WTypes.BSTRByReference bSTRByReference1 = new WTypes.BSTRByReference();
    WTypes.BSTRByReference bSTRByReference2 = new WTypes.BSTRByReference();
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
    WTypes.BSTRByReference bSTRByReference3 = new WTypes.BSTRByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetDocumentation(paramMEMBERID, bSTRByReference1, bSTRByReference2, dWORDByReference, bSTRByReference3);
    COMUtils.checkRC(hRESULT);
    TypeInfoDoc typeInfoDoc = new TypeInfoDoc(bSTRByReference1.getString(), bSTRByReference2.getString(), dWORDByReference.getValue().intValue(), bSTRByReference3.getString());
    OLEAUTO.SysFreeString(bSTRByReference1.getValue());
    OLEAUTO.SysFreeString(bSTRByReference2.getValue());
    OLEAUTO.SysFreeString(bSTRByReference3.getValue());
    return typeInfoDoc;
  }
  
  public DllEntry GetDllEntry(OaIdl.MEMBERID paramMEMBERID, OaIdl.INVOKEKIND paramINVOKEKIND) {
    WTypes.BSTRByReference bSTRByReference1 = new WTypes.BSTRByReference();
    WTypes.BSTRByReference bSTRByReference2 = new WTypes.BSTRByReference();
    WinDef.WORDByReference wORDByReference = new WinDef.WORDByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetDllEntry(paramMEMBERID, paramINVOKEKIND, bSTRByReference1, bSTRByReference2, wORDByReference);
    COMUtils.checkRC(hRESULT);
    OLEAUTO.SysFreeString(bSTRByReference1.getValue());
    OLEAUTO.SysFreeString(bSTRByReference2.getValue());
    return new DllEntry(bSTRByReference1.getString(), bSTRByReference2.getString(), wORDByReference.getValue().intValue());
  }
  
  public ITypeInfo getRefTypeInfo(OaIdl.HREFTYPE paramHREFTYPE) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetRefTypeInfo(paramHREFTYPE, pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new TypeInfo(pointerByReference.getValue());
  }
  
  public PointerByReference AddressOfMember(OaIdl.MEMBERID paramMEMBERID, OaIdl.INVOKEKIND paramINVOKEKIND) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.AddressOfMember(paramMEMBERID, paramINVOKEKIND, pointerByReference);
    COMUtils.checkRC(hRESULT);
    return pointerByReference;
  }
  
  public PointerByReference CreateInstance(IUnknown paramIUnknown, Guid.REFIID paramREFIID) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.CreateInstance(paramIUnknown, paramREFIID, pointerByReference);
    COMUtils.checkRC(hRESULT);
    return pointerByReference;
  }
  
  public String GetMops(OaIdl.MEMBERID paramMEMBERID) {
    WTypes.BSTRByReference bSTRByReference = new WTypes.BSTRByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetMops(paramMEMBERID, bSTRByReference);
    COMUtils.checkRC(hRESULT);
    return bSTRByReference.getString();
  }
  
  public ContainingTypeLib GetContainingTypeLib() {
    PointerByReference pointerByReference = new PointerByReference();
    WinDef.UINTByReference uINTByReference = new WinDef.UINTByReference();
    WinNT.HRESULT hRESULT = this.typeInfo.GetContainingTypeLib(pointerByReference, uINTByReference);
    COMUtils.checkRC(hRESULT);
    return new ContainingTypeLib(new TypeLib(pointerByReference.getValue()), uINTByReference.getValue().intValue());
  }
  
  public void ReleaseTypeAttr(OaIdl.TYPEATTR paramTYPEATTR) {
    this.typeInfo.ReleaseTypeAttr(paramTYPEATTR);
  }
  
  public void ReleaseFuncDesc(OaIdl.FUNCDESC paramFUNCDESC) {
    this.typeInfo.ReleaseFuncDesc(paramFUNCDESC);
  }
  
  public void ReleaseVarDesc(OaIdl.VARDESC paramVARDESC) {
    this.typeInfo.ReleaseVarDesc(paramVARDESC);
  }
  
  public static class Invoke {
    private Variant.VARIANT.ByReference pVarResult;
    
    private OaIdl.EXCEPINFO.ByReference pExcepInfo;
    
    private int puArgErr;
    
    public Invoke(Variant.VARIANT.ByReference param1ByReference, OaIdl.EXCEPINFO.ByReference param1ByReference1, int param1Int) {
      this.pVarResult = param1ByReference;
      this.pExcepInfo = param1ByReference1;
      this.puArgErr = param1Int;
    }
    
    public Variant.VARIANT.ByReference getpVarResult() {
      return this.pVarResult;
    }
    
    public OaIdl.EXCEPINFO.ByReference getpExcepInfo() {
      return this.pExcepInfo;
    }
    
    public int getPuArgErr() {
      return this.puArgErr;
    }
  }
  
  public static class TypeInfoDoc {
    private String name;
    
    private String docString;
    
    private int helpContext;
    
    private String helpFile;
    
    public TypeInfoDoc(String param1String1, String param1String2, int param1Int, String param1String3) {
      this.name = param1String1;
      this.docString = param1String2;
      this.helpContext = param1Int;
      this.helpFile = param1String3;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String getDocString() {
      return this.docString;
    }
    
    public int getHelpContext() {
      return this.helpContext;
    }
    
    public String getHelpFile() {
      return this.helpFile;
    }
  }
  
  public static class DllEntry {
    private String dllName;
    
    private String name;
    
    private int ordinal;
    
    public DllEntry(String param1String1, String param1String2, int param1Int) {
      this.dllName = param1String1;
      this.name = param1String2;
      this.ordinal = param1Int;
    }
    
    public String getDllName() {
      return this.dllName;
    }
    
    public void setDllName(String param1String) {
      this.dllName = param1String;
    }
    
    public String getName() {
      return this.name;
    }
    
    public void setName(String param1String) {
      this.name = param1String;
    }
    
    public int getOrdinal() {
      return this.ordinal;
    }
    
    public void setOrdinal(int param1Int) {
      this.ordinal = param1Int;
    }
  }
  
  public static class ContainingTypeLib {
    private ITypeLib typeLib;
    
    private int index;
    
    public ContainingTypeLib(ITypeLib param1ITypeLib, int param1Int) {
      this.typeLib = param1ITypeLib;
      this.index = param1Int;
    }
    
    public ITypeLib getTypeLib() {
      return this.typeLib;
    }
    
    public void setTypeLib(ITypeLib param1ITypeLib) {
      this.typeLib = param1ITypeLib;
    }
    
    public int getIndex() {
      return this.index;
    }
    
    public void setIndex(int param1Int) {
      this.index = param1Int;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\TypeInfoUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */