package com.sun.jna.win32;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.Method;

public class W32APIFunctionMapper implements FunctionMapper {
  public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
  
  public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);
  
  private final String suffix;
  
  protected W32APIFunctionMapper(boolean paramBoolean) {
    this.suffix = paramBoolean ? "W" : "A";
  }
  
  public String getFunctionName(NativeLibrary paramNativeLibrary, Method paramMethod) {
    String str = paramMethod.getName();
    if (!str.endsWith("W") && !str.endsWith("A"))
      try {
        str = paramNativeLibrary.getFunction(str + this.suffix, 63).getName();
      } catch (UnsatisfiedLinkError unsatisfiedLinkError) {} 
    return str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\win32\W32APIFunctionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */