package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.WinNT;

public class COMInvokeException extends COMException {
  private static final long serialVersionUID = 1L;
  
  private final Integer wCode = null;
  
  private final String source = null;
  
  private final String description = null;
  
  private final String helpFile = null;
  
  private final Integer helpContext = null;
  
  private final Integer scode = null;
  
  private final Integer errorArg = null;
  
  public COMInvokeException() {
    this("", (Throwable)null);
  }
  
  public COMInvokeException(String paramString) {
    this(paramString, (Throwable)null);
  }
  
  public COMInvokeException(Throwable paramThrowable) {
    this(null, paramThrowable);
  }
  
  public COMInvokeException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public COMInvokeException(String paramString1, WinNT.HRESULT paramHRESULT, Integer paramInteger1, String paramString2, Integer paramInteger2, String paramString3, Integer paramInteger3, String paramString4, Integer paramInteger4) {
    super(formatMessage(paramHRESULT, paramString1, paramInteger1), paramHRESULT);
  }
  
  public Integer getErrorArg() {
    return this.errorArg;
  }
  
  public Integer getWCode() {
    return this.wCode;
  }
  
  public String getSource() {
    return this.source;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String getHelpFile() {
    return this.helpFile;
  }
  
  public Integer getHelpContext() {
    return this.helpContext;
  }
  
  public Integer getScode() {
    return this.scode;
  }
  
  private static String formatMessage(WinNT.HRESULT paramHRESULT, String paramString, Integer paramInteger) {
    return (paramHRESULT.intValue() == -2147352571 || paramHRESULT.intValue() == -2147352572) ? (paramString + " (puArgErr=" + paramInteger + ")") : paramString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMInvokeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */