package com.sun.jna.platform.unix;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibC extends LibCAPI, Library {
  public static final String NAME = "c";
  
  public static final LibC INSTANCE = (LibC)Native.load("c", LibC.class);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\LibC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */