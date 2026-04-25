package com.sun.jna.platform.linux;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class XAttrUtil {
  public static void setXAttr(String paramString1, String paramString2, String paramString3) throws IOException {
    setXAttr(paramString1, paramString2, paramString3, Native.getDefaultStringEncoding());
  }
  
  public static void setXAttr(String paramString1, String paramString2, String paramString3, String paramString4) throws IOException {
    setXAttr(paramString1, paramString2, paramString3.getBytes(paramString4));
  }
  
  public static void setXAttr(String paramString1, String paramString2, byte[] paramArrayOfbyte) throws IOException {
    int i = XAttr.INSTANCE.setxattr(paramString1, paramString2, paramArrayOfbyte, new XAttr.size_t(paramArrayOfbyte.length), 0);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  public static void lSetXAttr(String paramString1, String paramString2, String paramString3) throws IOException {
    lSetXAttr(paramString1, paramString2, paramString3, Native.getDefaultStringEncoding());
  }
  
  public static void lSetXAttr(String paramString1, String paramString2, String paramString3, String paramString4) throws IOException {
    lSetXAttr(paramString1, paramString2, paramString3.getBytes(paramString4));
  }
  
  public static void lSetXAttr(String paramString1, String paramString2, byte[] paramArrayOfbyte) throws IOException {
    int i = XAttr.INSTANCE.lsetxattr(paramString1, paramString2, paramArrayOfbyte, new XAttr.size_t(paramArrayOfbyte.length), 0);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  public static void fSetXAttr(int paramInt, String paramString1, String paramString2) throws IOException {
    fSetXAttr(paramInt, paramString1, paramString2, Native.getDefaultStringEncoding());
  }
  
  public static void fSetXAttr(int paramInt, String paramString1, String paramString2, String paramString3) throws IOException {
    fSetXAttr(paramInt, paramString1, paramString2.getBytes(paramString3));
  }
  
  public static void fSetXAttr(int paramInt, String paramString, byte[] paramArrayOfbyte) throws IOException {
    int i = XAttr.INSTANCE.fsetxattr(paramInt, paramString, paramArrayOfbyte, new XAttr.size_t(paramArrayOfbyte.length), 0);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  public static String getXAttr(String paramString1, String paramString2) throws IOException {
    return getXAttr(paramString1, paramString2, Native.getDefaultStringEncoding());
  }
  
  public static String getXAttr(String paramString1, String paramString2, String paramString3) throws IOException {
    byte[] arrayOfByte = getXAttrBytes(paramString1, paramString2);
    return new String(arrayOfByte, Charset.forName(paramString3));
  }
  
  public static byte[] getXAttrBytes(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.getxattr(paramString1, paramString2, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.getxattr(paramString1, paramString2, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return arrayOfByte;
  }
  
  public static Memory getXAttrAsMemory(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    Memory memory;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.getxattr(paramString1, paramString2, (Pointer)null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      if (ssize_t.longValue() == 0L)
        return null; 
      memory = new Memory(ssize_t.longValue());
      ssize_t = XAttr.INSTANCE.getxattr(paramString1, paramString2, (Pointer)memory, new XAttr.size_t(memory.size()));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return memory;
  }
  
  public static String lGetXAttr(String paramString1, String paramString2) throws IOException {
    return lGetXAttr(paramString1, paramString2, Native.getDefaultStringEncoding());
  }
  
  public static String lGetXAttr(String paramString1, String paramString2, String paramString3) throws IOException {
    byte[] arrayOfByte = lGetXAttrBytes(paramString1, paramString2);
    return new String(arrayOfByte, Charset.forName(paramString3));
  }
  
  public static byte[] lGetXAttrBytes(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.lgetxattr(paramString1, paramString2, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.lgetxattr(paramString1, paramString2, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return arrayOfByte;
  }
  
  public static Memory lGetXAttrAsMemory(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    Memory memory;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.lgetxattr(paramString1, paramString2, (Pointer)null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      if (ssize_t.longValue() == 0L)
        return null; 
      memory = new Memory(ssize_t.longValue());
      ssize_t = XAttr.INSTANCE.lgetxattr(paramString1, paramString2, (Pointer)memory, new XAttr.size_t(memory.size()));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return memory;
  }
  
  public static String fGetXAttr(int paramInt, String paramString) throws IOException {
    return fGetXAttr(paramInt, paramString, Native.getDefaultStringEncoding());
  }
  
  public static String fGetXAttr(int paramInt, String paramString1, String paramString2) throws IOException {
    byte[] arrayOfByte = fGetXAttrBytes(paramInt, paramString1);
    return new String(arrayOfByte, Charset.forName(paramString2));
  }
  
  public static byte[] fGetXAttrBytes(int paramInt, String paramString) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.fgetxattr(paramInt, paramString, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.fgetxattr(paramInt, paramString, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return arrayOfByte;
  }
  
  public static Memory fGetXAttrAsMemory(int paramInt, String paramString) throws IOException {
    XAttr.ssize_t ssize_t;
    Memory memory;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.fgetxattr(paramInt, paramString, (Pointer)null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      if (ssize_t.longValue() == 0L)
        return null; 
      memory = new Memory(ssize_t.longValue());
      ssize_t = XAttr.INSTANCE.fgetxattr(paramInt, paramString, (Pointer)memory, new XAttr.size_t(memory.size()));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return memory;
  }
  
  public static Collection<String> listXAttr(String paramString) throws IOException {
    return listXAttr(paramString, Native.getDefaultStringEncoding());
  }
  
  public static Collection<String> listXAttr(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.listxattr(paramString1, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.listxattr(paramString1, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return splitBufferToStrings(arrayOfByte, paramString2);
  }
  
  public static Collection<String> lListXAttr(String paramString) throws IOException {
    return lListXAttr(paramString, Native.getDefaultStringEncoding());
  }
  
  public static Collection<String> lListXAttr(String paramString1, String paramString2) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.llistxattr(paramString1, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.llistxattr(paramString1, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return splitBufferToStrings(arrayOfByte, paramString2);
  }
  
  public static Collection<String> fListXAttr(int paramInt) throws IOException {
    return fListXAttr(paramInt, Native.getDefaultStringEncoding());
  }
  
  public static Collection<String> fListXAttr(int paramInt, String paramString) throws IOException {
    XAttr.ssize_t ssize_t;
    byte[] arrayOfByte;
    int i = 0;
    do {
      ssize_t = XAttr.INSTANCE.flistxattr(paramInt, (byte[])null, XAttr.size_t.ZERO);
      if (ssize_t.longValue() < 0L) {
        i = Native.getLastError();
        throw new IOException("errno: " + i);
      } 
      arrayOfByte = new byte[ssize_t.intValue()];
      ssize_t = XAttr.INSTANCE.flistxattr(paramInt, arrayOfByte, new XAttr.size_t(arrayOfByte.length));
      if (ssize_t.longValue() >= 0L)
        continue; 
      i = Native.getLastError();
      if (i != 34)
        throw new IOException("errno: " + i); 
    } while (ssize_t.longValue() < 0L && i == 34);
    return splitBufferToStrings(arrayOfByte, paramString);
  }
  
  public static void removeXAttr(String paramString1, String paramString2) throws IOException {
    int i = XAttr.INSTANCE.removexattr(paramString1, paramString2);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  public static void lRemoveXAttr(String paramString1, String paramString2) throws IOException {
    int i = XAttr.INSTANCE.lremovexattr(paramString1, paramString2);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  public static void fRemoveXAttr(int paramInt, String paramString) throws IOException {
    int i = XAttr.INSTANCE.fremovexattr(paramInt, paramString);
    if (i != 0) {
      int j = Native.getLastError();
      throw new IOException("errno: " + j);
    } 
  }
  
  private static Collection<String> splitBufferToStrings(byte[] paramArrayOfbyte, String paramString) throws IOException {
    Charset charset = Charset.forName(paramString);
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet(1);
    int i = 0;
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      if (paramArrayOfbyte[b] == 0) {
        String str = new String(paramArrayOfbyte, i, b - i, charset);
        linkedHashSet.add(str);
        i = b + 1;
      } 
    } 
    return linkedHashSet;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\linux\XAttrUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */