package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class IntByReference extends ByReference {
  public IntByReference() {
    this(0);
  }
  
  public IntByReference(int paramInt) {
    super(4);
    setValue(paramInt);
  }
  
  public void setValue(int paramInt) {
    getPointer().setInt(0L, paramInt);
  }
  
  public int getValue() {
    return getPointer().getInt(0L);
  }
  
  public String toString() {
    return String.format("int@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Integer.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\IntByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */