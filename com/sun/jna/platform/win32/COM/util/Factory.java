package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Factory extends ObjectFactory {
  private ComThread comThread;
  
  public Factory() {
    this(new ComThread("Default Factory COM Thread", 5000L, new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread param1Thread, Throwable param1Throwable) {}
          }));
  }
  
  public Factory(ComThread paramComThread) {
    this.comThread = paramComThread;
  }
  
  public <T> T createProxy(Class<T> paramClass, IDispatch paramIDispatch) {
    Object object = super.createProxy((Class)paramClass, paramIDispatch);
    ProxyObject2 proxyObject2 = new ProxyObject2(object);
    return (T)Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, proxyObject2);
  }
  
  Guid.GUID discoverClsId(final ComObject annotation) {
    return runInComThread(new Callable<Guid.GUID>() {
          public Guid.GUID call() throws Exception {
            return Factory.this.discoverClsId(annotation);
          }
        });
  }
  
  public <T> T fetchObject(final Class<T> comInterface) throws COMException {
    return runInComThread(new Callable<T>() {
          public T call() throws Exception {
            return Factory.this.fetchObject(comInterface);
          }
        });
  }
  
  public <T> T createObject(final Class<T> comInterface) {
    return runInComThread(new Callable<T>() {
          public T call() throws Exception {
            return Factory.this.createObject(comInterface);
          }
        });
  }
  
  IDispatchCallback createDispatchCallback(Class<?> paramClass, IComEventCallbackListener paramIComEventCallbackListener) {
    return new CallbackProxy2(this, paramClass, paramIComEventCallbackListener);
  }
  
  public IRunningObjectTable getRunningObjectTable() {
    return super.getRunningObjectTable();
  }
  
  private <T> T runInComThread(Callable<T> paramCallable) {
    try {
      return this.comThread.execute(paramCallable);
    } catch (TimeoutException|InterruptedException timeoutException) {
      throw new RuntimeException(timeoutException);
    } catch (ExecutionException executionException) {
      Throwable throwable = executionException.getCause();
      if (throwable instanceof RuntimeException) {
        appendStacktrace(executionException, throwable);
        throw (RuntimeException)throwable;
      } 
      if (throwable instanceof InvocationTargetException) {
        throwable = ((InvocationTargetException)throwable).getTargetException();
        if (throwable instanceof RuntimeException) {
          appendStacktrace(executionException, throwable);
          throw (RuntimeException)throwable;
        } 
      } 
      throw new RuntimeException(executionException);
    } 
  }
  
  private static void appendStacktrace(Exception paramException, Throwable paramThrowable) {
    StackTraceElement[] arrayOfStackTraceElement1 = paramException.getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement2 = paramThrowable.getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement1.length + arrayOfStackTraceElement2.length];
    System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement2.length, arrayOfStackTraceElement1.length);
    System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement2.length);
    paramThrowable.setStackTrace(arrayOfStackTraceElement3);
  }
  
  public ComThread getComThread() {
    return this.comThread;
  }
  
  private class ProxyObject2 implements InvocationHandler {
    private final Object delegate;
    
    public ProxyObject2(Object param1Object) {
      this.delegate = param1Object;
    }
    
    public Object invoke(Object param1Object, final Method method, final Object[] args) throws Throwable {
      if (args != null)
        for (byte b = 0; b < args.length; b++) {
          if (args[b] != null && Proxy.isProxyClass(args[b].getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[b]);
            if (invocationHandler instanceof ProxyObject2)
              args[b] = ((ProxyObject2)invocationHandler).delegate; 
          } 
        }  
      return Factory.this.runInComThread(new Callable() {
            public Object call() throws Exception {
              return method.invoke(Factory.ProxyObject2.this.delegate, args);
            }
          });
    }
  }
  
  private class CallbackProxy2 extends CallbackProxy {
    public CallbackProxy2(ObjectFactory param1ObjectFactory, Class<?> param1Class, IComEventCallbackListener param1IComEventCallbackListener) {
      super(param1ObjectFactory, param1Class, param1IComEventCallbackListener);
    }
    
    public WinNT.HRESULT Invoke(OaIdl.DISPID param1DISPID, Guid.REFIID param1REFIID, WinDef.LCID param1LCID, WinDef.WORD param1WORD, OleAuto.DISPPARAMS.ByReference param1ByReference, Variant.VARIANT.ByReference param1ByReference1, OaIdl.EXCEPINFO.ByReference param1ByReference2, IntByReference param1IntByReference) {
      ComThread.setComThread(true);
      try {
        return super.Invoke(param1DISPID, param1REFIID, param1LCID, param1WORD, param1ByReference, param1ByReference1, param1ByReference2, param1IntByReference);
      } finally {
        ComThread.setComThread(false);
      } 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */