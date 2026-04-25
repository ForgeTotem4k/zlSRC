package com.google.gson.internal;

import java.lang.reflect.Type;

public final class Primitives {
  public static boolean isPrimitive(Type paramType) {
    return (paramType instanceof Class && ((Class)paramType).isPrimitive());
  }
  
  public static boolean isWrapperType(Type paramType) {
    return (paramType == Integer.class || paramType == Float.class || paramType == Byte.class || paramType == Double.class || paramType == Long.class || paramType == Character.class || paramType == Boolean.class || paramType == Short.class || paramType == Void.class);
  }
  
  public static <T> Class<T> wrap(Class<T> paramClass) {
    return (Class<T>)((paramClass == int.class) ? Integer.class : ((paramClass == float.class) ? Float.class : ((paramClass == byte.class) ? Byte.class : ((paramClass == double.class) ? Double.class : ((paramClass == long.class) ? Long.class : ((paramClass == char.class) ? Character.class : ((paramClass == boolean.class) ? Boolean.class : ((paramClass == short.class) ? Short.class : ((paramClass == void.class) ? Void.class : paramClass)))))))));
  }
  
  public static <T> Class<T> unwrap(Class<T> paramClass) {
    return (Class<T>)((paramClass == Integer.class) ? int.class : ((paramClass == Float.class) ? float.class : ((paramClass == Byte.class) ? byte.class : ((paramClass == Double.class) ? double.class : ((paramClass == Long.class) ? long.class : ((paramClass == Character.class) ? char.class : ((paramClass == Boolean.class) ? boolean.class : ((paramClass == Short.class) ? short.class : ((paramClass == Void.class) ? void.class : paramClass)))))))));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\Primitives.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */