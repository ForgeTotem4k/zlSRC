package com.google.gson;

import com.google.gson.internal.ReflectionAccessFilterHelper;

public interface ReflectionAccessFilter {
  public static final ReflectionAccessFilter BLOCK_INACCESSIBLE_JAVA = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> param1Class) {
        return ReflectionAccessFilterHelper.isJavaType(param1Class) ? ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
      
      public String toString() {
        return "ReflectionAccessFilter#BLOCK_INACCESSIBLE_JAVA";
      }
      
      static {
      
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_JAVA = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> param1Class) {
        return ReflectionAccessFilterHelper.isJavaType(param1Class) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
      
      public String toString() {
        return "ReflectionAccessFilter#BLOCK_ALL_JAVA";
      }
      
      static {
      
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_ANDROID = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> param1Class) {
        return ReflectionAccessFilterHelper.isAndroidType(param1Class) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
      
      public String toString() {
        return "ReflectionAccessFilter#BLOCK_ALL_ANDROID";
      }
      
      static {
      
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_PLATFORM = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> param1Class) {
        return ReflectionAccessFilterHelper.isAnyPlatformType(param1Class) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
      
      public String toString() {
        return "ReflectionAccessFilter#BLOCK_ALL_PLATFORM";
      }
      
      static {
      
      }
    };
  
  FilterResult check(Class<?> paramClass);
  
  public enum FilterResult {
    ALLOW, INDECISIVE, BLOCK_INACCESSIBLE, BLOCK_ALL;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\ReflectionAccessFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */