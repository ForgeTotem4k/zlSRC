package com.google.gson.reflect;

import com.google.gson.internal.;
import com.google.gson.internal.TroubleshootingGuide;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeToken<T> {
  private final Class<? super T> rawType = .Gson.Types.getRawType(this.type);
  
  private final Type type = getTypeTokenTypeArgument();
  
  private final int hashCode = this.type.hashCode();
  
  protected TypeToken() {}
  
  private TypeToken(Type paramType) {}
  
  private static boolean isCapturingTypeVariablesForbidden() {
    return !Objects.equals(System.getProperty("gson.allowCapturingTypeVariables"), "true");
  }
  
  private Type getTypeTokenTypeArgument() {
    Type type = getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)type;
      if (parameterizedType.getRawType() == TypeToken.class) {
        Type type1 = .Gson.Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
        if (isCapturingTypeVariablesForbidden())
          verifyNoTypeVariable(type1); 
        return type1;
      } 
    } else if (type == TypeToken.class) {
      throw new IllegalStateException("TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.\nSee " + TroubleshootingGuide.createUrl("type-token-raw"));
    } 
    throw new IllegalStateException("Must only create direct subclasses of TypeToken");
  }
  
  private static void verifyNoTypeVariable(Type paramType) {
    if (paramType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable)paramType;
      throw new IllegalArgumentException("TypeToken type argument must not contain a type variable; captured type variable " + typeVariable.getName() + " declared by " + typeVariable.getGenericDeclaration() + "\nSee " + TroubleshootingGuide.createUrl("typetoken-type-variable"));
    } 
    if (paramType instanceof GenericArrayType) {
      verifyNoTypeVariable(((GenericArrayType)paramType).getGenericComponentType());
    } else if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      Type type = parameterizedType.getOwnerType();
      if (type != null)
        verifyNoTypeVariable(type); 
      for (Type type1 : parameterizedType.getActualTypeArguments())
        verifyNoTypeVariable(type1); 
    } else if (paramType instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramType;
      for (Type type : wildcardType.getLowerBounds())
        verifyNoTypeVariable(type); 
      for (Type type : wildcardType.getUpperBounds())
        verifyNoTypeVariable(type); 
    } else if (paramType == null) {
      throw new IllegalArgumentException("TypeToken captured `null` as type argument; probably a compiler / runtime bug");
    } 
  }
  
  public final Class<? super T> getRawType() {
    return this.rawType;
  }
  
  public final Type getType() {
    return this.type;
  }
  
  @Deprecated
  public boolean isAssignableFrom(Class<?> paramClass) {
    return isAssignableFrom(paramClass);
  }
  
  @Deprecated
  public boolean isAssignableFrom(Type paramType) {
    if (paramType == null)
      return false; 
    if (this.type.equals(paramType))
      return true; 
    if (this.type instanceof Class)
      return this.rawType.isAssignableFrom(.Gson.Types.getRawType(paramType)); 
    if (this.type instanceof ParameterizedType)
      return isAssignableFrom(paramType, (ParameterizedType)this.type, new HashMap<>()); 
    if (this.type instanceof GenericArrayType)
      return (this.rawType.isAssignableFrom(.Gson.Types.getRawType(paramType)) && isAssignableFrom(paramType, (GenericArrayType)this.type)); 
    throw buildUnsupportedTypeException(this.type, new Class[] { Class.class, ParameterizedType.class, GenericArrayType.class });
  }
  
  @Deprecated
  public boolean isAssignableFrom(TypeToken<?> paramTypeToken) {
    return isAssignableFrom(paramTypeToken.getType());
  }
  
  private static boolean isAssignableFrom(Type<?> paramType, GenericArrayType paramGenericArrayType) {
    Type type = paramGenericArrayType.getGenericComponentType();
    if (type instanceof ParameterizedType) {
      Type<?> type1 = paramType;
      if (paramType instanceof GenericArrayType) {
        type1 = ((GenericArrayType)paramType).getGenericComponentType();
      } else if (paramType instanceof Class) {
        Class<?> clazz;
        for (clazz = (Class)paramType; clazz.isArray(); clazz = clazz.getComponentType());
        type1 = clazz;
      } 
      return isAssignableFrom(type1, (ParameterizedType)type, new HashMap<>());
    } 
    return true;
  }
  
  private static boolean isAssignableFrom(Type paramType, ParameterizedType paramParameterizedType, Map<String, Type> paramMap) {
    if (paramType == null)
      return false; 
    if (paramParameterizedType.equals(paramType))
      return true; 
    Class clazz = .Gson.Types.getRawType(paramType);
    ParameterizedType parameterizedType = null;
    if (paramType instanceof ParameterizedType)
      parameterizedType = (ParameterizedType)paramType; 
    if (parameterizedType != null) {
      Type[] arrayOfType = parameterizedType.getActualTypeArguments();
      TypeVariable[] arrayOfTypeVariable = clazz.getTypeParameters();
      for (byte b = 0; b < arrayOfType.length; b++) {
        Type type1 = arrayOfType[b];
        TypeVariable typeVariable = arrayOfTypeVariable[b];
        while (type1 instanceof TypeVariable) {
          TypeVariable typeVariable1 = (TypeVariable)type1;
          type1 = paramMap.get(typeVariable1.getName());
        } 
        paramMap.put(typeVariable.getName(), type1);
      } 
      if (typeEquals(parameterizedType, paramParameterizedType, paramMap))
        return true; 
    } 
    for (Type type1 : clazz.getGenericInterfaces()) {
      if (isAssignableFrom(type1, paramParameterizedType, new HashMap<>(paramMap)))
        return true; 
    } 
    Type type = clazz.getGenericSuperclass();
    return isAssignableFrom(type, paramParameterizedType, new HashMap<>(paramMap));
  }
  
  private static boolean typeEquals(ParameterizedType paramParameterizedType1, ParameterizedType paramParameterizedType2, Map<String, Type> paramMap) {
    if (paramParameterizedType1.getRawType().equals(paramParameterizedType2.getRawType())) {
      Type[] arrayOfType1 = paramParameterizedType1.getActualTypeArguments();
      Type[] arrayOfType2 = paramParameterizedType2.getActualTypeArguments();
      for (byte b = 0; b < arrayOfType1.length; b++) {
        if (!matches(arrayOfType1[b], arrayOfType2[b], paramMap))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private static IllegalArgumentException buildUnsupportedTypeException(Type paramType, Class<?>... paramVarArgs) {
    StringBuilder stringBuilder = new StringBuilder("Unsupported type, expected one of: ");
    for (Class<?> clazz : paramVarArgs)
      stringBuilder.append(clazz.getName()).append(", "); 
    stringBuilder.append("but got: ").append(paramType.getClass().getName()).append(", for type token: ").append(paramType.toString());
    return new IllegalArgumentException(stringBuilder.toString());
  }
  
  private static boolean matches(Type paramType1, Type paramType2, Map<String, Type> paramMap) {
    return (paramType2.equals(paramType1) || (paramType1 instanceof TypeVariable && paramType2.equals(paramMap.get(((TypeVariable)paramType1).getName()))));
  }
  
  public final int hashCode() {
    return this.hashCode;
  }
  
  public final boolean equals(Object paramObject) {
    return (paramObject instanceof TypeToken && .Gson.Types.equals(this.type, ((TypeToken)paramObject).type));
  }
  
  public final String toString() {
    return .Gson.Types.typeToString(this.type);
  }
  
  public static TypeToken<?> get(Type paramType) {
    return new TypeToken(paramType);
  }
  
  public static <T> TypeToken<T> get(Class<T> paramClass) {
    return new TypeToken<>(paramClass);
  }
  
  public static TypeToken<?> getParameterized(Type paramType, Type... paramVarArgs) {
    Objects.requireNonNull(paramType);
    Objects.requireNonNull(paramVarArgs);
    if (!(paramType instanceof Class))
      throw new IllegalArgumentException("rawType must be of type Class, but was " + paramType); 
    Class<?> clazz = (Class)paramType;
    TypeVariable[] arrayOfTypeVariable = clazz.getTypeParameters();
    int i = arrayOfTypeVariable.length;
    int j = paramVarArgs.length;
    if (j != i)
      throw new IllegalArgumentException(clazz.getName() + " requires " + i + " type arguments, but got " + j); 
    if (paramVarArgs.length == 0)
      return get(clazz); 
    if (.Gson.Types.requiresOwnerType(paramType))
      throw new IllegalArgumentException("Raw type " + clazz.getName() + " is not supported because it requires specifying an owner type"); 
    for (byte b = 0; b < i; b++) {
      Type type = Objects.<Type>requireNonNull(paramVarArgs[b], "Type argument must not be null");
      Class<?> clazz1 = .Gson.Types.getRawType(type);
      TypeVariable typeVariable = arrayOfTypeVariable[b];
      for (Type type1 : typeVariable.getBounds()) {
        Class clazz2 = .Gson.Types.getRawType(type1);
        if (!clazz2.isAssignableFrom(clazz1))
          throw new IllegalArgumentException("Type argument " + type + " does not satisfy bounds for type variable " + typeVariable + " declared by " + paramType); 
      } 
    } 
    return new TypeToken(.Gson.Types.newParameterizedTypeWithOwner(null, clazz, paramVarArgs));
  }
  
  public static TypeToken<?> getArray(Type paramType) {
    return new TypeToken(.Gson.Types.arrayOf(paramType));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\reflect\TypeToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */