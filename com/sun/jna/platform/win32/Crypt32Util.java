package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.Arrays;

public abstract class Crypt32Util {
  public static byte[] cryptProtectData(byte[] paramArrayOfbyte) {
    return cryptProtectData(paramArrayOfbyte, 0);
  }
  
  public static byte[] cryptProtectData(byte[] paramArrayOfbyte, int paramInt) {
    return cryptProtectData(paramArrayOfbyte, null, paramInt, "", null);
  }
  
  public static byte[] cryptProtectData(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt, String paramString, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT paramCRYPTPROTECT_PROMPTSTRUCT) {
    WinCrypt.DATA_BLOB dATA_BLOB1 = new WinCrypt.DATA_BLOB(paramArrayOfbyte1);
    WinCrypt.DATA_BLOB dATA_BLOB2 = new WinCrypt.DATA_BLOB();
    WinCrypt.DATA_BLOB dATA_BLOB3 = (paramArrayOfbyte2 == null) ? null : new WinCrypt.DATA_BLOB(paramArrayOfbyte2);
    Win32Exception win32Exception = null;
    byte[] arrayOfByte = null;
    try {
      if (!Crypt32.INSTANCE.CryptProtectData(dATA_BLOB1, paramString, dATA_BLOB3, null, paramCRYPTPROTECT_PROMPTSTRUCT, paramInt, dATA_BLOB2)) {
        win32Exception = new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
        arrayOfByte = dATA_BLOB2.getData();
      } 
    } finally {
      if (dATA_BLOB1.pbData != null)
        dATA_BLOB1.pbData.clear(dATA_BLOB1.cbData); 
      if (dATA_BLOB3 != null && dATA_BLOB3.pbData != null)
        dATA_BLOB3.pbData.clear(dATA_BLOB3.cbData); 
      if (dATA_BLOB2.pbData != null) {
        dATA_BLOB2.pbData.clear(dATA_BLOB2.cbData);
        try {
          Kernel32Util.freeLocalMemory(dATA_BLOB2.pbData);
        } catch (Win32Exception win32Exception1) {
          if (win32Exception == null) {
            win32Exception = win32Exception1;
          } else {
            win32Exception.addSuppressedReflected((Throwable)win32Exception1);
          } 
        } 
      } 
    } 
    if (win32Exception != null) {
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
      throw win32Exception;
    } 
    return arrayOfByte;
  }
  
  public static byte[] cryptUnprotectData(byte[] paramArrayOfbyte) {
    return cryptUnprotectData(paramArrayOfbyte, 0);
  }
  
  public static byte[] cryptUnprotectData(byte[] paramArrayOfbyte, int paramInt) {
    return cryptUnprotectData(paramArrayOfbyte, null, paramInt, null);
  }
  
  public static byte[] cryptUnprotectData(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT paramCRYPTPROTECT_PROMPTSTRUCT) {
    WinCrypt.DATA_BLOB dATA_BLOB1 = new WinCrypt.DATA_BLOB(paramArrayOfbyte1);
    WinCrypt.DATA_BLOB dATA_BLOB2 = new WinCrypt.DATA_BLOB();
    WinCrypt.DATA_BLOB dATA_BLOB3 = (paramArrayOfbyte2 == null) ? null : new WinCrypt.DATA_BLOB(paramArrayOfbyte2);
    Win32Exception win32Exception = null;
    byte[] arrayOfByte = null;
    try {
      if (!Crypt32.INSTANCE.CryptUnprotectData(dATA_BLOB1, null, dATA_BLOB3, null, paramCRYPTPROTECT_PROMPTSTRUCT, paramInt, dATA_BLOB2)) {
        win32Exception = new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
        arrayOfByte = dATA_BLOB2.getData();
      } 
    } finally {
      if (dATA_BLOB1.pbData != null)
        dATA_BLOB1.pbData.clear(dATA_BLOB1.cbData); 
      if (dATA_BLOB3 != null && dATA_BLOB3.pbData != null)
        dATA_BLOB3.pbData.clear(dATA_BLOB3.cbData); 
      if (dATA_BLOB2.pbData != null) {
        dATA_BLOB2.pbData.clear(dATA_BLOB2.cbData);
        try {
          Kernel32Util.freeLocalMemory(dATA_BLOB2.pbData);
        } catch (Win32Exception win32Exception1) {
          if (win32Exception == null) {
            win32Exception = win32Exception1;
          } else {
            win32Exception.addSuppressedReflected((Throwable)win32Exception1);
          } 
        } 
      } 
    } 
    if (win32Exception != null) {
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
      throw win32Exception;
    } 
    return arrayOfByte;
  }
  
  public static String CertNameToStr(int paramInt1, int paramInt2, WinCrypt.DATA_BLOB paramDATA_BLOB) {
    byte b = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
    int i = Crypt32.INSTANCE.CertNameToStr(paramInt1, paramDATA_BLOB, paramInt2, Pointer.NULL, 0);
    Memory memory = new Memory((i * b));
    int j = Crypt32.INSTANCE.CertNameToStr(paramInt1, paramDATA_BLOB, paramInt2, (Pointer)memory, i);
    assert j == i;
    return Boolean.getBoolean("w32.ascii") ? memory.getString(0L) : memory.getWideString(0L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Crypt32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */