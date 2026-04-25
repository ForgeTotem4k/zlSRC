package com.sun.jna;

public interface CallbackProxy extends Callback {
  Object callback(Object[] paramArrayOfObject);
  
  Class<?>[] getParameterTypes();
  
  Class<?> getReturnType();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\CallbackProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */