package com.sun.jna.platform.mac;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class XAttrUtil {
  public static List<String> listXAttr(String paramString) {
    long l1 = XAttr.INSTANCE.listxattr(paramString, null, 0L, 0);
    if (l1 < 0L)
      return null; 
    if (l1 == 0L)
      return new ArrayList<>(0); 
    Memory memory = new Memory(l1);
    long l2 = XAttr.INSTANCE.listxattr(paramString, (Pointer)memory, l1, 0);
    return (l2 < 0L) ? null : decodeStringSequence(memory.getByteBuffer(0L, l2));
  }
  
  public static String getXAttr(String paramString1, String paramString2) {
    long l1 = XAttr.INSTANCE.getxattr(paramString1, paramString2, null, 0L, 0, 0);
    if (l1 < 0L)
      return null; 
    if (l1 == 0L)
      return ""; 
    Memory memory = new Memory(l1);
    memory.clear();
    long l2 = XAttr.INSTANCE.getxattr(paramString1, paramString2, (Pointer)memory, l1, 0, 0);
    return (l2 < 0L) ? null : Native.toString(memory.getByteArray(0L, (int)l1), "UTF-8");
  }
  
  public static int setXAttr(String paramString1, String paramString2, String paramString3) {
    Memory memory = encodeString(paramString3);
    return XAttr.INSTANCE.setxattr(paramString1, paramString2, (Pointer)memory, memory.size(), 0, 0);
  }
  
  public static int removeXAttr(String paramString1, String paramString2) {
    return XAttr.INSTANCE.removexattr(paramString1, paramString2, 0);
  }
  
  protected static Memory encodeString(String paramString) {
    byte[] arrayOfByte = paramString.getBytes(Charset.forName("UTF-8"));
    Memory memory = new Memory(arrayOfByte.length);
    memory.write(0L, arrayOfByte, 0, arrayOfByte.length);
    return memory;
  }
  
  protected static String decodeString(ByteBuffer paramByteBuffer) {
    return Charset.forName("UTF-8").decode(paramByteBuffer).toString();
  }
  
  protected static List<String> decodeStringSequence(ByteBuffer paramByteBuffer) {
    ArrayList<String> arrayList = new ArrayList();
    paramByteBuffer.mark();
    while (paramByteBuffer.hasRemaining()) {
      if (paramByteBuffer.get() == 0) {
        ByteBuffer byteBuffer = (ByteBuffer)paramByteBuffer.duplicate().limit(paramByteBuffer.position() - 1).reset();
        if (byteBuffer.hasRemaining())
          arrayList.add(decodeString(byteBuffer)); 
        paramByteBuffer.mark();
      } 
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\mac\XAttrUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */