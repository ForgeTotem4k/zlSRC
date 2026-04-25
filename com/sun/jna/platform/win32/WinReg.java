package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public interface WinReg {
  public static final HKEY HKEY_CLASSES_ROOT = new HKEY(-2147483648);
  
  public static final HKEY HKEY_CURRENT_USER = new HKEY(-2147483647);
  
  public static final HKEY HKEY_LOCAL_MACHINE = new HKEY(-2147483646);
  
  public static final HKEY HKEY_USERS = new HKEY(-2147483645);
  
  public static final HKEY HKEY_PERFORMANCE_DATA = new HKEY(-2147483644);
  
  public static final HKEY HKEY_PERFORMANCE_TEXT = new HKEY(-2147483568);
  
  public static final HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(-2147483552);
  
  public static final HKEY HKEY_CURRENT_CONFIG = new HKEY(-2147483643);
  
  public static final HKEY HKEY_DYN_DATA = new HKEY(-2147483642);
  
  public static final HKEY HKEY_CURRENT_USER_LOCAL_SETTINGS = new HKEY(-2147483641);
  
  public static class HKEY extends WinNT.HANDLE {
    public HKEY() {}
    
    public HKEY(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public HKEY(int param1Int) {
      super(new Pointer(param1Int));
    }
  }
  
  public static class HKEYByReference extends ByReference {
    public HKEYByReference() {
      this(null);
    }
    
    public HKEYByReference(WinReg.HKEY param1HKEY) {
      super(Native.POINTER_SIZE);
      setValue(param1HKEY);
    }
    
    public void setValue(WinReg.HKEY param1HKEY) {
      getPointer().setPointer(0L, (param1HKEY != null) ? param1HKEY.getPointer() : null);
    }
    
    public WinReg.HKEY getValue() {
      Pointer pointer = getPointer().getPointer(0L);
      if (pointer == null)
        return null; 
      if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(pointer))
        return (WinReg.HKEY)WinBase.INVALID_HANDLE_VALUE; 
      WinReg.HKEY hKEY = new WinReg.HKEY();
      hKEY.setPointer(pointer);
      return hKEY;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinReg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */