package com.sun.jna.platform.win32;

import com.sun.jna.Native;

public abstract class Ole32Util {
  public static Guid.GUID getGUIDFromString(String paramString) {
    Guid.GUID gUID = new Guid.GUID();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.IIDFromString(paramString, gUID);
    if (!hRESULT.equals(W32Errors.S_OK))
      throw new RuntimeException(hRESULT.toString()); 
    return gUID;
  }
  
  public static String getStringFromGUID(Guid.GUID paramGUID) {
    Guid.GUID gUID = new Guid.GUID(paramGUID.getPointer());
    byte b = 39;
    char[] arrayOfChar = new char[b];
    int i = Ole32.INSTANCE.StringFromGUID2(gUID, arrayOfChar, b);
    if (i == 0)
      throw new RuntimeException("StringFromGUID2"); 
    arrayOfChar[i - 1] = Character.MIN_VALUE;
    return Native.toString(arrayOfChar);
  }
  
  public static Guid.GUID generateGUID() {
    Guid.GUID gUID = new Guid.GUID();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoCreateGuid(gUID);
    if (!hRESULT.equals(W32Errors.S_OK))
      throw new RuntimeException(hRESULT.toString()); 
    return gUID;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Ole32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */