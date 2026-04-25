package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class FloatByReference extends ByReference {
  public FloatByReference() {
    this(0.0F);
  }
  
  public FloatByReference(float paramFloat) {
    super(4);
    setValue(paramFloat);
  }
  
  public void setValue(float paramFloat) {
    getPointer().setFloat(0L, paramFloat);
  }
  
  public float getValue() {
    return getPointer().getFloat(0L);
  }
  
  public String toString() {
    return String.format("float@0x%x=%s", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Float.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\FloatByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */