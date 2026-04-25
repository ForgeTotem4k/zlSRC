package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class GDI32Util {
  private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(24, 16711680, 65280, 255);
  
  private static final int[] SCREENSHOT_BAND_MASKS = new int[] { SCREENSHOT_COLOR_MODEL.getRedMask(), SCREENSHOT_COLOR_MODEL.getGreenMask(), SCREENSHOT_COLOR_MODEL.getBlueMask() };
  
  public static BufferedImage getScreenshot(WinDef.HWND paramHWND) {
    Win32Exception win32Exception;
    WinDef.RECT rECT = new WinDef.RECT();
    if (!User32.INSTANCE.GetWindowRect(paramHWND, rECT))
      throw new Win32Exception(Native.getLastError()); 
    Rectangle rectangle = rECT.toRectangle();
    int i = rectangle.width;
    int j = rectangle.height;
    if (i == 0 || j == 0)
      throw new IllegalStateException("Window width and/or height were 0 even though GetWindowRect did not appear to fail."); 
    WinDef.HDC hDC1 = User32.INSTANCE.GetDC(paramHWND);
    if (hDC1 == null)
      throw new Win32Exception(Native.getLastError()); 
    Throwable throwable = null;
    WinDef.HDC hDC2 = null;
    WinDef.HBITMAP hBITMAP = null;
    WinNT.HANDLE hANDLE = null;
    BufferedImage bufferedImage = null;
    try {
      hDC2 = GDI32.INSTANCE.CreateCompatibleDC(hDC1);
      if (hDC2 == null)
        throw new Win32Exception(Native.getLastError()); 
      hBITMAP = GDI32.INSTANCE.CreateCompatibleBitmap(hDC1, i, j);
      if (hBITMAP == null)
        throw new Win32Exception(Native.getLastError()); 
      hANDLE = GDI32.INSTANCE.SelectObject(hDC2, hBITMAP);
      if (hANDLE == null)
        throw new Win32Exception(Native.getLastError()); 
      if (!GDI32.INSTANCE.BitBlt(hDC2, 0, 0, i, j, hDC1, 0, 0, 13369376))
        throw new Win32Exception(Native.getLastError()); 
      WinGDI.BITMAPINFO bITMAPINFO = new WinGDI.BITMAPINFO();
      bITMAPINFO.bmiHeader.biWidth = i;
      bITMAPINFO.bmiHeader.biHeight = -j;
      bITMAPINFO.bmiHeader.biPlanes = 1;
      bITMAPINFO.bmiHeader.biBitCount = 32;
      bITMAPINFO.bmiHeader.biCompression = 0;
      Memory memory = new Memory((i * j * 4));
      int k = GDI32.INSTANCE.GetDIBits(hDC1, hBITMAP, 0, j, (Pointer)memory, bITMAPINFO, 0);
      if (k == 0 || k == 87)
        throw new Win32Exception(Native.getLastError()); 
      int m = i * j;
      DataBufferInt dataBufferInt = new DataBufferInt(memory.getIntArray(0L, m), m);
      WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, i, j, i, SCREENSHOT_BAND_MASKS, (Point)null);
      bufferedImage = new BufferedImage(SCREENSHOT_COLOR_MODEL, writableRaster, false, null);
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      if (hANDLE != null) {
        WinNT.HANDLE hANDLE1 = GDI32.INSTANCE.SelectObject(hDC2, hANDLE);
        if (hANDLE1 == null || WinGDI.HGDI_ERROR.equals(hANDLE1)) {
          Win32Exception win32Exception1 = new Win32Exception(Native.getLastError());
          if (win32Exception != null)
            win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
          win32Exception = win32Exception1;
        } 
      } 
      if (hBITMAP != null && !GDI32.INSTANCE.DeleteObject(hBITMAP)) {
        Win32Exception win32Exception1 = new Win32Exception(Native.getLastError());
        if (win32Exception != null)
          win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
        win32Exception = win32Exception1;
      } 
      if (hDC2 != null && !GDI32.INSTANCE.DeleteDC(hDC2)) {
        Win32Exception win32Exception1 = new Win32Exception(Native.getLastError());
        if (win32Exception != null)
          win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
        win32Exception = win32Exception1;
      } 
      if (hDC1 != null && 0 == User32.INSTANCE.ReleaseDC(paramHWND, hDC1))
        throw new IllegalStateException("Device context did not release properly."); 
    } 
    if (win32Exception != null)
      throw win32Exception; 
    return bufferedImage;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\GDI32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */