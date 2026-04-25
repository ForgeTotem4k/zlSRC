package com.sun.jna;

public class FromNativeContext {
  private Class<?> type;
  
  FromNativeContext(Class<?> paramClass) {
    this.type = paramClass;
  }
  
  public Class<?> getTargetType() {
    return this.type;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\FromNativeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */