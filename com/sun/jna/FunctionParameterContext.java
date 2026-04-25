package com.sun.jna;

public class FunctionParameterContext extends ToNativeContext {
  private Function function;
  
  private Object[] args;
  
  private int index;
  
  FunctionParameterContext(Function paramFunction, Object[] paramArrayOfObject, int paramInt) {
    this.function = paramFunction;
    this.args = paramArrayOfObject;
    this.index = paramInt;
  }
  
  public Function getFunction() {
    return this.function;
  }
  
  public Object[] getParameters() {
    return this.args;
  }
  
  public int getParameterIndex() {
    return this.index;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\FunctionParameterContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */