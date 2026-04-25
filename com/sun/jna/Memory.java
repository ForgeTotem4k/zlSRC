package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.Closeable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory extends Pointer implements Closeable {
  private static final Map<Long, Reference<Memory>> allocatedMemory = new ConcurrentHashMap<>();
  
  private static final WeakMemoryHolder buffers = new WeakMemoryHolder();
  
  private final Cleaner.Cleanable cleanable;
  
  protected long size;
  
  public static void purge() {
    buffers.clean();
  }
  
  public static void disposeAll() {
    ArrayList arrayList = new ArrayList(allocatedMemory.values());
    for (Reference<Memory> reference : (Iterable<Reference<Memory>>)arrayList) {
      Memory memory = reference.get();
      if (memory != null)
        memory.close(); 
    } 
  }
  
  public Memory(long paramLong) {
    this.size = paramLong;
    if (paramLong <= 0L)
      throw new IllegalArgumentException("Allocation size must be greater than zero"); 
    this.peer = malloc(paramLong);
    if (this.peer == 0L)
      throw new OutOfMemoryError("Cannot allocate " + paramLong + " bytes"); 
    allocatedMemory.put(Long.valueOf(this.peer), new WeakReference<>(this));
    this.cleanable = Cleaner.getCleaner().register(this, new MemoryDisposer(this.peer));
  }
  
  protected Memory() {
    this.cleanable = null;
  }
  
  public Pointer share(long paramLong) {
    return share(paramLong, size() - paramLong);
  }
  
  public Pointer share(long paramLong1, long paramLong2) {
    boundsCheck(paramLong1, paramLong2);
    return new SharedMemory(paramLong1, paramLong2);
  }
  
  public Memory align(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Byte boundary must be positive: " + paramInt); 
    for (byte b = 0; b < 32; b++) {
      if (paramInt == 1 << b) {
        long l = paramInt - 1L ^ 0xFFFFFFFFFFFFFFFFL;
        if ((this.peer & l) != this.peer) {
          long l1 = this.peer + paramInt - 1L & l;
          long l2 = this.peer + this.size - l1;
          if (l2 <= 0L)
            throw new IllegalArgumentException("Insufficient memory to align to the requested boundary"); 
          return (Memory)share(l1 - this.peer, l2);
        } 
        return this;
      } 
    } 
    throw new IllegalArgumentException("Byte boundary must be a power of two");
  }
  
  public void close() {
    this.peer = 0L;
    if (this.cleanable != null)
      this.cleanable.clean(); 
  }
  
  @Deprecated
  protected void dispose() {
    close();
  }
  
  public void clear() {
    clear(this.size);
  }
  
  public boolean valid() {
    return (this.peer != 0L);
  }
  
  public long size() {
    return this.size;
  }
  
  protected void boundsCheck(long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      throw new IndexOutOfBoundsException("Invalid offset: " + paramLong1); 
    if (paramLong1 + paramLong2 > this.size) {
      String str = "Bounds exceeds available space : size=" + this.size + ", offset=" + (paramLong1 + paramLong2);
      throw new IndexOutOfBoundsException(str);
    } 
  }
  
  public void read(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 1L);
    super.read(paramLong, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 2L);
    super.read(paramLong, paramArrayOfshort, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, (paramInt2 * Native.WCHAR_SIZE));
    super.read(paramLong, paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 4L);
    super.read(paramLong, paramArrayOfint, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, long[] paramArrayOflong, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 8L);
    super.read(paramLong, paramArrayOflong, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 4L);
    super.read(paramLong, paramArrayOffloat, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 8L);
    super.read(paramLong, paramArrayOfdouble, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, Pointer[] paramArrayOfPointer, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, (paramInt2 * Native.POINTER_SIZE));
    super.read(paramLong, paramArrayOfPointer, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 1L);
    super.write(paramLong, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 2L);
    super.write(paramLong, paramArrayOfshort, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, (paramInt2 * Native.WCHAR_SIZE));
    super.write(paramLong, paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 4L);
    super.write(paramLong, paramArrayOfint, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, long[] paramArrayOflong, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 8L);
    super.write(paramLong, paramArrayOflong, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 4L);
    super.write(paramLong, paramArrayOffloat, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, paramInt2 * 8L);
    super.write(paramLong, paramArrayOfdouble, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, Pointer[] paramArrayOfPointer, int paramInt1, int paramInt2) {
    boundsCheck(paramLong, (paramInt2 * Native.POINTER_SIZE));
    super.write(paramLong, paramArrayOfPointer, paramInt1, paramInt2);
  }
  
  public byte getByte(long paramLong) {
    boundsCheck(paramLong, 1L);
    return super.getByte(paramLong);
  }
  
  public char getChar(long paramLong) {
    boundsCheck(paramLong, Native.WCHAR_SIZE);
    return super.getChar(paramLong);
  }
  
  public short getShort(long paramLong) {
    boundsCheck(paramLong, 2L);
    return super.getShort(paramLong);
  }
  
  public int getInt(long paramLong) {
    boundsCheck(paramLong, 4L);
    return super.getInt(paramLong);
  }
  
  public long getLong(long paramLong) {
    boundsCheck(paramLong, 8L);
    return super.getLong(paramLong);
  }
  
  public float getFloat(long paramLong) {
    boundsCheck(paramLong, 4L);
    return super.getFloat(paramLong);
  }
  
  public double getDouble(long paramLong) {
    boundsCheck(paramLong, 8L);
    return super.getDouble(paramLong);
  }
  
  public Pointer getPointer(long paramLong) {
    boundsCheck(paramLong, Native.POINTER_SIZE);
    return shareReferenceIfInBounds(super.getPointer(paramLong));
  }
  
  public ByteBuffer getByteBuffer(long paramLong1, long paramLong2) {
    boundsCheck(paramLong1, paramLong2);
    ByteBuffer byteBuffer = super.getByteBuffer(paramLong1, paramLong2);
    buffers.put(byteBuffer, this);
    return byteBuffer;
  }
  
  public String getString(long paramLong, String paramString) {
    boundsCheck(paramLong, 0L);
    return super.getString(paramLong, paramString);
  }
  
  public String getWideString(long paramLong) {
    boundsCheck(paramLong, 0L);
    return super.getWideString(paramLong);
  }
  
  public void setByte(long paramLong, byte paramByte) {
    boundsCheck(paramLong, 1L);
    super.setByte(paramLong, paramByte);
  }
  
  public void setChar(long paramLong, char paramChar) {
    boundsCheck(paramLong, Native.WCHAR_SIZE);
    super.setChar(paramLong, paramChar);
  }
  
  public void setShort(long paramLong, short paramShort) {
    boundsCheck(paramLong, 2L);
    super.setShort(paramLong, paramShort);
  }
  
  public void setInt(long paramLong, int paramInt) {
    boundsCheck(paramLong, 4L);
    super.setInt(paramLong, paramInt);
  }
  
  public void setLong(long paramLong1, long paramLong2) {
    boundsCheck(paramLong1, 8L);
    super.setLong(paramLong1, paramLong2);
  }
  
  public void setFloat(long paramLong, float paramFloat) {
    boundsCheck(paramLong, 4L);
    super.setFloat(paramLong, paramFloat);
  }
  
  public void setDouble(long paramLong, double paramDouble) {
    boundsCheck(paramLong, 8L);
    super.setDouble(paramLong, paramDouble);
  }
  
  public void setPointer(long paramLong, Pointer paramPointer) {
    boundsCheck(paramLong, Native.POINTER_SIZE);
    super.setPointer(paramLong, paramPointer);
  }
  
  public void setString(long paramLong, String paramString1, String paramString2) {
    boundsCheck(paramLong, (Native.getBytes(paramString1, paramString2)).length + 1L);
    super.setString(paramLong, paramString1, paramString2);
  }
  
  public void setWideString(long paramLong, String paramString) {
    boundsCheck(paramLong, (paramString.length() + 1L) * Native.WCHAR_SIZE);
    super.setWideString(paramLong, paramString);
  }
  
  public String toString() {
    return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
  }
  
  protected static void free(long paramLong) {
    if (paramLong != 0L)
      Native.free(paramLong); 
  }
  
  protected static long malloc(long paramLong) {
    return Native.malloc(paramLong);
  }
  
  public String dump() {
    return dump(0L, (int)size());
  }
  
  private Pointer shareReferenceIfInBounds(Pointer paramPointer) {
    if (paramPointer == null)
      return null; 
    long l = paramPointer.peer - this.peer;
    return (l >= 0L && l < this.size) ? share(l) : paramPointer;
  }
  
  private static final class MemoryDisposer implements Runnable {
    private long peer;
    
    public MemoryDisposer(long param1Long) {
      this.peer = param1Long;
    }
    
    public synchronized void run() {
      try {
        Memory.free(this.peer);
      } finally {
        Memory.allocatedMemory.remove(Long.valueOf(this.peer));
        this.peer = 0L;
      } 
    }
  }
  
  private class SharedMemory extends Memory {
    public SharedMemory(long param1Long1, long param1Long2) {
      this.size = param1Long2;
      this.peer = Memory.this.peer + param1Long1;
    }
    
    protected synchronized void dispose() {
      this.peer = 0L;
    }
    
    protected void boundsCheck(long param1Long1, long param1Long2) {
      Memory.this.boundsCheck(this.peer - Memory.this.peer + param1Long1, param1Long2);
    }
    
    public String toString() {
      return super.toString() + " (shared from " + Memory.this.toString() + ")";
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Memory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */