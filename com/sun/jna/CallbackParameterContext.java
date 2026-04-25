package com.sun.jna;

import java.lang.reflect.Method;

public class CallbackParameterContext extends FromNativeContext {
  private Method method;
  
  private Object[] args;
  
  private int index;
  
  CallbackParameterContext(Class<?> paramClass, Method paramMethod, Object[] paramArrayOfObject, int paramInt) {
    super(paramClass);
    this.method = paramMethod;
    this.args = paramArrayOfObject;
    this.index = paramInt;
  }
  
  public Method getMethod() {
    return this.method;
  }
  
  public Object[] getArguments() {
    return this.args;
  }
  
  public int getIndex() {
    return this.index;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\CallbackParameterContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */