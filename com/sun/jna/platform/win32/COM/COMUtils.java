package com.sun.jna.platform.win32.COM;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;

public abstract class COMUtils {
  public static final int S_OK = 0;
  
  public static final int S_FALSE = 1;
  
  public static final int E_UNEXPECTED = -2147418113;
  
  public static boolean SUCCEEDED(WinNT.HRESULT paramHRESULT) {
    return SUCCEEDED(paramHRESULT.intValue());
  }
  
  public static boolean SUCCEEDED(int paramInt) {
    return (paramInt >= 0);
  }
  
  public static boolean FAILED(WinNT.HRESULT paramHRESULT) {
    return FAILED(paramHRESULT.intValue());
  }
  
  public static boolean FAILED(int paramInt) {
    return (paramInt < 0);
  }
  
  public static void checkRC(WinNT.HRESULT paramHRESULT) {
    if (FAILED(paramHRESULT)) {
      String str;
      try {
        str = Kernel32Util.formatMessage(paramHRESULT) + "(HRESULT: " + Integer.toHexString(paramHRESULT.intValue()) + ")";
      } catch (LastErrorException lastErrorException) {
        str = "(HRESULT: " + Integer.toHexString(paramHRESULT.intValue()) + ")";
      } 
      throw new COMException(str, paramHRESULT);
    } 
  }
  
  public static void checkRC(WinNT.HRESULT paramHRESULT, OaIdl.EXCEPINFO paramEXCEPINFO, IntByReference paramIntByReference) {
    Object object = null;
    if (FAILED(paramHRESULT)) {
      StringBuilder stringBuilder = new StringBuilder();
      Integer integer1 = null;
      Integer integer2 = null;
      Integer integer3 = null;
      String str1 = null;
      String str2 = null;
      Integer integer4 = null;
      String str3 = null;
      if (paramIntByReference != null)
        integer1 = Integer.valueOf(paramIntByReference.getValue()); 
      try {
        stringBuilder.append(Kernel32Util.formatMessage(paramHRESULT));
      } catch (LastErrorException lastErrorException) {}
      stringBuilder.append("(HRESULT: ");
      stringBuilder.append(Integer.toHexString(paramHRESULT.intValue()));
      stringBuilder.append(")");
      if (paramEXCEPINFO != null) {
        integer2 = Integer.valueOf(paramEXCEPINFO.wCode.intValue());
        integer3 = Integer.valueOf(paramEXCEPINFO.scode.intValue());
        integer4 = Integer.valueOf(paramEXCEPINFO.dwHelpContext.intValue());
        if (paramEXCEPINFO.bstrSource != null) {
          str3 = paramEXCEPINFO.bstrSource.getValue();
          stringBuilder.append("\nSource:      ");
          stringBuilder.append(str3);
        } 
        if (paramEXCEPINFO.bstrDescription != null) {
          str1 = paramEXCEPINFO.bstrDescription.getValue();
          stringBuilder.append("\nDescription: ");
          stringBuilder.append(str1);
        } 
        if (paramEXCEPINFO.bstrHelpFile != null)
          str2 = paramEXCEPINFO.bstrHelpFile.getValue(); 
      } 
      throw new COMInvokeException(stringBuilder.toString(), paramHRESULT, integer1, str1, integer4, str2, integer3, str3, integer2);
    } 
    if (paramEXCEPINFO != null) {
      if (paramEXCEPINFO.bstrSource != null)
        OleAuto.INSTANCE.SysFreeString(paramEXCEPINFO.bstrSource); 
      if (paramEXCEPINFO.bstrDescription != null)
        OleAuto.INSTANCE.SysFreeString(paramEXCEPINFO.bstrDescription); 
      if (paramEXCEPINFO.bstrHelpFile != null)
        OleAuto.INSTANCE.SysFreeString(paramEXCEPINFO.bstrHelpFile); 
    } 
    if (object != null)
      throw object; 
  }
  
  public static ArrayList<COMInfo> getAllCOMInfoOnSystem() {
    WinReg.HKEYByReference hKEYByReference1 = new WinReg.HKEYByReference();
    WinReg.HKEYByReference hKEYByReference2 = new WinReg.HKEYByReference();
    ArrayList<COMInfo> arrayList = new ArrayList();
    try {
      hKEYByReference1 = Advapi32Util.registryGetKey(WinReg.HKEY_CLASSES_ROOT, "CLSID", 131097);
      Advapi32Util.InfoKey infoKey = Advapi32Util.registryQueryInfoKey(hKEYByReference1.getValue(), 131097);
      for (byte b = 0; b < infoKey.lpcSubKeys.getValue(); b++) {
        Advapi32Util.EnumKey enumKey = Advapi32Util.registryRegEnumKey(hKEYByReference1.getValue(), b);
        String str = Native.toString(enumKey.lpName);
        COMInfo cOMInfo = new COMInfo(str);
        hKEYByReference2 = Advapi32Util.registryGetKey(hKEYByReference1.getValue(), str, 131097);
        Advapi32Util.InfoKey infoKey1 = Advapi32Util.registryQueryInfoKey(hKEYByReference2.getValue(), 131097);
        for (byte b1 = 0; b1 < infoKey1.lpcSubKeys.getValue(); b1++) {
          Advapi32Util.EnumKey enumKey1 = Advapi32Util.registryRegEnumKey(hKEYByReference2.getValue(), b1);
          String str1 = Native.toString(enumKey1.lpName);
          if (str1.equals("InprocHandler32")) {
            cOMInfo.inprocHandler32 = (String)Advapi32Util.registryGetValue(hKEYByReference2.getValue(), str1, null);
          } else if (str1.equals("InprocServer32")) {
            cOMInfo.inprocServer32 = (String)Advapi32Util.registryGetValue(hKEYByReference2.getValue(), str1, null);
          } else if (str1.equals("LocalServer32")) {
            cOMInfo.localServer32 = (String)Advapi32Util.registryGetValue(hKEYByReference2.getValue(), str1, null);
          } else if (str1.equals("ProgID")) {
            cOMInfo.progID = (String)Advapi32Util.registryGetValue(hKEYByReference2.getValue(), str1, null);
          } else if (str1.equals("TypeLib")) {
            cOMInfo.typeLib = (String)Advapi32Util.registryGetValue(hKEYByReference2.getValue(), str1, null);
          } 
        } 
        Advapi32.INSTANCE.RegCloseKey(hKEYByReference2.getValue());
        arrayList.add(cOMInfo);
      } 
    } finally {
      Advapi32.INSTANCE.RegCloseKey(hKEYByReference1.getValue());
      Advapi32.INSTANCE.RegCloseKey(hKEYByReference2.getValue());
    } 
    return arrayList;
  }
  
  public static boolean comIsInitialized() {
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, 0);
    if (hRESULT.equals(W32Errors.S_OK)) {
      Ole32.INSTANCE.CoUninitialize();
      return false;
    } 
    if (hRESULT.equals(W32Errors.S_FALSE)) {
      Ole32.INSTANCE.CoUninitialize();
      return true;
    } 
    if (hRESULT.intValue() == -2147417850)
      return true; 
    checkRC(hRESULT);
    return false;
  }
  
  public static class COMInfo {
    public String clsid;
    
    public String inprocHandler32;
    
    public String inprocServer32;
    
    public String localServer32;
    
    public String progID;
    
    public String typeLib;
    
    public COMInfo() {}
    
    public COMInfo(String param1String) {
      this.clsid = param1String;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */