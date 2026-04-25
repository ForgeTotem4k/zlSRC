package oshi.software.os;

import java.awt.Rectangle;
import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSDesktopWindow {
  private final long windowId;
  
  private final String title;
  
  private final String command;
  
  private final Rectangle locAndSize;
  
  private final long owningProcessId;
  
  private final int order;
  
  private final boolean visible;
  
  public OSDesktopWindow(long paramLong1, String paramString1, String paramString2, Rectangle paramRectangle, long paramLong2, int paramInt, boolean paramBoolean) {
    this.windowId = paramLong1;
    this.title = paramString1;
    this.command = paramString2;
    this.locAndSize = paramRectangle;
    this.owningProcessId = paramLong2;
    this.order = paramInt;
    this.visible = paramBoolean;
  }
  
  public long getWindowId() {
    return this.windowId;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public String getCommand() {
    return this.command;
  }
  
  public Rectangle getLocAndSize() {
    return this.locAndSize;
  }
  
  public long getOwningProcessId() {
    return this.owningProcessId;
  }
  
  public int getOrder() {
    return this.order;
  }
  
  public boolean isVisible() {
    return this.visible;
  }
  
  public String toString() {
    return "OSDesktopWindow [windowId=" + this.windowId + ", title=" + this.title + ", command=" + this.command + ", locAndSize=" + this.locAndSize.toString() + ", owningProcessId=" + this.owningProcessId + ", order=" + this.order + ", visible=" + this.visible + "]";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\OSDesktopWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */