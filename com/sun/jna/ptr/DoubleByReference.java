package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class DoubleByReference extends ByReference {
  public DoubleByReference() {
    this(0.0D);
  }
  
  public DoubleByReference(double paramDouble) {
    super(8);
    setValue(paramDouble);
  }
  
  public void setValue(double paramDouble) {
    getPointer().setDouble(0L, paramDouble);
  }
  
  public double getValue() {
    return getPointer().getDouble(0L);
  }
  
  public String toString() {
    return String.format("double@0x%x=%s", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Double.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\DoubleByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */