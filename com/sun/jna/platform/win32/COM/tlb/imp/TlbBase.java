package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TlbBase {
  public static final String CR = "\n";
  
  public static final String CRCR = "\n\n";
  
  public static final String TAB = "\t";
  
  public static final String TABTAB = "\t\t";
  
  protected TypeLibUtil typeLibUtil;
  
  protected TypeInfoUtil typeInfoUtil;
  
  protected int index;
  
  protected StringBuffer templateBuffer;
  
  protected StringBuffer classBuffer;
  
  protected String content = "";
  
  protected String filename = "DefaultFilename";
  
  protected String name = "DefaultName";
  
  public static String[] IUNKNOWN_METHODS = new String[] { "QueryInterface", "AddRef", "Release" };
  
  public static String[] IDISPATCH_METHODS = new String[] { "GetTypeInfoCount", "GetTypeInfo", "GetIDsOfNames", "Invoke" };
  
  protected String bindingMode = "dispid";
  
  public TlbBase(int paramInt, TypeLibUtil paramTypeLibUtil, TypeInfoUtil paramTypeInfoUtil) {
    this(paramInt, paramTypeLibUtil, paramTypeInfoUtil, "dispid");
  }
  
  public TlbBase(int paramInt, TypeLibUtil paramTypeLibUtil, TypeInfoUtil paramTypeInfoUtil, String paramString) {
    this.index = paramInt;
    this.typeLibUtil = paramTypeLibUtil;
    this.typeInfoUtil = paramTypeInfoUtil;
    this.bindingMode = paramString;
    String str = getClassTemplate();
    try {
      readTemplateFile(str);
      this.classBuffer = this.templateBuffer;
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public void logError(String paramString) {
    log("ERROR", paramString);
  }
  
  public void logInfo(String paramString) {
    log("INFO", paramString);
  }
  
  public StringBuffer getClassBuffer() {
    return this.classBuffer;
  }
  
  public void createContent(String paramString) {
    replaceVariable("content", paramString);
  }
  
  public void setFilename(String paramString) {
    if (!paramString.endsWith("java"))
      paramString = paramString + ".java"; 
    this.filename = paramString;
  }
  
  public String getFilename() {
    return this.filename;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String paramString) {
    this.name = paramString;
  }
  
  protected void log(String paramString1, String paramString2) {
    String str = paramString1 + " " + getTime() + " : " + paramString2;
    System.out.println(str);
  }
  
  private String getTime() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return simpleDateFormat.format(new Date());
  }
  
  protected abstract String getClassTemplate();
  
  protected void readTemplateFile(String paramString) throws IOException {
    this.templateBuffer = new StringBuffer();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(paramString);
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String str = null;
      while ((str = bufferedReader.readLine()) != null)
        this.templateBuffer.append(str + "\n"); 
    } finally {
      if (bufferedReader != null)
        bufferedReader.close(); 
    } 
  }
  
  protected void replaceVariable(String paramString1, String paramString2) {
    if (paramString2 == null)
      paramString2 = ""; 
    Pattern pattern = Pattern.compile("\\$\\{" + paramString1 + "\\}");
    Matcher matcher = pattern.matcher(this.classBuffer);
    String str1 = paramString2;
    String str2;
    for (str2 = ""; matcher.find(); str2 = matcher.replaceAll(str1));
    if (str2.length() > 0)
      this.classBuffer = new StringBuffer(str2); 
  }
  
  protected void createPackageName(String paramString) {
    replaceVariable("packagename", paramString);
  }
  
  protected void createClassName(String paramString) {
    replaceVariable("classname", paramString);
  }
  
  protected boolean isReservedMethod(String paramString) {
    byte b;
    for (b = 0; b < IUNKNOWN_METHODS.length; b++) {
      if (IUNKNOWN_METHODS[b].equalsIgnoreCase(paramString))
        return true; 
    } 
    for (b = 0; b < IDISPATCH_METHODS.length; b++) {
      if (IDISPATCH_METHODS[b].equalsIgnoreCase(paramString))
        return true; 
    } 
    return false;
  }
  
  protected boolean isVTableMode() {
    return this.bindingMode.equalsIgnoreCase("vtable");
  }
  
  protected boolean isDispIdMode() {
    return this.bindingMode.equalsIgnoreCase("dispid");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */