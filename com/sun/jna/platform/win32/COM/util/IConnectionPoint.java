package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;

@ComInterface(iid = "{B196B284-BAB4-101A-B69C-00AA00341D07}")
public interface IConnectionPoint {
  IComEventCallbackCookie advise(Class<?> paramClass, IComEventCallbackListener paramIComEventCallbackListener) throws COMException;
  
  void unadvise(Class<?> paramClass, IComEventCallbackCookie paramIComEventCallbackCookie) throws COMException;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\IConnectionPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */