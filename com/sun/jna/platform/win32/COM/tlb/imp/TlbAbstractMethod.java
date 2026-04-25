package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public abstract class TlbAbstractMethod extends TlbBase implements Variant {
  protected TypeInfoUtil.TypeInfoDoc typeInfoDoc;
  
  protected String methodName;
  
  protected String docStr;
  
  protected short vtableId;
  
  protected OaIdl.MEMBERID memberid;
  
  protected short paramCount;
  
  protected String returnType;
  
  protected String methodparams = "";
  
  protected String methodvariables = "";
  
  public TlbAbstractMethod(int paramInt, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt, paramTypeLibUtil, paramTypeInfoUtil);
    this.typeInfoDoc = paramTypeInfoUtil.getDocumentation(paramFUNCDESC.memid);
    this.methodName = this.typeInfoDoc.getName();
    this.docStr = this.typeInfoDoc.getDocString();
    this.vtableId = paramFUNCDESC.oVft.shortValue();
    this.memberid = paramFUNCDESC.memid;
    this.paramCount = paramFUNCDESC.cParams.shortValue();
    this.returnType = getType(paramFUNCDESC);
  }
  
  public TypeInfoUtil.TypeInfoDoc getTypeInfoDoc() {
    return this.typeInfoDoc;
  }
  
  public String getMethodName() {
    return this.methodName;
  }
  
  public String getDocStr() {
    return this.docStr;
  }
  
  protected String getVarType(WTypes.VARTYPE paramVARTYPE) {
    switch (paramVARTYPE.intValue()) {
      case 0:
        return "";
      case 1:
        return "null";
      case 2:
        return "short";
      case 3:
        return "int";
      case 4:
        return "float";
      case 5:
        return "double";
      case 6:
        return OaIdl.CURRENCY.class.getSimpleName();
      case 7:
        return OaIdl.DATE.class.getSimpleName();
      case 8:
        return WTypes.BSTR.class.getSimpleName();
      case 9:
        return IDispatch.class.getSimpleName();
      case 10:
        return WinDef.SCODE.class.getSimpleName();
      case 11:
        return WinDef.BOOL.class.getSimpleName();
      case 12:
        return Variant.VARIANT.class.getSimpleName();
      case 13:
        return IUnknown.class.getSimpleName();
      case 14:
        return OaIdl.DECIMAL.class.getSimpleName();
      case 16:
        return WinDef.CHAR.class.getSimpleName();
      case 17:
        return WinDef.UCHAR.class.getSimpleName();
      case 18:
        return WinDef.USHORT.class.getSimpleName();
      case 19:
        return WinDef.UINT.class.getSimpleName();
      case 20:
        return WinDef.LONG.class.getSimpleName();
      case 21:
        return WinDef.ULONG.class.getSimpleName();
      case 22:
        return "int";
      case 23:
        return WinDef.UINT.class.getSimpleName();
      case 24:
        return WinDef.PVOID.class.getSimpleName();
      case 25:
        return WinNT.HRESULT.class.getSimpleName();
      case 26:
        return Pointer.class.getSimpleName();
      case 27:
        return "safearray";
      case 28:
        return "carray";
      case 29:
        return "userdefined";
      case 30:
        return WTypes.LPSTR.class.getSimpleName();
      case 31:
        return WTypes.LPWSTR.class.getSimpleName();
      case 36:
        return "record";
      case 37:
        return WinDef.INT_PTR.class.getSimpleName();
      case 38:
        return WinDef.UINT_PTR.class.getSimpleName();
      case 64:
        return WinBase.FILETIME.class.getSimpleName();
      case 66:
        return "steam";
      case 67:
        return "storage";
      case 68:
        return "steamed_object";
      case 69:
        return "stored_object";
      case 70:
        return "blob_object";
      case 71:
        return "cf";
      case 72:
        return Guid.CLSID.class.getSimpleName();
      case 73:
        return "";
      case 4096:
        return "";
      case 8192:
        return "";
      case 16384:
        return WinDef.PVOID.class.getSimpleName();
      case 32768:
        return "";
      case 65535:
        return "illegal";
    } 
    return null;
  }
  
  protected String getUserdefinedType(OaIdl.HREFTYPE paramHREFTYPE) {
    ITypeInfo iTypeInfo = this.typeInfoUtil.getRefTypeInfo(paramHREFTYPE);
    TypeInfoUtil typeInfoUtil = new TypeInfoUtil(iTypeInfo);
    TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(OaIdl.MEMBERID_NIL);
    return typeInfoDoc.getName();
  }
  
  protected String getType(OaIdl.FUNCDESC paramFUNCDESC) {
    OaIdl.ELEMDESC eLEMDESC = paramFUNCDESC.elemdescFunc;
    return getType(eLEMDESC);
  }
  
  protected String getType(OaIdl.ELEMDESC paramELEMDESC) {
    OaIdl.TYPEDESC tYPEDESC = paramELEMDESC.tdesc;
    return getType(tYPEDESC);
  }
  
  protected String getType(OaIdl.TYPEDESC paramTYPEDESC) {
    WTypes.VARTYPE vARTYPE = paramTYPEDESC.vt;
    String str = "not_defined";
    if (vARTYPE.intValue() == 26) {
      OaIdl.TYPEDESC.ByReference byReference = paramTYPEDESC._typedesc.getLptdesc();
      str = getType((OaIdl.TYPEDESC)byReference);
    } else if (vARTYPE.intValue() == 27 || vARTYPE.intValue() == 28) {
      OaIdl.TYPEDESC tYPEDESC = (paramTYPEDESC._typedesc.getLpadesc()).tdescElem;
      str = getType(tYPEDESC);
    } else if (vARTYPE.intValue() == 29) {
      OaIdl.HREFTYPE hREFTYPE = paramTYPEDESC._typedesc.hreftype;
      str = getUserdefinedType(hREFTYPE);
    } else {
      str = getVarType(vARTYPE);
    } 
    return str;
  }
  
  protected String replaceJavaKeyword(String paramString) {
    return paramString.equals("final") ? ("_" + paramString) : (paramString.equals("default") ? ("_" + paramString) : (paramString.equals("case") ? ("_" + paramString) : (paramString.equals("char") ? ("_" + paramString) : (paramString.equals("private") ? ("_" + paramString) : (paramString.equals("default") ? ("_" + paramString) : paramString)))));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbAbstractMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */