package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class ByteByReference extends ByReference {
  public ByteByReference() {
    this((byte)0);
  }
  
  public ByteByReference(byte paramByte) {
    super(1);
    setValue(paramByte);
  }
  
  public void setValue(byte paramByte) {
    getPointer().setByte(0L, paramByte);
  }
  
  public byte getValue() {
    return getPointer().getByte(0L);
  }
  
  public String toString() {
    return String.format("byte@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Byte.valueOf(getValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\ByteByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */