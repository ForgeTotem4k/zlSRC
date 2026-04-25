package com.sun.jna;

public class NativeLong extends IntegerType {
  private static final long serialVersionUID = 1L;
  
  public static final int SIZE = Native.LONG_SIZE;
  
  public NativeLong() {
    this(0L);
  }
  
  public NativeLong(long paramLong) {
    this(paramLong, false);
  }
  
  public NativeLong(long paramLong, boolean paramBoolean) {
    super(SIZE, paramLong, paramBoolean);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\NativeLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */