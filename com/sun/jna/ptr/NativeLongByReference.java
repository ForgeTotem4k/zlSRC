package com.sun.jna.ptr;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class NativeLongByReference extends ByReference {
  public NativeLongByReference() {
    this(new NativeLong(0L));
  }
  
  public NativeLongByReference(NativeLong paramNativeLong) {
    super(NativeLong.SIZE);
    setValue(paramNativeLong);
  }
  
  public void setValue(NativeLong paramNativeLong) {
    getPointer().setNativeLong(0L, paramNativeLong);
  }
  
  public NativeLong getValue() {
    return getPointer().getNativeLong(0L);
  }
  
  public String toString() {
    return (NativeLong.SIZE > 4) ? String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Long.valueOf(getValue().longValue()) }) : String.format("NativeLong@0x1$%x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Integer.valueOf(getValue().intValue()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ptr\NativeLongByReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */