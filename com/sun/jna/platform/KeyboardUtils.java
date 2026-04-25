package com.sun.jna.platform;

import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.User32;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

public class KeyboardUtils {
  static final NativeKeyboardUtils INSTANCE;
  
  public static boolean isPressed(int paramInt1, int paramInt2) {
    return INSTANCE.isPressed(paramInt1, paramInt2);
  }
  
  public static boolean isPressed(int paramInt) {
    return INSTANCE.isPressed(paramInt);
  }
  
  static {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException("KeyboardUtils requires a keyboard"); 
    if (Platform.isWindows()) {
      INSTANCE = new W32KeyboardUtils();
    } else {
      if (Platform.isMac()) {
        INSTANCE = new MacKeyboardUtils();
        throw new UnsupportedOperationException("No support (yet) for " + System.getProperty("os.name"));
      } 
      INSTANCE = new X11KeyboardUtils();
    } 
  }
  
  private static abstract class NativeKeyboardUtils {
    private NativeKeyboardUtils() {}
    
    public abstract boolean isPressed(int param1Int1, int param1Int2);
    
    public boolean isPressed(int param1Int) {
      return isPressed(param1Int, 0);
    }
  }
  
  private static class W32KeyboardUtils extends NativeKeyboardUtils {
    private W32KeyboardUtils() {}
    
    private int toNative(int param1Int1, int param1Int2) {
      return ((param1Int1 >= 65 && param1Int1 <= 90) || (param1Int1 >= 48 && param1Int1 <= 57)) ? param1Int1 : ((param1Int1 == 16) ? (((param1Int2 & 0x3) != 0) ? 161 : (((param1Int2 & 0x2) != 0) ? 160 : 16)) : ((param1Int1 == 17) ? (((param1Int2 & 0x3) != 0) ? 163 : (((param1Int2 & 0x2) != 0) ? 162 : 17)) : ((param1Int1 == 18) ? (((param1Int2 & 0x3) != 0) ? 165 : (((param1Int2 & 0x2) != 0) ? 164 : 18)) : 0)));
    }
    
    public boolean isPressed(int param1Int1, int param1Int2) {
      User32 user32 = User32.INSTANCE;
      return ((user32.GetAsyncKeyState(toNative(param1Int1, param1Int2)) & 0x8000) != 0);
    }
  }
  
  private static class MacKeyboardUtils extends NativeKeyboardUtils {
    private MacKeyboardUtils() {}
    
    public boolean isPressed(int param1Int1, int param1Int2) {
      return false;
    }
  }
  
  private static class X11KeyboardUtils extends NativeKeyboardUtils {
    private X11KeyboardUtils() {}
    
    private int toKeySym(int param1Int1, int param1Int2) {
      return (param1Int1 >= 65 && param1Int1 <= 90) ? (97 + param1Int1 - 65) : ((param1Int1 >= 48 && param1Int1 <= 57) ? (48 + param1Int1 - 48) : ((param1Int1 == 16) ? (((param1Int2 & 0x3) != 0) ? 65506 : 65505) : ((param1Int1 == 17) ? (((param1Int2 & 0x3) != 0) ? 65508 : 65507) : ((param1Int1 == 18) ? (((param1Int2 & 0x3) != 0) ? 65514 : 65513) : ((param1Int1 == 157) ? (((param1Int2 & 0x3) != 0) ? 65512 : 65511) : 0)))));
    }
    
    public boolean isPressed(int param1Int1, int param1Int2) {
      X11 x11 = X11.INSTANCE;
      X11.Display display = x11.XOpenDisplay(null);
      if (display == null)
        throw new Error("Can't open X Display"); 
      try {
        byte[] arrayOfByte = new byte[32];
        x11.XQueryKeymap(display, arrayOfByte);
        int i = toKeySym(param1Int1, param1Int2);
        for (byte b = 5; b < 'Ā'; b++) {
          int j = b / 8;
          int k = b % 8;
          if ((arrayOfByte[j] & 1 << k) != 0) {
            int m = x11.XKeycodeToKeysym(display, (byte)b, 0).intValue();
            if (m == i)
              return true; 
          } 
        } 
      } finally {
        x11.XCloseDisplay(display);
      } 
      return false;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\KeyboardUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */