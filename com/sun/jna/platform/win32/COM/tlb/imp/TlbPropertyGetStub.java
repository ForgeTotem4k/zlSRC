package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbPropertyGetStub extends TlbAbstractMethod {
  public TlbPropertyGetStub(int paramInt, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    TypeInfoUtil.TypeInfoDoc typeInfoDoc = paramTypeInfoUtil.getDocumentation(paramFUNCDESC.memid);
    String str1 = typeInfoDoc.getDocString();
    String str2 = "get" + typeInfoDoc.getName();
    replaceVariable("helpstring", str1);
    replaceVariable("returntype", this.returnType);
    replaceVariable("methodname", str2);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGetStub.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyGetStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */