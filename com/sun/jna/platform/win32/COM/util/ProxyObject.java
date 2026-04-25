package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.internal.ReflectionUtils;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ConnectionPoint;
import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.IUnknownCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyObject implements InvocationHandler, IDispatch, IRawDispatchHandle, IConnectionPoint {
  private long unknownId = -1L;
  
  private final Class<?> theInterface;
  
  private final ObjectFactory factory;
  
  private final IDispatch rawDispatch;
  
  public ProxyObject(Class<?> paramClass, IDispatch paramIDispatch, ObjectFactory paramObjectFactory) {
    this.rawDispatch = paramIDispatch;
    this.theInterface = paramClass;
    this.factory = paramObjectFactory;
    int i = this.rawDispatch.AddRef();
    getUnknownId();
    paramObjectFactory.register(this);
  }
  
  private long getUnknownId() {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    if (-1L == this.unknownId)
      try {
        PointerByReference pointerByReference = new PointerByReference();
        Thread thread = Thread.currentThread();
        String str = thread.getName();
        Guid.IID iID = IUnknown.IID_IUNKNOWN;
        WinNT.HRESULT hRESULT = getRawDispatch().QueryInterface(new Guid.REFIID(iID), pointerByReference);
        if (WinNT.S_OK.equals(hRESULT)) {
          Dispatch dispatch = new Dispatch(pointerByReference.getValue());
          this.unknownId = Pointer.nativeValue(dispatch.getPointer());
          int i = dispatch.Release();
        } else {
          String str1 = Kernel32Util.formatMessage(hRESULT);
          throw new COMException("getUnknownId: " + str1, hRESULT);
        } 
      } catch (RuntimeException runtimeException) {
        if (runtimeException instanceof COMException)
          throw runtimeException; 
        throw new COMException("Error occured when trying get Unknown Id ", runtimeException);
      }  
    return this.unknownId;
  }
  
  protected void finalize() throws Throwable {
    dispose();
    super.finalize();
  }
  
  public synchronized void dispose() {
    if (((Dispatch)this.rawDispatch).getPointer() != Pointer.NULL) {
      this.rawDispatch.Release();
      ((Dispatch)this.rawDispatch).setPointer(Pointer.NULL);
      this.factory.unregister(this);
    } 
  }
  
  public IDispatch getRawDispatch() {
    return this.rawDispatch;
  }
  
  public boolean equals(Object paramObject) {
    if (null == paramObject)
      return false; 
    if (paramObject instanceof ProxyObject) {
      ProxyObject proxyObject = (ProxyObject)paramObject;
      return (getUnknownId() == proxyObject.getUnknownId());
    } 
    if (Proxy.isProxyClass(paramObject.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      if (invocationHandler instanceof ProxyObject)
        try {
          ProxyObject proxyObject = (ProxyObject)invocationHandler;
          return (getUnknownId() == proxyObject.getUnknownId());
        } catch (Exception exception) {
          return false;
        }  
      return false;
    } 
    return false;
  }
  
  public int hashCode() {
    long l = getUnknownId();
    return (int)((l >>> 32L) + (l & 0xFFFFFFFFL));
  }
  
  public String toString() {
    return this.theInterface.getName() + "{unk=" + hashCode() + "}";
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    boolean bool = (paramMethod.getAnnotation(ComMethod.class) != null || paramMethod.getAnnotation(ComProperty.class) != null) ? true : false;
    if (!bool && (paramMethod.getDeclaringClass().equals(Object.class) || paramMethod.getDeclaringClass().equals(IRawDispatchHandle.class) || paramMethod.getDeclaringClass().equals(IUnknown.class) || paramMethod.getDeclaringClass().equals(IDispatch.class) || paramMethod.getDeclaringClass().equals(IConnectionPoint.class)))
      try {
        return paramMethod.invoke(this, paramArrayOfObject);
      } catch (InvocationTargetException invocationTargetException) {
        throw invocationTargetException.getCause();
      }  
    if (!bool && ReflectionUtils.isDefault(paramMethod)) {
      Object object = ReflectionUtils.getMethodHandle(paramMethod);
      return ReflectionUtils.invokeDefaultMethod(paramObject, object, paramArrayOfObject);
    } 
    Class<?> clazz = paramMethod.getReturnType();
    boolean bool1 = void.class.equals(clazz);
    ComProperty comProperty = paramMethod.<ComProperty>getAnnotation(ComProperty.class);
    if (null != comProperty) {
      int i = comProperty.dispId();
      Object[] arrayOfObject = unfoldWhenVarargs(paramMethod, paramArrayOfObject);
      if (bool1) {
        if (i != -1) {
          setProperty(new OaIdl.DISPID(i), arrayOfObject);
          return null;
        } 
        String str1 = getMutatorName(paramMethod, comProperty);
        setProperty(str1, arrayOfObject);
        return null;
      } 
      if (i != -1)
        return getProperty(clazz, new OaIdl.DISPID(i), paramArrayOfObject); 
      String str = getAccessorName(paramMethod, comProperty);
      return getProperty(clazz, str, paramArrayOfObject);
    } 
    ComMethod comMethod = paramMethod.<ComMethod>getAnnotation(ComMethod.class);
    if (null != comMethod) {
      Object[] arrayOfObject = unfoldWhenVarargs(paramMethod, paramArrayOfObject);
      int i = comMethod.dispId();
      if (i != -1)
        return invokeMethod(clazz, new OaIdl.DISPID(i), arrayOfObject); 
      String str = getMethodName(paramMethod, comMethod);
      return invokeMethod(clazz, str, arrayOfObject);
    } 
    return null;
  }
  
  private ConnectionPoint fetchRawConnectionPoint(Guid.IID paramIID) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    IConnectionPointContainer iConnectionPointContainer = queryInterface(IConnectionPointContainer.class);
    Dispatch dispatch = (Dispatch)iConnectionPointContainer.getRawDispatch();
    ConnectionPointContainer connectionPointContainer = new ConnectionPointContainer(dispatch.getPointer());
    Guid.REFIID rEFIID = new Guid.REFIID(paramIID.getPointer());
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = connectionPointContainer.FindConnectionPoint(rEFIID, pointerByReference);
    COMUtils.checkRC(hRESULT);
    return new ConnectionPoint(pointerByReference.getValue());
  }
  
  public IComEventCallbackCookie advise(Class<?> paramClass, IComEventCallbackListener paramIComEventCallbackListener) throws COMException {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    try {
      ComInterface comInterface = paramClass.<ComInterface>getAnnotation(ComInterface.class);
      if (null == comInterface)
        throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation"); 
      Guid.IID iID = getIID(comInterface);
      ConnectionPoint connectionPoint = fetchRawConnectionPoint(iID);
      IDispatchCallback iDispatchCallback = this.factory.createDispatchCallback(paramClass, paramIComEventCallbackListener);
      paramIComEventCallbackListener.setDispatchCallbackListener(iDispatchCallback);
      WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
      WinNT.HRESULT hRESULT = connectionPoint.Advise((IUnknownCallback)iDispatchCallback, dWORDByReference);
      int i = connectionPoint.Release();
      COMUtils.checkRC(hRESULT);
      return new ComEventCallbackCookie(dWORDByReference.getValue());
    } catch (RuntimeException runtimeException) {
      if (runtimeException instanceof COMException)
        throw runtimeException; 
      throw new COMException("Error occured in advise when trying to connect the listener " + paramIComEventCallbackListener, runtimeException);
    } 
  }
  
  public void unadvise(Class<?> paramClass, IComEventCallbackCookie paramIComEventCallbackCookie) throws COMException {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    try {
      ComInterface comInterface = paramClass.<ComInterface>getAnnotation(ComInterface.class);
      if (null == comInterface)
        throw new COMException("unadvise: Interface must define a value for iid via the ComInterface annotation"); 
      Guid.IID iID = getIID(comInterface);
      ConnectionPoint connectionPoint = fetchRawConnectionPoint(iID);
      WinNT.HRESULT hRESULT = connectionPoint.Unadvise(((ComEventCallbackCookie)paramIComEventCallbackCookie).getValue());
      connectionPoint.Release();
      COMUtils.checkRC(hRESULT);
    } catch (RuntimeException runtimeException) {
      if (runtimeException instanceof COMException)
        throw runtimeException; 
      throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, runtimeException);
    } 
  }
  
  public <T> void setProperty(String paramString, T paramT) {
    OaIdl.DISPID dISPID = resolveDispId(getRawDispatch(), paramString);
    setProperty(dISPID, paramT);
  }
  
  public <T> void setProperty(OaIdl.DISPID paramDISPID, T paramT) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    Variant.VARIANT vARIANT = Convert.toVariant(paramT);
    WinNT.HRESULT hRESULT = oleMethod(4, (Variant.VARIANT.ByReference)null, getRawDispatch(), paramDISPID, vARIANT);
    Convert.free(vARIANT, paramT);
    COMUtils.checkRC(hRESULT);
  }
  
  private void setProperty(String paramString, Object... paramVarArgs) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    OaIdl.DISPID dISPID = resolveDispId(getRawDispatch(), paramString);
    setProperty(dISPID, paramVarArgs);
  }
  
  private void setProperty(OaIdl.DISPID paramDISPID, Object... paramVarArgs) {
    Variant.VARIANT[] arrayOfVARIANT;
    assert COMUtils.comIsInitialized() : "COM not initialized";
    if (null == paramVarArgs) {
      arrayOfVARIANT = new Variant.VARIANT[0];
    } else {
      arrayOfVARIANT = new Variant.VARIANT[paramVarArgs.length];
    } 
    for (byte b1 = 0; b1 < arrayOfVARIANT.length; b1++)
      arrayOfVARIANT[b1] = Convert.toVariant(paramVarArgs[b1]); 
    WinNT.HRESULT hRESULT = oleMethod(4, (Variant.VARIANT.ByReference)null, getRawDispatch(), paramDISPID, arrayOfVARIANT);
    for (byte b2 = 0; b2 < arrayOfVARIANT.length; b2++)
      Convert.free(arrayOfVARIANT[b2], paramVarArgs[b2]); 
    COMUtils.checkRC(hRESULT);
  }
  
  public <T> T getProperty(Class<T> paramClass, String paramString, Object... paramVarArgs) {
    OaIdl.DISPID dISPID = resolveDispId(getRawDispatch(), paramString);
    return getProperty(paramClass, dISPID, paramVarArgs);
  }
  
  public <T> T getProperty(Class<T> paramClass, OaIdl.DISPID paramDISPID, Object... paramVarArgs) {
    Variant.VARIANT[] arrayOfVARIANT;
    if (null == paramVarArgs) {
      arrayOfVARIANT = new Variant.VARIANT[0];
    } else {
      arrayOfVARIANT = new Variant.VARIANT[paramVarArgs.length];
    } 
    for (byte b1 = 0; b1 < arrayOfVARIANT.length; b1++)
      arrayOfVARIANT[b1] = Convert.toVariant(paramVarArgs[b1]); 
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    WinNT.HRESULT hRESULT = oleMethod(2, byReference, getRawDispatch(), paramDISPID, arrayOfVARIANT);
    for (byte b2 = 0; b2 < arrayOfVARIANT.length; b2++)
      Convert.free(arrayOfVARIANT[b2], paramVarArgs[b2]); 
    COMUtils.checkRC(hRESULT);
    return (T)Convert.toJavaObject((Variant.VARIANT)byReference, paramClass, this.factory, false, true);
  }
  
  public <T> T invokeMethod(Class<T> paramClass, String paramString, Object... paramVarArgs) {
    OaIdl.DISPID dISPID = resolveDispId(getRawDispatch(), paramString);
    return invokeMethod(paramClass, dISPID, paramVarArgs);
  }
  
  public <T> T invokeMethod(Class<T> paramClass, OaIdl.DISPID paramDISPID, Object... paramVarArgs) {
    Variant.VARIANT[] arrayOfVARIANT;
    assert COMUtils.comIsInitialized() : "COM not initialized";
    if (null == paramVarArgs) {
      arrayOfVARIANT = new Variant.VARIANT[0];
    } else {
      arrayOfVARIANT = new Variant.VARIANT[paramVarArgs.length];
    } 
    for (byte b1 = 0; b1 < arrayOfVARIANT.length; b1++)
      arrayOfVARIANT[b1] = Convert.toVariant(paramVarArgs[b1]); 
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    WinNT.HRESULT hRESULT = oleMethod(1, byReference, getRawDispatch(), paramDISPID, arrayOfVARIANT);
    for (byte b2 = 0; b2 < arrayOfVARIANT.length; b2++)
      Convert.free(arrayOfVARIANT[b2], paramVarArgs[b2]); 
    COMUtils.checkRC(hRESULT);
    return (T)Convert.toJavaObject((Variant.VARIANT)byReference, paramClass, this.factory, false, true);
  }
  
  private Object[] unfoldWhenVarargs(Method paramMethod, Object[] paramArrayOfObject) {
    if (null == paramArrayOfObject)
      return null; 
    if (paramArrayOfObject.length == 0 || !paramMethod.isVarArgs() || !(paramArrayOfObject[paramArrayOfObject.length - 1] instanceof Object[]))
      return paramArrayOfObject; 
    Object[] arrayOfObject1 = (Object[])paramArrayOfObject[paramArrayOfObject.length - 1];
    Object[] arrayOfObject2 = new Object[paramArrayOfObject.length - 1 + arrayOfObject1.length];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject2, 0, paramArrayOfObject.length - 1);
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, paramArrayOfObject.length - 1, arrayOfObject1.length);
    return arrayOfObject2;
  }
  
  public <T> T queryInterface(Class<T> paramClass) throws COMException {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    try {
      ComInterface comInterface = paramClass.<ComInterface>getAnnotation(ComInterface.class);
      if (null == comInterface)
        throw new COMException("queryInterface: Interface must define a value for iid via the ComInterface annotation"); 
      Guid.IID iID = getIID(comInterface);
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = getRawDispatch().QueryInterface(new Guid.REFIID(iID), pointerByReference);
      if (WinNT.S_OK.equals(hRESULT)) {
        Dispatch dispatch = new Dispatch(pointerByReference.getValue());
        T t = (T)this.factory.createProxy((Class)paramClass, (IDispatch)dispatch);
        int i = dispatch.Release();
        return t;
      } 
      String str = Kernel32Util.formatMessage(hRESULT);
      throw new COMException("queryInterface: " + str, hRESULT);
    } catch (RuntimeException runtimeException) {
      if (runtimeException instanceof COMException)
        throw runtimeException; 
      throw new COMException("Error occured when trying to query for interface " + paramClass.getName(), runtimeException);
    } 
  }
  
  private Guid.IID getIID(ComInterface paramComInterface) {
    String str = paramComInterface.iid();
    if (null != str && !str.isEmpty())
      return new Guid.IID(str); 
    throw new COMException("ComInterface must define a value for iid");
  }
  
  private String getAccessorName(Method paramMethod, ComProperty paramComProperty) {
    if (paramComProperty.name().isEmpty()) {
      String str = paramMethod.getName();
      if (str.startsWith("get"))
        return str.replaceFirst("get", ""); 
      throw new RuntimeException("Property Accessor name must start with 'get', or set the anotation 'name' value");
    } 
    return paramComProperty.name();
  }
  
  private String getMutatorName(Method paramMethod, ComProperty paramComProperty) {
    if (paramComProperty.name().isEmpty()) {
      String str = paramMethod.getName();
      if (str.startsWith("set"))
        return str.replaceFirst("set", ""); 
      throw new RuntimeException("Property Mutator name must start with 'set', or set the anotation 'name' value");
    } 
    return paramComProperty.name();
  }
  
  private String getMethodName(Method paramMethod, ComMethod paramComMethod) {
    return paramComMethod.name().isEmpty() ? paramMethod.getName() : paramComMethod.name();
  }
  
  protected OaIdl.DISPID resolveDispId(String paramString) {
    return resolveDispId(getRawDispatch(), paramString);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramString, new Variant.VARIANT[] { paramVARIANT });
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramDISPID, new Variant.VARIANT[] { paramVARIANT });
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString) throws COMException {
    return oleMethod(paramInt, paramByReference, paramString, (Variant.VARIANT[])null);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID) throws COMException {
    return oleMethod(paramInt, paramByReference, paramDISPID, (Variant.VARIANT[])null);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, String paramString, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, resolveDispId(paramString), paramArrayOfVARIANT);
  }
  
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, OaIdl.DISPID paramDISPID, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, getRawDispatch(), paramDISPID, paramArrayOfVARIANT);
  }
  
  @Deprecated
  protected OaIdl.DISPID resolveDispId(IDispatch paramIDispatch, String paramString) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    if (paramIDispatch == null)
      throw new COMException("pDisp (IDispatch) parameter is null!"); 
    WString[] arrayOfWString = { new WString(paramString) };
    OaIdl.DISPIDByReference dISPIDByReference = new OaIdl.DISPIDByReference();
    WinNT.HRESULT hRESULT = paramIDispatch.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), arrayOfWString, 1, this.factory.getLCID(), dISPIDByReference);
    COMUtils.checkRC(hRESULT);
    return dISPIDByReference.getValue();
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramString, new Variant.VARIANT[] { paramVARIANT });
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID, Variant.VARIANT paramVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramDISPID, new Variant.VARIANT[] { paramVARIANT });
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramString, (Variant.VARIANT[])null);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, paramDISPID, (Variant.VARIANT[])null);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, String paramString, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    return oleMethod(paramInt, paramByReference, paramIDispatch, resolveDispId(paramIDispatch, paramString), paramArrayOfVARIANT);
  }
  
  @Deprecated
  protected WinNT.HRESULT oleMethod(int paramInt, Variant.VARIANT.ByReference paramByReference, IDispatch paramIDispatch, OaIdl.DISPID paramDISPID, Variant.VARIANT[] paramArrayOfVARIANT) throws COMException {
    int j;
    assert COMUtils.comIsInitialized() : "COM not initialized";
    if (paramIDispatch == null)
      throw new COMException("pDisp (IDispatch) parameter is null!"); 
    int i = 0;
    Variant.VARIANT[] arrayOfVARIANT = null;
    OleAuto.DISPPARAMS.ByReference byReference = new OleAuto.DISPPARAMS.ByReference();
    OaIdl.EXCEPINFO.ByReference byReference1 = new OaIdl.EXCEPINFO.ByReference();
    IntByReference intByReference = new IntByReference();
    if (paramArrayOfVARIANT != null && paramArrayOfVARIANT.length > 0) {
      i = paramArrayOfVARIANT.length;
      arrayOfVARIANT = new Variant.VARIANT[i];
      j = i;
      for (byte b = 0; b < i; b++)
        arrayOfVARIANT[b] = paramArrayOfVARIANT[--j]; 
    } 
    if (paramInt == 4)
      byReference.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT }); 
    if (paramInt == 1 || paramInt == 2) {
      j = 3;
    } else {
      j = paramInt;
    } 
    if (i > 0) {
      byReference.setArgs(arrayOfVARIANT);
      byReference.write();
    } 
    WinNT.HRESULT hRESULT = paramIDispatch.Invoke(paramDISPID, new Guid.REFIID(Guid.IID_NULL), this.factory.getLCID(), new WinDef.WORD(j), byReference, paramByReference, byReference1, intByReference);
    COMUtils.checkRC(hRESULT, (OaIdl.EXCEPINFO)byReference1, intByReference);
    return hRESULT;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\ProxyObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */