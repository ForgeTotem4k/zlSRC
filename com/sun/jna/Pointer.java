package com.sun.jna;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Pointer {
  public static final Pointer NULL = null;
  
  protected long peer;
  
  public static final Pointer createConstant(long paramLong) {
    return new Opaque(paramLong);
  }
  
  public static final Pointer createConstant(int paramInt) {
    return new Opaque(paramInt & 0xFFFFFFFFL);
  }
  
  Pointer() {}
  
  public Pointer(long paramLong) {
    this.peer = paramLong;
  }
  
  public Pointer share(long paramLong) {
    return share(paramLong, 0L);
  }
  
  public Pointer share(long paramLong1, long paramLong2) {
    return (paramLong1 == 0L) ? this : new Pointer(this.peer + paramLong1);
  }
  
  public void clear(long paramLong) {
    setMemory(0L, paramLong, (byte)0);
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == this) ? true : ((paramObject == null) ? false : ((paramObject instanceof Pointer && ((Pointer)paramObject).peer == this.peer)));
  }
  
  public int hashCode() {
    return (int)((this.peer >>> 32L) + (this.peer & 0xFFFFFFFFL));
  }
  
  public long indexOf(long paramLong, byte paramByte) {
    return Native.indexOf(this, this.peer, paramLong, paramByte);
  }
  
  public void read(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOfshort, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOfint, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, long[] paramArrayOflong, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOflong, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOffloat, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    Native.read(this, this.peer, paramLong, paramArrayOfdouble, paramInt1, paramInt2);
  }
  
  public void read(long paramLong, Pointer[] paramArrayOfPointer, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramInt2; b++) {
      Pointer pointer1 = getPointer(paramLong + (b * Native.POINTER_SIZE));
      Pointer pointer2 = paramArrayOfPointer[b + paramInt1];
      if (pointer2 == null || pointer1 == null || pointer1.peer != pointer2.peer)
        paramArrayOfPointer[b + paramInt1] = pointer1; 
    } 
  }
  
  public void write(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, short[] paramArrayOfshort, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOfshort, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOfint, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, long[] paramArrayOflong, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOflong, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOffloat, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    Native.write(this, this.peer, paramLong, paramArrayOfdouble, paramInt1, paramInt2);
  }
  
  public void write(long paramLong, Pointer[] paramArrayOfPointer, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramInt2; b++)
      setPointer(paramLong + (b * Native.POINTER_SIZE), paramArrayOfPointer[paramInt1 + b]); 
  }
  
  Object getValue(long paramLong, Class<?> paramClass, Object paramObject) {
    Object object = null;
    if (Structure.class.isAssignableFrom(paramClass)) {
      Structure structure = (Structure)paramObject;
      if (Structure.ByReference.class.isAssignableFrom(paramClass)) {
        structure = Structure.updateStructureByReference(paramClass, structure, getPointer(paramLong));
      } else {
        structure.useMemory(this, (int)paramLong, true);
        structure.read();
      } 
      object = structure;
    } else if (paramClass == boolean.class || paramClass == Boolean.class) {
      Boolean bool = Function.valueOf((getInt(paramLong) != 0));
    } else if (paramClass == byte.class || paramClass == Byte.class) {
      Byte byte_ = Byte.valueOf(getByte(paramLong));
    } else if (paramClass == short.class || paramClass == Short.class) {
      Short short_ = Short.valueOf(getShort(paramLong));
    } else if (paramClass == char.class || paramClass == Character.class) {
      Character character = Character.valueOf(getChar(paramLong));
    } else if (paramClass == int.class || paramClass == Integer.class) {
      Integer integer = Integer.valueOf(getInt(paramLong));
    } else if (paramClass == long.class || paramClass == Long.class) {
      Long long_ = Long.valueOf(getLong(paramLong));
    } else if (paramClass == float.class || paramClass == Float.class) {
      Float float_ = Float.valueOf(getFloat(paramLong));
    } else if (paramClass == double.class || paramClass == Double.class) {
      Double double_ = Double.valueOf(getDouble(paramLong));
    } else if (Pointer.class.isAssignableFrom(paramClass)) {
      Pointer pointer = getPointer(paramLong);
      if (pointer != null) {
        Pointer pointer1 = (paramObject instanceof Pointer) ? (Pointer)paramObject : null;
        if (pointer1 == null || pointer.peer != pointer1.peer) {
          Pointer pointer2 = pointer;
        } else {
          Pointer pointer2 = pointer1;
        } 
      } 
    } else if (paramClass == String.class) {
      Pointer pointer = getPointer(paramLong);
      String str = (pointer != null) ? pointer.getString(0L) : null;
    } else if (paramClass == WString.class) {
      Pointer pointer = getPointer(paramLong);
      WString wString = (pointer != null) ? new WString(pointer.getWideString(0L)) : null;
    } else if (Callback.class.isAssignableFrom(paramClass)) {
      Pointer pointer = getPointer(paramLong);
      if (pointer == null) {
        object = null;
      } else {
        Callback callback2 = (Callback)paramObject;
        Pointer pointer1 = CallbackReference.getFunctionPointer(callback2);
        if (!pointer.equals(pointer1))
          callback2 = CallbackReference.getCallback(paramClass, pointer); 
        Callback callback1 = callback2;
      } 
    } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(paramClass)) {
      Pointer pointer = getPointer(paramLong);
      if (pointer == null) {
        object = null;
      } else {
        Pointer pointer1 = (paramObject == null) ? null : Native.getDirectBufferPointer((Buffer)paramObject);
        if (pointer1 == null || !pointer1.equals(pointer))
          throw new IllegalStateException("Can't autogenerate a direct buffer on memory read"); 
        Object object1 = paramObject;
      } 
    } else if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMapped nativeMapped = (NativeMapped)paramObject;
      if (nativeMapped != null) {
        Object object2 = getValue(paramLong, nativeMapped.nativeType(), null);
        Object object1 = nativeMapped.fromNative(object2, new FromNativeContext(paramClass));
        if (nativeMapped.equals(object1))
          object1 = nativeMapped; 
      } else {
        NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
        Object object2 = getValue(paramLong, nativeMappedConverter.nativeType(), null);
        Object object1 = nativeMappedConverter.fromNative(object2, new FromNativeContext(paramClass));
      } 
    } else if (paramClass.isArray()) {
      object = paramObject;
      if (object == null)
        throw new IllegalStateException("Need an initialized array"); 
      readArray(paramLong, object, paramClass.getComponentType());
    } else {
      throw new IllegalArgumentException("Reading \"" + paramClass + "\" from memory is not supported");
    } 
    return object;
  }
  
  private void readArray(long paramLong, Object paramObject, Class<?> paramClass) {
    int i = 0;
    i = Array.getLength(paramObject);
    Object object = paramObject;
    if (paramClass == byte.class) {
      read(paramLong, (byte[])object, 0, i);
    } else if (paramClass == short.class) {
      read(paramLong, (short[])object, 0, i);
    } else if (paramClass == char.class) {
      read(paramLong, (char[])object, 0, i);
    } else if (paramClass == int.class) {
      read(paramLong, (int[])object, 0, i);
    } else if (paramClass == long.class) {
      read(paramLong, (long[])object, 0, i);
    } else if (paramClass == float.class) {
      read(paramLong, (float[])object, 0, i);
    } else if (paramClass == double.class) {
      read(paramLong, (double[])object, 0, i);
    } else if (Pointer.class.isAssignableFrom(paramClass)) {
      read(paramLong, (Pointer[])object, 0, i);
    } else if (Structure.class.isAssignableFrom(paramClass)) {
      Structure[] arrayOfStructure = (Structure[])object;
      if (Structure.ByReference.class.isAssignableFrom(paramClass)) {
        Pointer[] arrayOfPointer = getPointerArray(paramLong, arrayOfStructure.length);
        for (byte b = 0; b < arrayOfStructure.length; b++)
          arrayOfStructure[b] = Structure.updateStructureByReference(paramClass, arrayOfStructure[b], arrayOfPointer[b]); 
      } else {
        Structure structure = arrayOfStructure[0];
        if (structure == null) {
          structure = Structure.newInstance(paramClass, share(paramLong));
          structure.conditionalAutoRead();
          arrayOfStructure[0] = structure;
        } else {
          structure.useMemory(this, (int)paramLong, true);
          structure.read();
        } 
        Structure[] arrayOfStructure1 = structure.toArray(arrayOfStructure.length);
        for (byte b = 1; b < arrayOfStructure.length; b++) {
          if (arrayOfStructure[b] == null) {
            arrayOfStructure[b] = arrayOfStructure1[b];
          } else {
            arrayOfStructure[b].useMemory(this, (int)(paramLong + (b * arrayOfStructure[b].size())), true);
            arrayOfStructure[b].read();
          } 
        } 
      } 
    } else if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMapped[] arrayOfNativeMapped = (NativeMapped[])object;
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      int j = Native.getNativeSize(object.getClass(), object) / arrayOfNativeMapped.length;
      for (byte b = 0; b < arrayOfNativeMapped.length; b++) {
        Object object1 = getValue(paramLong + (j * b), nativeMappedConverter.nativeType(), arrayOfNativeMapped[b]);
        arrayOfNativeMapped[b] = (NativeMapped)nativeMappedConverter.fromNative(object1, new FromNativeContext(paramClass));
      } 
    } else {
      throw new IllegalArgumentException("Reading array of " + paramClass + " from memory not supported");
    } 
  }
  
  public byte getByte(long paramLong) {
    return Native.getByte(this, this.peer, paramLong);
  }
  
  public char getChar(long paramLong) {
    return Native.getChar(this, this.peer, paramLong);
  }
  
  public short getShort(long paramLong) {
    return Native.getShort(this, this.peer, paramLong);
  }
  
  public int getInt(long paramLong) {
    return Native.getInt(this, this.peer, paramLong);
  }
  
  public long getLong(long paramLong) {
    return Native.getLong(this, this.peer, paramLong);
  }
  
  public NativeLong getNativeLong(long paramLong) {
    return new NativeLong((NativeLong.SIZE == 8) ? getLong(paramLong) : getInt(paramLong));
  }
  
  public float getFloat(long paramLong) {
    return Native.getFloat(this, this.peer, paramLong);
  }
  
  public double getDouble(long paramLong) {
    return Native.getDouble(this, this.peer, paramLong);
  }
  
  public Pointer getPointer(long paramLong) {
    return Native.getPointer(this.peer + paramLong);
  }
  
  public ByteBuffer getByteBuffer(long paramLong1, long paramLong2) {
    return Native.getDirectByteBuffer(this, this.peer, paramLong1, paramLong2).order(ByteOrder.nativeOrder());
  }
  
  public String getWideString(long paramLong) {
    return Native.getWideString(this, this.peer, paramLong);
  }
  
  public String getString(long paramLong) {
    return getString(paramLong, Native.getDefaultStringEncoding());
  }
  
  public String getString(long paramLong, String paramString) {
    return Native.getString(this, paramLong, paramString);
  }
  
  public byte[] getByteArray(long paramLong, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    read(paramLong, arrayOfByte, 0, paramInt);
    return arrayOfByte;
  }
  
  public char[] getCharArray(long paramLong, int paramInt) {
    char[] arrayOfChar = new char[paramInt];
    read(paramLong, arrayOfChar, 0, paramInt);
    return arrayOfChar;
  }
  
  public short[] getShortArray(long paramLong, int paramInt) {
    short[] arrayOfShort = new short[paramInt];
    read(paramLong, arrayOfShort, 0, paramInt);
    return arrayOfShort;
  }
  
  public int[] getIntArray(long paramLong, int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    read(paramLong, arrayOfInt, 0, paramInt);
    return arrayOfInt;
  }
  
  public long[] getLongArray(long paramLong, int paramInt) {
    long[] arrayOfLong = new long[paramInt];
    read(paramLong, arrayOfLong, 0, paramInt);
    return arrayOfLong;
  }
  
  public float[] getFloatArray(long paramLong, int paramInt) {
    float[] arrayOfFloat = new float[paramInt];
    read(paramLong, arrayOfFloat, 0, paramInt);
    return arrayOfFloat;
  }
  
  public double[] getDoubleArray(long paramLong, int paramInt) {
    double[] arrayOfDouble = new double[paramInt];
    read(paramLong, arrayOfDouble, 0, paramInt);
    return arrayOfDouble;
  }
  
  public Pointer[] getPointerArray(long paramLong) {
    ArrayList<Pointer> arrayList = new ArrayList();
    int i = 0;
    for (Pointer pointer = getPointer(paramLong); pointer != null; pointer = getPointer(paramLong + i)) {
      arrayList.add(pointer);
      i += Native.POINTER_SIZE;
    } 
    return arrayList.<Pointer>toArray(new Pointer[0]);
  }
  
  public Pointer[] getPointerArray(long paramLong, int paramInt) {
    Pointer[] arrayOfPointer = new Pointer[paramInt];
    read(paramLong, arrayOfPointer, 0, paramInt);
    return arrayOfPointer;
  }
  
  public String[] getStringArray(long paramLong) {
    return getStringArray(paramLong, -1, Native.getDefaultStringEncoding());
  }
  
  public String[] getStringArray(long paramLong, String paramString) {
    return getStringArray(paramLong, -1, paramString);
  }
  
  public String[] getStringArray(long paramLong, int paramInt) {
    return getStringArray(paramLong, paramInt, Native.getDefaultStringEncoding());
  }
  
  public String[] getWideStringArray(long paramLong) {
    return getWideStringArray(paramLong, -1);
  }
  
  public String[] getWideStringArray(long paramLong, int paramInt) {
    return getStringArray(paramLong, paramInt, "--WIDE-STRING--");
  }
  
  public String[] getStringArray(long paramLong, int paramInt, String paramString) {
    ArrayList<String> arrayList = new ArrayList();
    int i = 0;
    if (paramInt != -1) {
      Pointer pointer = getPointer(paramLong + i);
      byte b = 0;
      while (b++ < paramInt) {
        String str = (pointer == null) ? null : ("--WIDE-STRING--".equals(paramString) ? pointer.getWideString(0L) : pointer.getString(0L, paramString));
        arrayList.add(str);
        if (b < paramInt) {
          i += Native.POINTER_SIZE;
          pointer = getPointer(paramLong + i);
        } 
      } 
    } else {
      Pointer pointer;
      while ((pointer = getPointer(paramLong + i)) != null) {
        String str = "--WIDE-STRING--".equals(paramString) ? pointer.getWideString(0L) : pointer.getString(0L, paramString);
        arrayList.add(str);
        i += Native.POINTER_SIZE;
      } 
    } 
    return arrayList.<String>toArray(new String[0]);
  }
  
  void setValue(long paramLong, Object paramObject, Class<?> paramClass) {
    if (paramClass == boolean.class || paramClass == Boolean.class) {
      setInt(paramLong, Boolean.TRUE.equals(paramObject) ? -1 : 0);
    } else if (paramClass == byte.class || paramClass == Byte.class) {
      setByte(paramLong, (paramObject == null) ? 0 : ((Byte)paramObject).byteValue());
    } else if (paramClass == short.class || paramClass == Short.class) {
      setShort(paramLong, (paramObject == null) ? 0 : ((Short)paramObject).shortValue());
    } else if (paramClass == char.class || paramClass == Character.class) {
      setChar(paramLong, (paramObject == null) ? Character.MIN_VALUE : ((Character)paramObject).charValue());
    } else if (paramClass == int.class || paramClass == Integer.class) {
      setInt(paramLong, (paramObject == null) ? 0 : ((Integer)paramObject).intValue());
    } else if (paramClass == long.class || paramClass == Long.class) {
      setLong(paramLong, (paramObject == null) ? 0L : ((Long)paramObject).longValue());
    } else if (paramClass == float.class || paramClass == Float.class) {
      setFloat(paramLong, (paramObject == null) ? 0.0F : ((Float)paramObject).floatValue());
    } else if (paramClass == double.class || paramClass == Double.class) {
      setDouble(paramLong, (paramObject == null) ? 0.0D : ((Double)paramObject).doubleValue());
    } else if (paramClass == Pointer.class) {
      setPointer(paramLong, (Pointer)paramObject);
    } else if (paramClass == String.class) {
      setPointer(paramLong, (Pointer)paramObject);
    } else if (paramClass == WString.class) {
      setPointer(paramLong, (Pointer)paramObject);
    } else if (Structure.class.isAssignableFrom(paramClass)) {
      Structure structure = (Structure)paramObject;
      if (Structure.ByReference.class.isAssignableFrom(paramClass)) {
        setPointer(paramLong, (structure == null) ? null : structure.getPointer());
        if (structure != null)
          structure.autoWrite(); 
      } else {
        structure.useMemory(this, (int)paramLong, true);
        structure.write();
      } 
    } else if (Callback.class.isAssignableFrom(paramClass)) {
      setPointer(paramLong, CallbackReference.getFunctionPointer((Callback)paramObject));
    } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(paramClass)) {
      Pointer pointer = (paramObject == null) ? null : Native.getDirectBufferPointer((Buffer)paramObject);
      setPointer(paramLong, pointer);
    } else if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      Class<?> clazz = nativeMappedConverter.nativeType();
      setValue(paramLong, nativeMappedConverter.toNative(paramObject, new ToNativeContext()), clazz);
    } else if (paramClass.isArray()) {
      writeArray(paramLong, paramObject, paramClass.getComponentType());
    } else {
      throw new IllegalArgumentException("Writing " + paramClass + " to memory is not supported");
    } 
  }
  
  private void writeArray(long paramLong, Object paramObject, Class<?> paramClass) {
    if (paramClass == byte.class) {
      byte[] arrayOfByte = (byte[])paramObject;
      write(paramLong, arrayOfByte, 0, arrayOfByte.length);
    } else if (paramClass == short.class) {
      short[] arrayOfShort = (short[])paramObject;
      write(paramLong, arrayOfShort, 0, arrayOfShort.length);
    } else if (paramClass == char.class) {
      char[] arrayOfChar = (char[])paramObject;
      write(paramLong, arrayOfChar, 0, arrayOfChar.length);
    } else if (paramClass == int.class) {
      int[] arrayOfInt = (int[])paramObject;
      write(paramLong, arrayOfInt, 0, arrayOfInt.length);
    } else if (paramClass == long.class) {
      long[] arrayOfLong = (long[])paramObject;
      write(paramLong, arrayOfLong, 0, arrayOfLong.length);
    } else if (paramClass == float.class) {
      float[] arrayOfFloat = (float[])paramObject;
      write(paramLong, arrayOfFloat, 0, arrayOfFloat.length);
    } else if (paramClass == double.class) {
      double[] arrayOfDouble = (double[])paramObject;
      write(paramLong, arrayOfDouble, 0, arrayOfDouble.length);
    } else if (Pointer.class.isAssignableFrom(paramClass)) {
      Pointer[] arrayOfPointer = (Pointer[])paramObject;
      write(paramLong, arrayOfPointer, 0, arrayOfPointer.length);
    } else if (Structure.class.isAssignableFrom(paramClass)) {
      Structure[] arrayOfStructure = (Structure[])paramObject;
      if (Structure.ByReference.class.isAssignableFrom(paramClass)) {
        Pointer[] arrayOfPointer = new Pointer[arrayOfStructure.length];
        for (byte b = 0; b < arrayOfStructure.length; b++) {
          if (arrayOfStructure[b] == null) {
            arrayOfPointer[b] = null;
          } else {
            arrayOfPointer[b] = arrayOfStructure[b].getPointer();
            arrayOfStructure[b].write();
          } 
        } 
        write(paramLong, arrayOfPointer, 0, arrayOfPointer.length);
      } else {
        Structure structure = arrayOfStructure[0];
        if (structure == null) {
          structure = Structure.newInstance(paramClass, share(paramLong));
          arrayOfStructure[0] = structure;
        } else {
          structure.useMemory(this, (int)paramLong, true);
        } 
        structure.write();
        Structure[] arrayOfStructure1 = structure.toArray(arrayOfStructure.length);
        for (byte b = 1; b < arrayOfStructure.length; b++) {
          if (arrayOfStructure[b] == null) {
            arrayOfStructure[b] = arrayOfStructure1[b];
          } else {
            arrayOfStructure[b].useMemory(this, (int)(paramLong + (b * arrayOfStructure[b].size())), true);
          } 
          arrayOfStructure[b].write();
        } 
      } 
    } else if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMapped[] arrayOfNativeMapped = (NativeMapped[])paramObject;
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      Class<?> clazz = nativeMappedConverter.nativeType();
      int i = Native.getNativeSize(paramObject.getClass(), paramObject) / arrayOfNativeMapped.length;
      for (byte b = 0; b < arrayOfNativeMapped.length; b++) {
        Object object = nativeMappedConverter.toNative(arrayOfNativeMapped[b], new ToNativeContext());
        setValue(paramLong + (b * i), object, clazz);
      } 
    } else {
      throw new IllegalArgumentException("Writing array of " + paramClass + " to memory not supported");
    } 
  }
  
  public void setMemory(long paramLong1, long paramLong2, byte paramByte) {
    Native.setMemory(this, this.peer, paramLong1, paramLong2, paramByte);
  }
  
  public void setByte(long paramLong, byte paramByte) {
    Native.setByte(this, this.peer, paramLong, paramByte);
  }
  
  public void setShort(long paramLong, short paramShort) {
    Native.setShort(this, this.peer, paramLong, paramShort);
  }
  
  public void setChar(long paramLong, char paramChar) {
    Native.setChar(this, this.peer, paramLong, paramChar);
  }
  
  public void setInt(long paramLong, int paramInt) {
    Native.setInt(this, this.peer, paramLong, paramInt);
  }
  
  public void setLong(long paramLong1, long paramLong2) {
    Native.setLong(this, this.peer, paramLong1, paramLong2);
  }
  
  public void setNativeLong(long paramLong, NativeLong paramNativeLong) {
    if (NativeLong.SIZE == 8) {
      setLong(paramLong, paramNativeLong.longValue());
    } else {
      setInt(paramLong, paramNativeLong.intValue());
    } 
  }
  
  public void setFloat(long paramLong, float paramFloat) {
    Native.setFloat(this, this.peer, paramLong, paramFloat);
  }
  
  public void setDouble(long paramLong, double paramDouble) {
    Native.setDouble(this, this.peer, paramLong, paramDouble);
  }
  
  public void setPointer(long paramLong, Pointer paramPointer) {
    Native.setPointer(this, this.peer, paramLong, (paramPointer != null) ? paramPointer.peer : 0L);
  }
  
  public void setWideString(long paramLong, String paramString) {
    Native.setWideString(this, this.peer, paramLong, paramString);
  }
  
  public void setString(long paramLong, WString paramWString) {
    setWideString(paramLong, (paramWString == null) ? null : paramWString.toString());
  }
  
  public void setString(long paramLong, String paramString) {
    setString(paramLong, paramString, Native.getDefaultStringEncoding());
  }
  
  public void setString(long paramLong, String paramString1, String paramString2) {
    byte[] arrayOfByte = Native.getBytes(paramString1, paramString2);
    write(paramLong, arrayOfByte, 0, arrayOfByte.length);
    setByte(paramLong + arrayOfByte.length, (byte)0);
  }
  
  public String dump(long paramLong, int paramInt) {
    byte b1 = 4;
    String str = "memory dump";
    StringWriter stringWriter = new StringWriter("memory dump".length() + 2 + paramInt * 2 + paramInt / 4 * 4);
    PrintWriter printWriter = new PrintWriter(stringWriter);
    printWriter.println("memory dump");
    for (byte b2 = 0; b2 < paramInt; b2++) {
      byte b = getByte(paramLong + b2);
      if (b2 % 4 == 0)
        printWriter.print("["); 
      if (b >= 0 && b < 16)
        printWriter.print("0"); 
      printWriter.print(Integer.toHexString(b & 0xFF));
      if (b2 % 4 == 3 && b2 < paramInt - 1)
        printWriter.println("]"); 
    } 
    if (stringWriter.getBuffer().charAt(stringWriter.getBuffer().length() - 2) != ']')
      printWriter.println("]"); 
    return stringWriter.toString();
  }
  
  public String toString() {
    return "native@0x" + Long.toHexString(this.peer);
  }
  
  public static long nativeValue(Pointer paramPointer) {
    return (paramPointer == null) ? 0L : paramPointer.peer;
  }
  
  public static void nativeValue(Pointer paramPointer, long paramLong) {
    paramPointer.peer = paramLong;
  }
  
  private static class Opaque extends Pointer {
    private final String MSG = "This pointer is opaque: " + this;
    
    private Opaque(long param1Long) {
      super(param1Long);
    }
    
    public Pointer share(long param1Long1, long param1Long2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void clear(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public long indexOf(long param1Long, byte param1Byte) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, char[] param1ArrayOfchar, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, short[] param1ArrayOfshort, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, int[] param1ArrayOfint, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, long[] param1ArrayOflong, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, float[] param1ArrayOffloat, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, double[] param1ArrayOfdouble, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void read(long param1Long, Pointer[] param1ArrayOfPointer, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, char[] param1ArrayOfchar, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, short[] param1ArrayOfshort, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, int[] param1ArrayOfint, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, long[] param1ArrayOflong, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, float[] param1ArrayOffloat, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, double[] param1ArrayOfdouble, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void write(long param1Long, Pointer[] param1ArrayOfPointer, int param1Int1, int param1Int2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public ByteBuffer getByteBuffer(long param1Long1, long param1Long2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public byte getByte(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public char getChar(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public short getShort(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public int getInt(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public long getLong(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public float getFloat(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public double getDouble(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public Pointer getPointer(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public String getString(long param1Long, String param1String) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public String getWideString(long param1Long) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setByte(long param1Long, byte param1Byte) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setChar(long param1Long, char param1Char) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setShort(long param1Long, short param1Short) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setInt(long param1Long, int param1Int) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setLong(long param1Long1, long param1Long2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setFloat(long param1Long, float param1Float) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setDouble(long param1Long, double param1Double) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setPointer(long param1Long, Pointer param1Pointer) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setString(long param1Long, String param1String1, String param1String2) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setWideString(long param1Long, String param1String) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public void setMemory(long param1Long1, long param1Long2, byte param1Byte) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public String dump(long param1Long, int param1Int) {
      throw new UnsupportedOperationException(this.MSG);
    }
    
    public String toString() {
      return "const@0x" + Long.toHexString(this.peer);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Pointer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */