package com.sun.jna.platform.win32.COM.util;

import java.util.List;

public interface IRunningObjectTable {
  Iterable<IDispatch> enumRunning();
  
  <T> List<T> getActiveObjectsByInterface(Class<T> paramClass);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\IRunningObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */