package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.security.SecureRandom;
import java.util.Arrays;

public interface Guid {
  public static final IID IID_NULL = new IID();
  
  public static class IID extends GUID {
    public IID() {}
    
    public IID(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public IID(String param1String) {
      super(param1String);
    }
    
    public IID(byte[] param1ArrayOfbyte) {
      super(param1ArrayOfbyte);
    }
    
    public IID(Guid.GUID param1GUID) {
      this(param1GUID.toGuidString());
    }
  }
  
  public static class REFIID extends PointerType {
    public REFIID() {}
    
    public REFIID(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public REFIID(Guid.IID param1IID) {
      super(param1IID.getPointer());
    }
    
    public void setValue(Guid.IID param1IID) {
      setPointer(param1IID.getPointer());
    }
    
    public Guid.IID getValue() {
      return new Guid.IID(getPointer());
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (this == param1Object)
        return true; 
      if (getClass() != param1Object.getClass())
        return false; 
      REFIID rEFIID = (REFIID)param1Object;
      return getValue().equals(rEFIID.getValue());
    }
    
    public int hashCode() {
      return getValue().hashCode();
    }
  }
  
  public static class CLSID extends GUID {
    public CLSID() {}
    
    public CLSID(String param1String) {
      super(param1String);
    }
    
    public CLSID(Guid.GUID param1GUID) {
      super(param1GUID);
    }
    
    public static class ByReference extends Guid.GUID {
      public ByReference() {}
      
      public ByReference(Guid.GUID param2GUID) {
        super(param2GUID);
      }
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"Data1", "Data2", "Data3", "Data4"})
  public static class GUID extends Structure {
    public int Data1;
    
    public short Data2;
    
    public short Data3;
    
    public byte[] Data4 = new byte[8];
    
    public GUID() {}
    
    public GUID(GUID param1GUID) {
      this.Data1 = param1GUID.Data1;
      this.Data2 = param1GUID.Data2;
      this.Data3 = param1GUID.Data3;
      this.Data4 = param1GUID.Data4;
      writeFieldsToMemory();
    }
    
    public GUID(String param1String) {
      this(fromString(param1String));
    }
    
    public GUID(byte[] param1ArrayOfbyte) {
      this(fromBinary(param1ArrayOfbyte));
    }
    
    public GUID(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (this == param1Object)
        return true; 
      if (getClass() != param1Object.getClass())
        return false; 
      GUID gUID = (GUID)param1Object;
      return (this.Data1 == gUID.Data1 && this.Data2 == gUID.Data2 && this.Data3 == gUID.Data3 && Arrays.equals(this.Data4, gUID.Data4));
    }
    
    public int hashCode() {
      return this.Data1 + this.Data2 & 65535 + this.Data3 & 65535 + Arrays.hashCode(this.Data4);
    }
    
    public static GUID fromBinary(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte.length != 16)
        throw new IllegalArgumentException("Invalid data length: " + param1ArrayOfbyte.length); 
      GUID gUID = new GUID();
      long l = (param1ArrayOfbyte[0] & 0xFF);
      l <<= 8L;
      l |= (param1ArrayOfbyte[1] & 0xFF);
      l <<= 8L;
      l |= (param1ArrayOfbyte[2] & 0xFF);
      l <<= 8L;
      l |= (param1ArrayOfbyte[3] & 0xFF);
      gUID.Data1 = (int)l;
      int i = param1ArrayOfbyte[4] & 0xFF;
      i <<= 8;
      i |= param1ArrayOfbyte[5] & 0xFF;
      gUID.Data2 = (short)i;
      int j = param1ArrayOfbyte[6] & 0xFF;
      j <<= 8;
      j |= param1ArrayOfbyte[7] & 0xFF;
      gUID.Data3 = (short)j;
      gUID.Data4[0] = param1ArrayOfbyte[8];
      gUID.Data4[1] = param1ArrayOfbyte[9];
      gUID.Data4[2] = param1ArrayOfbyte[10];
      gUID.Data4[3] = param1ArrayOfbyte[11];
      gUID.Data4[4] = param1ArrayOfbyte[12];
      gUID.Data4[5] = param1ArrayOfbyte[13];
      gUID.Data4[6] = param1ArrayOfbyte[14];
      gUID.Data4[7] = param1ArrayOfbyte[15];
      gUID.writeFieldsToMemory();
      return gUID;
    }
    
    public static GUID fromString(String param1String) {
      byte b1 = 0;
      char[] arrayOfChar1 = new char[32];
      char[] arrayOfChar2 = param1String.toCharArray();
      byte[] arrayOfByte = new byte[16];
      GUID gUID = new GUID();
      if (param1String.length() > 38)
        throw new IllegalArgumentException("Invalid guid length: " + param1String.length()); 
      byte b2;
      for (b2 = 0; b2 < arrayOfChar2.length; b2++) {
        if (arrayOfChar2[b2] != '{' && arrayOfChar2[b2] != '-' && arrayOfChar2[b2] != '}')
          arrayOfChar1[b1++] = arrayOfChar2[b2]; 
      } 
      for (b2 = 0; b2 < 32; b2 += 2)
        arrayOfByte[b2 / 2] = (byte)((Character.digit(arrayOfChar1[b2], 16) << 4) + Character.digit(arrayOfChar1[b2 + 1], 16) & 0xFF); 
      if (arrayOfByte.length != 16)
        throw new IllegalArgumentException("Invalid data length: " + arrayOfByte.length); 
      long l = (arrayOfByte[0] & 0xFF);
      l <<= 8L;
      l |= (arrayOfByte[1] & 0xFF);
      l <<= 8L;
      l |= (arrayOfByte[2] & 0xFF);
      l <<= 8L;
      l |= (arrayOfByte[3] & 0xFF);
      gUID.Data1 = (int)l;
      int i = arrayOfByte[4] & 0xFF;
      i <<= 8;
      i |= arrayOfByte[5] & 0xFF;
      gUID.Data2 = (short)i;
      int j = arrayOfByte[6] & 0xFF;
      j <<= 8;
      j |= arrayOfByte[7] & 0xFF;
      gUID.Data3 = (short)j;
      gUID.Data4[0] = arrayOfByte[8];
      gUID.Data4[1] = arrayOfByte[9];
      gUID.Data4[2] = arrayOfByte[10];
      gUID.Data4[3] = arrayOfByte[11];
      gUID.Data4[4] = arrayOfByte[12];
      gUID.Data4[5] = arrayOfByte[13];
      gUID.Data4[6] = arrayOfByte[14];
      gUID.Data4[7] = arrayOfByte[15];
      gUID.writeFieldsToMemory();
      return gUID;
    }
    
    public static GUID newGuid() {
      SecureRandom secureRandom = new SecureRandom();
      byte[] arrayOfByte = new byte[16];
      secureRandom.nextBytes(arrayOfByte);
      arrayOfByte[6] = (byte)(arrayOfByte[6] & 0xF);
      arrayOfByte[6] = (byte)(arrayOfByte[6] | 0x40);
      arrayOfByte[8] = (byte)(arrayOfByte[8] & 0x3F);
      arrayOfByte[8] = (byte)(arrayOfByte[8] | 0x80);
      return new GUID(arrayOfByte);
    }
    
    public byte[] toByteArray() {
      byte[] arrayOfByte1 = new byte[16];
      byte[] arrayOfByte2 = new byte[4];
      arrayOfByte2[0] = (byte)(this.Data1 >> 24);
      arrayOfByte2[1] = (byte)(this.Data1 >> 16);
      arrayOfByte2[2] = (byte)(this.Data1 >> 8);
      arrayOfByte2[3] = (byte)(this.Data1 >> 0);
      byte[] arrayOfByte3 = new byte[4];
      arrayOfByte3[0] = (byte)(this.Data2 >> 24);
      arrayOfByte3[1] = (byte)(this.Data2 >> 16);
      arrayOfByte3[2] = (byte)(this.Data2 >> 8);
      arrayOfByte3[3] = (byte)(this.Data2 >> 0);
      byte[] arrayOfByte4 = new byte[4];
      arrayOfByte4[0] = (byte)(this.Data3 >> 24);
      arrayOfByte4[1] = (byte)(this.Data3 >> 16);
      arrayOfByte4[2] = (byte)(this.Data3 >> 8);
      arrayOfByte4[3] = (byte)(this.Data3 >> 0);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, 4);
      System.arraycopy(arrayOfByte3, 2, arrayOfByte1, 4, 2);
      System.arraycopy(arrayOfByte4, 2, arrayOfByte1, 6, 2);
      System.arraycopy(this.Data4, 0, arrayOfByte1, 8, 8);
      return arrayOfByte1;
    }
    
    public String toGuidString() {
      String str = "0123456789ABCDEF";
      byte[] arrayOfByte = toByteArray();
      StringBuilder stringBuilder = new StringBuilder(2 * arrayOfByte.length);
      stringBuilder.append("{");
      for (byte b = 0; b < arrayOfByte.length; b++) {
        char c1 = "0123456789ABCDEF".charAt((arrayOfByte[b] & 0xF0) >> 4);
        char c2 = "0123456789ABCDEF".charAt(arrayOfByte[b] & 0xF);
        stringBuilder.append(c1).append(c2);
        if (b == 3 || b == 5 || b == 7 || b == 9)
          stringBuilder.append("-"); 
      } 
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    
    protected void writeFieldsToMemory() {
      for (String str : getFieldOrder())
        writeField(str); 
    }
    
    public static class ByReference extends GUID implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Guid.GUID param2GUID) {
        super(param2GUID.getPointer());
        this.Data1 = param2GUID.Data1;
        this.Data2 = param2GUID.Data2;
        this.Data3 = param2GUID.Data3;
        this.Data4 = param2GUID.Data4;
      }
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
    
    public static class ByValue extends GUID implements Structure.ByValue {
      public ByValue() {}
      
      public ByValue(Guid.GUID param2GUID) {
        super(param2GUID.getPointer());
        this.Data1 = param2GUID.Data1;
        this.Data2 = param2GUID.Data2;
        this.Data3 = param2GUID.Data3;
        this.Data4 = param2GUID.Data4;
      }
      
      public ByValue(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Guid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */