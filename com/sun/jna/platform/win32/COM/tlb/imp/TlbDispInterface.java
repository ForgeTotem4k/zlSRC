package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbDispInterface extends TlbBase {
  public TlbDispInterface(int paramInt, String paramString, TypeLibUtil paramTypeLibUtil) {
    super(paramInt, paramTypeLibUtil, null);
    TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(paramInt);
    String str = typeLibDoc.getDocString();
    if (typeLibDoc.getName().length() > 0)
      this.name = typeLibDoc.getName(); 
    logInfo("Type of kind 'DispInterface' found: " + this.name);
    createPackageName(paramString);
    createClassName(this.name);
    setFilename(this.name);
    TypeInfoUtil typeInfoUtil = paramTypeLibUtil.getTypeInfoUtil(paramInt);
    OaIdl.TYPEATTR tYPEATTR = typeInfoUtil.getTypeAttr();
    createJavaDocHeader(tYPEATTR.guid.toGuidString(), str);
    int i = tYPEATTR.cFuncs.intValue();
    for (byte b = 0; b < i; b++) {
      OaIdl.FUNCDESC fUNCDESC = typeInfoUtil.getFuncDesc(b);
      OaIdl.MEMBERID mEMBERID = fUNCDESC.memid;
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(mEMBERID);
      String str1 = typeInfoDoc.getName();
      TlbFunctionStub tlbFunctionStub = null;
      if (!isReservedMethod(str1)) {
        TlbPropertyPutStub tlbPropertyPutStub;
        if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
          tlbFunctionStub = new TlbFunctionStub(paramInt, paramTypeLibUtil, fUNCDESC, typeInfoUtil);
        } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
          TlbPropertyGetStub tlbPropertyGetStub = new TlbPropertyGetStub(paramInt, paramTypeLibUtil, fUNCDESC, typeInfoUtil);
        } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value) {
          tlbPropertyPutStub = new TlbPropertyPutStub(paramInt, paramTypeLibUtil, fUNCDESC, typeInfoUtil);
        } else if (fUNCDESC.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
          tlbPropertyPutStub = new TlbPropertyPutStub(paramInt, paramTypeLibUtil, fUNCDESC, typeInfoUtil);
        } 
        this.content += tlbPropertyPutStub.getClassBuffer();
        if (b < i - 1)
          this.content += "\n"; 
      } 
      typeInfoUtil.ReleaseFuncDesc(fUNCDESC);
    } 
    createContent(this.content);
  }
  
  protected void createJavaDocHeader(String paramString1, String paramString2) {
    replaceVariable("uuid", paramString1);
    replaceVariable("helpstring", paramString2);
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispInterface.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbDispInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */