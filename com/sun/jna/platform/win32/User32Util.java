package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class User32Util {
  public static final EnumSet<Win32VK> WIN32VK_MAPPABLE = EnumSet.of(Win32VK.VK_BACK, new Win32VK[] { 
        Win32VK.VK_TAB, Win32VK.VK_CLEAR, Win32VK.VK_RETURN, Win32VK.VK_ESCAPE, Win32VK.VK_SPACE, Win32VK.VK_SELECT, Win32VK.VK_EXECUTE, Win32VK.VK_0, Win32VK.VK_1, Win32VK.VK_2, 
        Win32VK.VK_3, Win32VK.VK_4, Win32VK.VK_5, Win32VK.VK_6, Win32VK.VK_7, Win32VK.VK_8, Win32VK.VK_9, Win32VK.VK_A, Win32VK.VK_B, Win32VK.VK_C, 
        Win32VK.VK_D, Win32VK.VK_E, Win32VK.VK_F, Win32VK.VK_G, Win32VK.VK_H, Win32VK.VK_I, Win32VK.VK_J, Win32VK.VK_K, Win32VK.VK_L, Win32VK.VK_M, 
        Win32VK.VK_N, Win32VK.VK_O, Win32VK.VK_P, Win32VK.VK_Q, Win32VK.VK_R, Win32VK.VK_S, Win32VK.VK_T, Win32VK.VK_U, Win32VK.VK_V, Win32VK.VK_W, 
        Win32VK.VK_X, Win32VK.VK_Y, Win32VK.VK_Z, Win32VK.VK_NUMPAD0, Win32VK.VK_NUMPAD1, Win32VK.VK_NUMPAD2, Win32VK.VK_NUMPAD3, Win32VK.VK_NUMPAD4, Win32VK.VK_NUMPAD5, Win32VK.VK_NUMPAD6, 
        Win32VK.VK_NUMPAD7, Win32VK.VK_NUMPAD8, Win32VK.VK_NUMPAD9, Win32VK.VK_MULTIPLY, Win32VK.VK_ADD, Win32VK.VK_SEPARATOR, Win32VK.VK_SUBTRACT, Win32VK.VK_DECIMAL, Win32VK.VK_DIVIDE, Win32VK.VK_OEM_NEC_EQUAL, 
        Win32VK.VK_OEM_FJ_MASSHOU, Win32VK.VK_OEM_FJ_TOUROKU, Win32VK.VK_OEM_FJ_LOYA, Win32VK.VK_OEM_FJ_ROYA, Win32VK.VK_OEM_1, Win32VK.VK_OEM_PLUS, Win32VK.VK_OEM_COMMA, Win32VK.VK_OEM_MINUS, Win32VK.VK_OEM_PERIOD, Win32VK.VK_OEM_2, 
        Win32VK.VK_OEM_3, Win32VK.VK_RESERVED_C1, Win32VK.VK_RESERVED_C2, Win32VK.VK_OEM_4, Win32VK.VK_OEM_5, Win32VK.VK_OEM_6, Win32VK.VK_OEM_7, Win32VK.VK_OEM_8, Win32VK.VK_OEM_AX, Win32VK.VK_OEM_102, 
        Win32VK.VK_ICO_HELP, Win32VK.VK_PROCESSKEY, Win32VK.VK_ICO_CLEAR, Win32VK.VK_PACKET, Win32VK.VK_OEM_RESET, Win32VK.VK_OEM_JUMP, Win32VK.VK_OEM_PA1, Win32VK.VK_OEM_PA2, Win32VK.VK_OEM_PA3, Win32VK.VK_OEM_WSCTRL, 
        Win32VK.VK_OEM_CUSEL, Win32VK.VK_OEM_ATTN, Win32VK.VK_OEM_FINISH, Win32VK.VK_OEM_COPY, Win32VK.VK_OEM_AUTO, Win32VK.VK_OEM_ENLW, Win32VK.VK_OEM_BACKTAB, Win32VK.VK_ATTN, Win32VK.VK_CRSEL, Win32VK.VK_EXSEL, 
        Win32VK.VK_EREOF, Win32VK.VK_PLAY, Win32VK.VK_ZOOM, Win32VK.VK_NONAME, Win32VK.VK_PA1, Win32VK.VK_OEM_CLEAR });
  
  public static final int registerWindowMessage(String paramString) {
    int i = User32.INSTANCE.RegisterWindowMessage(paramString);
    if (i == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return i;
  }
  
  public static final WinDef.HWND createWindow(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, WinDef.HWND paramHWND, WinDef.HMENU paramHMENU, WinDef.HINSTANCE paramHINSTANCE, WinDef.LPVOID paramLPVOID) {
    return createWindowEx(0, paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramHWND, paramHMENU, paramHINSTANCE, paramLPVOID);
  }
  
  public static final WinDef.HWND createWindowEx(int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, WinDef.HWND paramHWND, WinDef.HMENU paramHMENU, WinDef.HINSTANCE paramHINSTANCE, WinDef.LPVOID paramLPVOID) {
    WinDef.HWND hWND = User32.INSTANCE.CreateWindowEx(paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramHWND, paramHMENU, paramHINSTANCE, paramLPVOID);
    if (hWND == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return hWND;
  }
  
  public static final void destroyWindow(WinDef.HWND paramHWND) {
    if (!User32.INSTANCE.DestroyWindow(paramHWND))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static final List<WinUser.RAWINPUTDEVICELIST> GetRawInputDeviceList() {
    IntByReference intByReference = new IntByReference(0);
    WinUser.RAWINPUTDEVICELIST rAWINPUTDEVICELIST = new WinUser.RAWINPUTDEVICELIST();
    int i = rAWINPUTDEVICELIST.sizeof();
    int j = User32.INSTANCE.GetRawInputDeviceList(null, intByReference, i);
    if (j != 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    int k = intByReference.getValue();
    WinUser.RAWINPUTDEVICELIST[] arrayOfRAWINPUTDEVICELIST = (WinUser.RAWINPUTDEVICELIST[])rAWINPUTDEVICELIST.toArray(k);
    j = User32.INSTANCE.GetRawInputDeviceList(arrayOfRAWINPUTDEVICELIST, intByReference, i);
    if (j == -1)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    if (j != arrayOfRAWINPUTDEVICELIST.length)
      throw new IllegalStateException("Mismatched allocated (" + arrayOfRAWINPUTDEVICELIST.length + ") vs. received devices count (" + j + ")"); 
    return Arrays.asList(arrayOfRAWINPUTDEVICELIST);
  }
  
  public static String loadString(String paramString) throws UnsupportedEncodingException {
    int i = paramString.lastIndexOf(',');
    String str1 = paramString.substring(0, i);
    int j = Math.abs(Integer.parseInt(paramString.substring(i + 1)));
    String str2 = Kernel32Util.expandEnvironmentStrings(str1);
    WinDef.HMODULE hMODULE = Kernel32.INSTANCE.LoadLibraryEx(str2, null, 2);
    if (hMODULE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Memory memory = new Memory(Native.POINTER_SIZE);
    i = User32.INSTANCE.LoadString(hMODULE, j, (Pointer)memory, 0);
    if (0 == i)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? new String(memory.getPointer(0L).getCharArray(0L, i)) : new String(memory.getPointer(0L).getByteArray(0L, i), Native.getDefaultStringEncoding());
  }
  
  public static class MessageLoopThread extends Thread {
    private volatile int nativeThreadId = 0;
    
    private volatile long javaThreadId = 0L;
    
    private final List<FutureTask> workQueue = Collections.synchronizedList(new ArrayList<>());
    
    private static long messageLoopId = 0L;
    
    public MessageLoopThread() {
      setName("JNA User32 MessageLoop " + ++messageLoopId);
    }
    
    public void run() {
      WinUser.MSG mSG = new WinUser.MSG();
      User32.INSTANCE.PeekMessage(mSG, null, 0, 0, 0);
      this.javaThreadId = Thread.currentThread().getId();
      this.nativeThreadId = Kernel32.INSTANCE.GetCurrentThreadId();
      int i;
      while ((i = User32.INSTANCE.GetMessage(mSG, null, 0, 0)) != 0) {
        if (i != -1) {
          while (!this.workQueue.isEmpty()) {
            try {
              FutureTask futureTask = this.workQueue.remove(0);
              futureTask.run();
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
              break;
            } 
          } 
          User32.INSTANCE.TranslateMessage(mSG);
          User32.INSTANCE.DispatchMessage(mSG);
          continue;
        } 
        if (getMessageFailed())
          break; 
      } 
      while (!this.workQueue.isEmpty())
        ((FutureTask)this.workQueue.remove(0)).cancel(false); 
    }
    
    public <V> Future<V> runAsync(Callable<V> param1Callable) {
      while (this.nativeThreadId == 0) {
        try {
          Thread.sleep(20L);
        } catch (InterruptedException interruptedException) {
          Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, interruptedException);
        } 
      } 
      FutureTask<V> futureTask = new FutureTask<>(param1Callable);
      this.workQueue.add(futureTask);
      User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 1024, null, null);
      return futureTask;
    }
    
    public <V> V runOnThread(Callable<V> param1Callable) throws Exception {
      while (this.javaThreadId == 0L) {
        try {
          Thread.sleep(20L);
        } catch (InterruptedException interruptedException) {
          Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, (String)null, interruptedException);
        } 
      } 
      if (this.javaThreadId == Thread.currentThread().getId())
        return param1Callable.call(); 
      Future<V> future = runAsync(param1Callable);
      try {
        return future.get();
      } catch (InterruptedException interruptedException) {
        throw interruptedException;
      } catch (ExecutionException executionException) {
        Throwable throwable = executionException.getCause();
        if (throwable instanceof Exception)
          throw (Exception)throwable; 
        throw executionException;
      } 
    }
    
    public void exit() {
      User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 18, null, null);
    }
    
    protected boolean getMessageFailed() {
      int i = Kernel32.INSTANCE.GetLastError();
      Logger.getLogger("com.sun.jna.platform.win32.User32Util.MessageLoopThread").log(Level.WARNING, "Message loop was interrupted by an error. [lastError: {0}]", Integer.valueOf(i));
      return true;
    }
    
    public class Handler implements InvocationHandler {
      private final Object delegate;
      
      public Handler(Object param2Object) {
        this.delegate = param2Object;
      }
      
      public Object invoke(Object param2Object, final Method method, final Object[] args) throws Throwable {
        try {
          return User32Util.MessageLoopThread.this.runOnThread(new Callable() {
                public Object call() throws Exception {
                  return method.invoke(User32Util.MessageLoopThread.Handler.this.delegate, args);
                }
              });
        } catch (InvocationTargetException invocationTargetException) {
          Throwable throwable = invocationTargetException.getCause();
          if (throwable instanceof Exception) {
            StackTraceElement[] arrayOfStackTraceElement1 = throwable.getStackTrace();
            throwable.fillInStackTrace();
            StackTraceElement[] arrayOfStackTraceElement2 = throwable.getStackTrace();
            StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement2.length + arrayOfStackTraceElement1.length];
            System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement1.length);
            System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement1.length, arrayOfStackTraceElement2.length);
            throwable.setStackTrace(arrayOfStackTraceElement3);
            throw (Exception)throwable;
          } 
          throw invocationTargetException;
        } 
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\User32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */