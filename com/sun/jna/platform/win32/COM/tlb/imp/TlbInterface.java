package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;

public class TlbInterface extends TlbBase {
  public TlbInterface(int paramInt, String paramString, TypeLibUtil paramTypeLibUtil) {
    super(paramInt, paramTypeLibUtil, null);
    TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(paramInt);
    String str = typeLibDoc.getDocString();
    if (typeLibDoc.getName().length() > 0)
      this.name = typeLibDoc.getName(); 
    logInfo("Type of kind 'Interface' found: " + this.name);
    createPackageName(paramString);
    createClassName(this.name);
    setFilename(this.name);
    TypeInfoUtil typeInfoUtil = paramTypeLibUtil.getTypeInfoUtil(paramInt);
    OaIdl.TYPEATTR tYPEATTR = typeInfoUtil.getTypeAttr();
    createJavaDocHeader(tYPEATTR.guid.toGuidString(), str);
    int i = tYPEATTR.cVars.intValue();
    for (byte b = 0; b < i; b++) {
      OaIdl.VARDESC vARDESC = typeInfoUtil.getVarDesc(b);
      Variant.VARIANT.ByReference byReference = vARDESC._vardesc.lpvarValue;
      Object object = byReference.getValue();
      OaIdl.MEMBERID mEMBERID = vARDESC.memid;
      TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(mEMBERID);
      this.content += "\t\t//" + typeInfoDoc.getName() + "\n";
      this.content += "\t\tpublic static final int " + typeInfoDoc.getName() + " = " + object.toString() + ";";
      if (b < i - 1)
        this.content += "\n"; 
    } 
    createContent(this.content);
  }
  
  protected void createJavaDocHeader(String paramString1, String paramString2) {
    replaceVariable("uuid", paramString1);
    replaceVariable("helpstring", paramString2);
  }
  
  protected String getClassTemplate() {
    return "com/sun/jna/platform/win32/COM/tlb/imp/TlbInterface.template";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */