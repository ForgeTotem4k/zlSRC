package pro.gravit.launcher.core.serialize;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;
import pro.gravit.utils.helper.IOHelper;

public final class HOutput implements AutoCloseable, Flushable {
  public final OutputStream stream;
  
  public HOutput(OutputStream paramOutputStream) {
    this.stream = Objects.<OutputStream>requireNonNull(paramOutputStream, "stream");
  }
  
  public void close() throws IOException {
    this.stream.close();
  }
  
  public void flush() throws IOException {
    this.stream.flush();
  }
  
  public void writeASCII(String paramString, int paramInt) throws IOException {
    writeByteArray(IOHelper.encodeASCII(paramString), paramInt);
  }
  
  public void writeBigInteger(BigInteger paramBigInteger, int paramInt) throws IOException {
    writeByteArray(paramBigInteger.toByteArray(), paramInt);
  }
  
  public void writeBoolean(boolean paramBoolean) throws IOException {
    writeUnsignedByte(paramBoolean ? 1 : 0);
  }
  
  public void writeByteArray(byte[] paramArrayOfbyte, int paramInt) throws IOException {
    writeLength(paramArrayOfbyte.length, paramInt);
    this.stream.write(paramArrayOfbyte);
  }
  
  public void writeInt(int paramInt) throws IOException {
    writeUnsignedByte(paramInt >>> 24 & 0xFF);
    writeUnsignedByte(paramInt >>> 16 & 0xFF);
    writeUnsignedByte(paramInt >>> 8 & 0xFF);
    writeUnsignedByte(paramInt & 0xFF);
  }
  
  public void writeLength(int paramInt1, int paramInt2) throws IOException {
    IOHelper.verifyLength(paramInt1, paramInt2);
    if (paramInt2 >= 0)
      writeVarInt(paramInt1); 
  }
  
  public void writeLong(long paramLong) throws IOException {
    writeInt((int)(paramLong >> 32L));
    writeInt((int)paramLong);
  }
  
  public void writeShort(short paramShort) throws IOException {
    writeUnsignedByte(paramShort >>> 8 & 0xFF);
    writeUnsignedByte(paramShort & 0xFF);
  }
  
  public void writeString(String paramString, int paramInt) throws IOException {
    writeByteArray(IOHelper.encode(paramString), paramInt);
  }
  
  public void writeUnsignedByte(int paramInt) throws IOException {
    this.stream.write(paramInt);
  }
  
  public void writeUUID(UUID paramUUID) throws IOException {
    writeLong(paramUUID.getMostSignificantBits());
    writeLong(paramUUID.getLeastSignificantBits());
  }
  
  public void writeVarInt(int paramInt) throws IOException {
    while ((paramInt & 0xFFFFFFFFFFFFFF80L) != 0L) {
      writeUnsignedByte(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    } 
    writeUnsignedByte(paramInt);
  }
  
  public void writeVarLong(long paramLong) throws IOException {
    while ((paramLong & 0xFFFFFFFFFFFFFF80L) != 0L) {
      writeUnsignedByte((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7L;
    } 
    writeUnsignedByte((int)paramLong);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\HOutput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */