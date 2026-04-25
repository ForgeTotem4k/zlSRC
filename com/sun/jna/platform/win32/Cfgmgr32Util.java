package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;

public abstract class Cfgmgr32Util {
  public static String CM_Get_Device_ID(int paramInt) throws Cfgmgr32Exception {
    byte b = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
    IntByReference intByReference = new IntByReference();
    int i = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(intByReference, paramInt, 0);
    if (i != 0)
      throw new Cfgmgr32Exception(i); 
    Memory memory = new Memory(((intByReference.getValue() + 1) * b));
    memory.clear();
    i = Cfgmgr32.INSTANCE.CM_Get_Device_ID(paramInt, (Pointer)memory, intByReference.getValue(), 0);
    if (i == 26) {
      i = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(intByReference, paramInt, 0);
      if (i != 0)
        throw new Cfgmgr32Exception(i); 
      memory = new Memory(((intByReference.getValue() + 1) * b));
      memory.clear();
      i = Cfgmgr32.INSTANCE.CM_Get_Device_ID(paramInt, (Pointer)memory, intByReference.getValue(), 0);
    } 
    if (i != 0)
      throw new Cfgmgr32Exception(i); 
    return (b == 1) ? memory.getString(0L) : memory.getWideString(0L);
  }
  
  public static Object CM_Get_DevNode_Registry_Property(int paramInt1, int paramInt2) throws Cfgmgr32Exception {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(paramInt1, paramInt2, intByReference2, null, intByReference1, 0);
    if (i == 37)
      return null; 
    if (i != 26)
      throw new Cfgmgr32Exception(i); 
    Memory memory = null;
    if (intByReference1.getValue() > 0) {
      memory = new Memory(intByReference1.getValue());
      i = Cfgmgr32.INSTANCE.CM_Get_DevNode_Registry_Property(paramInt1, paramInt2, intByReference2, (Pointer)memory, intByReference1, 0);
      if (i != 0)
        throw new Cfgmgr32Exception(i); 
    } 
    switch (intByReference2.getValue()) {
      case 1:
        return (memory == null) ? "" : ((W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? memory.getWideString(0L) : memory.getString(0L));
      case 7:
        return (memory == null) ? new String[0] : Advapi32Util.regMultiSzBufferToStringArray(memory);
      case 4:
        return (memory == null) ? Integer.valueOf(0) : Integer.valueOf(memory.getInt(0L));
      case 0:
        return null;
    } 
    return (memory == null) ? new byte[0] : memory.getByteArray(0L, (int)memory.size());
  }
  
  public static class Cfgmgr32Exception extends RuntimeException {
    private final int errorCode;
    
    public Cfgmgr32Exception(int param1Int) {
      this.errorCode = param1Int;
    }
    
    public int getErrorCode() {
      return this.errorCode;
    }
    
    public String toString() {
      return super.toString() + String.format(" [errorCode: 0x%08x]", new Object[] { Integer.valueOf(this.errorCode) });
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Cfgmgr32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */