package com.sun.jna;

import java.lang.reflect.Method;

public class CallbackResultContext extends ToNativeContext {
  private Method method;
  
  CallbackResultContext(Method paramMethod) {
    this.method = paramMethod;
  }
  
  public Method getMethod() {
    return this.method;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\CallbackResultContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */