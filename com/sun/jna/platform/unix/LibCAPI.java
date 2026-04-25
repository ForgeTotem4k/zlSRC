package com.sun.jna.platform.unix;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface LibCAPI extends Reboot, Resource {
  public static final int HOST_NAME_MAX = 255;
  
  int getuid();
  
  int geteuid();
  
  int getgid();
  
  int getegid();
  
  int setuid(int paramInt);
  
  int seteuid(int paramInt);
  
  int setgid(int paramInt);
  
  int setegid(int paramInt);
  
  int gethostname(byte[] paramArrayOfbyte, int paramInt);
  
  int sethostname(String paramString, int paramInt);
  
  int getdomainname(byte[] paramArrayOfbyte, int paramInt);
  
  int setdomainname(String paramString, int paramInt);
  
  String getenv(String paramString);
  
  int setenv(String paramString1, String paramString2, int paramInt);
  
  int unsetenv(String paramString);
  
  int getloadavg(double[] paramArrayOfdouble, int paramInt);
  
  int close(int paramInt);
  
  int msync(Pointer paramPointer, size_t paramsize_t, int paramInt);
  
  int munmap(Pointer paramPointer, size_t paramsize_t);
  
  public static class ssize_t extends IntegerType {
    public static final ssize_t ZERO = new ssize_t();
    
    private static final long serialVersionUID = 1L;
    
    public ssize_t() {
      this(0L);
    }
    
    public ssize_t(long param1Long) {
      super(Native.SIZE_T_SIZE, param1Long, false);
    }
  }
  
  public static class size_t extends IntegerType {
    public static final size_t ZERO = new size_t();
    
    private static final long serialVersionUID = 1L;
    
    public size_t() {
      this(0L);
    }
    
    public size_t(long param1Long) {
      super(Native.SIZE_T_SIZE, param1Long, true);
    }
    
    public static class ByReference extends com.sun.jna.ptr.ByReference {
      public ByReference() {
        this(0L);
      }
      
      public ByReference(long param2Long) {
        this(new LibCAPI.size_t(param2Long));
      }
      
      public ByReference(LibCAPI.size_t param2size_t) {
        super(Native.SIZE_T_SIZE);
        setValue(param2size_t);
      }
      
      public void setValue(long param2Long) {
        setValue(new LibCAPI.size_t(param2Long));
      }
      
      public void setValue(LibCAPI.size_t param2size_t) {
        if (Native.SIZE_T_SIZE > 4) {
          getPointer().setLong(0L, param2size_t.longValue());
        } else {
          getPointer().setInt(0L, param2size_t.intValue());
        } 
      }
      
      public long longValue() {
        return (Native.SIZE_T_SIZE > 4) ? getPointer().getLong(0L) : getPointer().getInt(0L);
      }
      
      public LibCAPI.size_t getValue() {
        return new LibCAPI.size_t(longValue());
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\LibCAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */