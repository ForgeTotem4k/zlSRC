package com.sun.jna.platform;

import com.sun.jna.platform.win32.WinDef;
import java.awt.Rectangle;

public class DesktopWindow {
  private WinDef.HWND hwnd;
  
  private String title;
  
  private String filePath;
  
  private Rectangle locAndSize;
  
  public DesktopWindow(WinDef.HWND paramHWND, String paramString1, String paramString2, Rectangle paramRectangle) {
    this.hwnd = paramHWND;
    this.title = paramString1;
    this.filePath = paramString2;
    this.locAndSize = paramRectangle;
  }
  
  public WinDef.HWND getHWND() {
    return this.hwnd;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public String getFilePath() {
    return this.filePath;
  }
  
  public Rectangle getLocAndSize() {
    return this.locAndSize;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\DesktopWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */