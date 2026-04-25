package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import java.io.UnsupportedEncodingException;

public interface WTypes {
  public static final int CLSCTX_INPROC_SERVER = 1;
  
  public static final int CLSCTX_INPROC_HANDLER = 2;
  
  public static final int CLSCTX_LOCAL_SERVER = 4;
  
  public static final int CLSCTX_INPROC_SERVER16 = 8;
  
  public static final int CLSCTX_REMOTE_SERVER = 16;
  
  public static final int CLSCTX_INPROC_HANDLER16 = 32;
  
  public static final int CLSCTX_RESERVED1 = 64;
  
  public static final int CLSCTX_RESERVED2 = 128;
  
  public static final int CLSCTX_RESERVED3 = 256;
  
  public static final int CLSCTX_RESERVED4 = 512;
  
  public static final int CLSCTX_NO_CODE_DOWNLOAD = 1024;
  
  public static final int CLSCTX_RESERVED5 = 2048;
  
  public static final int CLSCTX_NO_CUSTOM_MARSHAL = 4096;
  
  public static final int CLSCTX_ENABLE_CODE_DOWNLOAD = 8192;
  
  public static final int CLSCTX_NO_FAILURE_LOG = 16384;
  
  public static final int CLSCTX_DISABLE_AAA = 32768;
  
  public static final int CLSCTX_ENABLE_AAA = 65536;
  
  public static final int CLSCTX_FROM_DEFAULT_CONTEXT = 131072;
  
  public static final int CLSCTX_ACTIVATE_32_BIT_SERVER = 262144;
  
  public static final int CLSCTX_ACTIVATE_64_BIT_SERVER = 524288;
  
  public static final int CLSCTX_ENABLE_CLOAKING = 1048576;
  
  public static final int CLSCTX_APPCONTAINER = 4194304;
  
  public static final int CLSCTX_ACTIVATE_AAA_AS_IU = 8388608;
  
  public static final int CLSCTX_PS_DLL = -2147483648;
  
  public static final int CLSCTX_SERVER = 21;
  
  public static final int CLSCTX_ALL = 7;
  
  public static class VARTYPEByReference extends com.sun.jna.ptr.ByReference {
    public VARTYPEByReference() {
      super(2);
    }
    
    public VARTYPEByReference(WTypes.VARTYPE param1VARTYPE) {
      super(2);
      setValue(param1VARTYPE);
    }
    
    public VARTYPEByReference(short param1Short) {
      super(2);
      getPointer().setShort(0L, param1Short);
    }
    
    public void setValue(WTypes.VARTYPE param1VARTYPE) {
      getPointer().setShort(0L, param1VARTYPE.shortValue());
    }
    
    public WTypes.VARTYPE getValue() {
      return new WTypes.VARTYPE(getPointer().getShort(0L));
    }
  }
  
  public static class VARTYPE extends WinDef.USHORT {
    private static final long serialVersionUID = 1L;
    
    public VARTYPE() {
      this(0);
    }
    
    public VARTYPE(int param1Int) {
      super(param1Int);
    }
  }
  
  public static class LPOLESTR extends PointerType {
    public LPOLESTR() {
      super(Pointer.NULL);
    }
    
    public LPOLESTR(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public LPOLESTR(String param1String) {
      super((Pointer)new Memory((param1String.length() + 1L) * Native.WCHAR_SIZE));
      setValue(param1String);
    }
    
    public void setValue(String param1String) {
      getPointer().setWideString(0L, param1String);
    }
    
    public String getValue() {
      Pointer pointer = getPointer();
      String str = null;
      if (pointer != null)
        str = pointer.getWideString(0L); 
      return str;
    }
    
    public String toString() {
      return getValue();
    }
    
    public static class ByReference extends LPOLESTR implements Structure.ByReference {}
  }
  
  public static class LPWSTR extends PointerType {
    public LPWSTR() {
      super(Pointer.NULL);
    }
    
    public LPWSTR(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public LPWSTR(String param1String) {
      this((Pointer)new Memory((param1String.length() + 1L) * Native.WCHAR_SIZE));
      setValue(param1String);
    }
    
    public void setValue(String param1String) {
      getPointer().setWideString(0L, param1String);
    }
    
    public String getValue() {
      Pointer pointer = getPointer();
      String str = null;
      if (pointer != null)
        str = pointer.getWideString(0L); 
      return str;
    }
    
    public String toString() {
      return getValue();
    }
    
    public static class ByReference extends LPWSTR implements Structure.ByReference {}
  }
  
  public static class LPSTR extends PointerType {
    public LPSTR() {
      super(Pointer.NULL);
    }
    
    public LPSTR(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public LPSTR(String param1String) {
      this((Pointer)new Memory(param1String.length() + 1L));
      setValue(param1String);
    }
    
    public void setValue(String param1String) {
      getPointer().setString(0L, param1String);
    }
    
    public String getValue() {
      Pointer pointer = getPointer();
      String str = null;
      if (pointer != null)
        str = pointer.getString(0L); 
      return str;
    }
    
    public String toString() {
      return getValue();
    }
    
    public static class ByReference extends LPSTR implements Structure.ByReference {}
  }
  
  public static class BSTRByReference extends com.sun.jna.ptr.ByReference {
    public BSTRByReference() {
      super(Native.POINTER_SIZE);
    }
    
    public BSTRByReference(WTypes.BSTR param1BSTR) {
      this();
      setValue(param1BSTR);
    }
    
    public void setValue(WTypes.BSTR param1BSTR) {
      getPointer().setPointer(0L, param1BSTR.getPointer());
    }
    
    public WTypes.BSTR getValue() {
      return new WTypes.BSTR(getPointer().getPointer(0L));
    }
    
    public String getString() {
      WTypes.BSTR bSTR = getValue();
      return (bSTR == null) ? null : bSTR.getValue();
    }
  }
  
  public static class BSTR extends PointerType {
    public BSTR() {
      super(Pointer.NULL);
    }
    
    public BSTR(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    @Deprecated
    public BSTR(String param1String) {
      setValue(param1String);
    }
    
    @Deprecated
    public void setValue(String param1String) {
      if (param1String == null)
        param1String = ""; 
      try {
        byte[] arrayOfByte = param1String.getBytes("UTF-16LE");
        Memory memory = new Memory((4 + arrayOfByte.length + 2));
        memory.clear();
        memory.setInt(0L, arrayOfByte.length);
        memory.write(4L, arrayOfByte, 0, arrayOfByte.length);
        setPointer(memory.share(4L));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new RuntimeException("UTF-16LE charset is not supported", unsupportedEncodingException);
      } 
    }
    
    public String getValue() {
      try {
        Pointer pointer = getPointer();
        if (pointer == null)
          return ""; 
        int i = pointer.getInt(-4L);
        return new String(pointer.getByteArray(0L, i), "UTF-16LE");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new RuntimeException("UTF-16LE charset is not supported", unsupportedEncodingException);
      } 
    }
    
    public String toString() {
      return getValue();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */