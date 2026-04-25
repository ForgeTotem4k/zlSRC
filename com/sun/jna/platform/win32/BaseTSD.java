package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public interface BaseTSD {
  public static class SIZE_T extends ULONG_PTR {
    public SIZE_T() {
      this(0L);
    }
    
    public SIZE_T(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class DWORD_PTR extends IntegerType {
    public DWORD_PTR() {
      this(0L);
    }
    
    public DWORD_PTR(long param1Long) {
      super(Native.POINTER_SIZE, param1Long);
    }
  }
  
  public static class ULONG_PTRByReference extends ByReference {
    public ULONG_PTRByReference() {
      this(new BaseTSD.ULONG_PTR(0L));
    }
    
    public ULONG_PTRByReference(BaseTSD.ULONG_PTR param1ULONG_PTR) {
      super(Native.POINTER_SIZE);
      setValue(param1ULONG_PTR);
    }
    
    public void setValue(BaseTSD.ULONG_PTR param1ULONG_PTR) {
      if (Native.POINTER_SIZE == 4) {
        getPointer().setInt(0L, param1ULONG_PTR.intValue());
      } else {
        getPointer().setLong(0L, param1ULONG_PTR.longValue());
      } 
    }
    
    public BaseTSD.ULONG_PTR getValue() {
      return new BaseTSD.ULONG_PTR((Native.POINTER_SIZE == 4) ? getPointer().getInt(0L) : getPointer().getLong(0L));
    }
  }
  
  public static class ULONG_PTR extends IntegerType {
    public ULONG_PTR() {
      this(0L);
    }
    
    public ULONG_PTR(long param1Long) {
      super(Native.POINTER_SIZE, param1Long, true);
    }
    
    public Pointer toPointer() {
      return Pointer.createConstant(longValue());
    }
  }
  
  public static class SSIZE_T extends LONG_PTR {
    public SSIZE_T() {
      this(0L);
    }
    
    public SSIZE_T(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class LONG_PTR extends IntegerType {
    public LONG_PTR() {
      this(0L);
    }
    
    public LONG_PTR(long param1Long) {
      super(Native.POINTER_SIZE, param1Long);
    }
    
    public Pointer toPointer() {
      return Pointer.createConstant(longValue());
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\BaseTSD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */