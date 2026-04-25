package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

public class TlbPropertyPutStub extends TlbAbstractMethod {
  public TlbPropertyPutStub(int paramInt, TypeLibUtil paramTypeLibUtil, OaIdl.FUNCDESC paramFUNCDESC, TypeInfoUtil paramTypeInfoUtil) {
    super(paramInt, paramTypeLibUtil, paramFUNCDESC, paramTypeInfoUtil);
    TypeInfoUtil.TypeInfoDoc typeInfoDoc = paramTypeInfoUtil.getDocumentation(paramFUNCDESC.memid);
    String str1 = typeInfoDoc.getDocString();
    String str2 = "set" + typeInfoDoc.getName();
    String[] arrayOfString = paramTypeInfoUtil.getNames(paramFUNCDESC.memid, this.paramCount + 1);
    for (byte b = 0; b < this.paramCount; b++) {
      OaIdl.ELEMDESC eLEMDESC = paramFUNCDESC.lprgelemdescParam.elemDescArg[b];
      String str = getType(eLEMDESC);
      this.methodparams += str + " " + replaceJavaKeyword(arrayOfString[b].toLowerCase());
      if (b < this.paramCount - 1)
        this.methodparams += ", "; 
    } 
    replaceVariable("helpstring", str1);
    replaceVariable("methodname", str2);
    replaceVariable("methodparams", this.methodparams);
    replaceVariable("vtableid", String.valueOf(this.vtableId));
    replaceVariable("memberid", String.valueOf(this.memberid));
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbPropertyPutStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */