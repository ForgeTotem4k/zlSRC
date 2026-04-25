package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.DispatchListener;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallbackProxy implements IDispatchCallback {
  private static boolean DEFAULT_BOOLEAN;
  
  private static byte DEFAULT_BYTE;
  
  private static short DEFAULT_SHORT;
  
  private static int DEFAULT_INT;
  
  private static long DEFAULT_LONG;
  
  private static float DEFAULT_FLOAT;
  
  private static double DEFAULT_DOUBLE;
  
  ObjectFactory factory;
  
  Class<?> comEventCallbackInterface;
  
  IComEventCallbackListener comEventCallbackListener;
  
  Guid.REFIID listenedToRiid;
  
  public DispatchListener dispatchListener;
  
  Map<OaIdl.DISPID, Method> dsipIdMap;
  
  public CallbackProxy(ObjectFactory paramObjectFactory, Class<?> paramClass, IComEventCallbackListener paramIComEventCallbackListener) {
    this.factory = paramObjectFactory;
    this.comEventCallbackInterface = paramClass;
    this.comEventCallbackListener = paramIComEventCallbackListener;
    this.listenedToRiid = createRIID(paramClass);
    this.dsipIdMap = createDispIdMap(paramClass);
    this.dispatchListener = new DispatchListener(this);
  }
  
  private Guid.REFIID createRIID(Class<?> paramClass) {
    ComInterface comInterface = paramClass.<ComInterface>getAnnotation(ComInterface.class);
    if (null == comInterface)
      throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation"); 
    String str = comInterface.iid();
    if (null == str || str.isEmpty())
      throw new COMException("ComInterface must define a value for iid"); 
    return new Guid.REFIID((new Guid.IID(str)).getPointer());
  }
  
  private Map<OaIdl.DISPID, Method> createDispIdMap(Class<?> paramClass) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Method method : paramClass.getMethods()) {
      ComEventCallback comEventCallback = method.<ComEventCallback>getAnnotation(ComEventCallback.class);
      ComMethod comMethod = method.<ComMethod>getAnnotation(ComMethod.class);
      if (comMethod != null) {
        int i = comMethod.dispId();
        if (-1 == i)
          i = fetchDispIdFromName(comEventCallback); 
        if (i == -1)
          this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + method.getName() + " not found", null); 
        hashMap.put(new OaIdl.DISPID(i), method);
      } else if (null != comEventCallback) {
        int i = comEventCallback.dispid();
        if (-1 == i)
          i = fetchDispIdFromName(comEventCallback); 
        if (i == -1)
          this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + method.getName() + " not found", null); 
        hashMap.put(new OaIdl.DISPID(i), method);
      } 
    } 
    return (Map)hashMap;
  }
  
  private int fetchDispIdFromName(ComEventCallback paramComEventCallback) {
    return -1;
  }
  
  void invokeOnThread(OaIdl.DISPID paramDISPID, Guid.REFIID paramREFIID, WinDef.LCID paramLCID, WinDef.WORD paramWORD, OleAuto.DISPPARAMS.ByReference paramByReference) {
    Variant.VARIANT[] arrayOfVARIANT = paramByReference.getArgs();
    Method method = this.dsipIdMap.get(paramDISPID);
    if (method == null) {
      this.comEventCallbackListener.errorReceivingCallbackEvent("No method found with dispId = " + paramDISPID, null);
      return;
    } 
    OaIdl.DISPID[] arrayOfDISPID = paramByReference.getRgdispidNamedArgs();
    Class[] arrayOfClass = method.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    byte b;
    for (b = 0; b < arrayOfObject.length && arrayOfVARIANT.length - arrayOfDISPID.length - b > 0; b++) {
      Class<?> clazz = arrayOfClass[b];
      Variant.VARIANT vARIANT = arrayOfVARIANT[arrayOfVARIANT.length - b - 1];
      arrayOfObject[b] = Convert.toJavaObject(vARIANT, clazz, this.factory, true, false);
    } 
    for (b = 0; b < arrayOfDISPID.length; b++) {
      int i = arrayOfDISPID[b].intValue();
      if (i < arrayOfObject.length) {
        Class<?> clazz = arrayOfClass[i];
        Variant.VARIANT vARIANT = arrayOfVARIANT[b];
        arrayOfObject[i] = Convert.toJavaObject(vARIANT, clazz, this.factory, true, false);
      } 
    } 
    for (b = 0; b < arrayOfObject.length; b++) {
      if (arrayOfObject[b] == null && arrayOfClass[b].isPrimitive())
        if (arrayOfClass[b].equals(boolean.class)) {
          arrayOfObject[b] = Boolean.valueOf(DEFAULT_BOOLEAN);
        } else if (arrayOfClass[b].equals(byte.class)) {
          arrayOfObject[b] = Byte.valueOf(DEFAULT_BYTE);
        } else if (arrayOfClass[b].equals(short.class)) {
          arrayOfObject[b] = Short.valueOf(DEFAULT_SHORT);
        } else if (arrayOfClass[b].equals(int.class)) {
          arrayOfObject[b] = Integer.valueOf(DEFAULT_INT);
        } else if (arrayOfClass[b].equals(long.class)) {
          arrayOfObject[b] = Long.valueOf(DEFAULT_LONG);
        } else if (arrayOfClass[b].equals(float.class)) {
          arrayOfObject[b] = Float.valueOf(DEFAULT_FLOAT);
        } else if (arrayOfClass[b].equals(double.class)) {
          arrayOfObject[b] = Double.valueOf(DEFAULT_DOUBLE);
        } else {
          throw new IllegalArgumentException("Class type " + arrayOfClass[b].getName() + " not mapped to primitive default value.");
        }  
    } 
    try {
      method.invoke(this.comEventCallbackListener, arrayOfObject);
    } catch (Exception exception) {
      ArrayList<String> arrayList = new ArrayList(arrayOfObject.length);
      for (Object object : arrayOfObject) {
        if (object == null) {
          arrayList.add("NULL");
        } else {
          arrayList.add(object.getClass().getName());
        } 
      } 
      this.comEventCallbackListener.errorReceivingCallbackEvent("Exception invoking method " + method + " supplied: " + arrayList.toString(), exception);
    } 
  }
  
  public Pointer getPointer() {
    return this.dispatchListener.getPointer();
  }
  
  public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference paramUINTByReference) {
    return new WinNT.HRESULT(-2147467263);
  }
  
  public WinNT.HRESULT GetTypeInfo(WinDef.UINT paramUINT, WinDef.LCID paramLCID, PointerByReference paramPointerByReference) {
    return new WinNT.HRESULT(-2147467263);
  }
  
  public WinNT.HRESULT GetIDsOfNames(Guid.REFIID paramREFIID, WString[] paramArrayOfWString, int paramInt, WinDef.LCID paramLCID, OaIdl.DISPIDByReference paramDISPIDByReference) {
    return new WinNT.HRESULT(-2147467263);
  }
  
  public WinNT.HRESULT Invoke(OaIdl.DISPID paramDISPID, Guid.REFIID paramREFIID, WinDef.LCID paramLCID, WinDef.WORD paramWORD, OleAuto.DISPPARAMS.ByReference paramByReference, Variant.VARIANT.ByReference paramByReference1, OaIdl.EXCEPINFO.ByReference paramByReference2, IntByReference paramIntByReference) {
    assert COMUtils.comIsInitialized() : "Assumption about COM threading broken.";
    invokeOnThread(paramDISPID, paramREFIID, paramLCID, paramWORD, paramByReference);
    return WinError.S_OK;
  }
  
  public WinNT.HRESULT QueryInterface(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference) {
    if (null == paramPointerByReference)
      return new WinNT.HRESULT(-2147467261); 
    if (paramREFIID.equals(this.listenedToRiid)) {
      paramPointerByReference.setValue(getPointer());
      return WinError.S_OK;
    } 
    if (paramREFIID.getValue().equals(Unknown.IID_IUNKNOWN)) {
      paramPointerByReference.setValue(getPointer());
      return WinError.S_OK;
    } 
    if (paramREFIID.getValue().equals(Dispatch.IID_IDISPATCH)) {
      paramPointerByReference.setValue(getPointer());
      return WinError.S_OK;
    } 
    return new WinNT.HRESULT(-2147467262);
  }
  
  public int AddRef() {
    return 0;
  }
  
  public int Release() {
    return 0;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\CallbackProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */