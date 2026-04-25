package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public abstract class COMInvoker extends PointerType {
  protected int _invokeNativeInt(int paramInt, Object[] paramArrayOfObject) {
    Pointer pointer = getPointer().getPointer(0L);
    Function function = Function.getFunction(pointer.getPointer((paramInt * Native.POINTER_SIZE)));
    return function.invokeInt(paramArrayOfObject);
  }
  
  protected Object _invokeNativeObject(int paramInt, Object[] paramArrayOfObject, Class<?> paramClass) {
    Pointer pointer = getPointer().getPointer(0L);
    Function function = Function.getFunction(pointer.getPointer((paramInt * Native.POINTER_SIZE)));
    return function.invoke(paramClass, paramArrayOfObject);
  }
  
  protected void _invokeNativeVoid(int paramInt, Object[] paramArrayOfObject) {
    Pointer pointer = getPointer().getPointer(0L);
    Function function = Function.getFunction(pointer.getPointer((paramInt * Native.POINTER_SIZE)));
    function.invokeVoid(paramArrayOfObject);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */