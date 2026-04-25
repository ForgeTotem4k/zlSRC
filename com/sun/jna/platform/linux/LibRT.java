package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibRT extends Library {
  public static final LibRT INSTANCE = (LibRT)Native.load("rt", LibRT.class);
  
  int shm_open(String paramString, int paramInt1, int paramInt2);
  
  int shm_unlink(String paramString);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\linux\LibRT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */