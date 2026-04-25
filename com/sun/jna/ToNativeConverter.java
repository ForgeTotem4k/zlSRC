package com.sun.jna;

public interface ToNativeConverter {
  Object toNative(Object paramObject, ToNativeContext paramToNativeContext);
  
  Class<?> nativeType();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ToNativeConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */