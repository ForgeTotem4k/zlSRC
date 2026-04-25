package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class TypeLibUtil {
  public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
  
  private ITypeLib typelib;
  
  private WinDef.LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
  
  private String name;
  
  private String docString;
  
  private int helpContext;
  
  private String helpFile;
  
  public TypeLibUtil(String paramString, int paramInt1, int paramInt2) {
    Guid.CLSID.ByReference byReference = new Guid.CLSID.ByReference();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CLSIDFromString(paramString, byReference);
    COMUtils.checkRC(hRESULT);
    PointerByReference pointerByReference = new PointerByReference();
    hRESULT = OleAuto.INSTANCE.LoadRegTypeLib((Guid.GUID)byReference, paramInt1, paramInt2, this.lcid, pointerByReference);
    COMUtils.checkRC(hRESULT);
    this.typelib = new TypeLib(pointerByReference.getValue());
    initTypeLibInfo();
  }
  
  public TypeLibUtil(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = OleAuto.INSTANCE.LoadTypeLib(paramString, pointerByReference);
    COMUtils.checkRC(hRESULT);
    this.typelib = new TypeLib(pointerByReference.getValue());
    initTypeLibInfo();
  }
  
  private void initTypeLibInfo() {
    TypeLibDoc typeLibDoc = getDocumentation(-1);
    this.name = typeLibDoc.getName();
    this.docString = typeLibDoc.getDocString();
    this.helpContext = typeLibDoc.getHelpContext();
    this.helpFile = typeLibDoc.getHelpFile();
  }
  
  public int getTypeInfoCount() {
    return this.typelib.GetTypeInfoCount().intValue();
  }
  
  public OaIdl.TYPEKIND getTypeInfoType(int paramInt) {
    OaIdl.TYPEKIND.ByReference byReference = new OaIdl.TYPEKIND.ByReference();
    WinNT.HRESULT hRESULT = this.typelib.GetTypeInfoType(new WinDef.UINT(paramInt), byReference);
    COMUtils.checkRC(hRESULT);
    return (OaIdl.TYPEKIND)byReference;
  }
  
  public ITypeInfo getTypeInfo(int paramInt) {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typelib.GetTypeInfo(new WinDef.UINT(paramInt), pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new TypeInfo(pointerByReference.getValue());
  }
  
  public TypeInfoUtil getTypeInfoUtil(int paramInt) {
    return new TypeInfoUtil(getTypeInfo(paramInt));
  }
  
  public OaIdl.TLIBATTR getLibAttr() {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typelib.GetLibAttr(pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new OaIdl.TLIBATTR(pointerByReference.getValue());
  }
  
  public TypeComp GetTypeComp() {
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.typelib.GetTypeComp(pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new TypeComp(pointerByReference.getValue());
  }
  
  public TypeLibDoc getDocumentation(int paramInt) {
    WTypes.BSTRByReference bSTRByReference1 = new WTypes.BSTRByReference();
    WTypes.BSTRByReference bSTRByReference2 = new WTypes.BSTRByReference();
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
    WTypes.BSTRByReference bSTRByReference3 = new WTypes.BSTRByReference();
    WinNT.HRESULT hRESULT = this.typelib.GetDocumentation(paramInt, bSTRByReference1, bSTRByReference2, dWORDByReference, bSTRByReference3);
    COMUtils.checkRC(hRESULT);
    TypeLibDoc typeLibDoc = new TypeLibDoc(bSTRByReference1.getString(), bSTRByReference2.getString(), dWORDByReference.getValue().intValue(), bSTRByReference3.getString());
    OLEAUTO.SysFreeString(bSTRByReference1.getValue());
    OLEAUTO.SysFreeString(bSTRByReference2.getValue());
    OLEAUTO.SysFreeString(bSTRByReference3.getValue());
    return typeLibDoc;
  }
  
  public IsName IsName(String paramString, int paramInt) {
    WTypes.LPOLESTR lPOLESTR = new WTypes.LPOLESTR(paramString);
    WinDef.ULONG uLONG = new WinDef.ULONG(paramInt);
    WinDef.BOOLByReference bOOLByReference = new WinDef.BOOLByReference();
    WinNT.HRESULT hRESULT = this.typelib.IsName(lPOLESTR, uLONG, bOOLByReference);
    COMUtils.checkRC(hRESULT);
    return new IsName(lPOLESTR.getValue(), bOOLByReference.getValue().booleanValue());
  }
  
  public FindName FindName(String paramString, int paramInt, short paramShort) {
    Pointer pointer = Ole32.INSTANCE.CoTaskMemAlloc((paramString.length() + 1L) * Native.WCHAR_SIZE);
    WTypes.LPOLESTR lPOLESTR = new WTypes.LPOLESTR(pointer);
    lPOLESTR.setValue(paramString);
    WinDef.ULONG uLONG = new WinDef.ULONG(paramInt);
    WinDef.USHORTByReference uSHORTByReference = new WinDef.USHORTByReference(paramShort);
    Pointer[] arrayOfPointer = new Pointer[paramShort];
    OaIdl.MEMBERID[] arrayOfMEMBERID = new OaIdl.MEMBERID[paramShort];
    WinNT.HRESULT hRESULT = this.typelib.FindName(lPOLESTR, uLONG, arrayOfPointer, arrayOfMEMBERID, uSHORTByReference);
    COMUtils.checkRC(hRESULT);
    FindName findName = new FindName(lPOLESTR.getValue(), arrayOfPointer, arrayOfMEMBERID, uSHORTByReference.getValue().shortValue());
    Ole32.INSTANCE.CoTaskMemFree(pointer);
    return findName;
  }
  
  public void ReleaseTLibAttr(OaIdl.TLIBATTR paramTLIBATTR) {
    this.typelib.ReleaseTLibAttr(paramTLIBATTR);
  }
  
  public WinDef.LCID getLcid() {
    return this.lcid;
  }
  
  public ITypeLib getTypelib() {
    return this.typelib;
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
  
  public static class TypeLibDoc {
    private String name;
    
    private String docString;
    
    private int helpContext;
    
    private String helpFile;
    
    public TypeLibDoc(String param1String1, String param1String2, int param1Int, String param1String3) {
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
  
  public static class IsName {
    private String nameBuf;
    
    private boolean name;
    
    public IsName(String param1String, boolean param1Boolean) {
      this.nameBuf = param1String;
      this.name = param1Boolean;
    }
    
    public String getNameBuf() {
      return this.nameBuf;
    }
    
    public boolean isName() {
      return this.name;
    }
  }
  
  public static class FindName {
    private String nameBuf;
    
    private Pointer[] pTInfo;
    
    private OaIdl.MEMBERID[] rgMemId;
    
    private short pcFound;
    
    FindName(String param1String, Pointer[] param1ArrayOfPointer, OaIdl.MEMBERID[] param1ArrayOfMEMBERID, short param1Short) {
      this.nameBuf = param1String;
      this.pTInfo = new Pointer[param1Short];
      this.rgMemId = new OaIdl.MEMBERID[param1Short];
      this.pcFound = param1Short;
      System.arraycopy(param1ArrayOfPointer, 0, this.pTInfo, 0, param1Short);
      System.arraycopy(param1ArrayOfMEMBERID, 0, this.rgMemId, 0, param1Short);
    }
    
    public String getNameBuf() {
      return this.nameBuf;
    }
    
    public ITypeInfo[] getTInfo() {
      ITypeInfo[] arrayOfITypeInfo = new ITypeInfo[this.pcFound];
      for (byte b = 0; b < this.pcFound; b++)
        arrayOfITypeInfo[b] = new TypeInfo(this.pTInfo[b]); 
      return arrayOfITypeInfo;
    }
    
    public OaIdl.MEMBERID[] getMemId() {
      return this.rgMemId;
    }
    
    public short getFound() {
      return this.pcFound;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\TypeLibUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */