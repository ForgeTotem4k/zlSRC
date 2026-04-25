package oshi.jna;

import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import oshi.util.Util;

public interface ByRef {
  public static class CloseablePROCESSENTRY32ByReference extends Tlhelp32.PROCESSENTRY32.ByReference implements AutoCloseable {
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableSizeTByReference extends LibCAPI.size_t.ByReference implements AutoCloseable {
    public CloseableSizeTByReference() {}
    
    public CloseableSizeTByReference(long param1Long) {
      super(param1Long);
    }
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableHANDLEByReference extends WinNT.HANDLEByReference implements AutoCloseable {
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableULONGptrByReference extends BaseTSD.ULONG_PTRByReference implements AutoCloseable {
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableLONGLONGByReference extends WinDef.LONGLONGByReference implements AutoCloseable {
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseablePointerByReference extends PointerByReference implements AutoCloseable {
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableNativeLongByReference extends NativeLongByReference implements AutoCloseable {
    public CloseableNativeLongByReference() {}
    
    public CloseableNativeLongByReference(NativeLong param1NativeLong) {
      super(param1NativeLong);
    }
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableLongByReference extends LongByReference implements AutoCloseable {
    public CloseableLongByReference() {}
    
    public CloseableLongByReference(long param1Long) {
      super(param1Long);
    }
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public static class CloseableIntByReference extends IntByReference implements AutoCloseable {
    public CloseableIntByReference() {}
    
    public CloseableIntByReference(int param1Int) {
      super(param1Int);
    }
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\jna\ByRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */