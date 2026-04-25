package com.sun.jna;

public class LastErrorException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  private int errorCode;
  
  private static String formatMessage(int paramInt) {
    return Platform.isWindows() ? ("GetLastError() returned " + paramInt) : ("errno was " + paramInt);
  }
  
  private static String parseMessage(String paramString) {
    try {
      return formatMessage(Integer.parseInt(paramString));
    } catch (NumberFormatException numberFormatException) {
      return paramString;
    } 
  }
  
  public int getErrorCode() {
    return this.errorCode;
  }
  
  public LastErrorException(String paramString) {
    super(parseMessage(paramString.trim()));
    try {
      if (paramString.startsWith("["))
        paramString = paramString.substring(1, paramString.indexOf("]")); 
      this.errorCode = Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      this.errorCode = -1;
    } 
  }
  
  public LastErrorException(int paramInt) {
    this(paramInt, formatMessage(paramInt));
  }
  
  protected LastErrorException(int paramInt, String paramString) {
    super(paramString);
    this.errorCode = paramInt;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\LastErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */