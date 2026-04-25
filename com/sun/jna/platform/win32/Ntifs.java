package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;

public interface Ntifs extends WinDef, BaseTSD {
  public static final int MAXIMUM_REPARSE_DATA_BUFFER_SIZE = 16384;
  
  public static final int REPARSE_BUFFER_HEADER_SIZE = 8;
  
  public static final int SYMLINK_FLAG_RELATIVE = 1;
  
  @FieldOrder({"ReparseTag", "ReparseDataLength", "Reserved", "u"})
  public static class REPARSE_DATA_BUFFER extends Structure {
    public int ReparseTag = 0;
    
    public short ReparseDataLength = 0;
    
    public short Reserved = 0;
    
    public REPARSE_UNION u;
    
    public static int sizeOf() {
      return Native.getNativeSize(REPARSE_DATA_BUFFER.class, null);
    }
    
    public int getSize() {
      return 8 + this.ReparseDataLength;
    }
    
    public REPARSE_DATA_BUFFER() {}
    
    public REPARSE_DATA_BUFFER(int param1Int, short param1Short) {
      this.ReparseTag = param1Int;
      this.Reserved = param1Short;
      this.ReparseDataLength = 0;
      write();
    }
    
    public REPARSE_DATA_BUFFER(int param1Int, short param1Short, Ntifs.SymbolicLinkReparseBuffer param1SymbolicLinkReparseBuffer) {
      this.ReparseTag = param1Int;
      this.Reserved = param1Short;
      this.ReparseDataLength = (short)param1SymbolicLinkReparseBuffer.size();
      this.u.setType(Ntifs.SymbolicLinkReparseBuffer.class);
      this.u.symLinkReparseBuffer = param1SymbolicLinkReparseBuffer;
      write();
    }
    
    public REPARSE_DATA_BUFFER(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public void read() {
      super.read();
      switch (this.ReparseTag) {
        default:
          this.u.setType(Ntifs.GenericReparseBuffer.class);
          break;
        case -1610612733:
          this.u.setType(Ntifs.MountPointReparseBuffer.class);
          break;
        case -1610612724:
          this.u.setType(Ntifs.SymbolicLinkReparseBuffer.class);
          break;
      } 
      this.u.read();
    }
    
    public static class REPARSE_UNION extends Union {
      public Ntifs.SymbolicLinkReparseBuffer symLinkReparseBuffer;
      
      public Ntifs.MountPointReparseBuffer mountPointReparseBuffer;
      
      public Ntifs.GenericReparseBuffer genericReparseBuffer;
      
      public REPARSE_UNION() {}
      
      public REPARSE_UNION(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public static class ByReference extends REPARSE_UNION implements Structure.ByReference {}
    }
    
    public static class ByReference extends REPARSE_DATA_BUFFER implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"DataBuffer"})
  public static class GenericReparseBuffer extends Structure {
    public byte[] DataBuffer = new byte[16384];
    
    public static int sizeOf() {
      return Native.getNativeSize(GenericReparseBuffer.class, null);
    }
    
    public GenericReparseBuffer() {}
    
    public GenericReparseBuffer(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public GenericReparseBuffer(String param1String) {
      this.DataBuffer = param1String.getBytes();
      write();
    }
    
    public static class ByReference extends GenericReparseBuffer implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "PathBuffer"})
  public static class MountPointReparseBuffer extends Structure {
    public short SubstituteNameOffset = 0;
    
    public short SubstituteNameLength = 0;
    
    public short PrintNameOffset = 0;
    
    public short PrintNameLength = 0;
    
    public char[] PathBuffer = new char[8192];
    
    public static int sizeOf() {
      return Native.getNativeSize(MountPointReparseBuffer.class, null);
    }
    
    public MountPointReparseBuffer() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public MountPointReparseBuffer(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.UNICODE);
      read();
    }
    
    public MountPointReparseBuffer(String param1String1, String param1String2) {
      String str = param1String1 + param1String2;
      this.PathBuffer = str.toCharArray();
      this.SubstituteNameOffset = 0;
      this.SubstituteNameLength = (short)param1String1.length();
      this.PrintNameOffset = (short)(param1String1.length() * 2);
      this.PrintNameLength = (short)(param1String2.length() * 2);
      write();
    }
    
    public MountPointReparseBuffer(short param1Short1, short param1Short2, short param1Short3, short param1Short4, String param1String) {
      this.SubstituteNameOffset = param1Short1;
      this.SubstituteNameLength = param1Short2;
      this.PrintNameOffset = param1Short3;
      this.PrintNameLength = param1Short4;
      this.PathBuffer = param1String.toCharArray();
      write();
    }
    
    public static class ByReference extends MountPointReparseBuffer implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "Flags", "PathBuffer"})
  public static class SymbolicLinkReparseBuffer extends Structure {
    public short SubstituteNameOffset = 0;
    
    public short SubstituteNameLength = 0;
    
    public short PrintNameOffset = 0;
    
    public short PrintNameLength = 0;
    
    public int Flags = 0;
    
    public char[] PathBuffer = new char[8192];
    
    public static int sizeOf() {
      return Native.getNativeSize(Ntifs.MountPointReparseBuffer.class, null);
    }
    
    public SymbolicLinkReparseBuffer() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public SymbolicLinkReparseBuffer(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.UNICODE);
      read();
    }
    
    public SymbolicLinkReparseBuffer(String param1String1, String param1String2, int param1Int) {
      String str = param1String1 + param1String2;
      this.PathBuffer = str.toCharArray();
      this.SubstituteNameOffset = 0;
      this.SubstituteNameLength = (short)(param1String1.length() * 2);
      this.PrintNameOffset = (short)(param1String1.length() * 2);
      this.PrintNameLength = (short)(param1String2.length() * 2);
      this.Flags = param1Int;
      write();
    }
    
    public SymbolicLinkReparseBuffer(short param1Short1, short param1Short2, short param1Short3, short param1Short4, int param1Int, String param1String) {
      this.SubstituteNameOffset = param1Short1;
      this.SubstituteNameLength = param1Short2;
      this.PrintNameOffset = param1Short3;
      this.PrintNameLength = param1Short4;
      this.Flags = param1Int;
      this.PathBuffer = param1String.toCharArray();
      write();
    }
    
    public String getPrintName() {
      return String.copyValueOf(this.PathBuffer, this.PrintNameOffset / 2, this.PrintNameLength / 2);
    }
    
    public String getSubstituteName() {
      return String.copyValueOf(this.PathBuffer, this.SubstituteNameOffset / 2, this.SubstituteNameLength / 2);
    }
    
    public static class ByReference extends SymbolicLinkReparseBuffer implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Ntifs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */