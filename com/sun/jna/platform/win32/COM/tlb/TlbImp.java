package com.sun.jna.platform.win32.COM.tlb;

import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCmdlineArgs;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCoClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispInterface;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;
import com.sun.jna.platform.win32.OaIdl;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TlbImp implements TlbConst {
  private TypeLibUtil typeLibUtil;
  
  private File comRootDir;
  
  private File outputDir;
  
  private TlbCmdlineArgs cmdlineArgs;
  
  public static void main(String[] paramArrayOfString) {
    new TlbImp(paramArrayOfString);
  }
  
  public TlbImp(String[] paramArrayOfString) {
    this.cmdlineArgs = new TlbCmdlineArgs(paramArrayOfString);
    if (this.cmdlineArgs.isTlbId()) {
      String str = this.cmdlineArgs.getRequiredParam("tlb.id");
      int i = this.cmdlineArgs.getIntParam("tlb.major.version");
      int j = this.cmdlineArgs.getIntParam("tlb.minor.version");
      this.typeLibUtil = new TypeLibUtil(str, i, j);
      startCOM2Java();
    } else if (this.cmdlineArgs.isTlbFile()) {
      String str = this.cmdlineArgs.getRequiredParam("tlb.file");
      this.typeLibUtil = new TypeLibUtil(str);
      startCOM2Java();
    } else {
      this.cmdlineArgs.showCmdHelp();
    } 
  }
  
  public void startCOM2Java() {
    try {
      createDir();
      String str = this.cmdlineArgs.getBindingMode();
      int i = this.typeLibUtil.getTypeInfoCount();
      for (byte b = 0; b < i; b++) {
        OaIdl.TYPEKIND tYPEKIND = this.typeLibUtil.getTypeInfoType(b);
        if (tYPEKIND.value == 0) {
          createCOMEnum(b, getPackageName(), this.typeLibUtil);
        } else if (tYPEKIND.value == 1) {
          logInfo("'TKIND_RECORD' objects are currently not supported!");
        } else if (tYPEKIND.value == 2) {
          logInfo("'TKIND_MODULE' objects are currently not supported!");
        } else if (tYPEKIND.value == 3) {
          createCOMInterface(b, getPackageName(), this.typeLibUtil);
        } else if (tYPEKIND.value == 4) {
          createCOMDispInterface(b, getPackageName(), this.typeLibUtil);
        } else if (tYPEKIND.value == 5) {
          createCOMCoClass(b, getPackageName(), this.typeLibUtil, str);
        } else if (tYPEKIND.value == 6) {
          logInfo("'TKIND_ALIAS' objects are currently not supported!");
        } else if (tYPEKIND.value == 7) {
          logInfo("'TKIND_UNION' objects are currently not supported!");
        } 
      } 
      logInfo(i + " files sucessfully written to: " + this.comRootDir.toString());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void createDir() throws FileNotFoundException {
    String str1 = this.cmdlineArgs.getParam("output.dir");
    String str2 = "_jnaCOM_" + System.currentTimeMillis() + "\\myPackage\\" + this.typeLibUtil.getName().toLowerCase() + "\\";
    if (str1 != null) {
      this.comRootDir = new File(str1 + "\\" + str2);
    } else {
      String str = System.getProperty("java.io.tmpdir");
      this.comRootDir = new File(str + "\\" + str2);
    } 
    if (this.comRootDir.exists())
      this.comRootDir.delete(); 
    if (this.comRootDir.mkdirs()) {
      logInfo("Output directory sucessfully created.");
    } else {
      throw new FileNotFoundException("Output directory NOT sucessfully created to: " + this.comRootDir.toString());
    } 
  }
  
  private String getPackageName() {
    return "myPackage." + this.typeLibUtil.getName().toLowerCase();
  }
  
  private void writeTextFile(String paramString1, String paramString2) throws IOException {
    String str = this.comRootDir + File.separator + paramString1;
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str));
    bufferedOutputStream.write(paramString2.getBytes());
    bufferedOutputStream.close();
  }
  
  private void writeTlbClass(TlbBase paramTlbBase) throws IOException {
    StringBuffer stringBuffer = paramTlbBase.getClassBuffer();
    writeTextFile(paramTlbBase.getFilename(), stringBuffer.toString());
  }
  
  private void createCOMEnum(int paramInt, String paramString, TypeLibUtil paramTypeLibUtil) throws IOException {
    TlbEnum tlbEnum = new TlbEnum(paramInt, paramString, paramTypeLibUtil);
    writeTlbClass((TlbBase)tlbEnum);
  }
  
  private void createCOMInterface(int paramInt, String paramString, TypeLibUtil paramTypeLibUtil) throws IOException {
    TlbInterface tlbInterface = new TlbInterface(paramInt, paramString, paramTypeLibUtil);
    writeTlbClass((TlbBase)tlbInterface);
  }
  
  private void createCOMDispInterface(int paramInt, String paramString, TypeLibUtil paramTypeLibUtil) throws IOException {
    TlbDispInterface tlbDispInterface = new TlbDispInterface(paramInt, paramString, paramTypeLibUtil);
    writeTlbClass((TlbBase)tlbDispInterface);
  }
  
  private void createCOMCoClass(int paramInt, String paramString1, TypeLibUtil paramTypeLibUtil, String paramString2) throws IOException {
    TlbCoClass tlbCoClass = new TlbCoClass(paramInt, getPackageName(), paramTypeLibUtil, paramString2);
    writeTlbClass((TlbBase)tlbCoClass);
  }
  
  public static void logInfo(String paramString) {
    System.out.println(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\TlbImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */