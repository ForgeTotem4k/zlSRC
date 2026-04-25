package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor {
  private final Map<Type, InstanceCreator<?>> instanceCreators;
  
  private final boolean useJdkUnsafe;
  
  private final List<ReflectionAccessFilter> reflectionFilters;
  
  public ConstructorConstructor(Map<Type, InstanceCreator<?>> paramMap, boolean paramBoolean, List<ReflectionAccessFilter> paramList) {
    this.instanceCreators = paramMap;
    this.useJdkUnsafe = paramBoolean;
    this.reflectionFilters = paramList;
  }
  
  static String checkInstantiable(Class<?> paramClass) {
    int i = paramClass.getModifiers();
    return Modifier.isInterface(i) ? ("Interfaces can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Interface name: " + paramClass.getName()) : (Modifier.isAbstract(i) ? ("Abstract classes can't be instantiated! Adjust the R8 configuration or register an InstanceCreator or a TypeAdapter for this type. Class name: " + paramClass.getName() + "\nSee " + TroubleshootingGuide.createUrl("r8-abstract-class")) : null);
  }
  
  public <T> ObjectConstructor<T> get(TypeToken<T> paramTypeToken) {
    Type type = paramTypeToken.getType();
    Class<?> clazz = paramTypeToken.getRawType();
    InstanceCreator instanceCreator1 = this.instanceCreators.get(type);
    if (instanceCreator1 != null)
      return () -> paramInstanceCreator.createInstance(paramType); 
    InstanceCreator instanceCreator2 = this.instanceCreators.get(clazz);
    if (instanceCreator2 != null)
      return () -> paramInstanceCreator.createInstance(paramType); 
    ObjectConstructor<?> objectConstructor1 = newSpecialCollectionConstructor(type, clazz);
    if (objectConstructor1 != null)
      return (ObjectConstructor)objectConstructor1; 
    ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, clazz);
    ObjectConstructor<?> objectConstructor2 = newDefaultConstructor(clazz, filterResult);
    if (objectConstructor2 != null)
      return (ObjectConstructor)objectConstructor2; 
    ObjectConstructor<?> objectConstructor3 = newDefaultImplementationConstructor(type, clazz);
    if (objectConstructor3 != null)
      return (ObjectConstructor)objectConstructor3; 
    String str1 = checkInstantiable(clazz);
    if (str1 != null)
      return () -> {
          throw new JsonIOException(paramString);
        }; 
    if (filterResult == ReflectionAccessFilter.FilterResult.ALLOW)
      return newUnsafeAllocator((Class)clazz); 
    String str2 = "Unable to create instance of " + clazz + "; ReflectionAccessFilter does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter for this type or adjust the access filter to allow using reflection.";
    return () -> {
        throw new JsonIOException(paramString);
      };
  }
  
  private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(Type paramType, Class<? super T> paramClass) {
    return EnumSet.class.isAssignableFrom(paramClass) ? (() -> {
        if (paramType instanceof ParameterizedType) {
          Type type = ((ParameterizedType)paramType).getActualTypeArguments()[0];
          if (type instanceof Class)
            return EnumSet.noneOf((Class<Enum>)type); 
          throw new JsonIOException("Invalid EnumSet type: " + paramType.toString());
        } 
        throw new JsonIOException("Invalid EnumSet type: " + paramType.toString());
      }) : ((paramClass == EnumMap.class) ? (() -> {
        if (paramType instanceof ParameterizedType) {
          Type type = ((ParameterizedType)paramType).getActualTypeArguments()[0];
          if (type instanceof Class)
            return new EnumMap<>((Class<Enum>)type); 
          throw new JsonIOException("Invalid EnumMap type: " + paramType.toString());
        } 
        throw new JsonIOException("Invalid EnumMap type: " + paramType.toString());
      }) : null);
  }
  
  private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> paramClass, ReflectionAccessFilter.FilterResult paramFilterResult) {
    Constructor<? super T> constructor;
    if (Modifier.isAbstract(paramClass.getModifiers()))
      return null; 
    try {
      constructor = paramClass.getDeclaredConstructor(new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
    boolean bool = (paramFilterResult == ReflectionAccessFilter.FilterResult.ALLOW || (ReflectionAccessFilterHelper.canAccess(constructor, null) && (paramFilterResult != ReflectionAccessFilter.FilterResult.BLOCK_ALL || Modifier.isPublic(constructor.getModifiers())))) ? true : false;
    if (!bool) {
      String str = "Unable to invoke no-args constructor of " + paramClass + "; constructor is not accessible and ReflectionAccessFilter does not permit making it accessible. Register an InstanceCreator or a TypeAdapter for this type, change the visibility of the constructor or adjust the access filter.";
      return () -> {
          throw new JsonIOException(paramString);
        };
    } 
    if (paramFilterResult == ReflectionAccessFilter.FilterResult.ALLOW) {
      String str = ReflectionHelper.tryMakeAccessible(constructor);
      if (str != null)
        return () -> {
            throw new JsonIOException(paramString);
          }; 
    } 
    return () -> {
        try {
          return paramConstructor.newInstance(new Object[0]);
        } catch (InstantiationException instantiationException) {
          throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(paramConstructor) + "' with no args", instantiationException);
        } catch (InvocationTargetException invocationTargetException) {
          throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(paramConstructor) + "' with no args", invocationTargetException.getCause());
        } catch (IllegalAccessException illegalAccessException) {
          throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(illegalAccessException);
        } 
      };
  }
  
  private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type paramType, Class<? super T> paramClass) {
    return Collection.class.isAssignableFrom(paramClass) ? (SortedSet.class.isAssignableFrom(paramClass) ? (() -> new TreeSet()) : (Set.class.isAssignableFrom(paramClass) ? (() -> new LinkedHashSet()) : (Queue.class.isAssignableFrom(paramClass) ? (() -> new ArrayDeque()) : (() -> new ArrayList())))) : (Map.class.isAssignableFrom(paramClass) ? (ConcurrentNavigableMap.class.isAssignableFrom(paramClass) ? (() -> new ConcurrentSkipListMap<>()) : (ConcurrentMap.class.isAssignableFrom(paramClass) ? (() -> new ConcurrentHashMap<>()) : (SortedMap.class.isAssignableFrom(paramClass) ? (() -> new TreeMap<>()) : ((paramType instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)paramType).getActualTypeArguments()[0]).getRawType())) ? (() -> new LinkedHashMap<>()) : (() -> new LinkedTreeMap<>()))))) : null);
  }
  
  private <T> ObjectConstructor<T> newUnsafeAllocator(Class<? super T> paramClass) {
    if (this.useJdkUnsafe)
      return () -> {
          try {
            return UnsafeAllocator.INSTANCE.newInstance(paramClass);
          } catch (Exception exception) {
            throw new RuntimeException("Unable to create instance of " + paramClass + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", exception);
          } 
        }; 
    String str1 = "Unable to create instance of " + paramClass + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
    if ((paramClass.getDeclaredConstructors()).length == 0)
      str1 = str1 + " Or adjust your R8 configuration to keep the no-args constructor of the class."; 
    String str2 = str1;
    return () -> {
        throw new JsonIOException(paramString);
      };
  }
  
  public String toString() {
    return this.instanceCreators.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\ConstructorConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */