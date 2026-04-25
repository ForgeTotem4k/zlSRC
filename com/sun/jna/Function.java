package com.sun.jna;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Function extends Pointer {
  public static final int MAX_NARGS = 256;
  
  public static final int C_CONVENTION = 0;
  
  public static final int ALT_CONVENTION = 63;
  
  private static final int MASK_CC = 63;
  
  public static final int THROW_LAST_ERROR = 64;
  
  public static final int USE_VARARGS = 255;
  
  private static final int USE_VARARGS_SHIFT = 7;
  
  static final Integer INTEGER_TRUE = Integer.valueOf(-1);
  
  static final Integer INTEGER_FALSE = Integer.valueOf(0);
  
  private NativeLibrary library;
  
  private final String functionName;
  
  final String encoding;
  
  final int callFlags;
  
  final Map<String, ?> options;
  
  static final String OPTION_INVOKING_METHOD = "invoking-method";
  
  private static final VarArgsChecker IS_VARARGS = VarArgsChecker.create();
  
  public static Function getFunction(String paramString1, String paramString2) {
    return NativeLibrary.getInstance(paramString1).getFunction(paramString2);
  }
  
  public static Function getFunction(String paramString1, String paramString2, int paramInt) {
    return NativeLibrary.getInstance(paramString1).getFunction(paramString2, paramInt, null);
  }
  
  public static Function getFunction(String paramString1, String paramString2, int paramInt, String paramString3) {
    return NativeLibrary.getInstance(paramString1).getFunction(paramString2, paramInt, paramString3);
  }
  
  public static Function getFunction(Pointer paramPointer) {
    return getFunction(paramPointer, 0, (String)null);
  }
  
  public static Function getFunction(Pointer paramPointer, int paramInt) {
    return getFunction(paramPointer, paramInt, (String)null);
  }
  
  public static Function getFunction(Pointer paramPointer, int paramInt, String paramString) {
    return new Function(paramPointer, paramInt, paramString);
  }
  
  Function(NativeLibrary paramNativeLibrary, String paramString1, int paramInt, String paramString2) {
    checkCallingConvention(paramInt & 0x3F);
    if (paramString1 == null)
      throw new NullPointerException("Function name must not be null"); 
    this.library = paramNativeLibrary;
    this.functionName = paramString1;
    this.callFlags = paramInt;
    this.options = paramNativeLibrary.getOptions();
    this.encoding = (paramString2 != null) ? paramString2 : Native.getDefaultStringEncoding();
    try {
      this.peer = paramNativeLibrary.getSymbolAddress(paramString1);
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      throw new UnsatisfiedLinkError("Error looking up function '" + paramString1 + "': " + unsatisfiedLinkError.getMessage());
    } 
  }
  
  Function(Pointer paramPointer, int paramInt, String paramString) {
    checkCallingConvention(paramInt & 0x3F);
    if (paramPointer == null || paramPointer.peer == 0L)
      throw new NullPointerException("Function address may not be null"); 
    this.functionName = paramPointer.toString();
    this.callFlags = paramInt;
    this.peer = paramPointer.peer;
    this.options = Collections.EMPTY_MAP;
    this.encoding = (paramString != null) ? paramString : Native.getDefaultStringEncoding();
  }
  
  private void checkCallingConvention(int paramInt) throws IllegalArgumentException {
    if ((paramInt & 0x3F) != paramInt)
      throw new IllegalArgumentException("Unrecognized calling convention: " + paramInt); 
  }
  
  public String getName() {
    return this.functionName;
  }
  
  public int getCallingConvention() {
    return this.callFlags & 0x3F;
  }
  
  public Object invoke(Class<?> paramClass, Object[] paramArrayOfObject) {
    return invoke(paramClass, paramArrayOfObject, this.options);
  }
  
  public Object invoke(Class<?> paramClass, Object[] paramArrayOfObject, Map<String, ?> paramMap) {
    Method method = (Method)paramMap.get("invoking-method");
    Class<?>[] arrayOfClass = (method != null) ? method.getParameterTypes() : null;
    return invoke(method, arrayOfClass, paramClass, paramArrayOfObject, paramMap);
  }
  
  Object invoke(Method paramMethod, Class<?>[] paramArrayOfClass, Class<?> paramClass, Object[] paramArrayOfObject, Map<String, ?> paramMap) {
    Object[] arrayOfObject = new Object[0];
    if (paramArrayOfObject != null) {
      if (paramArrayOfObject.length > 256)
        throw new UnsupportedOperationException("Maximum argument count is 256"); 
      arrayOfObject = new Object[paramArrayOfObject.length];
      System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, arrayOfObject.length);
    } 
    TypeMapper typeMapper = (TypeMapper)paramMap.get("type-mapper");
    boolean bool = Boolean.TRUE.equals(paramMap.get("allow-objects"));
    boolean bool1 = (arrayOfObject.length > 0 && paramMethod != null) ? isVarArgs(paramMethod) : false;
    boolean bool2 = (arrayOfObject.length > 0 && paramMethod != null) ? fixedArgs(paramMethod) : false;
    for (byte b = 0; b < arrayOfObject.length; b++) {
      Class<?> clazz1 = (paramMethod != null) ? ((bool1 && b >= paramArrayOfClass.length - 1) ? paramArrayOfClass[paramArrayOfClass.length - 1].getComponentType() : paramArrayOfClass[b]) : null;
      arrayOfObject[b] = convertArgument(arrayOfObject, b, paramMethod, typeMapper, bool, clazz1);
    } 
    Class<?> clazz = paramClass;
    FromNativeConverter fromNativeConverter = null;
    if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      fromNativeConverter = nativeMappedConverter;
      clazz = nativeMappedConverter.nativeType();
    } else if (typeMapper != null) {
      fromNativeConverter = typeMapper.getFromNativeConverter(paramClass);
      if (fromNativeConverter != null)
        clazz = fromNativeConverter.nativeType(); 
    } 
    Object object = invoke(arrayOfObject, clazz, bool, bool2);
    if (fromNativeConverter != null) {
      FunctionResultContext functionResultContext;
      if (paramMethod != null) {
        functionResultContext = new MethodResultContext(paramClass, this, paramArrayOfObject, paramMethod);
      } else {
        functionResultContext = new FunctionResultContext(paramClass, this, paramArrayOfObject);
      } 
      object = fromNativeConverter.fromNative(object, functionResultContext);
    } 
    if (paramArrayOfObject != null)
      for (byte b1 = 0; b1 < paramArrayOfObject.length; b1++) {
        Object object1 = paramArrayOfObject[b1];
        if (object1 != null)
          if (object1 instanceof Structure) {
            if (!(object1 instanceof Structure.ByValue))
              ((Structure)object1).autoRead(); 
          } else if (arrayOfObject[b1] instanceof PostCallRead) {
            ((PostCallRead)arrayOfObject[b1]).read();
            if (arrayOfObject[b1] instanceof PointerArray) {
              PointerArray pointerArray = (PointerArray)arrayOfObject[b1];
              if (Structure.ByReference[].class.isAssignableFrom(object1.getClass())) {
                Class<?> clazz1 = object1.getClass().getComponentType();
                Structure[] arrayOfStructure = (Structure[])object1;
                for (byte b2 = 0; b2 < arrayOfStructure.length; b2++) {
                  Pointer pointer = pointerArray.getPointer((Native.POINTER_SIZE * b2));
                  arrayOfStructure[b2] = Structure.updateStructureByReference(clazz1, arrayOfStructure[b2], pointer);
                } 
              } 
            } 
          } else if (Structure[].class.isAssignableFrom(object1.getClass())) {
            Structure.autoRead((Structure[])object1);
          }  
      }  
    return object;
  }
  
  Object invoke(Object[] paramArrayOfObject, Class<?> paramClass, boolean paramBoolean) {
    return invoke(paramArrayOfObject, paramClass, paramBoolean, 0);
  }
  
  Object invoke(Object[] paramArrayOfObject, Class<?> paramClass, boolean paramBoolean, int paramInt) {
    Object object = null;
    int i = this.callFlags | (paramInt & 0xFF) << 7;
    if (paramClass == null || paramClass == void.class || paramClass == Void.class) {
      Native.invokeVoid(this, this.peer, i, paramArrayOfObject);
      object = null;
    } else if (paramClass == boolean.class || paramClass == Boolean.class) {
      object = valueOf((Native.invokeInt(this, this.peer, i, paramArrayOfObject) != 0));
    } else if (paramClass == byte.class || paramClass == Byte.class) {
      Byte byte_ = Byte.valueOf((byte)Native.invokeInt(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == short.class || paramClass == Short.class) {
      Short short_ = Short.valueOf((short)Native.invokeInt(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == char.class || paramClass == Character.class) {
      Character character = Character.valueOf((char)Native.invokeInt(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == int.class || paramClass == Integer.class) {
      Integer integer = Integer.valueOf(Native.invokeInt(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == long.class || paramClass == Long.class) {
      Long long_ = Long.valueOf(Native.invokeLong(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == float.class || paramClass == Float.class) {
      Float float_ = Float.valueOf(Native.invokeFloat(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == double.class || paramClass == Double.class) {
      Double double_ = Double.valueOf(Native.invokeDouble(this, this.peer, i, paramArrayOfObject));
    } else if (paramClass == String.class) {
      String str = invokeString(i, paramArrayOfObject, false);
    } else if (paramClass == WString.class) {
      String str = invokeString(i, paramArrayOfObject, true);
      if (str != null)
        WString wString = new WString(str); 
    } else {
      if (Pointer.class.isAssignableFrom(paramClass))
        return invokePointer(i, paramArrayOfObject); 
      if (Structure.class.isAssignableFrom(paramClass)) {
        if (Structure.ByValue.class.isAssignableFrom(paramClass)) {
          Structure structure2 = Native.invokeStructure(this, this.peer, i, paramArrayOfObject, (Structure)Structure.newInstance(paramClass));
          structure2.autoRead();
          Structure structure1 = structure2;
        } else {
          Pointer pointer = invokePointer(i, paramArrayOfObject);
          if (pointer != null) {
            Structure structure2 = (Structure)Structure.newInstance((Class)paramClass, pointer);
            structure2.conditionalAutoRead();
            Structure structure1 = structure2;
          } 
        } 
      } else if (Callback.class.isAssignableFrom(paramClass)) {
        Pointer pointer = invokePointer(i, paramArrayOfObject);
        if (pointer != null)
          Callback callback = CallbackReference.getCallback(paramClass, pointer); 
      } else if (paramClass == String[].class) {
        Pointer pointer = invokePointer(i, paramArrayOfObject);
        if (pointer != null)
          String[] arrayOfString = pointer.getStringArray(0L, this.encoding); 
      } else if (paramClass == WString[].class) {
        Pointer pointer = invokePointer(i, paramArrayOfObject);
        if (pointer != null) {
          String[] arrayOfString = pointer.getWideStringArray(0L);
          WString[] arrayOfWString2 = new WString[arrayOfString.length];
          for (byte b = 0; b < arrayOfString.length; b++)
            arrayOfWString2[b] = new WString(arrayOfString[b]); 
          WString[] arrayOfWString1 = arrayOfWString2;
        } 
      } else if (paramClass == Pointer[].class) {
        Pointer pointer = invokePointer(i, paramArrayOfObject);
        if (pointer != null)
          Pointer[] arrayOfPointer = pointer.getPointerArray(0L); 
      } else if (paramBoolean) {
        object = Native.invokeObject(this, this.peer, i, paramArrayOfObject);
        if (object != null && !paramClass.isAssignableFrom(object.getClass()))
          throw new ClassCastException("Return type " + paramClass + " does not match result " + object.getClass()); 
      } else {
        throw new IllegalArgumentException("Unsupported return type " + paramClass + " in function " + getName());
      } 
    } 
    return object;
  }
  
  private Pointer invokePointer(int paramInt, Object[] paramArrayOfObject) {
    long l = Native.invokePointer(this, this.peer, paramInt, paramArrayOfObject);
    return (l == 0L) ? null : new Pointer(l);
  }
  
  private Object convertArgument(Object[] paramArrayOfObject, int paramInt, Method paramMethod, TypeMapper paramTypeMapper, boolean paramBoolean, Class<?> paramClass) {
    Object object = paramArrayOfObject[paramInt];
    if (object != null) {
      Class<?> clazz1 = object.getClass();
      ToNativeConverter toNativeConverter = null;
      if (NativeMapped.class.isAssignableFrom(clazz1)) {
        toNativeConverter = NativeMappedConverter.getInstance(clazz1);
      } else if (paramTypeMapper != null) {
        toNativeConverter = paramTypeMapper.getToNativeConverter(clazz1);
      } 
      if (toNativeConverter != null) {
        FunctionParameterContext functionParameterContext;
        if (paramMethod != null) {
          functionParameterContext = new MethodParameterContext(this, paramArrayOfObject, paramInt, paramMethod);
        } else {
          functionParameterContext = new FunctionParameterContext(this, paramArrayOfObject, paramInt);
        } 
        object = toNativeConverter.toNative(object, functionParameterContext);
      } 
    } 
    if (object == null || isPrimitiveArray(object.getClass()))
      return object; 
    Class<?> clazz = object.getClass();
    if (object instanceof Structure) {
      Structure structure = (Structure)object;
      structure.autoWrite();
      if (structure instanceof Structure.ByValue) {
        Class<?> clazz1 = structure.getClass();
        if (paramMethod != null) {
          Class[] arrayOfClass = paramMethod.getParameterTypes();
          if (IS_VARARGS.isVarArgs(paramMethod)) {
            if (paramInt < arrayOfClass.length - 1) {
              clazz1 = arrayOfClass[paramInt];
            } else {
              Class<?> clazz2 = arrayOfClass[arrayOfClass.length - 1].getComponentType();
              if (clazz2 != Object.class)
                clazz1 = clazz2; 
            } 
          } else {
            clazz1 = arrayOfClass[paramInt];
          } 
        } 
        if (Structure.ByValue.class.isAssignableFrom(clazz1))
          return structure; 
      } 
      return structure.getPointer();
    } 
    if (object instanceof Callback)
      return CallbackReference.getFunctionPointer((Callback)object); 
    if (object instanceof String)
      return (new NativeString((String)object, this.encoding)).getPointer(); 
    if (object instanceof WString)
      return (new NativeString(object.toString(), true)).getPointer(); 
    if (object instanceof Boolean)
      return Boolean.TRUE.equals(object) ? INTEGER_TRUE : INTEGER_FALSE; 
    if (String[].class == clazz)
      return new StringArray((String[])object, this.encoding); 
    if (WString[].class == clazz)
      return new StringArray((WString[])object); 
    if (Pointer[].class == clazz)
      return new PointerArray((Pointer[])object); 
    if (NativeMapped[].class.isAssignableFrom(clazz))
      return new NativeMappedArray((NativeMapped[])object); 
    if (Structure[].class.isAssignableFrom(clazz)) {
      Structure[] arrayOfStructure = (Structure[])object;
      Class<?> clazz1 = clazz.getComponentType();
      boolean bool = Structure.ByReference.class.isAssignableFrom(clazz1);
      if (paramClass != null && !Structure.ByReference[].class.isAssignableFrom(paramClass)) {
        if (bool)
          throw new IllegalArgumentException("Function " + getName() + " declared Structure[] at parameter " + paramInt + " but array of " + clazz1 + " was passed"); 
        for (byte b = 0; b < arrayOfStructure.length; b++) {
          if (arrayOfStructure[b] instanceof Structure.ByReference)
            throw new IllegalArgumentException("Function " + getName() + " declared Structure[] at parameter " + paramInt + " but element " + b + " is of Structure.ByReference type"); 
        } 
      } 
      if (bool) {
        Structure.autoWrite(arrayOfStructure);
        Pointer[] arrayOfPointer = new Pointer[arrayOfStructure.length + 1];
        for (byte b = 0; b < arrayOfStructure.length; b++)
          arrayOfPointer[b] = (arrayOfStructure[b] != null) ? arrayOfStructure[b].getPointer() : null; 
        return new PointerArray(arrayOfPointer);
      } 
      if (arrayOfStructure.length == 0)
        throw new IllegalArgumentException("Structure array must have non-zero length"); 
      if (arrayOfStructure[0] == null) {
        Structure.newInstance(clazz1).toArray(arrayOfStructure);
        return arrayOfStructure[0].getPointer();
      } 
      Structure.autoWrite(arrayOfStructure);
      return arrayOfStructure[0].getPointer();
    } 
    if (clazz.isArray())
      throw new IllegalArgumentException("Unsupported array argument type: " + clazz.getComponentType()); 
    if (paramBoolean)
      return object; 
    if (!Native.isSupportedNativeType(object.getClass()))
      throw new IllegalArgumentException("Unsupported argument type " + object.getClass().getName() + " at parameter " + paramInt + " of function " + getName()); 
    return object;
  }
  
  private boolean isPrimitiveArray(Class<?> paramClass) {
    return (paramClass.isArray() && paramClass.getComponentType().isPrimitive());
  }
  
  public void invoke(Object[] paramArrayOfObject) {
    invoke(Void.class, paramArrayOfObject);
  }
  
  private String invokeString(int paramInt, Object[] paramArrayOfObject, boolean paramBoolean) {
    Pointer pointer = invokePointer(paramInt, paramArrayOfObject);
    String str = null;
    if (pointer != null)
      if (paramBoolean) {
        str = pointer.getWideString(0L);
      } else {
        str = pointer.getString(0L, this.encoding);
      }  
    return str;
  }
  
  public String toString() {
    return (this.library != null) ? ("native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer)) : ("native function@0x" + Long.toHexString(this.peer));
  }
  
  public Object invokeObject(Object[] paramArrayOfObject) {
    return invoke(Object.class, paramArrayOfObject);
  }
  
  public Pointer invokePointer(Object[] paramArrayOfObject) {
    return (Pointer)invoke(Pointer.class, paramArrayOfObject);
  }
  
  public String invokeString(Object[] paramArrayOfObject, boolean paramBoolean) {
    Object object = invoke(paramBoolean ? WString.class : String.class, paramArrayOfObject);
    return (object != null) ? object.toString() : null;
  }
  
  public int invokeInt(Object[] paramArrayOfObject) {
    return ((Integer)invoke(Integer.class, paramArrayOfObject)).intValue();
  }
  
  public long invokeLong(Object[] paramArrayOfObject) {
    return ((Long)invoke(Long.class, paramArrayOfObject)).longValue();
  }
  
  public float invokeFloat(Object[] paramArrayOfObject) {
    return ((Float)invoke(Float.class, paramArrayOfObject)).floatValue();
  }
  
  public double invokeDouble(Object[] paramArrayOfObject) {
    return ((Double)invoke(Double.class, paramArrayOfObject)).doubleValue();
  }
  
  public void invokeVoid(Object[] paramArrayOfObject) {
    invoke(Void.class, paramArrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null)
      return false; 
    if (paramObject.getClass() == getClass()) {
      Function function = (Function)paramObject;
      return (function.callFlags == this.callFlags && function.options.equals(this.options) && function.peer == this.peer);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.callFlags + this.options.hashCode() + super.hashCode();
  }
  
  static Object[] concatenateVarArgs(Object[] paramArrayOfObject) {
    if (paramArrayOfObject != null && paramArrayOfObject.length > 0) {
      Object object = paramArrayOfObject[paramArrayOfObject.length - 1];
      Class<?> clazz = (object != null) ? object.getClass() : null;
      if (clazz != null && clazz.isArray()) {
        Object[] arrayOfObject1 = (Object[])object;
        for (byte b = 0; b < arrayOfObject1.length; b++) {
          if (arrayOfObject1[b] instanceof Float)
            arrayOfObject1[b] = Double.valueOf(((Float)arrayOfObject1[b]).floatValue()); 
        } 
        Object[] arrayOfObject2 = new Object[paramArrayOfObject.length + arrayOfObject1.length];
        System.arraycopy(paramArrayOfObject, 0, arrayOfObject2, 0, paramArrayOfObject.length - 1);
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, paramArrayOfObject.length - 1, arrayOfObject1.length);
        arrayOfObject2[arrayOfObject2.length - 1] = null;
        paramArrayOfObject = arrayOfObject2;
      } 
    } 
    return paramArrayOfObject;
  }
  
  static boolean isVarArgs(Method paramMethod) {
    return IS_VARARGS.isVarArgs(paramMethod);
  }
  
  static int fixedArgs(Method paramMethod) {
    return IS_VARARGS.fixedArgs(paramMethod);
  }
  
  static Boolean valueOf(boolean paramBoolean) {
    return paramBoolean ? Boolean.TRUE : Boolean.FALSE;
  }
  
  public static interface PostCallRead {
    void read();
  }
  
  private static class PointerArray extends Memory implements PostCallRead {
    private final Pointer[] original;
    
    public PointerArray(Pointer[] param1ArrayOfPointer) {
      super((Native.POINTER_SIZE * (param1ArrayOfPointer.length + 1)));
      this.original = param1ArrayOfPointer;
      for (byte b = 0; b < param1ArrayOfPointer.length; b++)
        setPointer((b * Native.POINTER_SIZE), param1ArrayOfPointer[b]); 
      setPointer((Native.POINTER_SIZE * param1ArrayOfPointer.length), (Pointer)null);
    }
    
    public void read() {
      read(0L, this.original, 0, this.original.length);
    }
  }
  
  private static class NativeMappedArray extends Memory implements PostCallRead {
    private final NativeMapped[] original;
    
    public NativeMappedArray(NativeMapped[] param1ArrayOfNativeMapped) {
      super(Native.getNativeSize(param1ArrayOfNativeMapped.getClass(), param1ArrayOfNativeMapped));
      this.original = param1ArrayOfNativeMapped;
      setValue(0L, this.original, this.original.getClass());
    }
    
    public void read() {
      getValue(0L, this.original.getClass(), this.original);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Function.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */