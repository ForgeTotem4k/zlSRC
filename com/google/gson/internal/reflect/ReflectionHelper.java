package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import com.google.gson.internal.TroubleshootingGuide;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
  private static final RecordHelper RECORD_HELPER;
  
  private static String getInaccessibleTroubleshootingSuffix(Exception paramException) {
    if (paramException.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
      String str1 = paramException.getMessage();
      String str2 = (str1 != null && str1.contains("to module com.google.gson")) ? "reflection-inaccessible-to-module-gson" : "reflection-inaccessible";
      return "\nSee " + TroubleshootingGuide.createUrl(str2);
    } 
    return "";
  }
  
  public static void makeAccessible(AccessibleObject paramAccessibleObject) throws JsonIOException {
    try {
      paramAccessibleObject.setAccessible(true);
    } catch (Exception exception) {
      String str = getAccessibleObjectDescription(paramAccessibleObject, false);
      throw new JsonIOException("Failed making " + str + " accessible; either increase its visibility or write a custom TypeAdapter for its declaring type." + getInaccessibleTroubleshootingSuffix(exception), exception);
    } 
  }
  
  public static String getAccessibleObjectDescription(AccessibleObject paramAccessibleObject, boolean paramBoolean) {
    String str;
    if (paramAccessibleObject instanceof Field) {
      str = "field '" + fieldToString((Field)paramAccessibleObject) + "'";
    } else if (paramAccessibleObject instanceof Method) {
      Method method = (Method)paramAccessibleObject;
      StringBuilder stringBuilder = new StringBuilder(method.getName());
      appendExecutableParameters(method, stringBuilder);
      String str1 = stringBuilder.toString();
      str = "method '" + method.getDeclaringClass().getName() + "#" + str1 + "'";
    } else if (paramAccessibleObject instanceof Constructor) {
      str = "constructor '" + constructorToString((Constructor)paramAccessibleObject) + "'";
    } else {
      str = "<unknown AccessibleObject> " + paramAccessibleObject.toString();
    } 
    if (paramBoolean && Character.isLowerCase(str.charAt(0)))
      str = Character.toUpperCase(str.charAt(0)) + str.substring(1); 
    return str;
  }
  
  public static String fieldToString(Field paramField) {
    return paramField.getDeclaringClass().getName() + "#" + paramField.getName();
  }
  
  public static String constructorToString(Constructor<?> paramConstructor) {
    StringBuilder stringBuilder = new StringBuilder(paramConstructor.getDeclaringClass().getName());
    appendExecutableParameters(paramConstructor, stringBuilder);
    return stringBuilder.toString();
  }
  
  private static void appendExecutableParameters(AccessibleObject paramAccessibleObject, StringBuilder paramStringBuilder) {
    paramStringBuilder.append('(');
    Class<?>[] arrayOfClass = (paramAccessibleObject instanceof Method) ? ((Method)paramAccessibleObject).getParameterTypes() : ((Constructor)paramAccessibleObject).getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (b > 0)
        paramStringBuilder.append(", "); 
      paramStringBuilder.append(arrayOfClass[b].getSimpleName());
    } 
    paramStringBuilder.append(')');
  }
  
  public static boolean isStatic(Class<?> paramClass) {
    return Modifier.isStatic(paramClass.getModifiers());
  }
  
  public static boolean isAnonymousOrNonStaticLocal(Class<?> paramClass) {
    return (!isStatic(paramClass) && (paramClass.isAnonymousClass() || paramClass.isLocalClass()));
  }
  
  public static String tryMakeAccessible(Constructor<?> paramConstructor) {
    try {
      paramConstructor.setAccessible(true);
      return null;
    } catch (Exception exception) {
      return "Failed making constructor '" + constructorToString(paramConstructor) + "' accessible; either increase its visibility or write a custom InstanceCreator or TypeAdapter for its declaring type: " + exception.getMessage() + getInaccessibleTroubleshootingSuffix(exception);
    } 
  }
  
  public static boolean isRecord(Class<?> paramClass) {
    return RECORD_HELPER.isRecord(paramClass);
  }
  
  public static String[] getRecordComponentNames(Class<?> paramClass) {
    return RECORD_HELPER.getRecordComponentNames(paramClass);
  }
  
  public static Method getAccessor(Class<?> paramClass, Field paramField) {
    return RECORD_HELPER.getAccessor(paramClass, paramField);
  }
  
  public static <T> Constructor<T> getCanonicalRecordConstructor(Class<T> paramClass) {
    return RECORD_HELPER.getCanonicalRecordConstructor(paramClass);
  }
  
  public static RuntimeException createExceptionForUnexpectedIllegalAccess(IllegalAccessException paramIllegalAccessException) {
    throw new RuntimeException("Unexpected IllegalAccessException occurred (Gson 2.12.1). Certain ReflectionAccessFilter features require Java >= 9 to work correctly. If you are not using ReflectionAccessFilter, report this to the Gson maintainers.", paramIllegalAccessException);
  }
  
  private static RuntimeException createExceptionForRecordReflectionException(ReflectiveOperationException paramReflectiveOperationException) {
    throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.12.1). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", paramReflectiveOperationException);
  }
  
  static {
    RecordNotSupportedHelper recordNotSupportedHelper;
    try {
      RecordSupportedHelper recordSupportedHelper = new RecordSupportedHelper();
    } catch (ReflectiveOperationException reflectiveOperationException) {
      recordNotSupportedHelper = new RecordNotSupportedHelper();
    } 
    RECORD_HELPER = recordNotSupportedHelper;
  }
  
  private static class RecordNotSupportedHelper extends RecordHelper {
    private RecordNotSupportedHelper() {}
    
    boolean isRecord(Class<?> param1Class) {
      return false;
    }
    
    String[] getRecordComponentNames(Class<?> param1Class) {
      throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
    }
    
    <T> Constructor<T> getCanonicalRecordConstructor(Class<T> param1Class) {
      throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
    }
    
    public Method getAccessor(Class<?> param1Class, Field param1Field) {
      throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
    }
    
    static {
    
    }
  }
  
  private static class RecordSupportedHelper extends RecordHelper {
    private final Method isRecord = Class.class.getMethod("isRecord", new Class[0]);
    
    private final Method getRecordComponents = Class.class.getMethod("getRecordComponents", new Class[0]);
    
    private final Method getName;
    
    private final Method getType;
    
    private RecordSupportedHelper() throws NoSuchMethodException, ClassNotFoundException {
      Class<?> clazz = Class.forName("java.lang.reflect.RecordComponent");
      this.getName = clazz.getMethod("getName", new Class[0]);
      this.getType = clazz.getMethod("getType", new Class[0]);
    }
    
    boolean isRecord(Class<?> param1Class) {
      try {
        return ((Boolean)this.isRecord.invoke(param1Class, new Object[0])).booleanValue();
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw ReflectionHelper.createExceptionForRecordReflectionException(reflectiveOperationException);
      } 
    }
    
    String[] getRecordComponentNames(Class<?> param1Class) {
      try {
        Object[] arrayOfObject = (Object[])this.getRecordComponents.invoke(param1Class, new Object[0]);
        String[] arrayOfString = new String[arrayOfObject.length];
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfString[b] = (String)this.getName.invoke(arrayOfObject[b], new Object[0]); 
        return arrayOfString;
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw ReflectionHelper.createExceptionForRecordReflectionException(reflectiveOperationException);
      } 
    }
    
    public <T> Constructor<T> getCanonicalRecordConstructor(Class<T> param1Class) {
      try {
        Object[] arrayOfObject = (Object[])this.getRecordComponents.invoke(param1Class, new Object[0]);
        Class[] arrayOfClass = new Class[arrayOfObject.length];
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfClass[b] = (Class)this.getType.invoke(arrayOfObject[b], new Object[0]); 
        return param1Class.getDeclaredConstructor(arrayOfClass);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw ReflectionHelper.createExceptionForRecordReflectionException(reflectiveOperationException);
      } 
    }
    
    public Method getAccessor(Class<?> param1Class, Field param1Field) {
      try {
        return param1Class.getMethod(param1Field.getName(), new Class[0]);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw ReflectionHelper.createExceptionForRecordReflectionException(reflectiveOperationException);
      } 
    }
  }
  
  private static abstract class RecordHelper {
    private RecordHelper() {}
    
    abstract boolean isRecord(Class<?> param1Class);
    
    abstract String[] getRecordComponentNames(Class<?> param1Class);
    
    abstract <T> Constructor<T> getCanonicalRecordConstructor(Class<T> param1Class);
    
    public abstract Method getAccessor(Class<?> param1Class, Field param1Field);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\reflect\ReflectionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */