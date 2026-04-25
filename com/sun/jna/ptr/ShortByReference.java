package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class ShortByReference extends ByReference {
  public ShortByReference() {
    this((short)0);
  }
  
  public ShortByReference(short paramShort) {
    super(2);
    setValue(paramShort);
  }
  
  public void setValue(short paramShort) {
    getPointer().setShort(0L, paramShort);
  }
  
  public short getValue() {
    return getPointer().getShort(0L);
  }
  
  public String toString() {
    return String.format("short@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Short.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\ShortByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */