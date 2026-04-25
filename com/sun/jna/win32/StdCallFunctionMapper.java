package com.sun.jna.win32;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import java.lang.reflect.Method;

public class StdCallFunctionMapper implements FunctionMapper {
  protected int getArgumentNativeStackSize(Class<?> paramClass) {
    if (NativeMapped.class.isAssignableFrom(paramClass))
      paramClass = NativeMappedConverter.getInstance(paramClass).nativeType(); 
    if (paramClass.isArray())
      return Native.POINTER_SIZE; 
    try {
      return Native.getNativeSize(paramClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IllegalArgumentException("Unknown native stack allocation size for " + paramClass);
    } 
  }
  
  public String getFunctionName(NativeLibrary paramNativeLibrary, Method paramMethod) {
    String str1 = paramMethod.getName();
    int i = 0;
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (Class<?> clazz : arrayOfClass)
      i += getArgumentNativeStackSize(clazz); 
    String str2 = str1 + "@" + i;
    byte b = 63;
    try {
      Function function = paramNativeLibrary.getFunction(str2, b);
      str1 = function.getName();
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      try {
        Function function = paramNativeLibrary.getFunction("_" + str2, b);
        str1 = function.getName();
      } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {}
    } 
    return str1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\win32\StdCallFunctionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */