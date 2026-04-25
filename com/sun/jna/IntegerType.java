package com.sun.jna;

public abstract class IntegerType extends Number implements NativeMapped {
  private static final long serialVersionUID = 1L;
  
  private int size;
  
  private Number number;
  
  private boolean unsigned;
  
  private long value;
  
  public IntegerType(int paramInt) {
    this(paramInt, 0L, false);
  }
  
  public IntegerType(int paramInt, boolean paramBoolean) {
    this(paramInt, 0L, paramBoolean);
  }
  
  public IntegerType(int paramInt, long paramLong) {
    this(paramInt, paramLong, false);
  }
  
  public IntegerType(int paramInt, long paramLong, boolean paramBoolean) {
    this.size = paramInt;
    this.unsigned = paramBoolean;
    setValue(paramLong);
  }
  
  public void setValue(long paramLong) {
    long l = paramLong;
    this.value = paramLong;
    switch (this.size) {
      case 1:
        if (this.unsigned)
          this.value = paramLong & 0xFFL; 
        l = (byte)(int)paramLong;
        this.number = Byte.valueOf((byte)(int)paramLong);
        break;
      case 2:
        if (this.unsigned)
          this.value = paramLong & 0xFFFFL; 
        l = (short)(int)paramLong;
        this.number = Short.valueOf((short)(int)paramLong);
        break;
      case 4:
        if (this.unsigned)
          this.value = paramLong & 0xFFFFFFFFL; 
        l = (int)paramLong;
        this.number = Integer.valueOf((int)paramLong);
        break;
      case 8:
        this.number = Long.valueOf(paramLong);
        break;
      default:
        throw new IllegalArgumentException("Unsupported size: " + this.size);
    } 
    if (this.size < 8) {
      long l1 = (1L << this.size * 8) - 1L ^ 0xFFFFFFFFFFFFFFFFL;
      if ((paramLong < 0L && l != paramLong) || (paramLong >= 0L && (l1 & paramLong) != 0L))
        throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(paramLong) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(l1)); 
    } 
  }
  
  public Object toNative() {
    return this.number;
  }
  
  public Object fromNative(Object paramObject, FromNativeContext paramFromNativeContext) {
    long l = (paramObject == null) ? 0L : ((Number)paramObject).longValue();
    IntegerType integerType = (IntegerType)Klass.newInstance(getClass());
    integerType.setValue(l);
    return integerType;
  }
  
  public Class<?> nativeType() {
    return this.number.getClass();
  }
  
  public int intValue() {
    return (int)this.value;
  }
  
  public long longValue() {
    return this.value;
  }
  
  public float floatValue() {
    return this.number.floatValue();
  }
  
  public double doubleValue() {
    return this.number.doubleValue();
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof IntegerType && this.number.equals(((IntegerType)paramObject).number));
  }
  
  public String toString() {
    return this.number.toString();
  }
  
  public int hashCode() {
    return this.number.hashCode();
  }
  
  public static <T extends IntegerType> int compare(T paramT1, T paramT2) {
    return (paramT1 == paramT2) ? 0 : ((paramT1 == null) ? 1 : ((paramT2 == null) ? -1 : compare(paramT1.longValue(), paramT2.longValue())));
  }
  
  public static int compare(IntegerType paramIntegerType, long paramLong) {
    return (paramIntegerType == null) ? 1 : compare(paramIntegerType.longValue(), paramLong);
  }
  
  public static final int compare(long paramLong1, long paramLong2) {
    return (paramLong1 == paramLong2) ? 0 : ((paramLong1 < paramLong2) ? -1 : 1);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\IntegerType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */