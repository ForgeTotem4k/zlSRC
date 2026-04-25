package com.sun.jna.platform.bsd;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI;
import java.nio.ByteBuffer;

public interface ExtAttr extends Library {
  public static final ExtAttr INSTANCE = (ExtAttr)Native.load(null, ExtAttr.class);
  
  public static final int EXTATTR_NAMESPACE_USER = 1;
  
  LibCAPI.ssize_t extattr_get_file(String paramString1, int paramInt, String paramString2, ByteBuffer paramByteBuffer, LibCAPI.size_t paramsize_t);
  
  LibCAPI.ssize_t extattr_set_file(String paramString1, int paramInt, String paramString2, ByteBuffer paramByteBuffer, LibCAPI.size_t paramsize_t);
  
  int extattr_delete_file(String paramString1, int paramInt, String paramString2);
  
  LibCAPI.ssize_t extattr_list_file(String paramString, int paramInt, ByteBuffer paramByteBuffer, LibCAPI.size_t paramsize_t);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\bsd\ExtAttr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */