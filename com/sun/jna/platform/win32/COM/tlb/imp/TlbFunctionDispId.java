package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbFunctionDispId extends TlbAbstractMethod {
  public TlbFunctionDispId(int paramInt1, int paramInt2, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt2, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    String str;
    String[] arrayOfString = paramTypeInfoUtil.getNames(paramFUNCDESC.memid, this.paramCount + 1);
    for (byte b = 0; b < this.paramCount; b++) {
      OaIdl.ELEMDESC eLEMDESC = paramFUNCDESC.lprgelemdescParam.elemDescArg[b];
      String str1 = arrayOfString[b + 1].toLowerCase();
      String str2 = getType(eLEMDESC.tdesc);
      String str3 = replaceJavaKeyword(str1);
      this.methodparams += str2 + " " + str3;
      if (str2.equals("VARIANT")) {
        this.methodvariables += str3;
      } else {
        this.methodvariables += "new VARIANT(" + str3 + ")";
      } 
      if (b < this.paramCount - 1) {
        this.methodparams += ", ";
        this.methodvariables += ", ";
      } 
    } 
    if (this.returnType.equalsIgnoreCase("VARIANT")) {
      str = "pResult";
    } else {
      str = "((" + this.returnType + ") pResult.getValue())";
    } 
    replaceVariable("helpstring", this.docStr);
    replaceVariable("returntype", this.returnType);
    replaceVariable("returnvalue", str);
    replaceVariable("methodname", this.methodName);
    replaceVariable("methodparams", this.methodparams);
    replaceVariable("methodvariables", this.methodvariables);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
    replaceVariable("functionCount", String.valueOf(paramInt1));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbFunctionDispId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */