package com.sun.jna.ptr;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import java.lang.reflect.Method;

public abstract class ByReference extends PointerType {
  protected ByReference(int paramInt) {
    setPointer((Pointer)new Memory(paramInt));
  }
  
  public String toString() {
    try {
      Method method = getClass().getMethod("getValue", new Class[0]);
      Object object = method.invoke(this, new Object[0]);
      return (object == null) ? String.format("null@0x%x", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())) }) : String.format("%s@0x%x=%s", new Object[] { object.getClass().getSimpleName(), Long.valueOf(Pointer.nativeValue(getPointer())), object });
    } catch (Exception exception) {
      return String.format("ByReference Contract violated - %s#getValue raised exception: %s", new Object[] { getClass().getName(), exception.getMessage() });
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\ByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */