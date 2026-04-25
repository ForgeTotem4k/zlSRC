package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbCoClass extends TlbBase {
  public TlbCoClass(int paramInt, String paramString1, TypeLibUtil paramTypeLibUtil, String paramString2) {
    super(paramInt, paramTypeLibUtil, null);
    TypeInfoUtil typeInfoUtil = paramTypeLibUtil.getTypeInfoUtil(paramInt);
    TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(paramInt);
    String str1 = typeLibDoc.getDocString();
    if (typeLibDoc.getName().length() > 0)
      this.name = typeLibDoc.getName(); 
    logInfo("Type of kind 'CoClass' found: " + this.name);
    createPackageName(paramString1);
    createClassName(this.name);
    setFilename(this.name);
    String str2 = (this.typeLibUtil.getLibAttr()).guid.toGuidString();
    int i = (this.typeLibUtil.getLibAttr()).wMajorVerNum.intValue();
    int j = (this.typeLibUtil.getLibAttr()).wMinorVerNum.intValue();
    String str3 = i + "." + j;
    String str4 = (typeInfoUtil.getTypeAttr()).guid.toGuidString();
    createJavaDocHeader(str2, str3, str1);
    createCLSID(str4);
    createCLSIDName(this.name);
    OaIdl.TYPEATTR tYPEATTR = typeInfoUtil.getTypeAttr();
    int k = tYPEATTR.cImplTypes.intValue();
    String str5 = "";
    for (byte b = 0; b < k; b++) {
      OaIdl.HREFTYPE hREFTYPE = typeInfoUtil.getRefTypeOfImplType(b);
      ITypeInfo iTypeInfo = typeInfoUtil.getRefTypeInfo(hREFTYPE);
      TypeInfoUtil typeInfoUtil1 = new TypeInfoUtil(iTypeInfo);
      createFunctions(typeInfoUtil1, paramString2);
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil1.getDocumentation(new OaIdl.MEMBERID(-1));
      str5 = str5 + typeInfoDoc.getName();
      if (b < k - 1)
        str5 = str5 + ", "; 
    } 
    createInterfaces(str5);
    createContent(this.content);
  }
  
  protected void createFunctions(TypeInfoUtil paramTypeInfoUtil, String paramString) {
    OaIdl.TYPEATTR tYPEATTR = paramTypeInfoUtil.getTypeAttr();
    int i = tYPEATTR.cFuncs.intValue();
    for (byte b = 0; b < i; b++) {
      TlbPropertyPut tlbPropertyPut;
      OaIdl.FUNCDESC fUNCDESC = paramTypeInfoUtil.getFuncDesc(b);
      TlbFunctionVTable tlbFunctionVTable = null;
      if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
        if (isVTableMode()) {
          tlbFunctionVTable = new TlbFunctionVTable(b, this.index, this.typeLibUtil, fUNCDESC, paramTypeInfoUtil);
        } else {
          TlbFunctionDispId tlbFunctionDispId = new TlbFunctionDispId(b, this.index, this.typeLibUtil, fUNCDESC, paramTypeInfoUtil);
        } 
      } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
        TlbPropertyGet tlbPropertyGet = new TlbPropertyGet(b, this.index, this.typeLibUtil, fUNCDESC, paramTypeInfoUtil);
      } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value) {
        tlbPropertyPut = new TlbPropertyPut(b, this.index, this.typeLibUtil, fUNCDESC, paramTypeInfoUtil);
      } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
        tlbPropertyPut = new TlbPropertyPut(b, this.index, this.typeLibUtil, fUNCDESC, paramTypeInfoUtil);
      } 
      if (!isReservedMethod(tlbPropertyPut.getMethodName())) {
        this.content += tlbPropertyPut.getClassBuffer();
        if (b < i - 1)
          this.content += "\n"; 
      } 
      paramTypeInfoUtil.ReleaseFuncDesc(fUNCDESC);
    } 
  }
  
  protected void createJavaDocHeader(String paramString1, String paramString2, String paramString3) {
    replaceVariable("uuid", paramString1);
    replaceVariable("version", paramString2);
    replaceVariable("helpstring", paramString3);
  }
  
  protected void createCLSIDName(String paramString) {
    replaceVariable("clsidname", paramString.toUpperCase());
  }
  
  protected void createCLSID(String paramString) {
    replaceVariable("clsid", paramString);
  }
  
  protected void createInterfaces(String paramString) {
    replaceVariable("interfaces", paramString);
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbCoClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */