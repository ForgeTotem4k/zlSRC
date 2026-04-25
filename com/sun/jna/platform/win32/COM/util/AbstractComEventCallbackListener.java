package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

public abstract class AbstractComEventCallbackListener implements IComEventCallbackListener {
  IDispatchCallback dispatchCallback = null;
  
  public void setDispatchCallbackListener(IDispatchCallback paramIDispatchCallback) {
    this.dispatchCallback = paramIDispatchCallback;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\AbstractComEventCallbackListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */