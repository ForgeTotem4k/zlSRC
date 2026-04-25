package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbFunctionStub extends TlbAbstractMethod {
  public TlbFunctionStub(int paramInt, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    TypeInfoUtil.TypeInfoDoc typeInfoDoc = paramTypeInfoUtil.getDocumentation(paramFUNCDESC.memid);
    String str1 = typeInfoDoc.getName();
    String str2 = typeInfoDoc.getDocString();
    String[] arrayOfString = paramTypeInfoUtil.getNames(paramFUNCDESC.memid, this.paramCount + 1);
    if (this.paramCount > 0)
      this.methodvariables = ", "; 
    for (byte b = 0; b < this.paramCount; b++) {
      OaIdl.ELEMDESC eLEMDESC = paramFUNCDESC.lprgelemdescParam.elemDescArg[b];
      String str = arrayOfString[b + 1].toLowerCase();
      this.methodparams += getType(eLEMDESC.tdesc) + " " + replaceJavaKeyword(str);
      this.methodvariables += str;
      if (b < this.paramCount - 1) {
        this.methodparams += ", ";
        this.methodvariables += ", ";
      } 
    } 
    replaceVariable("helpstring", str2);
    replaceVariable("returntype", this.returnType);
    replaceVariable("methodname", str1);
    replaceVariable("methodparams", this.methodparams);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionStub.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbFunctionStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */