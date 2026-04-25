package com.sun.jna.ptr;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class PointerByReference extends ByReference {
  public PointerByReference() {
    this(null);
  }
  
  public PointerByReference(Pointer paramPointer) {
    super(Native.POINTER_SIZE);
    setValue(paramPointer);
  }
  
  public void setValue(Pointer paramPointer) {
    getPointer().setPointer(0L, paramPointer);
  }
  
  public Pointer getValue() {
    return getPointer().getPointer(0L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\PointerByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */