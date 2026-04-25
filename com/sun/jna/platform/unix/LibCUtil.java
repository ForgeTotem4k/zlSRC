package com.sun.jna.platform.unix;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class LibCUtil {
  private static final NativeLibrary LIBC = NativeLibrary.getInstance("c");
  
  private static Function mmap = null;
  
  private static boolean mmap64 = false;
  
  private static Function ftruncate = null;
  
  private static boolean ftruncate64 = false;
  
  public static Pointer mmap(Pointer paramPointer, long paramLong1, int paramInt1, int paramInt2, int paramInt3, long paramLong2) {
    Object[] arrayOfObject = new Object[6];
    arrayOfObject[0] = paramPointer;
    if (Native.SIZE_T_SIZE == 4) {
      require32Bit(paramLong1, "length");
      arrayOfObject[1] = Integer.valueOf((int)paramLong1);
    } else {
      arrayOfObject[1] = Long.valueOf(paramLong1);
    } 
    arrayOfObject[2] = Integer.valueOf(paramInt1);
    arrayOfObject[3] = Integer.valueOf(paramInt2);
    arrayOfObject[4] = Integer.valueOf(paramInt3);
    if (mmap64 || Native.LONG_SIZE > 4) {
      arrayOfObject[5] = Long.valueOf(paramLong2);
    } else {
      require32Bit(paramLong2, "offset");
      arrayOfObject[5] = Integer.valueOf((int)paramLong2);
    } 
    return mmap.invokePointer(arrayOfObject);
  }
  
  public static int ftruncate(int paramInt, long paramLong) {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Integer.valueOf(paramInt);
    if (ftruncate64 || Native.LONG_SIZE > 4) {
      arrayOfObject[1] = Long.valueOf(paramLong);
    } else {
      require32Bit(paramLong, "length");
      arrayOfObject[1] = Integer.valueOf((int)paramLong);
    } 
    return ftruncate.invokeInt(arrayOfObject);
  }
  
  public static void require32Bit(long paramLong, String paramString) {
    if (paramLong > 2147483647L)
      throw new IllegalArgumentException(paramString + " exceeds 32bit"); 
  }
  
  static {
    try {
      mmap = LIBC.getFunction("mmap64", 64);
      mmap64 = true;
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      mmap = LIBC.getFunction("mmap", 64);
    } 
    try {
      ftruncate = LIBC.getFunction("ftruncate64", 64);
      ftruncate64 = true;
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      ftruncate = LIBC.getFunction("ftruncate", 64);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\LibCUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */