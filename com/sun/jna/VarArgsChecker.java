package com.sun.jna;

import java.lang.reflect.Method;

abstract class VarArgsChecker {
  private VarArgsChecker() {}
  
  static VarArgsChecker create() {
    try {
      Method method = Method.class.getMethod("isVarArgs", new Class[0]);
      return (VarArgsChecker)((method != null) ? new RealVarArgsChecker() : new NoVarArgsChecker());
    } catch (NoSuchMethodException|SecurityException noSuchMethodException) {
      return new NoVarArgsChecker();
    } 
  }
  
  abstract boolean isVarArgs(Method paramMethod);
  
  abstract int fixedArgs(Method paramMethod);
  
  private static final class RealVarArgsChecker extends VarArgsChecker {
    private RealVarArgsChecker() {}
    
    boolean isVarArgs(Method param1Method) {
      return param1Method.isVarArgs();
    }
    
    int fixedArgs(Method param1Method) {
      return param1Method.isVarArgs() ? ((param1Method.getParameterTypes()).length - 1) : 0;
    }
  }
  
  private static final class NoVarArgsChecker extends VarArgsChecker {
    private NoVarArgsChecker() {}
    
    boolean isVarArgs(Method param1Method) {
      return false;
    }
    
    int fixedArgs(Method param1Method) {
      return 0;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\VarArgsChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */