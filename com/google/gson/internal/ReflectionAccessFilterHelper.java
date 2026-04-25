package com.google.gson.internal;

import com.google.gson.ReflectionAccessFilter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectionAccessFilterHelper {
  public static boolean isJavaType(Class<?> paramClass) {
    return isJavaType(paramClass.getName());
  }
  
  private static boolean isJavaType(String paramString) {
    return (paramString.startsWith("java.") || paramString.startsWith("javax."));
  }
  
  public static boolean isAndroidType(Class<?> paramClass) {
    return isAndroidType(paramClass.getName());
  }
  
  private static boolean isAndroidType(String paramString) {
    return (paramString.startsWith("android.") || paramString.startsWith("androidx.") || isJavaType(paramString));
  }
  
  public static boolean isAnyPlatformType(Class<?> paramClass) {
    String str = paramClass.getName();
    return (isAndroidType(str) || str.startsWith("kotlin.") || str.startsWith("kotlinx.") || str.startsWith("scala."));
  }
  
  public static ReflectionAccessFilter.FilterResult getFilterResult(List<ReflectionAccessFilter> paramList, Class<?> paramClass) {
    for (ReflectionAccessFilter reflectionAccessFilter : paramList) {
      ReflectionAccessFilter.FilterResult filterResult = reflectionAccessFilter.check(paramClass);
      if (filterResult != ReflectionAccessFilter.FilterResult.INDECISIVE)
        return filterResult; 
    } 
    return ReflectionAccessFilter.FilterResult.ALLOW;
  }
  
  public static boolean canAccess(AccessibleObject paramAccessibleObject, Object paramObject) {
    return AccessChecker.INSTANCE.canAccess(paramAccessibleObject, paramObject);
  }
  
  static {
  
  }
  
  private static abstract class AccessChecker {
    public static final AccessChecker INSTANCE;
    
    private AccessChecker() {}
    
    public abstract boolean canAccess(AccessibleObject param1AccessibleObject, Object param1Object);
    
    static {
      AccessChecker accessChecker = null;
      if (JavaVersion.isJava9OrLater())
        try {
          final Method canAccessMethod = AccessibleObject.class.getDeclaredMethod("canAccess", new Class[] { Object.class });
          accessChecker = new AccessChecker() {
              public boolean canAccess(AccessibleObject param2AccessibleObject, Object param2Object) {
                try {
                  return ((Boolean)canAccessMethod.invoke(param2AccessibleObject, new Object[] { param2Object })).booleanValue();
                } catch (Exception exception) {
                  throw new RuntimeException("Failed invoking canAccess", exception);
                } 
              }
            };
        } catch (NoSuchMethodException noSuchMethodException) {} 
      if (accessChecker == null)
        accessChecker = new AccessChecker() {
            public boolean canAccess(AccessibleObject param2AccessibleObject, Object param2Object) {
              return true;
            }
            
            static {
            
            }
          }; 
      INSTANCE = accessChecker;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\ReflectionAccessFilterHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */