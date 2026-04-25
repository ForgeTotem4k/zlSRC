package pro.gravit.launcher.core.serialize;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;
import pro.gravit.utils.helper.IOHelper;

public final class HInput implements AutoCloseable {
  public final InputStream stream;
  
  public HInput(byte[] paramArrayOfbyte) {
    this.stream = new ByteArrayInputStream(paramArrayOfbyte);
  }
  
  public HInput(InputStream paramInputStream) {
    this.stream = Objects.<InputStream>requireNonNull(paramInputStream, "stream");
  }
  
  public void close() throws IOException {
    this.stream.close();
  }
  
  public String readASCII(int paramInt) throws IOException {
    return IOHelper.decodeASCII(readByteArray(paramInt));
  }
  
  public BigInteger readBigInteger(int paramInt) throws IOException {
    return new BigInteger(readByteArray(paramInt));
  }
  
  public boolean readBoolean() throws IOException {
    int i = readUnsignedByte();
    switch (i) {
      case 0:
      
      case 1:
      
    } 
    throw new IOException("Invalid boolean state: " + i);
  }
  
  public byte[] readByteArray(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[readLength(paramInt)];
    IOHelper.read(this.stream, arrayOfByte);
    return arrayOfByte;
  }
  
  public int readInt() throws IOException {
    return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
  }
  
  public int readLength(int paramInt) throws IOException {
    return (paramInt < 0) ? -paramInt : IOHelper.verifyLength(readVarInt(), paramInt);
  }
  
  public long readLong() throws IOException {
    return readInt() << 32L | readInt() & 0xFFFFFFFFL;
  }
  
  public short readShort() throws IOException {
    return (short)((readUnsignedByte() << 8) + readUnsignedByte());
  }
  
  public String readString(int paramInt) throws IOException {
    return IOHelper.decode(readByteArray(paramInt));
  }
  
  public int readUnsignedByte() throws IOException {
    int i = this.stream.read();
    if (i < 0)
      throw new EOFException("readUnsignedByte"); 
    return i;
  }
  
  public int readUnsignedShort() throws IOException {
    return Short.toUnsignedInt(readShort());
  }
  
  public UUID readUUID() throws IOException {
    return new UUID(readLong(), readLong());
  }
  
  public int readVarInt() throws IOException {
    byte b = 0;
    int i = 0;
    while (b < 32) {
      int j = readUnsignedByte();
      i |= (j & 0x7F) << b;
      if ((j & 0x80) == 0)
        return i; 
      b += 7;
    } 
    throw new IOException("VarInt too big");
  }
  
  public long readVarLong() throws IOException {
    byte b = 0;
    long l = 0L;
    while (b < 64) {
      int i = readUnsignedByte();
      l |= (i & 0x7F) << b;
      if ((i & 0x80) == 0)
        return l; 
      b += 7;
    } 
    throw new IOException("VarLong too big");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\HInput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */