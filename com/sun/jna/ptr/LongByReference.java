package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class LongByReference extends ByReference {
  public LongByReference() {
    this(0L);
  }
  
  public LongByReference(long paramLong) {
    super(8);
    setValue(paramLong);
  }
  
  public void setValue(long paramLong) {
    getPointer().setLong(0L, paramLong);
  }
  
  public long getValue() {
    return getPointer().getLong(0L);
  }
  
  public String toString() {
    return String.format("long@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Long.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\LongByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */