package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

class Convert {
  public static Variant.VARIANT toVariant(Object paramObject) {
    if (paramObject instanceof Variant.VARIANT)
      return (Variant.VARIANT)paramObject; 
    if (paramObject instanceof Byte)
      return new Variant.VARIANT(((Byte)paramObject).byteValue()); 
    if (paramObject instanceof Character)
      return new Variant.VARIANT(((Character)paramObject).charValue()); 
    if (paramObject instanceof Short)
      return new Variant.VARIANT(((Short)paramObject).shortValue()); 
    if (paramObject instanceof Integer)
      return new Variant.VARIANT(((Integer)paramObject).intValue()); 
    if (paramObject instanceof Long)
      return new Variant.VARIANT(((Long)paramObject).longValue()); 
    if (paramObject instanceof Float)
      return new Variant.VARIANT(((Float)paramObject).floatValue()); 
    if (paramObject instanceof Double)
      return new Variant.VARIANT(((Double)paramObject).doubleValue()); 
    if (paramObject instanceof String)
      return new Variant.VARIANT((String)paramObject); 
    if (paramObject instanceof Boolean)
      return new Variant.VARIANT(((Boolean)paramObject).booleanValue()); 
    if (paramObject instanceof Dispatch)
      return new Variant.VARIANT((Dispatch)paramObject); 
    if (paramObject instanceof Date)
      return new Variant.VARIANT((Date)paramObject); 
    if (paramObject instanceof Proxy) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      ProxyObject proxyObject = (ProxyObject)invocationHandler;
      return new Variant.VARIANT(proxyObject.getRawDispatch());
    } 
    if (paramObject instanceof IComEnum) {
      IComEnum iComEnum = (IComEnum)paramObject;
      return new Variant.VARIANT(new WinDef.LONG(iComEnum.getValue()));
    } 
    Constructor<?> constructor = null;
    if (paramObject != null)
      for (Constructor<?> constructor1 : Variant.VARIANT.class.getConstructors()) {
        Class[] arrayOfClass = constructor1.getParameterTypes();
        if (arrayOfClass.length == 1 && arrayOfClass[0].isAssignableFrom(paramObject.getClass()))
          constructor = constructor1; 
      }  
    if (constructor != null)
      try {
        return (Variant.VARIANT)constructor.newInstance(new Object[] { paramObject });
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }  
    return null;
  }
  
  public static Object toJavaObject(Variant.VARIANT paramVARIANT, Class<?> paramClass, ObjectFactory paramObjectFactory, boolean paramBoolean1, boolean paramBoolean2) {
    Object object;
    int i = (paramVARIANT != null) ? paramVARIANT.getVarType().intValue() : 1;
    if (!i || i == 1)
      return null; 
    if (paramClass != null && !paramClass.isAssignableFrom(Object.class)) {
      if (paramClass.isAssignableFrom(paramVARIANT.getClass()))
        return paramVARIANT; 
      Object object1 = paramVARIANT.getValue();
      if (object1 != null && paramClass.isAssignableFrom(object1.getClass()))
        return object1; 
    } 
    Variant.VARIANT vARIANT = paramVARIANT;
    if (i == 16396) {
      paramVARIANT = (Variant.VARIANT)paramVARIANT.getValue();
      i = paramVARIANT.getVarType().intValue();
    } 
    if (paramClass == null || paramClass.isAssignableFrom(Object.class)) {
      paramClass = null;
      switch (i) {
        case 16:
        case 17:
          paramClass = Byte.class;
          break;
        case 2:
          paramClass = Short.class;
          break;
        case 18:
          paramClass = Character.class;
          break;
        case 3:
        case 19:
        case 22:
        case 23:
          paramClass = Integer.class;
          break;
        case 20:
        case 21:
          paramClass = Long.class;
          break;
        case 4:
          paramClass = Float.class;
          break;
        case 5:
          paramClass = Double.class;
          break;
        case 11:
          paramClass = Boolean.class;
          break;
        case 10:
          paramClass = WinDef.SCODE.class;
          break;
        case 6:
          paramClass = OaIdl.CURRENCY.class;
          break;
        case 7:
          paramClass = Date.class;
          break;
        case 8:
          paramClass = String.class;
          break;
        case 13:
          paramClass = IUnknown.class;
          break;
        case 9:
          paramClass = IDispatch.class;
          break;
        case 16396:
          paramClass = Variant.class;
          break;
        case 16384:
          paramClass = WinDef.PVOID.class;
          break;
        case 16398:
          paramClass = OaIdl.DECIMAL.class;
          break;
        default:
          if ((i & 0x2000) > 0)
            paramClass = OaIdl.SAFEARRAY.class; 
          break;
      } 
    } 
    if (Byte.class.equals(paramClass) || byte.class.equals(paramClass)) {
      object = Byte.valueOf(paramVARIANT.byteValue());
    } else if (Short.class.equals(paramClass) || short.class.equals(paramClass)) {
      object = Short.valueOf(paramVARIANT.shortValue());
    } else if (Character.class.equals(paramClass) || char.class.equals(paramClass)) {
      object = Character.valueOf((char)paramVARIANT.intValue());
    } else if (Integer.class.equals(paramClass) || int.class.equals(paramClass)) {
      object = Integer.valueOf(paramVARIANT.intValue());
    } else if (Long.class.equals(paramClass) || long.class.equals(paramClass) || IComEnum.class.isAssignableFrom(paramClass)) {
      object = Long.valueOf(paramVARIANT.longValue());
    } else if (Float.class.equals(paramClass) || float.class.equals(paramClass)) {
      object = Float.valueOf(paramVARIANT.floatValue());
    } else if (Double.class.equals(paramClass) || double.class.equals(paramClass)) {
      object = Double.valueOf(paramVARIANT.doubleValue());
    } else if (Boolean.class.equals(paramClass) || boolean.class.equals(paramClass)) {
      object = Boolean.valueOf(paramVARIANT.booleanValue());
    } else if (Date.class.equals(paramClass)) {
      object = paramVARIANT.dateValue();
    } else if (String.class.equals(paramClass)) {
      object = paramVARIANT.stringValue();
    } else {
      object = paramVARIANT.getValue();
      if (object instanceof Dispatch) {
        Dispatch dispatch = (Dispatch)object;
        if (paramClass != null && paramClass.isInterface()) {
          Object object1 = paramObjectFactory.createProxy((Class)paramClass, (IDispatch)dispatch);
          if (!paramBoolean1)
            int j = dispatch.Release(); 
          object = object1;
        } else {
          object = dispatch;
        } 
      } 
    } 
    if (IComEnum.class.isAssignableFrom(paramClass))
      object = paramClass.cast(toComEnum(paramClass, object)); 
    if (paramBoolean2)
      free(vARIANT, object); 
    return object;
  }
  
  public static <T extends IComEnum> T toComEnum(Class<T> paramClass, Object paramObject) {
    try {
      Method method = paramClass.getMethod("values", new Class[0]);
      IComEnum[] arrayOfIComEnum = (IComEnum[])method.invoke(null, new Object[0]);
      for (IComEnum iComEnum : arrayOfIComEnum) {
        if (paramObject.equals(Long.valueOf(iComEnum.getValue())))
          return (T)iComEnum; 
      } 
    } catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException noSuchMethodException) {}
    return null;
  }
  
  public static void free(Variant.VARIANT paramVARIANT, Class<?> paramClass) {
    if ((paramClass == null || !WTypes.BSTR.class.isAssignableFrom(paramClass)) && paramVARIANT != null && paramVARIANT.getVarType().intValue() == 8) {
      Object object = paramVARIANT.getValue();
      if (object instanceof WTypes.BSTR)
        OleAuto.INSTANCE.SysFreeString((WTypes.BSTR)object); 
    } 
  }
  
  public static void free(Variant.VARIANT paramVARIANT, Object paramObject) {
    free(paramVARIANT, (paramObject == null) ? null : paramObject.getClass());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\Convert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */