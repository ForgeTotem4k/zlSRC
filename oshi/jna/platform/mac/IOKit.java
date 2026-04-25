package oshi.jna.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.ptr.NativeLongByReference;
import oshi.util.Util;

public interface IOKit extends IOKit {
  public static final IOKit INSTANCE = (IOKit)Native.load("IOKit", IOKit.class);
  
  int IOConnectCallStructMethod(IOKit.IOConnect paramIOConnect, int paramInt, Structure paramStructure1, NativeLong paramNativeLong, Structure paramStructure2, NativeLongByReference paramNativeLongByReference);
  
  @FieldOrder({"key", "dataSize", "dataType", "bytes"})
  public static class SMCVal extends Structure implements AutoCloseable {
    public byte[] key = new byte[5];
    
    public int dataSize;
    
    public byte[] dataType = new byte[5];
    
    public byte[] bytes = new byte[32];
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
  public static class SMCKeyData extends Structure implements AutoCloseable {
    public int key;
    
    public IOKit.SMCKeyDataVers vers;
    
    public IOKit.SMCKeyDataPLimitData pLimitData;
    
    public IOKit.SMCKeyDataKeyInfo keyInfo;
    
    public byte result;
    
    public byte status;
    
    public byte data8;
    
    public int data32;
    
    public byte[] bytes = new byte[32];
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"dataSize", "dataType", "dataAttributes"})
  public static class SMCKeyDataKeyInfo extends Structure {
    public int dataSize;
    
    public int dataType;
    
    public byte dataAttributes;
  }
  
  @FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
  public static class SMCKeyDataPLimitData extends Structure {
    public short version;
    
    public short length;
    
    public int cpuPLimit;
    
    public int gpuPLimit;
    
    public int memPLimit;
  }
  
  @FieldOrder({"major", "minor", "build", "reserved", "release"})
  public static class SMCKeyDataVers extends Structure {
    public byte major;
    
    public byte minor;
    
    public byte build;
    
    public byte reserved;
    
    public short release;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\jna\platform\mac\IOKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */