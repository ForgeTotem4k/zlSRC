package com.sun.jna;

import java.lang.reflect.Method;

public class MethodResultContext extends FunctionResultContext {
  private final Method method;
  
  MethodResultContext(Class<?> paramClass, Function paramFunction, Object[] paramArrayOfObject, Method paramMethod) {
    super(paramClass, paramFunction, paramArrayOfObject);
    this.method = paramMethod;
  }
  
  public Method getMethod() {
    return this.method;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\MethodResultContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */