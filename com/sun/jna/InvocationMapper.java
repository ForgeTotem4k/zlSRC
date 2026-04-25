package com.sun.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface InvocationMapper {
  InvocationHandler getInvocationHandler(NativeLibrary paramNativeLibrary, Method paramMethod);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\InvocationMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */