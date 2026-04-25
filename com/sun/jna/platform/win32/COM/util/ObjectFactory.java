package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.RunningObjectTable;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ObjectFactory {
  private final List<WeakReference<ProxyObject>> registeredObjects = new LinkedList<>();
  
  private static final WinDef.LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
  
  private WinDef.LCID LCID;
  
  protected void finalize() throws Throwable {
    try {
      disposeAll();
    } finally {
      super.finalize();
    } 
  }
  
  public IRunningObjectTable getRunningObjectTable() {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0L), pointerByReference);
    COMUtils.checkRC(hRESULT);
    RunningObjectTable runningObjectTable = new RunningObjectTable(pointerByReference.getValue());
    return new RunningObjectTable(runningObjectTable, this);
  }
  
  public <T> T createProxy(Class<T> paramClass, IDispatch paramIDispatch) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    ProxyObject proxyObject = new ProxyObject(paramClass, paramIDispatch, this);
    Object object = Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, proxyObject);
    return paramClass.cast(object);
  }
  
  public <T> T createObject(Class<T> paramClass) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    ComObject comObject = paramClass.<ComObject>getAnnotation(ComObject.class);
    if (null == comObject)
      throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation"); 
    Guid.GUID gUID = discoverClsId(comObject);
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoCreateInstance(gUID, null, 21, (Guid.GUID)IDispatch.IID_IDISPATCH, pointerByReference);
    COMUtils.checkRC(hRESULT);
    Dispatch dispatch = new Dispatch(pointerByReference.getValue());
    T t = (T)createProxy((Class)paramClass, (IDispatch)dispatch);
    int i = dispatch.Release();
    return t;
  }
  
  public <T> T fetchObject(Class<T> paramClass) throws COMException {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    ComObject comObject = paramClass.<ComObject>getAnnotation(ComObject.class);
    if (null == comObject)
      throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation"); 
    Guid.GUID gUID = discoverClsId(comObject);
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = OleAuto.INSTANCE.GetActiveObject(gUID, null, pointerByReference);
    COMUtils.checkRC(hRESULT);
    Dispatch dispatch = new Dispatch(pointerByReference.getValue());
    T t = (T)createProxy((Class)paramClass, (IDispatch)dispatch);
    dispatch.Release();
    return t;
  }
  
  Guid.GUID discoverClsId(ComObject paramComObject) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    String str1 = paramComObject.clsId();
    String str2 = paramComObject.progId();
    if (null != str1 && !str1.isEmpty())
      return (Guid.GUID)new Guid.CLSID(str1); 
    if (null != str2 && !str2.isEmpty()) {
      Guid.CLSID.ByReference byReference = new Guid.CLSID.ByReference();
      WinNT.HRESULT hRESULT = Ole32.INSTANCE.CLSIDFromProgID(str2, byReference);
      COMUtils.checkRC(hRESULT);
      return (Guid.GUID)byReference;
    } 
    throw new COMException("ComObject must define a value for either clsId or progId");
  }
  
  IDispatchCallback createDispatchCallback(Class<?> paramClass, IComEventCallbackListener paramIComEventCallbackListener) {
    return new CallbackProxy(this, paramClass, paramIComEventCallbackListener);
  }
  
  public void register(ProxyObject paramProxyObject) {
    synchronized (this.registeredObjects) {
      this.registeredObjects.add(new WeakReference<>(paramProxyObject));
    } 
  }
  
  public void unregister(ProxyObject paramProxyObject) {
    synchronized (this.registeredObjects) {
      Iterator<WeakReference<ProxyObject>> iterator = this.registeredObjects.iterator();
      while (iterator.hasNext()) {
        WeakReference<ProxyObject> weakReference = iterator.next();
        ProxyObject proxyObject = weakReference.get();
        if (proxyObject == null || proxyObject == paramProxyObject)
          iterator.remove(); 
      } 
    } 
  }
  
  public void disposeAll() {
    synchronized (this.registeredObjects) {
      ArrayList<WeakReference<ProxyObject>> arrayList = new ArrayList<>(this.registeredObjects);
      for (WeakReference<ProxyObject> weakReference : arrayList) {
        ProxyObject proxyObject = weakReference.get();
        if (proxyObject != null)
          proxyObject.dispose(); 
      } 
      this.registeredObjects.clear();
    } 
  }
  
  public WinDef.LCID getLCID() {
    return (this.LCID != null) ? this.LCID : LOCALE_USER_DEFAULT;
  }
  
  public void setLCID(WinDef.LCID paramLCID) {
    this.LCID = paramLCID;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\ObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */