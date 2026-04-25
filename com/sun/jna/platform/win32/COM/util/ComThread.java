package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ComThread {
  private static ThreadLocal<Boolean> isCOMThread = new ThreadLocal<>();
  
  ExecutorService executor;
  
  Runnable firstTask;
  
  boolean requiresInitialisation = true;
  
  long timeoutMilliseconds;
  
  Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  
  public ComThread(String paramString, long paramLong, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {
    this(paramString, paramLong, paramUncaughtExceptionHandler, 0);
  }
  
  public ComThread(final String threadName, long paramLong, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, final int coinitialiseExFlag) {
    this.timeoutMilliseconds = paramLong;
    this.uncaughtExceptionHandler = paramUncaughtExceptionHandler;
    this.firstTask = new Runnable() {
        public void run() {
          try {
            WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoInitializeEx(null, coinitialiseExFlag);
            ComThread.isCOMThread.set(Boolean.valueOf(true));
            COMUtils.checkRC(hRESULT);
            ComThread.this.requiresInitialisation = false;
          } catch (Throwable throwable) {
            ComThread.this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
          } 
        }
      };
    this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
          public Thread newThread(Runnable param1Runnable) {
            if (!ComThread.this.requiresInitialisation)
              throw new RuntimeException("ComThread executor has a problem."); 
            Thread thread = new Thread(param1Runnable, threadName);
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                  public void uncaughtException(Thread param2Thread, Throwable param2Throwable) {
                    ComThread.this.requiresInitialisation = true;
                    ComThread.this.uncaughtExceptionHandler.uncaughtException(param2Thread, param2Throwable);
                  }
                });
            return thread;
          }
        });
  }
  
  public void terminate(long paramLong) {
    try {
      this.executor.submit(new Runnable() {
            public void run() {
              Ole32.INSTANCE.CoUninitialize();
            }
          }).get(paramLong, TimeUnit.MILLISECONDS);
      this.executor.shutdown();
    } catch (InterruptedException|ExecutionException interruptedException) {
      interruptedException.printStackTrace();
    } catch (TimeoutException timeoutException) {
      this.executor.shutdownNow();
    } 
  }
  
  protected void finalize() throws Throwable {
    if (!this.executor.isShutdown())
      terminate(100L); 
  }
  
  static void setComThread(boolean paramBoolean) {
    isCOMThread.set(Boolean.valueOf(paramBoolean));
  }
  
  public <T> T execute(Callable<T> paramCallable) throws TimeoutException, InterruptedException, ExecutionException {
    Boolean bool = isCOMThread.get();
    if (bool == null)
      bool = Boolean.valueOf(false); 
    if (bool.booleanValue())
      try {
        return paramCallable.call();
      } catch (Exception exception) {
        throw new ExecutionException(exception);
      }  
    if (this.requiresInitialisation)
      this.executor.execute(this.firstTask); 
    return this.executor.<T>submit(paramCallable).get(this.timeoutMilliseconds, TimeUnit.MILLISECONDS);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\ComThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */