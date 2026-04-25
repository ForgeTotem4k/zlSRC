package com.sun.jna.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionUtils {
  private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());
  
  private static final Method METHOD_IS_DEFAULT = lookupMethod(Method.class, "isDefault", new Class[0]);
  
  private static final Method METHOD_HANDLES_LOOKUP;
  
  private static final Method METHOD_HANDLES_LOOKUP_IN;
  
  private static final Method METHOD_HANDLES_PRIVATE_LOOKUP_IN;
  
  private static final Method METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL;
  
  private static final Method METHOD_HANDLES_LOOKUP_FIND_SPECIAL;
  
  private static final Method METHOD_HANDLES_BIND_TO;
  
  private static final Method METHOD_HANDLES_INVOKE_WITH_ARGUMENTS;
  
  private static final Method METHOD_TYPE;
  
  private static Constructor CONSTRUCTOR_LOOKUP_CLASS;
  
  private static Constructor getConstructorLookupClass() {
    if (CONSTRUCTOR_LOOKUP_CLASS == null) {
      Class clazz = lookupClass("java.lang.invoke.MethodHandles$Lookup");
      CONSTRUCTOR_LOOKUP_CLASS = lookupDeclaredConstructor(clazz, new Class[] { Class.class });
    } 
    return CONSTRUCTOR_LOOKUP_CLASS;
  }
  
  private static Constructor lookupDeclaredConstructor(Class paramClass, Class... paramVarArgs) {
    if (paramClass == null) {
      LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[] { paramClass, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
    try {
      Constructor constructor = paramClass.getDeclaredConstructor(paramVarArgs);
      constructor.setAccessible(true);
      return constructor;
    } catch (Exception exception) {
      LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[] { paramClass, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
  }
  
  private static Method lookupMethod(Class paramClass, String paramString, Class... paramVarArgs) {
    if (paramClass == null) {
      LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[] { paramClass, paramString, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
    try {
      return paramClass.getMethod(paramString, paramVarArgs);
    } catch (Exception exception) {
      LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[] { paramClass, paramString, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
  }
  
  private static Class lookupClass(String paramString) {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      LOG.log(Level.FINE, "Failed to lookup class: " + paramString, classNotFoundException);
      return null;
    } 
  }
  
  public static boolean isDefault(Method paramMethod) {
    if (METHOD_IS_DEFAULT == null)
      return false; 
    try {
      return ((Boolean)METHOD_IS_DEFAULT.invoke(paramMethod, new Object[0])).booleanValue();
    } catch (IllegalAccessException|IllegalArgumentException illegalAccessException) {
      throw new RuntimeException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new RuntimeException(throwable);
    } 
  }
  
  public static Object getMethodHandle(Method paramMethod) throws Exception {
    assert isDefault(paramMethod);
    Object object = createLookup();
    try {
      Object object1 = createPrivateLookupIn(paramMethod.getDeclaringClass(), object);
      return mhViaFindSpecial(object1, paramMethod);
    } catch (Exception exception) {
      Object object1 = getConstructorLookupClass().newInstance(new Object[] { paramMethod.getDeclaringClass() });
      return mhViaUnreflectSpecial(object1, paramMethod);
    } 
  }
  
  private static Object mhViaFindSpecial(Object paramObject, Method paramMethod) throws Exception {
    return METHOD_HANDLES_LOOKUP_FIND_SPECIAL.invoke(paramObject, new Object[] { paramMethod.getDeclaringClass(), paramMethod.getName(), METHOD_TYPE.invoke(null, new Object[] { paramMethod.getReturnType(), paramMethod.getParameterTypes() }), paramMethod.getDeclaringClass() });
  }
  
  private static Object mhViaUnreflectSpecial(Object paramObject, Method paramMethod) throws Exception {
    Object object = METHOD_HANDLES_LOOKUP_IN.invoke(paramObject, new Object[] { paramMethod.getDeclaringClass() });
    return METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL.invoke(object, new Object[] { paramMethod, paramMethod.getDeclaringClass() });
  }
  
  private static Object createPrivateLookupIn(Class paramClass, Object paramObject) throws Exception {
    return METHOD_HANDLES_PRIVATE_LOOKUP_IN.invoke(null, new Object[] { paramClass, paramObject });
  }
  
  private static Object createLookup() throws Exception {
    return METHOD_HANDLES_LOOKUP.invoke(null, new Object[0]);
  }
  
  public static Object invokeDefaultMethod(Object paramObject1, Object paramObject2, Object... paramVarArgs) throws Throwable {
    Object object = METHOD_HANDLES_BIND_TO.invoke(paramObject2, new Object[] { paramObject1 });
    return METHOD_HANDLES_INVOKE_WITH_ARGUMENTS.invoke(object, new Object[] { paramVarArgs });
  }
  
  static {
    METHOD_HANDLES_LOOKUP = lookupMethod(clazz1, "lookup", new Class[0]);
    METHOD_HANDLES_LOOKUP_IN = lookupMethod(clazz3, "in", new Class[] { Class.class });
    METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL = lookupMethod(clazz3, "unreflectSpecial", new Class[] { Method.class, Class.class });
    METHOD_HANDLES_LOOKUP_FIND_SPECIAL = lookupMethod(clazz3, "findSpecial", new Class[] { Class.class, String.class, clazz4, Class.class });
    METHOD_HANDLES_BIND_TO = lookupMethod(clazz2, "bindTo", new Class[] { Object.class });
    METHOD_HANDLES_INVOKE_WITH_ARGUMENTS = lookupMethod(clazz2, "invokeWithArguments", new Class[] { Object[].class });
    METHOD_HANDLES_PRIVATE_LOOKUP_IN = lookupMethod(clazz1, "privateLookupIn", new Class[] { Class.class, clazz3 });
    METHOD_TYPE = lookupMethod(clazz4, "methodType", new Class[] { Class.class, Class[].class });
  }
  
  static {
    Class clazz1 = lookupClass("java.lang.invoke.MethodHandles");
    Class clazz2 = lookupClass("java.lang.invoke.MethodHandle");
    Class clazz3 = lookupClass("java.lang.invoke.MethodHandles$Lookup");
    Class clazz4 = lookupClass("java.lang.invoke.MethodType");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\internal\ReflectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */