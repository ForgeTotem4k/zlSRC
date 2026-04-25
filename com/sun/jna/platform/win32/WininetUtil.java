package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WininetUtil {
  public static Map<String, String> getCache() {
    Win32Exception win32Exception;
    ArrayList<Wininet.INTERNET_CACHE_ENTRY_INFO> arrayList = new ArrayList();
    WinNT.HANDLE hANDLE = null;
    Throwable throwable = null;
    int i = 0;
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    try {
      IntByReference intByReference = new IntByReference();
      hANDLE = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, intByReference);
      i = Native.getLastError();
      if (i == 259)
        return (Map)linkedHashMap; 
      if (i != 0 && i != 122)
        throw new Win32Exception(i); 
      Wininet.INTERNET_CACHE_ENTRY_INFO iNTERNET_CACHE_ENTRY_INFO = new Wininet.INTERNET_CACHE_ENTRY_INFO(intByReference.getValue());
      hANDLE = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, iNTERNET_CACHE_ENTRY_INFO, intByReference);
      if (hANDLE == null)
        throw new Win32Exception(Native.getLastError()); 
      arrayList.add(iNTERNET_CACHE_ENTRY_INFO);
      while (true) {
        intByReference = new IntByReference();
        boolean bool = Wininet.INSTANCE.FindNextUrlCacheEntry(hANDLE, null, intByReference);
        if (!bool) {
          i = Native.getLastError();
          if (i == 259)
            break; 
          if (i != 0 && i != 122)
            throw new Win32Exception(i); 
        } 
        iNTERNET_CACHE_ENTRY_INFO = new Wininet.INTERNET_CACHE_ENTRY_INFO(intByReference.getValue());
        bool = Wininet.INSTANCE.FindNextUrlCacheEntry(hANDLE, iNTERNET_CACHE_ENTRY_INFO, intByReference);
        if (!bool) {
          i = Native.getLastError();
          if (i == 259)
            break; 
          if (i != 0 && i != 122)
            throw new Win32Exception(i); 
        } 
        arrayList.add(iNTERNET_CACHE_ENTRY_INFO);
      } 
      for (Wininet.INTERNET_CACHE_ENTRY_INFO iNTERNET_CACHE_ENTRY_INFO1 : arrayList)
        linkedHashMap.put(iNTERNET_CACHE_ENTRY_INFO1.lpszSourceUrlName.getWideString(0L), (iNTERNET_CACHE_ENTRY_INFO1.lpszLocalFileName == null) ? "" : iNTERNET_CACHE_ENTRY_INFO1.lpszLocalFileName.getWideString(0L)); 
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      if (hANDLE != null && !Wininet.INSTANCE.FindCloseUrlCache(hANDLE) && win32Exception != null) {
        Win32Exception win32Exception1 = new Win32Exception(Native.getLastError());
        win32Exception1.addSuppressedReflected((Throwable)win32Exception);
        win32Exception = win32Exception1;
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
    return (Map)linkedHashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WininetUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */