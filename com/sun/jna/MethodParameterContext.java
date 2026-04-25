package com.sun.jna;

import java.lang.reflect.Method;

public class MethodParameterContext extends FunctionParameterContext {
  private Method method;
  
  MethodParameterContext(Function paramFunction, Object[] paramArrayOfObject, int paramInt, Method paramMethod) {
    super(paramFunction, paramArrayOfObject, paramInt);
    this.method = paramMethod;
  }
  
  public Method getMethod() {
    return this.method;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\MethodParameterContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */