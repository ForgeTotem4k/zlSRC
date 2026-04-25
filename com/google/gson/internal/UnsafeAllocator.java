package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {
  public static final UnsafeAllocator INSTANCE = create();
  
  public abstract <T> T newInstance(Class<T> paramClass) throws Exception;
  
  private static void assertInstantiable(Class<?> paramClass) {
    String str = ConstructorConstructor.checkInstantiable(paramClass);
    if (str != null)
      throw new AssertionError("UnsafeAllocator is used for non-instantiable type: " + str); 
  }
  
  private static UnsafeAllocator create() {
    try {
      Class<?> clazz = Class.forName("sun.misc.Unsafe");
      Field field = clazz.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      final Object unsafe = field.get(null);
      final Method newInstance = clazz.getMethod("allocateInstance", new Class[] { Class.class });
      return new UnsafeAllocator() {
          public <T> T newInstance(Class<T> param1Class) throws Exception {
            UnsafeAllocator.assertInstantiable(param1Class);
            return (T)allocateInstance.invoke(unsafe, new Object[] { param1Class });
          }
        };
    } catch (Exception exception) {
      try {
        Method method1 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
        method1.setAccessible(true);
        final int constructorId = ((Integer)method1.invoke(null, new Object[] { Object.class })).intValue();
        final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, int.class });
        method2.setAccessible(true);
        return new UnsafeAllocator() {
            public <T> T newInstance(Class<T> param1Class) throws Exception {
              UnsafeAllocator.assertInstantiable(param1Class);
              return (T)newInstance.invoke(null, new Object[] { param1Class, Integer.valueOf(this.val$constructorId) });
            }
          };
      } catch (Exception exception1) {
        try {
          final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
          method.setAccessible(true);
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> param1Class) throws Exception {
                UnsafeAllocator.assertInstantiable(param1Class);
                return (T)newInstance.invoke(null, new Object[] { param1Class, Object.class });
              }
            };
        } catch (Exception exception2) {
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> param1Class) {
                throw new UnsupportedOperationException("Cannot allocate " + param1Class + ". Usage of JDK sun.misc.Unsafe is enabled, but it could not be used. Make sure your runtime is configured correctly.");
              }
              
              static {
              
              }
            };
        } 
      } 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\UnsafeAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */