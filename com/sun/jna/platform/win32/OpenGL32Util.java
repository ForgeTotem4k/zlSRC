package com.sun.jna.platform.win32;

import com.sun.jna.Function;
import com.sun.jna.Pointer;

public abstract class OpenGL32Util {
  public static Function wglGetProcAddress(String paramString) {
    Pointer pointer = OpenGL32.INSTANCE.wglGetProcAddress(paramString);
    return (pointer == null) ? null : Function.getFunction(pointer);
  }
  
  public static int countGpusNV() {
    WinDef.HWND hWND = User32Util.createWindow("Message", null, 0, 0, 0, 0, 0, null, null, null, null);
    WinDef.HDC hDC = User32.INSTANCE.GetDC(hWND);
    WinGDI.PIXELFORMATDESCRIPTOR.ByReference byReference = new WinGDI.PIXELFORMATDESCRIPTOR.ByReference();
    byReference.nVersion = 1;
    byReference.dwFlags = 37;
    byReference.iPixelType = 0;
    byReference.cColorBits = 24;
    byReference.cDepthBits = 16;
    byReference.iLayerType = 0;
    GDI32.INSTANCE.SetPixelFormat(hDC, GDI32.INSTANCE.ChoosePixelFormat(hDC, byReference), byReference);
    WinDef.HGLRC hGLRC = OpenGL32.INSTANCE.wglCreateContext(hDC);
    OpenGL32.INSTANCE.wglMakeCurrent(hDC, hGLRC);
    Pointer pointer = OpenGL32.INSTANCE.wglGetProcAddress("wglEnumGpusNV");
    Function function = (pointer == null) ? null : Function.getFunction(pointer);
    OpenGL32.INSTANCE.wglDeleteContext(hGLRC);
    User32.INSTANCE.ReleaseDC(hWND, hDC);
    User32Util.destroyWindow(hWND);
    if (function == null)
      return 0; 
    WinDef.HGLRCByReference hGLRCByReference = new WinDef.HGLRCByReference();
    for (byte b = 0; b < 16; b++) {
      Boolean bool = (Boolean)function.invoke(Boolean.class, new Object[] { Integer.valueOf(b), hGLRCByReference });
      if (!bool.booleanValue())
        return b; 
    } 
    return 0;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\OpenGL32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */