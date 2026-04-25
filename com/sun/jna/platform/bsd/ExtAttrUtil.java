package com.sun.jna.platform.bsd;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtAttrUtil {
  public static List<String> list(String paramString) throws IOException {
    long l1 = ExtAttr.INSTANCE.extattr_list_file(paramString, 1, null, new LibCAPI.size_t(0L)).longValue();
    if (l1 < 0L)
      throw new IOException("errno: " + Native.getLastError()); 
    if (l1 == 0L)
      return Collections.emptyList(); 
    ByteBuffer byteBuffer = ByteBuffer.allocate((int)l1);
    long l2 = ExtAttr.INSTANCE.extattr_list_file(paramString, 1, byteBuffer, new LibCAPI.size_t(l1)).longValue();
    if (l2 < 0L)
      throw new IOException("errno: " + Native.getLastError()); 
    return decodeStringList(byteBuffer);
  }
  
  public static ByteBuffer get(String paramString1, String paramString2) throws IOException {
    long l1 = ExtAttr.INSTANCE.extattr_get_file(paramString1, 1, paramString2, null, new LibCAPI.size_t(0L)).longValue();
    if (l1 < 0L)
      throw new IOException("errno: " + Native.getLastError()); 
    if (l1 == 0L)
      return ByteBuffer.allocate(0); 
    ByteBuffer byteBuffer = ByteBuffer.allocate((int)l1);
    long l2 = ExtAttr.INSTANCE.extattr_get_file(paramString1, 1, paramString2, byteBuffer, new LibCAPI.size_t(l1)).longValue();
    if (l2 < 0L)
      throw new IOException("errno: " + Native.getLastError()); 
    return byteBuffer;
  }
  
  public static void set(String paramString1, String paramString2, ByteBuffer paramByteBuffer) throws IOException {
    long l = ExtAttr.INSTANCE.extattr_set_file(paramString1, 1, paramString2, paramByteBuffer, new LibCAPI.size_t(paramByteBuffer.remaining())).longValue();
    if (l < 0L)
      throw new IOException("errno: " + Native.getLastError()); 
  }
  
  public static void delete(String paramString1, String paramString2) throws IOException {
    int i = ExtAttr.INSTANCE.extattr_delete_file(paramString1, 1, paramString2);
    if (i < 0)
      throw new IOException("errno: " + Native.getLastError()); 
  }
  
  private static List<String> decodeStringList(ByteBuffer paramByteBuffer) {
    ArrayList<String> arrayList = new ArrayList();
    while (paramByteBuffer.hasRemaining()) {
      int i = paramByteBuffer.get() & 0xFF;
      byte[] arrayOfByte = new byte[i];
      paramByteBuffer.get(arrayOfByte);
      try {
        arrayList.add(new String(arrayOfByte, "UTF-8"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new RuntimeException(unsupportedEncodingException);
      } 
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\bsd\ExtAttrUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */