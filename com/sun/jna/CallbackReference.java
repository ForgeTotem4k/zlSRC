package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.Closeable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackReference extends WeakReference<Callback> implements Closeable {
  static final Map<Callback, CallbackReference> callbackMap = new WeakHashMap<>();
  
  static final Map<Callback, CallbackReference> directCallbackMap = new WeakHashMap<>();
  
  static final Map<Pointer, Reference<Callback>[]> pointerCallbackMap = (Map)new WeakHashMap<>();
  
  static final Map<Object, Object> allocations = Collections.synchronizedMap(new WeakHashMap<>());
  
  private static final Map<Long, Reference<CallbackReference>> allocatedMemory = new ConcurrentHashMap<>();
  
  private static final Method PROXY_CALLBACK_METHOD;
  
  private static final Class<?> DLL_CALLBACK_CLASS;
  
  private static final Map<Callback, CallbackThreadInitializer> initializers = new WeakHashMap<>();
  
  Cleaner.Cleanable cleanable;
  
  Pointer cbstruct;
  
  Pointer trampoline;
  
  CallbackProxy proxy;
  
  Method method;
  
  int callingConvention;
  
  static CallbackThreadInitializer setCallbackThreadInitializer(Callback paramCallback, CallbackThreadInitializer paramCallbackThreadInitializer) {
    synchronized (initializers) {
      if (paramCallbackThreadInitializer != null)
        return initializers.put(paramCallback, paramCallbackThreadInitializer); 
      return initializers.remove(paramCallback);
    } 
  }
  
  private static ThreadGroup initializeThread(Callback paramCallback, AttachOptions paramAttachOptions) {
    CallbackThreadInitializer callbackThreadInitializer = null;
    if (paramCallback instanceof DefaultCallbackProxy)
      paramCallback = ((DefaultCallbackProxy)paramCallback).getCallback(); 
    synchronized (initializers) {
      callbackThreadInitializer = initializers.get(paramCallback);
    } 
    ThreadGroup threadGroup = null;
    if (callbackThreadInitializer != null) {
      threadGroup = callbackThreadInitializer.getThreadGroup(paramCallback);
      paramAttachOptions.name = callbackThreadInitializer.getName(paramCallback);
      paramAttachOptions.daemon = callbackThreadInitializer.isDaemon(paramCallback);
      paramAttachOptions.detach = callbackThreadInitializer.detach(paramCallback);
      paramAttachOptions.write();
    } 
    return threadGroup;
  }
  
  public static Callback getCallback(Class<?> paramClass, Pointer paramPointer) {
    return getCallback(paramClass, paramPointer, false);
  }
  
  private static Callback getCallback(Class<?> paramClass, Pointer paramPointer, boolean paramBoolean) {
    if (paramPointer == null)
      return null; 
    if (!paramClass.isInterface())
      throw new IllegalArgumentException("Callback type must be an interface"); 
    Map<Callback, CallbackReference> map = paramBoolean ? directCallbackMap : callbackMap;
    synchronized (pointerCallbackMap) {
      Reference[] arrayOfReference = (Reference[])pointerCallbackMap.get(paramPointer);
      Callback callback = getTypeAssignableCallback(paramClass, (Reference<Callback>[])arrayOfReference);
      if (callback != null)
        return callback; 
      callback = createCallback(paramClass, paramPointer);
      pointerCallbackMap.put(paramPointer, addCallbackToArray(callback, (Reference<Callback>[])arrayOfReference));
      map.remove(callback);
      return callback;
    } 
  }
  
  private static Callback getTypeAssignableCallback(Class<?> paramClass, Reference<Callback>[] paramArrayOfReference) {
    if (paramArrayOfReference != null)
      for (byte b = 0; b < paramArrayOfReference.length; b++) {
        Callback callback = paramArrayOfReference[b].get();
        if (callback != null && paramClass.isAssignableFrom(callback.getClass()))
          return callback; 
      }  
    return null;
  }
  
  private static Reference<Callback>[] addCallbackToArray(Callback paramCallback, Reference<Callback>[] paramArrayOfReference) {
    byte b1 = 1;
    if (paramArrayOfReference != null)
      for (byte b = 0; b < paramArrayOfReference.length; b++) {
        if (paramArrayOfReference[b].get() == null) {
          paramArrayOfReference[b] = null;
        } else {
          b1++;
        } 
      }  
    Reference[] arrayOfReference = new Reference[b1];
    byte b2 = 0;
    if (paramArrayOfReference != null)
      for (byte b = 0; b < paramArrayOfReference.length; b++) {
        if (paramArrayOfReference[b] != null)
          arrayOfReference[b2++] = paramArrayOfReference[b]; 
      }  
    arrayOfReference[b2] = new WeakReference<>(paramCallback);
    return (Reference<Callback>[])arrayOfReference;
  }
  
  private static Callback createCallback(Class<?> paramClass, Pointer paramPointer) {
    boolean bool = AltCallingConvention.class.isAssignableFrom(paramClass) ? true : false;
    HashMap<String, Object> hashMap = new HashMap<>(Native.getLibraryOptions(paramClass));
    hashMap.put("invoking-method", getCallbackMethod(paramClass));
    NativeFunctionHandler nativeFunctionHandler = new NativeFunctionHandler(paramPointer, bool, hashMap);
    return (Callback)Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, nativeFunctionHandler);
  }
  
  private CallbackReference(Callback paramCallback, int paramInt, boolean paramBoolean) {
    super(paramCallback);
    TypeMapper typeMapper = Native.getTypeMapper(paramCallback.getClass());
    this.callingConvention = paramInt;
    boolean bool = Platform.isPPC();
    if (paramBoolean) {
      Method method = getCallbackMethod(paramCallback);
      Class[] arrayOfClass = method.getParameterTypes();
      for (byte b = 0; b < arrayOfClass.length; b++) {
        if (bool && (arrayOfClass[b] == float.class || arrayOfClass[b] == double.class)) {
          paramBoolean = false;
          break;
        } 
        if (typeMapper != null && typeMapper.getFromNativeConverter(arrayOfClass[b]) != null) {
          paramBoolean = false;
          break;
        } 
      } 
      if (typeMapper != null && typeMapper.getToNativeConverter(method.getReturnType()) != null)
        paramBoolean = false; 
    } 
    String str = Native.getStringEncoding(paramCallback.getClass());
    long l = 0L;
    if (paramBoolean) {
      this.method = getCallbackMethod(paramCallback);
      Class[] arrayOfClass = this.method.getParameterTypes();
      Class<?> clazz = this.method.getReturnType();
      int i = 1;
      if (DLL_CALLBACK_CLASS != null && DLL_CALLBACK_CLASS.isInstance(paramCallback))
        i |= 0x2; 
      l = Native.createNativeCallback(paramCallback, this.method, arrayOfClass, clazz, paramInt, i, str);
    } else {
      if (paramCallback instanceof CallbackProxy) {
        this.proxy = (CallbackProxy)paramCallback;
      } else {
        this.proxy = new DefaultCallbackProxy(getCallbackMethod(paramCallback), typeMapper, str);
      } 
      Class[] arrayOfClass = this.proxy.getParameterTypes();
      Class<?> clazz = this.proxy.getReturnType();
      if (typeMapper != null) {
        for (byte b1 = 0; b1 < arrayOfClass.length; b1++) {
          FromNativeConverter fromNativeConverter = typeMapper.getFromNativeConverter(arrayOfClass[b1]);
          if (fromNativeConverter != null)
            arrayOfClass[b1] = fromNativeConverter.nativeType(); 
        } 
        ToNativeConverter toNativeConverter = typeMapper.getToNativeConverter(clazz);
        if (toNativeConverter != null)
          clazz = toNativeConverter.nativeType(); 
      } 
      byte b;
      for (b = 0; b < arrayOfClass.length; b++) {
        arrayOfClass[b] = getNativeType(arrayOfClass[b]);
        if (!isAllowableNativeType(arrayOfClass[b])) {
          String str1 = "Callback argument " + arrayOfClass[b] + " requires custom type conversion";
          throw new IllegalArgumentException(str1);
        } 
      } 
      clazz = getNativeType(clazz);
      if (!isAllowableNativeType(clazz)) {
        String str1 = "Callback return type " + clazz + " requires custom type conversion";
        throw new IllegalArgumentException(str1);
      } 
      b = (DLL_CALLBACK_CLASS != null && DLL_CALLBACK_CLASS.isInstance(paramCallback)) ? 2 : 0;
      l = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, arrayOfClass, clazz, paramInt, b, str);
    } 
    this.cbstruct = (l != 0L) ? new Pointer(l) : null;
    if (l != 0L) {
      allocatedMemory.put(Long.valueOf(l), new WeakReference<>(this));
      this.cleanable = Cleaner.getCleaner().register(this, new CallbackReferenceDisposer(this.cbstruct));
    } 
  }
  
  private Class<?> getNativeType(Class<?> paramClass) {
    if (Structure.class.isAssignableFrom(paramClass)) {
      Structure.validate((Class)paramClass);
      if (!Structure.ByValue.class.isAssignableFrom(paramClass))
        return Pointer.class; 
    } else {
      if (NativeMapped.class.isAssignableFrom(paramClass))
        return NativeMappedConverter.getInstance(paramClass).nativeType(); 
      if (paramClass == String.class || paramClass == WString.class || paramClass == String[].class || paramClass == WString[].class || Callback.class.isAssignableFrom(paramClass))
        return Pointer.class; 
    } 
    return paramClass;
  }
  
  private static Method checkMethod(Method paramMethod) {
    if ((paramMethod.getParameterTypes()).length > 256) {
      String str = "Method signature exceeds the maximum parameter count: " + paramMethod;
      throw new UnsupportedOperationException(str);
    } 
    return paramMethod;
  }
  
  static Class<?> findCallbackClass(Class<?> paramClass) {
    if (!Callback.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass.getName() + " is not derived from com.sun.jna.Callback"); 
    if (paramClass.isInterface())
      return paramClass; 
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (Callback.class.isAssignableFrom(arrayOfClass[b]))
        try {
          getCallbackMethod(arrayOfClass[b]);
          return arrayOfClass[b];
        } catch (IllegalArgumentException illegalArgumentException) {
          break;
        }  
    } 
    return Callback.class.isAssignableFrom(paramClass.getSuperclass()) ? findCallbackClass(paramClass.getSuperclass()) : paramClass;
  }
  
  private static Method getCallbackMethod(Callback paramCallback) {
    return getCallbackMethod(findCallbackClass(paramCallback.getClass()));
  }
  
  private static Method getCallbackMethod(Class<?> paramClass) {
    Method[] arrayOfMethod1 = paramClass.getDeclaredMethods();
    Method[] arrayOfMethod2 = paramClass.getMethods();
    HashSet hashSet = new HashSet(Arrays.asList((Object[])arrayOfMethod1));
    hashSet.retainAll(Arrays.asList((Object[])arrayOfMethod2));
    Iterator<Method> iterator = hashSet.iterator();
    while (iterator.hasNext()) {
      Method method = iterator.next();
      if (Callback.FORBIDDEN_NAMES.contains(method.getName()))
        iterator.remove(); 
    } 
    Method[] arrayOfMethod3 = (Method[])hashSet.toArray((Object[])new Method[0]);
    if (arrayOfMethod3.length == 1)
      return checkMethod(arrayOfMethod3[0]); 
    for (byte b = 0; b < arrayOfMethod3.length; b++) {
      Method method = arrayOfMethod3[b];
      if ("callback".equals(method.getName()))
        return checkMethod(method); 
    } 
    String str = "Callback must implement a single public method, or one public method named 'callback'";
    throw new IllegalArgumentException(str);
  }
  
  private void setCallbackOptions(int paramInt) {
    this.cbstruct.setInt(Native.POINTER_SIZE, paramInt);
  }
  
  public Pointer getTrampoline() {
    if (this.trampoline == null)
      this.trampoline = this.cbstruct.getPointer(0L); 
    return this.trampoline;
  }
  
  public void close() {
    if (this.cleanable != null)
      this.cleanable.clean(); 
    this.cbstruct = null;
  }
  
  @Deprecated
  protected void dispose() {
    close();
  }
  
  static void disposeAll() {
    LinkedList linkedList = new LinkedList(allocatedMemory.values());
    for (Reference<CallbackReference> reference : (Iterable<Reference<CallbackReference>>)linkedList) {
      CallbackReference callbackReference = reference.get();
      if (callbackReference != null)
        callbackReference.close(); 
    } 
  }
  
  private Callback getCallback() {
    return get();
  }
  
  private static Pointer getNativeFunctionPointer(Callback paramCallback) {
    if (Proxy.isProxyClass(paramCallback.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramCallback);
      if (invocationHandler instanceof NativeFunctionHandler)
        return ((NativeFunctionHandler)invocationHandler).getPointer(); 
    } 
    return null;
  }
  
  public static Pointer getFunctionPointer(Callback paramCallback) {
    return getFunctionPointer(paramCallback, false);
  }
  
  private static Pointer getFunctionPointer(Callback paramCallback, boolean paramBoolean) {
    Pointer pointer = null;
    if (paramCallback == null)
      return null; 
    if ((pointer = getNativeFunctionPointer(paramCallback)) != null)
      return pointer; 
    Map<String, Object> map = Native.getLibraryOptions(paramCallback.getClass());
    boolean bool = (paramCallback instanceof AltCallingConvention) ? true : ((map != null && map.containsKey("calling-convention")) ? ((Integer)map.get("calling-convention")).intValue() : false);
    Map<Callback, CallbackReference> map1 = paramBoolean ? directCallbackMap : callbackMap;
    synchronized (pointerCallbackMap) {
      CallbackReference callbackReference = map1.get(paramCallback);
      if (callbackReference == null || callbackReference.cbstruct == null) {
        callbackReference = new CallbackReference(paramCallback, bool, paramBoolean);
        map1.put(paramCallback, callbackReference);
        pointerCallbackMap.put(callbackReference.getTrampoline(), addCallbackToArray(paramCallback, null));
        if (initializers.containsKey(paramCallback))
          callbackReference.setCallbackOptions(1); 
      } 
      return callbackReference.getTrampoline();
    } 
  }
  
  private static boolean isAllowableNativeType(Class<?> paramClass) {
    return (paramClass == void.class || paramClass == Void.class || paramClass == boolean.class || paramClass == Boolean.class || paramClass == byte.class || paramClass == Byte.class || paramClass == short.class || paramClass == Short.class || paramClass == char.class || paramClass == Character.class || paramClass == int.class || paramClass == Integer.class || paramClass == long.class || paramClass == Long.class || paramClass == float.class || paramClass == Float.class || paramClass == double.class || paramClass == Double.class || (Structure.ByValue.class.isAssignableFrom(paramClass) && Structure.class.isAssignableFrom(paramClass)) || Pointer.class.isAssignableFrom(paramClass));
  }
  
  private static Pointer getNativeString(Object paramObject, boolean paramBoolean) {
    if (paramObject != null) {
      NativeString nativeString = new NativeString(paramObject.toString(), paramBoolean);
      allocations.put(paramObject, nativeString);
      return nativeString.getPointer();
    } 
    return null;
  }
  
  static {
    try {
      PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", new Class[] { Object[].class });
    } catch (Exception exception) {
      throw new Error("Error looking up CallbackProxy.callback() method");
    } 
    if (Platform.isWindows()) {
      try {
        DLL_CALLBACK_CLASS = Class.forName("com.sun.jna.win32.DLLCallback");
      } catch (ClassNotFoundException classNotFoundException) {
        throw new Error("Error loading DLLCallback class", classNotFoundException);
      } 
    } else {
      DLL_CALLBACK_CLASS = null;
    } 
  }
  
  private class DefaultCallbackProxy implements CallbackProxy {
    private final Method callbackMethod;
    
    private ToNativeConverter toNative;
    
    private final FromNativeConverter[] fromNative;
    
    private final String encoding;
    
    public DefaultCallbackProxy(Method param1Method, TypeMapper param1TypeMapper, String param1String) {
      this.callbackMethod = param1Method;
      this.encoding = param1String;
      Class[] arrayOfClass = param1Method.getParameterTypes();
      Class<?> clazz = param1Method.getReturnType();
      this.fromNative = new FromNativeConverter[arrayOfClass.length];
      if (NativeMapped.class.isAssignableFrom(clazz)) {
        this.toNative = NativeMappedConverter.getInstance(clazz);
      } else if (param1TypeMapper != null) {
        this.toNative = param1TypeMapper.getToNativeConverter(clazz);
      } 
      for (byte b = 0; b < this.fromNative.length; b++) {
        if (NativeMapped.class.isAssignableFrom(arrayOfClass[b])) {
          this.fromNative[b] = new NativeMappedConverter(arrayOfClass[b]);
        } else if (param1TypeMapper != null) {
          this.fromNative[b] = param1TypeMapper.getFromNativeConverter(arrayOfClass[b]);
        } 
      } 
      if (!param1Method.isAccessible())
        try {
          param1Method.setAccessible(true);
        } catch (SecurityException securityException) {
          throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + param1Method);
        }  
    }
    
    public Callback getCallback() {
      return CallbackReference.this.getCallback();
    }
    
    private Object invokeCallback(Object[] param1ArrayOfObject) {
      Class[] arrayOfClass = this.callbackMethod.getParameterTypes();
      Object[] arrayOfObject = new Object[param1ArrayOfObject.length];
      for (byte b1 = 0; b1 < param1ArrayOfObject.length; b1++) {
        Class<?> clazz = arrayOfClass[b1];
        Object object1 = param1ArrayOfObject[b1];
        if (this.fromNative[b1] != null) {
          CallbackParameterContext callbackParameterContext = new CallbackParameterContext(clazz, this.callbackMethod, param1ArrayOfObject, b1);
          arrayOfObject[b1] = this.fromNative[b1].fromNative(object1, callbackParameterContext);
        } else {
          arrayOfObject[b1] = convertArgument(object1, clazz);
        } 
      } 
      Object object = null;
      Callback callback = getCallback();
      if (callback != null)
        try {
          object = convertResult(this.callbackMethod.invoke(callback, arrayOfObject));
        } catch (IllegalArgumentException|IllegalAccessException illegalArgumentException) {
          Native.getCallbackExceptionHandler().uncaughtException(callback, illegalArgumentException);
        } catch (InvocationTargetException invocationTargetException) {
          Native.getCallbackExceptionHandler().uncaughtException(callback, invocationTargetException.getTargetException());
        }  
      for (byte b2 = 0; b2 < arrayOfObject.length; b2++) {
        if (arrayOfObject[b2] instanceof Structure && !(arrayOfObject[b2] instanceof Structure.ByValue))
          ((Structure)arrayOfObject[b2]).autoWrite(); 
      } 
      return object;
    }
    
    public Object callback(Object[] param1ArrayOfObject) {
      try {
        return invokeCallback(param1ArrayOfObject);
      } catch (Throwable throwable) {
        Native.getCallbackExceptionHandler().uncaughtException(getCallback(), throwable);
        return null;
      } 
    }
    
    private Object convertArgument(Object param1Object, Class<?> param1Class) {
      if (param1Object instanceof Pointer) {
        if (param1Class == String.class) {
          param1Object = ((Pointer)param1Object).getString(0L, this.encoding);
        } else if (param1Class == WString.class) {
          param1Object = new WString(((Pointer)param1Object).getWideString(0L));
        } else if (param1Class == String[].class) {
          param1Object = ((Pointer)param1Object).getStringArray(0L, this.encoding);
        } else if (param1Class == WString[].class) {
          param1Object = ((Pointer)param1Object).getWideStringArray(0L);
        } else if (Callback.class.isAssignableFrom(param1Class)) {
          param1Object = CallbackReference.getCallback(param1Class, (Pointer)param1Object);
        } else if (Structure.class.isAssignableFrom(param1Class)) {
          if (Structure.ByValue.class.isAssignableFrom(param1Class)) {
            Structure structure = (Structure)Structure.newInstance((Class)param1Class);
            byte[] arrayOfByte = new byte[structure.size()];
            ((Pointer)param1Object).read(0L, arrayOfByte, 0, arrayOfByte.length);
            structure.getPointer().write(0L, arrayOfByte, 0, arrayOfByte.length);
            structure.read();
            param1Object = structure;
          } else {
            Structure structure = (Structure)Structure.newInstance((Class)param1Class, (Pointer)param1Object);
            structure.conditionalAutoRead();
            param1Object = structure;
          } 
        } 
      } else if ((boolean.class == param1Class || Boolean.class == param1Class) && param1Object instanceof Number) {
        param1Object = Function.valueOf((((Number)param1Object).intValue() != 0));
      } 
      return param1Object;
    }
    
    private Object convertResult(Object param1Object) {
      if (this.toNative != null)
        param1Object = this.toNative.toNative(param1Object, new CallbackResultContext(this.callbackMethod)); 
      if (param1Object == null)
        return null; 
      Class<?> clazz = param1Object.getClass();
      if (Structure.class.isAssignableFrom(clazz))
        return Structure.ByValue.class.isAssignableFrom(clazz) ? param1Object : ((Structure)param1Object).getPointer(); 
      if (clazz == boolean.class || clazz == Boolean.class)
        return Boolean.TRUE.equals(param1Object) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE; 
      if (clazz == String.class || clazz == WString.class)
        return CallbackReference.getNativeString(param1Object, (clazz == WString.class)); 
      if (clazz == String[].class || clazz == WString[].class) {
        StringArray stringArray = (clazz == String[].class) ? new StringArray((String[])param1Object, this.encoding) : new StringArray((WString[])param1Object);
        CallbackReference.allocations.put(param1Object, stringArray);
        return stringArray;
      } 
      return Callback.class.isAssignableFrom(clazz) ? CallbackReference.getFunctionPointer((Callback)param1Object) : param1Object;
    }
    
    public Class<?>[] getParameterTypes() {
      return this.callbackMethod.getParameterTypes();
    }
    
    public Class<?> getReturnType() {
      return this.callbackMethod.getReturnType();
    }
  }
  
  static class AttachOptions extends Structure {
    public static final List<String> FIELDS = createFieldsOrder(new String[] { "daemon", "detach", "name" });
    
    public boolean daemon;
    
    public boolean detach;
    
    public String name;
    
    AttachOptions() {
      setStringEncoding("utf8");
    }
    
    protected List<String> getFieldOrder() {
      return FIELDS;
    }
  }
  
  private static class NativeFunctionHandler implements InvocationHandler {
    private final Function function;
    
    private final Map<String, ?> options;
    
    public NativeFunctionHandler(Pointer param1Pointer, int param1Int, Map<String, ?> param1Map) {
      this.options = param1Map;
      this.function = new Function(param1Pointer, param1Int, (String)param1Map.get("string-encoding"));
    }
    
    public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
      if (Library.Handler.OBJECT_TOSTRING.equals(param1Method)) {
        null = "Proxy interface to " + this.function;
        Method method = (Method)this.options.get("invoking-method");
        Class<?> clazz = CallbackReference.findCallbackClass(method.getDeclaringClass());
        return null + " (" + clazz.getName() + ")";
      } 
      if (Library.Handler.OBJECT_HASHCODE.equals(param1Method))
        return Integer.valueOf(hashCode()); 
      if (Library.Handler.OBJECT_EQUALS.equals(param1Method)) {
        Object object = param1ArrayOfObject[0];
        return (object != null && Proxy.isProxyClass(object.getClass())) ? Function.valueOf((Proxy.getInvocationHandler(object) == this)) : Boolean.FALSE;
      } 
      if (Function.isVarArgs(param1Method))
        param1ArrayOfObject = Function.concatenateVarArgs(param1ArrayOfObject); 
      return this.function.invoke(param1Method.getReturnType(), param1ArrayOfObject, this.options);
    }
    
    public Pointer getPointer() {
      return this.function;
    }
  }
  
  private static final class CallbackReferenceDisposer implements Runnable {
    private Pointer cbstruct;
    
    public CallbackReferenceDisposer(Pointer param1Pointer) {
      this.cbstruct = param1Pointer;
    }
    
    public synchronized void run() {
      if (this.cbstruct != null)
        try {
          Native.freeNativeCallback(this.cbstruct.peer);
        } finally {
          CallbackReference.allocatedMemory.remove(Long.valueOf(this.cbstruct.peer));
          this.cbstruct.peer = 0L;
          this.cbstruct = null;
        }  
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\CallbackReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */