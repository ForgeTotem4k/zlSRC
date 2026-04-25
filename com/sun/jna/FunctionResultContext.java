package com.sun.jna;

public class FunctionResultContext extends FromNativeContext {
  private Function function;
  
  private Object[] args;
  
  FunctionResultContext(Class<?> paramClass, Function paramFunction, Object[] paramArrayOfObject) {
    super(paramClass);
    this.function = paramFunction;
    this.args = paramArrayOfObject;
  }
  
  public Function getFunction() {
    return this.function;
  }
  
  public Object[] getArguments() {
    return this.args;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\FunctionResultContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */