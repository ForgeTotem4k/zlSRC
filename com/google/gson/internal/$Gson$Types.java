package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;

public final class $Gson$Types {
  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  
  private $Gson$Types() {
    throw new UnsupportedOperationException();
  }
  
  public static ParameterizedType newParameterizedTypeWithOwner(Type paramType, Class<?> paramClass, Type... paramVarArgs) {
    return new ParameterizedTypeImpl(paramType, paramClass, paramVarArgs);
  }
  
  public static GenericArrayType arrayOf(Type paramType) {
    return new GenericArrayTypeImpl(paramType);
  }
  
  public static WildcardType subtypeOf(Type paramType) {
    Type[] arrayOfType;
    if (paramType instanceof WildcardType) {
      arrayOfType = ((WildcardType)paramType).getUpperBounds();
    } else {
      arrayOfType = new Type[] { paramType };
    } 
    return new WildcardTypeImpl(arrayOfType, EMPTY_TYPE_ARRAY);
  }
  
  public static WildcardType supertypeOf(Type paramType) {
    Type[] arrayOfType;
    if (paramType instanceof WildcardType) {
      arrayOfType = ((WildcardType)paramType).getLowerBounds();
    } else {
      arrayOfType = new Type[] { paramType };
    } 
    return new WildcardTypeImpl(new Type[] { Object.class }, arrayOfType);
  }
  
  public static Type canonicalize(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.isArray() ? new GenericArrayTypeImpl(canonicalize(clazz.getComponentType())) : clazz;
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      return new ParameterizedTypeImpl(parameterizedType.getOwnerType(), (Class)parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
    } 
    if (paramType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramType;
      return new GenericArrayTypeImpl(genericArrayType.getGenericComponentType());
    } 
    if (paramType instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramType;
      return new WildcardTypeImpl(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
    } 
    return paramType;
  }
  
  public static Class<?> getRawType(Type paramType) {
    if (paramType instanceof Class)
      return (Class)paramType; 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      Type type = parameterizedType.getRawType();
      $Gson$Preconditions.checkArgument(type instanceof Class);
      return (Class)type;
    } 
    if (paramType instanceof GenericArrayType) {
      Type type = ((GenericArrayType)paramType).getGenericComponentType();
      return Array.newInstance(getRawType(type), 0).getClass();
    } 
    if (paramType instanceof TypeVariable)
      return Object.class; 
    if (paramType instanceof WildcardType) {
      Type[] arrayOfType = ((WildcardType)paramType).getUpperBounds();
      assert arrayOfType.length == 1;
      return getRawType(arrayOfType[0]);
    } 
    String str = (paramType == null) ? "null" : paramType.getClass().getName();
    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + paramType + "> is of type " + str);
  }
  
  private static boolean equal(Object paramObject1, Object paramObject2) {
    return Objects.equals(paramObject1, paramObject2);
  }
  
  public static boolean equals(Type paramType1, Type paramType2) {
    if (paramType1 == paramType2)
      return true; 
    if (paramType1 instanceof Class)
      return paramType1.equals(paramType2); 
    if (paramType1 instanceof ParameterizedType) {
      if (!(paramType2 instanceof ParameterizedType))
        return false; 
      ParameterizedType parameterizedType1 = (ParameterizedType)paramType1;
      ParameterizedType parameterizedType2 = (ParameterizedType)paramType2;
      return (equal(parameterizedType1.getOwnerType(), parameterizedType2.getOwnerType()) && parameterizedType1.getRawType().equals(parameterizedType2.getRawType()) && Arrays.equals((Object[])parameterizedType1.getActualTypeArguments(), (Object[])parameterizedType2.getActualTypeArguments()));
    } 
    if (paramType1 instanceof GenericArrayType) {
      if (!(paramType2 instanceof GenericArrayType))
        return false; 
      GenericArrayType genericArrayType1 = (GenericArrayType)paramType1;
      GenericArrayType genericArrayType2 = (GenericArrayType)paramType2;
      return equals(genericArrayType1.getGenericComponentType(), genericArrayType2.getGenericComponentType());
    } 
    if (paramType1 instanceof WildcardType) {
      if (!(paramType2 instanceof WildcardType))
        return false; 
      WildcardType wildcardType1 = (WildcardType)paramType1;
      WildcardType wildcardType2 = (WildcardType)paramType2;
      return (Arrays.equals((Object[])wildcardType1.getUpperBounds(), (Object[])wildcardType2.getUpperBounds()) && Arrays.equals((Object[])wildcardType1.getLowerBounds(), (Object[])wildcardType2.getLowerBounds()));
    } 
    if (paramType1 instanceof TypeVariable) {
      if (!(paramType2 instanceof TypeVariable))
        return false; 
      TypeVariable typeVariable1 = (TypeVariable)paramType1;
      TypeVariable typeVariable2 = (TypeVariable)paramType2;
      return (Objects.equals(typeVariable1.getGenericDeclaration(), typeVariable2.getGenericDeclaration()) && typeVariable1.getName().equals(typeVariable2.getName()));
    } 
    return false;
  }
  
  public static String typeToString(Type paramType) {
    return (paramType instanceof Class) ? ((Class)paramType).getName() : paramType.toString();
  }
  
  private static Type getGenericSupertype(Type paramType, Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass2 == paramClass1)
      return paramType; 
    if (paramClass2.isInterface()) {
      Class[] arrayOfClass = paramClass1.getInterfaces();
      byte b = 0;
      int i = arrayOfClass.length;
      while (b < i) {
        if (arrayOfClass[b] == paramClass2)
          return paramClass1.getGenericInterfaces()[b]; 
        if (paramClass2.isAssignableFrom(arrayOfClass[b]))
          return getGenericSupertype(paramClass1.getGenericInterfaces()[b], arrayOfClass[b], paramClass2); 
        b++;
      } 
    } 
    if (!paramClass1.isInterface())
      while (paramClass1 != Object.class) {
        Class<?> clazz = paramClass1.getSuperclass();
        if (clazz == paramClass2)
          return paramClass1.getGenericSuperclass(); 
        if (paramClass2.isAssignableFrom(clazz))
          return getGenericSupertype(paramClass1.getGenericSuperclass(), clazz, paramClass2); 
        paramClass1 = clazz;
      }  
    return paramClass2;
  }
  
  private static Type getSupertype(Type paramType, Class<?> paramClass1, Class<?> paramClass2) {
    if (paramType instanceof WildcardType) {
      Type[] arrayOfType = ((WildcardType)paramType).getUpperBounds();
      assert arrayOfType.length == 1;
      paramType = arrayOfType[0];
    } 
    $Gson$Preconditions.checkArgument(paramClass2.isAssignableFrom(paramClass1));
    return resolve(paramType, paramClass1, getGenericSupertype(paramType, paramClass1, paramClass2));
  }
  
  public static Type getArrayComponentType(Type paramType) {
    return (paramType instanceof GenericArrayType) ? ((GenericArrayType)paramType).getGenericComponentType() : ((Class)paramType).getComponentType();
  }
  
  public static Type getCollectionElementType(Type paramType, Class<?> paramClass) {
    Type type = getSupertype(paramType, paramClass, Collection.class);
    return (type instanceof ParameterizedType) ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class;
  }
  
  public static Type[] getMapKeyAndValueTypes(Type paramType, Class<?> paramClass) {
    if (Properties.class.isAssignableFrom(paramClass))
      return new Type[] { String.class, String.class }; 
    Type type = getSupertype(paramType, paramClass, Map.class);
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)type;
      return parameterizedType.getActualTypeArguments();
    } 
    return new Type[] { Object.class, Object.class };
  }
  
  public static Type resolve(Type paramType1, Class<?> paramClass, Type paramType2) {
    return resolve(paramType1, paramClass, paramType2, new HashMap<>());
  }
  
  private static Type resolve(Type paramType1, Class<?> paramClass, Type paramType2, Map<TypeVariable<?>, Type> paramMap) {
    // Byte code:
    //   0: aconst_null
    //   1: astore #4
    //   3: aload_2
    //   4: instanceof java/lang/reflect/TypeVariable
    //   7: ifeq -> 90
    //   10: aload_2
    //   11: checkcast java/lang/reflect/TypeVariable
    //   14: astore #5
    //   16: aload_3
    //   17: aload #5
    //   19: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   24: checkcast java/lang/reflect/Type
    //   27: astore #6
    //   29: aload #6
    //   31: ifnull -> 49
    //   34: aload #6
    //   36: getstatic java/lang/Void.TYPE : Ljava/lang/Class;
    //   39: if_acmpne -> 46
    //   42: aload_2
    //   43: goto -> 48
    //   46: aload #6
    //   48: areturn
    //   49: aload_3
    //   50: aload #5
    //   52: getstatic java/lang/Void.TYPE : Ljava/lang/Class;
    //   55: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   60: pop
    //   61: aload #4
    //   63: ifnonnull -> 70
    //   66: aload #5
    //   68: astore #4
    //   70: aload_0
    //   71: aload_1
    //   72: aload #5
    //   74: invokestatic resolveTypeVariable : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/TypeVariable;)Ljava/lang/reflect/Type;
    //   77: astore_2
    //   78: aload_2
    //   79: aload #5
    //   81: if_acmpne -> 87
    //   84: goto -> 493
    //   87: goto -> 3
    //   90: aload_2
    //   91: instanceof java/lang/Class
    //   94: ifeq -> 154
    //   97: aload_2
    //   98: checkcast java/lang/Class
    //   101: invokevirtual isArray : ()Z
    //   104: ifeq -> 154
    //   107: aload_2
    //   108: checkcast java/lang/Class
    //   111: astore #5
    //   113: aload #5
    //   115: invokevirtual getComponentType : ()Ljava/lang/Class;
    //   118: astore #6
    //   120: aload_0
    //   121: aload_1
    //   122: aload #6
    //   124: aload_3
    //   125: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   128: astore #7
    //   130: aload #6
    //   132: aload #7
    //   134: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   137: ifeq -> 145
    //   140: aload #5
    //   142: goto -> 150
    //   145: aload #7
    //   147: invokestatic arrayOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/GenericArrayType;
    //   150: astore_2
    //   151: goto -> 493
    //   154: aload_2
    //   155: instanceof java/lang/reflect/GenericArrayType
    //   158: ifeq -> 210
    //   161: aload_2
    //   162: checkcast java/lang/reflect/GenericArrayType
    //   165: astore #5
    //   167: aload #5
    //   169: invokeinterface getGenericComponentType : ()Ljava/lang/reflect/Type;
    //   174: astore #6
    //   176: aload_0
    //   177: aload_1
    //   178: aload #6
    //   180: aload_3
    //   181: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   184: astore #7
    //   186: aload #6
    //   188: aload #7
    //   190: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   193: ifeq -> 201
    //   196: aload #5
    //   198: goto -> 206
    //   201: aload #7
    //   203: invokestatic arrayOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/GenericArrayType;
    //   206: astore_2
    //   207: goto -> 493
    //   210: aload_2
    //   211: instanceof java/lang/reflect/ParameterizedType
    //   214: ifeq -> 379
    //   217: aload_2
    //   218: checkcast java/lang/reflect/ParameterizedType
    //   221: astore #5
    //   223: aload #5
    //   225: invokeinterface getOwnerType : ()Ljava/lang/reflect/Type;
    //   230: astore #6
    //   232: aload_0
    //   233: aload_1
    //   234: aload #6
    //   236: aload_3
    //   237: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   240: astore #7
    //   242: aload #7
    //   244: aload #6
    //   246: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   249: ifne -> 256
    //   252: iconst_1
    //   253: goto -> 257
    //   256: iconst_0
    //   257: istore #8
    //   259: aload #5
    //   261: invokeinterface getActualTypeArguments : ()[Ljava/lang/reflect/Type;
    //   266: astore #9
    //   268: iconst_0
    //   269: istore #10
    //   271: iconst_0
    //   272: istore #11
    //   274: aload #9
    //   276: arraylength
    //   277: istore #12
    //   279: iload #11
    //   281: iload #12
    //   283: if_icmpge -> 343
    //   286: aload_0
    //   287: aload_1
    //   288: aload #9
    //   290: iload #11
    //   292: aaload
    //   293: aload_3
    //   294: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   297: astore #13
    //   299: aload #13
    //   301: aload #9
    //   303: iload #11
    //   305: aaload
    //   306: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   309: ifne -> 337
    //   312: iload #10
    //   314: ifne -> 330
    //   317: aload #9
    //   319: invokevirtual clone : ()Ljava/lang/Object;
    //   322: checkcast [Ljava/lang/reflect/Type;
    //   325: astore #9
    //   327: iconst_1
    //   328: istore #10
    //   330: aload #9
    //   332: iload #11
    //   334: aload #13
    //   336: aastore
    //   337: iinc #11, 1
    //   340: goto -> 279
    //   343: iload #8
    //   345: ifne -> 353
    //   348: iload #10
    //   350: ifeq -> 373
    //   353: aload #7
    //   355: aload #5
    //   357: invokeinterface getRawType : ()Ljava/lang/reflect/Type;
    //   362: checkcast java/lang/Class
    //   365: aload #9
    //   367: invokestatic newParameterizedTypeWithOwner : (Ljava/lang/reflect/Type;Ljava/lang/Class;[Ljava/lang/reflect/Type;)Ljava/lang/reflect/ParameterizedType;
    //   370: goto -> 375
    //   373: aload #5
    //   375: astore_2
    //   376: goto -> 493
    //   379: aload_2
    //   380: instanceof java/lang/reflect/WildcardType
    //   383: ifeq -> 493
    //   386: aload_2
    //   387: checkcast java/lang/reflect/WildcardType
    //   390: astore #5
    //   392: aload #5
    //   394: invokeinterface getLowerBounds : ()[Ljava/lang/reflect/Type;
    //   399: astore #6
    //   401: aload #5
    //   403: invokeinterface getUpperBounds : ()[Ljava/lang/reflect/Type;
    //   408: astore #7
    //   410: aload #6
    //   412: arraylength
    //   413: iconst_1
    //   414: if_icmpne -> 450
    //   417: aload_0
    //   418: aload_1
    //   419: aload #6
    //   421: iconst_0
    //   422: aaload
    //   423: aload_3
    //   424: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   427: astore #8
    //   429: aload #8
    //   431: aload #6
    //   433: iconst_0
    //   434: aaload
    //   435: if_acmpeq -> 447
    //   438: aload #8
    //   440: invokestatic supertypeOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/WildcardType;
    //   443: astore_2
    //   444: goto -> 493
    //   447: goto -> 487
    //   450: aload #7
    //   452: arraylength
    //   453: iconst_1
    //   454: if_icmpne -> 487
    //   457: aload_0
    //   458: aload_1
    //   459: aload #7
    //   461: iconst_0
    //   462: aaload
    //   463: aload_3
    //   464: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   467: astore #8
    //   469: aload #8
    //   471: aload #7
    //   473: iconst_0
    //   474: aaload
    //   475: if_acmpeq -> 487
    //   478: aload #8
    //   480: invokestatic subtypeOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/WildcardType;
    //   483: astore_2
    //   484: goto -> 493
    //   487: aload #5
    //   489: astore_2
    //   490: goto -> 493
    //   493: aload #4
    //   495: ifnull -> 508
    //   498: aload_3
    //   499: aload #4
    //   501: aload_2
    //   502: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   507: pop
    //   508: aload_2
    //   509: areturn
  }
  
  private static Type resolveTypeVariable(Type paramType, Class<?> paramClass, TypeVariable<?> paramTypeVariable) {
    Class<?> clazz = declaringClassOf(paramTypeVariable);
    if (clazz == null)
      return paramTypeVariable; 
    Type type = getGenericSupertype(paramType, paramClass, clazz);
    if (type instanceof ParameterizedType) {
      int i = indexOf((Object[])clazz.getTypeParameters(), paramTypeVariable);
      return ((ParameterizedType)type).getActualTypeArguments()[i];
    } 
    return paramTypeVariable;
  }
  
  private static int indexOf(Object[] paramArrayOfObject, Object paramObject) {
    byte b = 0;
    int i = paramArrayOfObject.length;
    while (b < i) {
      if (paramObject.equals(paramArrayOfObject[b]))
        return b; 
      b++;
    } 
    throw new NoSuchElementException();
  }
  
  private static Class<?> declaringClassOf(TypeVariable<?> paramTypeVariable) {
    Object object = paramTypeVariable.getGenericDeclaration();
    return (object instanceof Class) ? (Class)object : null;
  }
  
  static void checkNotPrimitive(Type paramType) {
    $Gson$Preconditions.checkArgument((!(paramType instanceof Class) || !((Class)paramType).isPrimitive()));
  }
  
  public static boolean requiresOwnerType(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return (!Modifier.isStatic(clazz.getModifiers()) && clazz.getDeclaringClass() != null);
    } 
    return false;
  }
  
  private static final class WildcardTypeImpl implements WildcardType, Serializable {
    private final Type upperBound;
    
    private final Type lowerBound;
    
    private static final long serialVersionUID = 0L;
    
    public WildcardTypeImpl(Type[] param1ArrayOfType1, Type[] param1ArrayOfType2) {
      $Gson$Preconditions.checkArgument((param1ArrayOfType2.length <= 1));
      $Gson$Preconditions.checkArgument((param1ArrayOfType1.length == 1));
      if (param1ArrayOfType2.length == 1) {
        Objects.requireNonNull(param1ArrayOfType2[0]);
        $Gson$Types.checkNotPrimitive(param1ArrayOfType2[0]);
        $Gson$Preconditions.checkArgument((param1ArrayOfType1[0] == Object.class));
        this.lowerBound = $Gson$Types.canonicalize(param1ArrayOfType2[0]);
        this.upperBound = Object.class;
      } else {
        Objects.requireNonNull(param1ArrayOfType1[0]);
        $Gson$Types.checkNotPrimitive(param1ArrayOfType1[0]);
        this.lowerBound = null;
        this.upperBound = $Gson$Types.canonicalize(param1ArrayOfType1[0]);
      } 
    }
    
    public Type[] getUpperBounds() {
      return new Type[] { this.upperBound };
    }
    
    public Type[] getLowerBounds() {
      (new Type[1])[0] = this.lowerBound;
      return (this.lowerBound != null) ? new Type[1] : $Gson$Types.EMPTY_TYPE_ARRAY;
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof WildcardType && $Gson$Types.equals(this, (WildcardType)param1Object));
    }
    
    public int hashCode() {
      return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound.hashCode();
    }
    
    public String toString() {
      return (this.lowerBound != null) ? ("? super " + $Gson$Types.typeToString(this.lowerBound)) : ((this.upperBound == Object.class) ? "?" : ("? extends " + $Gson$Types.typeToString(this.upperBound)));
    }
  }
  
  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
    private final Type componentType;
    
    private static final long serialVersionUID = 0L;
    
    public GenericArrayTypeImpl(Type param1Type) {
      Objects.requireNonNull(param1Type);
      this.componentType = $Gson$Types.canonicalize(param1Type);
    }
    
    public Type getGenericComponentType() {
      return this.componentType;
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType)param1Object));
    }
    
    public int hashCode() {
      return this.componentType.hashCode();
    }
    
    public String toString() {
      return $Gson$Types.typeToString(this.componentType) + "[]";
    }
  }
  
  private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private final Type ownerType;
    
    private final Type rawType;
    
    private final Type[] typeArguments;
    
    private static final long serialVersionUID = 0L;
    
    public ParameterizedTypeImpl(Type param1Type, Class<?> param1Class, Type... param1VarArgs) {
      Objects.requireNonNull(param1Class);
      if (param1Type == null && $Gson$Types.requiresOwnerType(param1Class))
        throw new IllegalArgumentException("Must specify owner type for " + param1Class); 
      this.ownerType = (param1Type == null) ? null : $Gson$Types.canonicalize(param1Type);
      this.rawType = $Gson$Types.canonicalize(param1Class);
      this.typeArguments = (Type[])param1VarArgs.clone();
      byte b = 0;
      int i = this.typeArguments.length;
      while (b < i) {
        Objects.requireNonNull(this.typeArguments[b]);
        $Gson$Types.checkNotPrimitive(this.typeArguments[b]);
        this.typeArguments[b] = $Gson$Types.canonicalize(this.typeArguments[b]);
        b++;
      } 
    }
    
    public Type[] getActualTypeArguments() {
      return (Type[])this.typeArguments.clone();
    }
    
    public Type getRawType() {
      return this.rawType;
    }
    
    public Type getOwnerType() {
      return this.ownerType;
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType)param1Object));
    }
    
    private static int hashCodeOrZero(Object param1Object) {
      return (param1Object != null) ? param1Object.hashCode() : 0;
    }
    
    public int hashCode() {
      return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType.hashCode() ^ hashCodeOrZero(this.ownerType);
    }
    
    public String toString() {
      int i = this.typeArguments.length;
      if (i == 0)
        return $Gson$Types.typeToString(this.rawType); 
      StringBuilder stringBuilder = new StringBuilder(30 * (i + 1));
      stringBuilder.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
      for (byte b = 1; b < i; b++)
        stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[b])); 
      return stringBuilder.append(">").toString();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\$Gson$Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */