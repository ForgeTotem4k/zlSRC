package org.slf4j.helpers;

public class FormattingTuple {
  public static FormattingTuple NULL = new FormattingTuple(null);
  
  private final String message;
  
  private final Throwable throwable;
  
  private final Object[] argArray;
  
  public FormattingTuple(String paramString) {
    this(paramString, null, null);
  }
  
  public FormattingTuple(String paramString, Object[] paramArrayOfObject, Throwable paramThrowable) {
    this.message = paramString;
    this.throwable = paramThrowable;
    this.argArray = paramArrayOfObject;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public Object[] getArgArray() {
    return this.argArray;
  }
  
  public Throwable getThrowable() {
    return this.throwable;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\FormattingTuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */