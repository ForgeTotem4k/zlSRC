package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.IDispatchCallback;

public interface IComEventCallbackListener {
  void setDispatchCallbackListener(IDispatchCallback paramIDispatchCallback);
  
  void errorReceivingCallbackEvent(String paramString, Exception paramException);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\IComEventCallbackListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */