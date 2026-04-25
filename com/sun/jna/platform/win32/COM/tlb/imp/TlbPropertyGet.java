package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbPropertyGet extends TlbAbstractMethod {
  public TlbPropertyGet(int paramInt1, int paramInt2, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt2, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    replaceVariable("helpstring", this.docStr);
    replaceVariable("returntype", this.returnType);
    replaceVariable("methodname", this.methodName);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
    replaceVariable("functionCount", String.valueOf(paramInt1));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGet.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyGet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */