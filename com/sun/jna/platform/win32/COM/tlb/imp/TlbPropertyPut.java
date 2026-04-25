package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbPropertyPut extends TlbAbstractMethod {
  public TlbPropertyPut(int paramInt1, int paramInt2, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt2, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    String[] arrayOfString = paramTypeInfoUtil.getNames(paramFUNCDESC.memid, this.paramCount + 1);
    if (this.paramCount > 0)
      this.methodvariables += ", "; 
    for (byte b = 0; b < this.paramCount; b++) {
      OaIdl.ELEMDESC eLEMDESC = paramFUNCDESC.lprgelemdescParam.elemDescArg[b];
      String str = getType(eLEMDESC);
      this.methodparams += str + " " + replaceJavaKeyword(arrayOfString[b].toLowerCase());
      this.methodvariables += replaceJavaKeyword(arrayOfString[b].toLowerCase());
      if (b < this.paramCount - 1) {
        this.methodparams += ", ";
        this.methodvariables += ", ";
      } 
    } 
    replaceVariable("helpstring", this.docStr);
    replaceVariable("methodname", this.methodName);
    replaceVariable("methodparams", this.methodparams);
    replaceVariable("methodvariables", this.methodvariables);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
    replaceVariable("functionCount", String.valueOf(paramInt1));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPut.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyPut.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */