package com.sun.jna;

public interface FromNativeConverter {
  Object fromNative(Object paramObject, FromNativeContext paramFromNativeContext);
  
  Class<?> nativeType();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\FromNativeConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */