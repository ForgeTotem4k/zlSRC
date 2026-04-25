package com.sun.jna;

import java.lang.reflect.InvocationTargetException;

abstract class Klass {
  public static <T> T newInstance(Class<T> paramClass) {
    try {
      return paramClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
    } catch (IllegalAccessException|IllegalArgumentException|InstantiationException|NoSuchMethodException|SecurityException illegalAccessException) {
      String str = "Can't create an instance of " + paramClass + ", requires a public no-arg constructor: " + illegalAccessException;
      throw new IllegalArgumentException(str, illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      if (invocationTargetException.getCause() instanceof RuntimeException)
        throw (RuntimeException)invocationTargetException.getCause(); 
      String str = "Can't create an instance of " + paramClass + ", requires a public no-arg constructor: " + invocationTargetException;
      throw new IllegalArgumentException(str, invocationTargetException);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Klass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */