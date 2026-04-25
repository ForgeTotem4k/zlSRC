package com.sun.jna.platform.win32.COM.tlb.imp;

import java.util.Hashtable;

public class TlbCmdlineArgs extends Hashtable<String, String> implements TlbConst {
  private static final long serialVersionUID = 1L;
  
  public TlbCmdlineArgs(String[] paramArrayOfString) {
    readCmdArgs(paramArrayOfString);
  }
  
  public int getIntParam(String paramString) {
    String str = getRequiredParam(paramString);
    return Integer.parseInt(str);
  }
  
  public String getParam(String paramString) {
    return get(paramString);
  }
  
  public String getRequiredParam(String paramString) {
    String str = getParam(paramString);
    if (str == null)
      throw new TlbParameterNotFoundException("Commandline parameter not found: " + paramString); 
    return str;
  }
  
  private void readCmdArgs(String[] paramArrayOfString) {
    if (paramArrayOfString.length < 2)
      showCmdHelp(); 
    byte b = 0;
    while (b < paramArrayOfString.length) {
      String str1 = paramArrayOfString[b];
      String str2 = paramArrayOfString[b + 1];
      if (str1.startsWith("-") && !str2.startsWith("-")) {
        put(str1.substring(1), str2);
        b += 2;
        continue;
      } 
      showCmdHelp();
    } 
  }
  
  public boolean isTlbFile() {
    return containsKey("tlb.file");
  }
  
  public boolean isTlbId() {
    return containsKey("tlb.id");
  }
  
  public String getBindingMode() {
    return containsKey("bind.mode") ? getParam("bind.mode") : "vtable";
  }
  
  public void showCmdHelp() {
    String str = "usage: TlbImp [-tlb.id -tlb.major.version -tlb.minor.version] [-tlb.file] [-bind.mode vTable, dispId] [-output.dir]\n\noptions:\n-tlb.id               The guid of the type library.\n-tlb.major.version    The major version of the type library.\n-tlb.minor.version    The minor version of the type library.\n-tlb.file             The file name containing the type library.\n-bind.mode            The binding mode used to create the Java code.\n-output.dir           The optional output directory, default is the user temp directory.\n\nsamples:\nMicrosoft Shell Controls And Automation:\n-tlb.file shell32.dll\n-tlb.id {50A7E9B0-70EF-11D1-B75A-00A0C90564FE} -tlb.major.version 1 -tlb.minor.version 0\n\nMicrosoft Word 12.0 Object Library:\n-tlb.id {00020905-0000-0000-C000-000000000046} -tlb.major.version 8 -tlb.minor.version 4\n\n";
    System.out.println(str);
    System.exit(0);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbCmdlineArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */