package com.sun.jna;

import com.sun.jna.internal.ReflectionUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public interface Library {
  public static final String OPTION_TYPE_MAPPER = "type-mapper";
  
  public static final String OPTION_FUNCTION_MAPPER = "function-mapper";
  
  public static final String OPTION_INVOCATION_MAPPER = "invocation-mapper";
  
  public static final String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
  
  public static final String OPTION_STRING_ENCODING = "string-encoding";
  
  public static final String OPTION_ALLOW_OBJECTS = "allow-objects";
  
  public static final String OPTION_CALLING_CONVENTION = "calling-convention";
  
  public static final String OPTION_OPEN_FLAGS = "open-flags";
  
  public static final String OPTION_CLASSLOADER = "classloader";
  
  public static final String OPTION_SYMBOL_PROVIDER = "symbol-provider";
  
  public static class Handler implements InvocationHandler {
    static final Method OBJECT_TOSTRING;
    
    static final Method OBJECT_HASHCODE;
    
    static final Method OBJECT_EQUALS;
    
    private final NativeLibrary nativeLibrary;
    
    private final Class<?> interfaceClass;
    
    private final Map<String, Object> options;
    
    private final InvocationMapper invocationMapper;
    
    private final Map<Method, FunctionInfo> functions = new WeakHashMap<>();
    
    public Handler(String param1String, Class<?> param1Class, Map<String, ?> param1Map) {
      if (param1String != null && "".equals(param1String.trim()))
        throw new IllegalArgumentException("Invalid library name \"" + param1String + "\""); 
      if (!param1Class.isInterface())
        throw new IllegalArgumentException(param1String + " does not implement an interface: " + param1Class.getName()); 
      this.interfaceClass = param1Class;
      this.options = new HashMap<>(param1Map);
      boolean bool = AltCallingConvention.class.isAssignableFrom(param1Class) ? true : false;
      if (this.options.get("calling-convention") == null)
        this.options.put("calling-convention", Integer.valueOf(bool)); 
      if (this.options.get("classloader") == null)
        this.options.put("classloader", param1Class.getClassLoader()); 
      this.nativeLibrary = NativeLibrary.getInstance(param1String, this.options);
      this.invocationMapper = (InvocationMapper)this.options.get("invocation-mapper");
    }
    
    public NativeLibrary getNativeLibrary() {
      return this.nativeLibrary;
    }
    
    public String getLibraryName() {
      return this.nativeLibrary.getName();
    }
    
    public Class<?> getInterfaceClass() {
      return this.interfaceClass;
    }
    
    public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
      if (OBJECT_TOSTRING.equals(param1Method))
        return "Proxy interface to " + this.nativeLibrary; 
      if (OBJECT_HASHCODE.equals(param1Method))
        return Integer.valueOf(hashCode()); 
      if (OBJECT_EQUALS.equals(param1Method)) {
        Object object = param1ArrayOfObject[0];
        return (object != null && Proxy.isProxyClass(object.getClass())) ? Function.valueOf((Proxy.getInvocationHandler(object) == this)) : Boolean.FALSE;
      } 
      FunctionInfo functionInfo = this.functions.get(param1Method);
      if (functionInfo == null)
        synchronized (this.functions) {
          functionInfo = this.functions.get(param1Method);
          if (functionInfo == null) {
            boolean bool = ReflectionUtils.isDefault(param1Method);
            if (!bool) {
              boolean bool1 = Function.isVarArgs(param1Method);
              InvocationHandler invocationHandler = null;
              if (this.invocationMapper != null)
                invocationHandler = this.invocationMapper.getInvocationHandler(this.nativeLibrary, param1Method); 
              Function function = null;
              Class[] arrayOfClass = null;
              HashMap<String, Object> hashMap = null;
              if (invocationHandler == null) {
                function = this.nativeLibrary.getFunction(param1Method.getName(), param1Method);
                arrayOfClass = param1Method.getParameterTypes();
                hashMap = new HashMap<>(this.options);
                hashMap.put("invoking-method", param1Method);
              } 
              functionInfo = new FunctionInfo(invocationHandler, function, arrayOfClass, bool1, hashMap);
            } else {
              functionInfo = new FunctionInfo(ReflectionUtils.getMethodHandle(param1Method));
            } 
            this.functions.put(param1Method, functionInfo);
          } 
        }  
      if (functionInfo.methodHandle != null)
        return ReflectionUtils.invokeDefaultMethod(param1Object, functionInfo.methodHandle, param1ArrayOfObject); 
      if (functionInfo.isVarArgs)
        param1ArrayOfObject = Function.concatenateVarArgs(param1ArrayOfObject); 
      return (functionInfo.handler != null) ? functionInfo.handler.invoke(param1Object, param1Method, param1ArrayOfObject) : functionInfo.function.invoke(param1Method, functionInfo.parameterTypes, param1Method.getReturnType(), param1ArrayOfObject, functionInfo.options);
    }
    
    static {
      try {
        OBJECT_TOSTRING = Object.class.getMethod("toString", new Class[0]);
        OBJECT_HASHCODE = Object.class.getMethod("hashCode", new Class[0]);
        OBJECT_EQUALS = Object.class.getMethod("equals", new Class[] { Object.class });
      } catch (Exception exception) {
        throw new Error("Error retrieving Object.toString() method");
      } 
    }
    
    private static final class FunctionInfo {
      final InvocationHandler handler = null;
      
      final Function function = null;
      
      final boolean isVarArgs = false;
      
      final Object methodHandle;
      
      final Map<String, ?> options = null;
      
      final Class<?>[] parameterTypes = null;
      
      FunctionInfo(Object param2Object) {
        this.methodHandle = param2Object;
      }
      
      FunctionInfo(InvocationHandler param2InvocationHandler, Function param2Function, Class<?>[] param2ArrayOfClass, boolean param2Boolean, Map<String, ?> param2Map) {
        this.methodHandle = null;
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Library.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */