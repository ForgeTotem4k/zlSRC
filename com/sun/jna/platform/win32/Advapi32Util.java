package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class Advapi32Util {
  public static String getUserName() {
    char[] arrayOfChar = new char[128];
    IntByReference intByReference = new IntByReference(arrayOfChar.length);
    boolean bool = Advapi32.INSTANCE.GetUserNameW(arrayOfChar, intByReference);
    if (!bool) {
      switch (Kernel32.INSTANCE.GetLastError()) {
        case 122:
          arrayOfChar = new char[intByReference.getValue()];
          break;
        default:
          throw new Win32Exception(Native.getLastError());
      } 
      bool = Advapi32.INSTANCE.GetUserNameW(arrayOfChar, intByReference);
    } 
    if (!bool)
      throw new Win32Exception(Native.getLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static Account getAccountByName(String paramString) {
    return getAccountByName(null, paramString);
  }
  
  public static Account getAccountByName(String paramString1, String paramString2) {
    IntByReference intByReference1 = new IntByReference(0);
    IntByReference intByReference2 = new IntByReference(0);
    PointerByReference pointerByReference = new PointerByReference();
    if (Advapi32.INSTANCE.LookupAccountName(paramString1, paramString2, null, intByReference1, null, intByReference2, pointerByReference))
      throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER"); 
    int i = Kernel32.INSTANCE.GetLastError();
    if (intByReference1.getValue() == 0 || i != 122)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference1.getValue());
    WinNT.PSID pSID = new WinNT.PSID((Pointer)memory);
    char[] arrayOfChar = new char[intByReference2.getValue() + 1];
    if (!Advapi32.INSTANCE.LookupAccountName(paramString1, paramString2, pSID, intByReference1, arrayOfChar, intByReference2, pointerByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Account account = new Account();
    account.accountType = pointerByReference.getPointer().getInt(0L);
    String[] arrayOfString1 = paramString2.split("\\\\", 2);
    String[] arrayOfString2 = paramString2.split("@", 2);
    if (arrayOfString1.length == 2) {
      account.name = arrayOfString1[1];
    } else if (arrayOfString2.length == 2) {
      account.name = arrayOfString2[0];
    } else {
      account.name = paramString2;
    } 
    if (intByReference2.getValue() > 0) {
      account.domain = Native.toString(arrayOfChar);
      account.fqn = account.domain + "\\" + account.name;
    } else {
      account.fqn = account.name;
    } 
    account.sid = pSID.getBytes();
    account.sidString = convertSidToStringSid(new WinNT.PSID(account.sid));
    return account;
  }
  
  public static Account getAccountBySid(WinNT.PSID paramPSID) {
    return getAccountBySid((String)null, paramPSID);
  }
  
  public static Account getAccountBySid(String paramString, WinNT.PSID paramPSID) {
    IntByReference intByReference1 = new IntByReference(257);
    IntByReference intByReference2 = new IntByReference(256);
    PointerByReference pointerByReference = new PointerByReference();
    char[] arrayOfChar1 = new char[intByReference2.getValue()];
    char[] arrayOfChar2 = new char[intByReference1.getValue()];
    int i = 0;
    if (!Advapi32.INSTANCE.LookupAccountSid(paramString, paramPSID, arrayOfChar2, intByReference1, arrayOfChar1, intByReference2, pointerByReference)) {
      i = Kernel32.INSTANCE.GetLastError();
      if (i != 1332)
        throw new Win32Exception(i); 
    } 
    Account account = new Account();
    if (i == 1332) {
      account.accountType = 8;
      account.name = "NONE_MAPPED";
    } else {
      account.accountType = pointerByReference.getPointer().getInt(0L);
      account.name = Native.toString(arrayOfChar2);
    } 
    account.domain = Native.toString(arrayOfChar1);
    if (account.domain.isEmpty()) {
      account.fqn = account.name;
    } else {
      account.fqn = account.domain + "\\" + account.name;
    } 
    account.sid = paramPSID.getBytes();
    account.sidString = convertSidToStringSid(paramPSID);
    return account;
  }
  
  public static String convertSidToStringSid(WinNT.PSID paramPSID) {
    PointerByReference pointerByReference = new PointerByReference();
    if (!Advapi32.INSTANCE.ConvertSidToStringSid(paramPSID, pointerByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Pointer pointer = pointerByReference.getValue();
    try {
      return pointer.getWideString(0L);
    } finally {
      Kernel32Util.freeLocalMemory(pointer);
    } 
  }
  
  public static byte[] convertStringSidToSid(String paramString) {
    WinNT.PSIDByReference pSIDByReference = new WinNT.PSIDByReference();
    if (!Advapi32.INSTANCE.ConvertStringSidToSid(paramString, pSIDByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    WinNT.PSID pSID = pSIDByReference.getValue();
    try {
      return pSID.getBytes();
    } finally {
      Kernel32Util.freeLocalMemory(pSID.getPointer());
    } 
  }
  
  public static boolean isWellKnownSid(String paramString, int paramInt) {
    WinNT.PSIDByReference pSIDByReference = new WinNT.PSIDByReference();
    if (!Advapi32.INSTANCE.ConvertStringSidToSid(paramString, pSIDByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    WinNT.PSID pSID = pSIDByReference.getValue();
    try {
      return Advapi32.INSTANCE.IsWellKnownSid(pSID, paramInt);
    } finally {
      Kernel32Util.freeLocalMemory(pSID.getPointer());
    } 
  }
  
  public static boolean isWellKnownSid(byte[] paramArrayOfbyte, int paramInt) {
    WinNT.PSID pSID = new WinNT.PSID(paramArrayOfbyte);
    return Advapi32.INSTANCE.IsWellKnownSid(pSID, paramInt);
  }
  
  public static int alignOnDWORD(int paramInt) {
    return paramInt + 3 & 0xFFFFFFFC;
  }
  
  public static int getAceSize(int paramInt) {
    return Native.getNativeSize(WinNT.ACCESS_ALLOWED_ACE.class, null) + paramInt - 4;
  }
  
  public static Account getAccountBySid(String paramString) {
    return getAccountBySid((String)null, paramString);
  }
  
  public static Account getAccountBySid(String paramString1, String paramString2) {
    return getAccountBySid(paramString1, new WinNT.PSID(convertStringSidToSid(paramString2)));
  }
  
  public static Account[] getTokenGroups(WinNT.HANDLE paramHANDLE) {
    IntByReference intByReference = new IntByReference();
    if (Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 2, null, 0, intByReference))
      throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER"); 
    int i = Kernel32.INSTANCE.GetLastError();
    if (i != 122)
      throw new Win32Exception(i); 
    WinNT.TOKEN_GROUPS tOKEN_GROUPS = new WinNT.TOKEN_GROUPS(intByReference.getValue());
    if (!Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 2, tOKEN_GROUPS, intByReference.getValue(), intByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    ArrayList<Account> arrayList = new ArrayList();
    for (WinNT.SID_AND_ATTRIBUTES sID_AND_ATTRIBUTES : tOKEN_GROUPS.getGroups()) {
      Account account;
      try {
        account = getAccountBySid(sID_AND_ATTRIBUTES.Sid);
      } catch (Exception exception) {
        account = new Account();
        account.sid = sID_AND_ATTRIBUTES.Sid.getBytes();
        account.sidString = convertSidToStringSid(sID_AND_ATTRIBUTES.Sid);
        account.name = account.sidString;
        account.fqn = account.sidString;
        account.accountType = 2;
      } 
      arrayList.add(account);
    } 
    return arrayList.<Account>toArray(new Account[0]);
  }
  
  public static Account getTokenPrimaryGroup(WinNT.HANDLE paramHANDLE) {
    Account account;
    IntByReference intByReference = new IntByReference();
    if (Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 5, null, 0, intByReference))
      throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER"); 
    int i = Kernel32.INSTANCE.GetLastError();
    if (i != 122)
      throw new Win32Exception(i); 
    WinNT.TOKEN_PRIMARY_GROUP tOKEN_PRIMARY_GROUP = new WinNT.TOKEN_PRIMARY_GROUP(intByReference.getValue());
    if (!Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 5, tOKEN_PRIMARY_GROUP, intByReference.getValue(), intByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    try {
      account = getAccountBySid(tOKEN_PRIMARY_GROUP.PrimaryGroup);
    } catch (Exception exception) {
      account = new Account();
      account.sid = tOKEN_PRIMARY_GROUP.PrimaryGroup.getBytes();
      account.sidString = convertSidToStringSid(tOKEN_PRIMARY_GROUP.PrimaryGroup);
      account.name = account.sidString;
      account.fqn = account.sidString;
      account.accountType = 2;
    } 
    return account;
  }
  
  public static Account getTokenAccount(WinNT.HANDLE paramHANDLE) {
    IntByReference intByReference = new IntByReference();
    if (Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 1, null, 0, intByReference))
      throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER"); 
    int i = Kernel32.INSTANCE.GetLastError();
    if (i != 122)
      throw new Win32Exception(i); 
    WinNT.TOKEN_USER tOKEN_USER = new WinNT.TOKEN_USER(intByReference.getValue());
    if (!Advapi32.INSTANCE.GetTokenInformation(paramHANDLE, 1, tOKEN_USER, intByReference.getValue(), intByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return getAccountBySid(tOKEN_USER.User.Sid);
  }
  
  public static Account[] getCurrentUserGroups() {
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    Win32Exception win32Exception = null;
    try {
      WinNT.HANDLE hANDLE1 = Kernel32.INSTANCE.GetCurrentThread();
      if (!Advapi32.INSTANCE.OpenThreadToken(hANDLE1, 10, true, hANDLEByReference)) {
        int i = Kernel32.INSTANCE.GetLastError();
        if (i != 1008)
          throw new Win32Exception(i); 
        WinNT.HANDLE hANDLE = Kernel32.INSTANCE.GetCurrentProcess();
        if (!Advapi32.INSTANCE.OpenProcessToken(hANDLE, 10, hANDLEByReference))
          throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      } 
      return getTokenGroups(hANDLEByReference.getValue());
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
      throw win32Exception;
    } finally {
      WinNT.HANDLE hANDLE = hANDLEByReference.getValue();
      if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLE))
        try {
          Kernel32Util.closeHandle(hANDLE);
        } catch (Win32Exception win32Exception1) {
          if (win32Exception == null) {
            win32Exception = win32Exception1;
          } else {
            win32Exception.addSuppressedReflected((Throwable)win32Exception1);
          } 
        }  
      if (win32Exception != null)
        throw win32Exception; 
    } 
  }
  
  public static boolean registryKeyExists(WinReg.HKEY paramHKEY, String paramString) {
    return registryKeyExists(paramHKEY, paramString, 0);
  }
  
  public static boolean registryKeyExists(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString, 0, 0x20019 | paramInt, hKEYByReference);
    switch (i) {
      case 0:
        Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
        return true;
      case 2:
        return false;
    } 
    throw new Win32Exception(i);
  }
  
  public static boolean registryValueExists(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryValueExists(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static boolean registryValueExists(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    switch (i) {
      case 0:
        break;
      case 2:
        return false;
      default:
        throw new Win32Exception(i);
    } 
    try {
      boolean bool;
      IntByReference intByReference1 = new IntByReference();
      IntByReference intByReference2 = new IntByReference();
      i = Advapi32.INSTANCE.RegQueryValueEx(hKEYByReference.getValue(), paramString2, 0, intByReference2, (Pointer)null, intByReference1);
      switch (i) {
        case 0:
        case 122:
        case 234:
          bool = true;
          return bool;
        case 2:
          bool = false;
          return bool;
      } 
      throw new Win32Exception(i);
    } finally {
      if (!WinBase.INVALID_HANDLE_VALUE.equals(hKEYByReference.getValue())) {
        i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
        if (i != 0)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static String registryGetStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetStringValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static String registryGetStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetStringValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static String registryGetStringValue(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (Pointer)null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 1 && intByReference2.getValue() != 2)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_SZ or REG_EXPAND_SZ"); 
    if (intByReference1.getValue() == 0)
      return ""; 
    Memory memory = new Memory((intByReference1.getValue() + Native.WCHAR_SIZE));
    memory.clear();
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (Pointer)memory, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? memory.getWideString(0L) : memory.getString(0L);
  }
  
  public static String registryGetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetExpandableStringValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static String registryGetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetExpandableStringValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static String registryGetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (char[])null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 2)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_SZ"); 
    if (intByReference1.getValue() == 0)
      return ""; 
    Memory memory = new Memory((intByReference1.getValue() + Native.WCHAR_SIZE));
    memory.clear();
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (Pointer)memory, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? memory.getWideString(0L) : memory.getString(0L);
  }
  
  public static String[] registryGetStringArray(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetStringArray(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static String[] registryGetStringArray(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetStringArray(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static String[] registryGetStringArray(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (char[])null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 7)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_SZ"); 
    Memory memory = new Memory((intByReference1.getValue() + 2 * Native.WCHAR_SIZE));
    memory.clear();
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (Pointer)memory, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return regMultiSzBufferToStringArray(memory);
  }
  
  static String[] regMultiSzBufferToStringArray(Memory paramMemory) {
    ArrayList<String> arrayList = new ArrayList();
    int i = 0;
    while (i < paramMemory.size()) {
      String str;
      if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
        str = paramMemory.getWideString(i);
        i += str.length() * Native.WCHAR_SIZE;
        i += Native.WCHAR_SIZE;
      } else {
        str = paramMemory.getString(i);
        i += str.length();
        i++;
      } 
      if (str.length() == 0)
        break; 
      arrayList.add(str);
    } 
    return arrayList.<String>toArray(new String[0]);
  }
  
  public static byte[] registryGetBinaryValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetBinaryValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static byte[] registryGetBinaryValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetBinaryValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static byte[] registryGetBinaryValue(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (Pointer)null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 3)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_BINARY"); 
    byte[] arrayOfByte = new byte[intByReference1.getValue()];
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, arrayOfByte, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return arrayOfByte;
  }
  
  public static int registryGetIntValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetIntValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static int registryGetIntValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetIntValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static int registryGetIntValue(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (char[])null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 4)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_DWORD"); 
    IntByReference intByReference3 = new IntByReference();
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, intByReference3, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return intByReference3.getValue();
  }
  
  public static long registryGetLongValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryGetLongValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static long registryGetLongValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetLongValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static long registryGetLongValue(WinReg.HKEY paramHKEY, String paramString) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, (char[])null, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    if (intByReference2.getValue() != 11)
      throw new RuntimeException("Unexpected registry type " + intByReference2.getValue() + ", expected REG_QWORD"); 
    LongByReference longByReference = new LongByReference();
    i = Advapi32.INSTANCE.RegQueryValueEx(paramHKEY, paramString, 0, intByReference2, longByReference, intByReference1);
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    return longByReference.getValue();
  }
  
  public static Object registryGetValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    String str;
    Integer integer = null;
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegGetValue(paramHKEY, paramString1, paramString2, 65535, intByReference1, (Pointer)null, intByReference2);
    if (intByReference1.getValue() == 0)
      return null; 
    if (i != 0 && i != 122)
      throw new Win32Exception(i); 
    Memory memory = new Memory((intByReference2.getValue() + Native.WCHAR_SIZE));
    memory.clear();
    i = Advapi32.INSTANCE.RegGetValue(paramHKEY, paramString1, paramString2, 65535, intByReference1, (Pointer)memory, intByReference2);
    if (i != 0)
      throw new Win32Exception(i); 
    if (intByReference1.getValue() == 4) {
      integer = Integer.valueOf(memory.getInt(0L));
    } else if (intByReference1.getValue() == 11) {
      Long long_ = Long.valueOf(memory.getLong(0L));
    } else if (intByReference1.getValue() == 3) {
      byte[] arrayOfByte = memory.getByteArray(0L, intByReference2.getValue());
    } else if (intByReference1.getValue() == 1 || intByReference1.getValue() == 2) {
      if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
        str = memory.getWideString(0L);
      } else {
        str = memory.getString(0L);
      } 
    } 
    return str;
  }
  
  public static boolean registryCreateKey(WinReg.HKEY paramHKEY, String paramString) {
    return registryCreateKey(paramHKEY, paramString, 0);
  }
  
  public static boolean registryCreateKey(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    IntByReference intByReference = new IntByReference();
    int i = Advapi32.INSTANCE.RegCreateKeyEx(paramHKEY, paramString, 0, null, 0, 0x20019 | paramInt, null, hKEYByReference, intByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
    if (i != 0)
      throw new Win32Exception(i); 
    return (1 == intByReference.getValue());
  }
  
  public static boolean registryCreateKey(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    return registryCreateKey(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static boolean registryCreateKey(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x4 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryCreateKey(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetIntValue(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(paramInt & 0xFF);
    arrayOfByte[1] = (byte)(paramInt >> 8 & 0xFF);
    arrayOfByte[2] = (byte)(paramInt >> 16 & 0xFF);
    arrayOfByte[3] = (byte)(paramInt >> 24 & 0xFF);
    int i = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString, 0, 4, arrayOfByte, 4);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registrySetIntValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    registrySetIntValue(paramHKEY, paramString1, paramString2, paramInt, 0);
  }
  
  public static void registrySetIntValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt1, int paramInt2) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt2, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetIntValue(hKEYByReference.getValue(), paramString2, paramInt1);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetLongValue(WinReg.HKEY paramHKEY, String paramString, long paramLong) {
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = (byte)(int)(paramLong & 0xFFL);
    arrayOfByte[1] = (byte)(int)(paramLong >> 8L & 0xFFL);
    arrayOfByte[2] = (byte)(int)(paramLong >> 16L & 0xFFL);
    arrayOfByte[3] = (byte)(int)(paramLong >> 24L & 0xFFL);
    arrayOfByte[4] = (byte)(int)(paramLong >> 32L & 0xFFL);
    arrayOfByte[5] = (byte)(int)(paramLong >> 40L & 0xFFL);
    arrayOfByte[6] = (byte)(int)(paramLong >> 48L & 0xFFL);
    arrayOfByte[7] = (byte)(int)(paramLong >> 56L & 0xFFL);
    int i = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString, 0, 11, arrayOfByte, 8);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registrySetLongValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, long paramLong) {
    registrySetLongValue(paramHKEY, paramString1, paramString2, paramLong, 0);
  }
  
  public static void registrySetLongValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, long paramLong, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetLongValue(hKEYByReference.getValue(), paramString2, paramLong);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    Memory memory;
    if (paramString2 == null)
      paramString2 = ""; 
    if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
      memory = new Memory(((paramString2.length() + 1) * Native.WCHAR_SIZE));
      memory.setWideString(0L, paramString2);
    } else {
      memory = new Memory((paramString2.length() + 1));
      memory.setString(0L, paramString2);
    } 
    int i = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString1, 0, 1, (Pointer)memory, (int)memory.size());
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registrySetStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String paramString3) {
    registrySetStringValue(paramHKEY, paramString1, paramString2, paramString3, 0);
  }
  
  public static void registrySetStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String paramString3, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetStringValue(hKEYByReference.getValue(), paramString2, paramString3);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    Memory memory;
    if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
      memory = new Memory(((paramString2.length() + 1) * Native.WCHAR_SIZE));
      memory.setWideString(0L, paramString2);
    } else {
      memory = new Memory((paramString2.length() + 1));
      memory.setString(0L, paramString2);
    } 
    int i = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString1, 0, 2, (Pointer)memory, (int)memory.size());
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registrySetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String paramString3) {
    registrySetExpandableStringValue(paramHKEY, paramString1, paramString2, paramString3, 0);
  }
  
  public static void registrySetExpandableStringValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String paramString3, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetExpandableStringValue(hKEYByReference.getValue(), paramString2, paramString3);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetStringArray(WinReg.HKEY paramHKEY, String paramString, String[] paramArrayOfString) {
    byte b = (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? Native.WCHAR_SIZE : 1;
    int i = 0;
    for (String str : paramArrayOfString) {
      i += str.length() * b;
      i += b;
    } 
    i += b;
    int j = 0;
    Memory memory = new Memory(i);
    memory.clear();
    for (String str : paramArrayOfString) {
      if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
        memory.setWideString(j, str);
      } else {
        memory.setString(j, str);
      } 
      j += str.length() * b;
      j += b;
    } 
    int k = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString, 0, 7, (Pointer)memory, i);
    if (k != 0)
      throw new Win32Exception(k); 
  }
  
  public static void registrySetStringArray(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String[] paramArrayOfString) {
    registrySetStringArray(paramHKEY, paramString1, paramString2, paramArrayOfString, 0);
  }
  
  public static void registrySetStringArray(WinReg.HKEY paramHKEY, String paramString1, String paramString2, String[] paramArrayOfString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetStringArray(hKEYByReference.getValue(), paramString2, paramArrayOfString);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registrySetBinaryValue(WinReg.HKEY paramHKEY, String paramString, byte[] paramArrayOfbyte) {
    int i = Advapi32.INSTANCE.RegSetValueEx(paramHKEY, paramString, 0, 3, paramArrayOfbyte, paramArrayOfbyte.length);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registrySetBinaryValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, byte[] paramArrayOfbyte) {
    registrySetBinaryValue(paramHKEY, paramString1, paramString2, paramArrayOfbyte, 0);
  }
  
  public static void registrySetBinaryValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, byte[] paramArrayOfbyte, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registrySetBinaryValue(hKEYByReference.getValue(), paramString2, paramArrayOfbyte);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registryDeleteKey(WinReg.HKEY paramHKEY, String paramString) {
    int i = Advapi32.INSTANCE.RegDeleteKey(paramHKEY, paramString);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registryDeleteKey(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    registryDeleteKey(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static void registryDeleteKey(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registryDeleteKey(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static void registryDeleteValue(WinReg.HKEY paramHKEY, String paramString) {
    int i = Advapi32.INSTANCE.RegDeleteValue(paramHKEY, paramString);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static void registryDeleteValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2) {
    registryDeleteValue(paramHKEY, paramString1, paramString2, 0);
  }
  
  public static void registryDeleteValue(WinReg.HKEY paramHKEY, String paramString1, String paramString2, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString1, 0, 0x2001F | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      registryDeleteValue(hKEYByReference.getValue(), paramString2);
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static String[] registryGetKeys(WinReg.HKEY paramHKEY) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryInfoKey(paramHKEY, null, null, null, intByReference1, intByReference2, null, null, null, null, null, null);
    if (i != 0)
      throw new Win32Exception(i); 
    ArrayList<String> arrayList = new ArrayList(intByReference1.getValue());
    char[] arrayOfChar = new char[intByReference2.getValue() + 1];
    for (byte b = 0; b < intByReference1.getValue(); b++) {
      IntByReference intByReference = new IntByReference(intByReference2.getValue() + 1);
      i = Advapi32.INSTANCE.RegEnumKeyEx(paramHKEY, b, arrayOfChar, intByReference, null, null, null, null);
      if (i != 0)
        throw new Win32Exception(i); 
      arrayList.add(Native.toString(arrayOfChar));
    } 
    return arrayList.<String>toArray(new String[0]);
  }
  
  public static String[] registryGetKeys(WinReg.HKEY paramHKEY, String paramString) {
    return registryGetKeys(paramHKEY, paramString, 0);
  }
  
  public static String[] registryGetKeys(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetKeys(hKEYByReference.getValue());
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static WinReg.HKEYByReference registryGetKey(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString, 0, paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    return hKEYByReference;
  }
  
  public static WinReg.HKEYByReference registryLoadAppKey(String paramString, int paramInt1, int paramInt2) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegLoadAppKey(paramString, hKEYByReference, paramInt1, paramInt2, 0);
    if (i != 0)
      throw new Win32Exception(i); 
    return hKEYByReference;
  }
  
  public static void registryCloseKey(WinReg.HKEY paramHKEY) {
    int i = Advapi32.INSTANCE.RegCloseKey(paramHKEY);
    if (i != 0)
      throw new Win32Exception(i); 
  }
  
  public static TreeMap<String, Object> registryGetValues(WinReg.HKEY paramHKEY) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    IntByReference intByReference3 = new IntByReference();
    int i = Advapi32.INSTANCE.RegQueryInfoKey(paramHKEY, null, null, null, null, null, null, intByReference1, intByReference2, intByReference3, null, null);
    if (i != 0)
      throw new Win32Exception(i); 
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    char[] arrayOfChar = new char[intByReference2.getValue() + 1];
    Memory memory = new Memory((intByReference3.getValue() + 2 * Native.WCHAR_SIZE));
    for (byte b = 0; b < intByReference1.getValue(); b++) {
      memory.clear();
      IntByReference intByReference4 = new IntByReference(intByReference2.getValue() + 1);
      IntByReference intByReference5 = new IntByReference(intByReference3.getValue());
      IntByReference intByReference6 = new IntByReference();
      i = Advapi32.INSTANCE.RegEnumValue(paramHKEY, b, arrayOfChar, intByReference4, (IntByReference)null, intByReference6, (Pointer)memory, intByReference5);
      if (i != 0)
        throw new Win32Exception(i); 
      String str = Native.toString(arrayOfChar);
      if (intByReference5.getValue() == 0) {
        switch (intByReference6.getValue()) {
          case 3:
            treeMap.put(str, new byte[0]);
            break;
          case 1:
          case 2:
            treeMap.put(str, new char[0]);
            break;
          case 7:
            treeMap.put(str, new String[0]);
            break;
          case 0:
            treeMap.put(str, null);
            break;
          default:
            throw new RuntimeException("Unsupported empty type: " + intByReference6.getValue());
        } 
      } else {
        ArrayList<String> arrayList;
        int j;
        switch (intByReference6.getValue()) {
          case 11:
            treeMap.put(str, Long.valueOf(memory.getLong(0L)));
            break;
          case 4:
            treeMap.put(str, Integer.valueOf(memory.getInt(0L)));
            break;
          case 1:
          case 2:
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
              treeMap.put(str, memory.getWideString(0L));
              break;
            } 
            treeMap.put(str, memory.getString(0L));
            break;
          case 3:
            treeMap.put(str, memory.getByteArray(0L, intByReference5.getValue()));
            break;
          case 7:
            arrayList = new ArrayList();
            j = 0;
            while (j < memory.size()) {
              String str1;
              if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                str1 = memory.getWideString(j);
                j += str1.length() * Native.WCHAR_SIZE;
                j += Native.WCHAR_SIZE;
              } else {
                str1 = memory.getString(j);
                j += str1.length();
                j++;
              } 
              if (str1.length() == 0)
                break; 
              arrayList.add(str1);
            } 
            treeMap.put(str, arrayList.toArray(new String[0]));
            break;
          default:
            throw new RuntimeException("Unsupported type: " + intByReference6.getValue());
        } 
      } 
    } 
    return (TreeMap)treeMap;
  }
  
  public static TreeMap<String, Object> registryGetValues(WinReg.HKEY paramHKEY, String paramString) {
    return registryGetValues(paramHKEY, paramString, 0);
  }
  
  public static TreeMap<String, Object> registryGetValues(WinReg.HKEY paramHKEY, String paramString, int paramInt) {
    WinReg.HKEYByReference hKEYByReference = new WinReg.HKEYByReference();
    int i = Advapi32.INSTANCE.RegOpenKeyEx(paramHKEY, paramString, 0, 0x20019 | paramInt, hKEYByReference);
    if (i != 0)
      throw new Win32Exception(i); 
    try {
      return registryGetValues(hKEYByReference.getValue());
    } finally {
      i = Advapi32.INSTANCE.RegCloseKey(hKEYByReference.getValue());
      if (i != 0)
        throw new Win32Exception(i); 
    } 
  }
  
  public static InfoKey registryQueryInfoKey(WinReg.HKEY paramHKEY, int paramInt) {
    InfoKey infoKey = new InfoKey(paramHKEY, paramInt);
    int i = Advapi32.INSTANCE.RegQueryInfoKey(paramHKEY, infoKey.lpClass, infoKey.lpcClass, null, infoKey.lpcSubKeys, infoKey.lpcMaxSubKeyLen, infoKey.lpcMaxClassLen, infoKey.lpcValues, infoKey.lpcMaxValueNameLen, infoKey.lpcMaxValueLen, infoKey.lpcbSecurityDescriptor, infoKey.lpftLastWriteTime);
    if (i != 0)
      throw new Win32Exception(i); 
    return infoKey;
  }
  
  public static EnumKey registryRegEnumKey(WinReg.HKEY paramHKEY, int paramInt) {
    EnumKey enumKey = new EnumKey(paramHKEY, paramInt);
    int i = Advapi32.INSTANCE.RegEnumKeyEx(paramHKEY, enumKey.dwIndex, enumKey.lpName, enumKey.lpcName, null, enumKey.lpClass, enumKey.lpcbClass, enumKey.lpftLastWriteTime);
    if (i != 0)
      throw new Win32Exception(i); 
    return enumKey;
  }
  
  public static String getEnvironmentBlock(Map<String, String> paramMap) {
    StringBuilder stringBuilder = new StringBuilder(paramMap.size() * 32);
    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = (String)entry.getValue();
      if (str2 != null)
        stringBuilder.append(str1).append("=").append(str2).append(false); 
    } 
    return stringBuilder.append(false).toString();
  }
  
  public static WinNT.ACE_HEADER[] getFileSecurity(String paramString, boolean paramBoolean) {
    byte b = 4;
    int i = 1024;
    while (true) {
      boolean bool = false;
      Memory memory = new Memory(i);
      IntByReference intByReference = new IntByReference();
      boolean bool1 = Advapi32.INSTANCE.GetFileSecurity(paramString, b, (Pointer)memory, i, intByReference);
      if (!bool1) {
        int k = Kernel32.INSTANCE.GetLastError();
        memory.clear();
        if (122 != k)
          throw new Win32Exception(k); 
      } 
      int j = intByReference.getValue();
      if (i < j) {
        bool = true;
        i = j;
        memory.clear();
      } 
      if (!bool) {
        WinNT.SECURITY_DESCRIPTOR_RELATIVE sECURITY_DESCRIPTOR_RELATIVE = new WinNT.SECURITY_DESCRIPTOR_RELATIVE((Pointer)memory);
        WinNT.ACL aCL = sECURITY_DESCRIPTOR_RELATIVE.getDiscretionaryACL();
        WinNT.ACE_HEADER[] arrayOfACE_HEADER = aCL.getACEs();
        if (paramBoolean) {
          ArrayList<WinNT.ACCESS_ACEStructure> arrayList = new ArrayList();
          HashMap<Object, Object> hashMap = new HashMap<>();
          for (WinNT.ACE_HEADER aCE_HEADER : arrayOfACE_HEADER) {
            if (aCE_HEADER instanceof WinNT.ACCESS_ACEStructure) {
              WinNT.ACCESS_ACEStructure aCCESS_ACEStructure1 = (WinNT.ACCESS_ACEStructure)aCE_HEADER;
              boolean bool2 = ((aCE_HEADER.AceFlags & 0x1F) != 0) ? true : false;
              String str = aCCESS_ACEStructure1.getSidString() + "/" + bool2 + "/" + aCE_HEADER.getClass().getName();
              WinNT.ACCESS_ACEStructure aCCESS_ACEStructure2 = (WinNT.ACCESS_ACEStructure)hashMap.get(str);
              if (aCCESS_ACEStructure2 != null) {
                int k = aCCESS_ACEStructure2.Mask;
                k |= aCCESS_ACEStructure1.Mask;
                aCCESS_ACEStructure2.Mask = k;
              } else {
                hashMap.put(str, aCCESS_ACEStructure1);
                arrayList.add(aCCESS_ACEStructure2);
              } 
            } else {
              arrayList.add(aCE_HEADER);
            } 
          } 
          return arrayList.<WinNT.ACE_HEADER>toArray(new WinNT.ACE_HEADER[0]);
        } 
        return arrayOfACE_HEADER;
      } 
    } 
  }
  
  private static Memory getSecurityDescriptorForFile(String paramString) {
    byte b = 7;
    IntByReference intByReference = new IntByReference();
    boolean bool = Advapi32.INSTANCE.GetFileSecurity(paramString, 7, null, 0, intByReference);
    if (!bool) {
      int j = Kernel32.INSTANCE.GetLastError();
      if (122 != j)
        throw new Win32Exception(j); 
    } 
    int i = intByReference.getValue();
    Memory memory = new Memory(i);
    bool = Advapi32.INSTANCE.GetFileSecurity(paramString, 7, (Pointer)memory, i, intByReference);
    if (!bool) {
      memory.clear();
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
    } 
    return memory;
  }
  
  public static Memory getSecurityDescriptorForObject(String paramString, int paramInt, boolean paramBoolean) {
    int i = 0x7 | (paramBoolean ? 8 : 0);
    PointerByReference pointerByReference = new PointerByReference();
    int j = Advapi32.INSTANCE.GetNamedSecurityInfo(paramString, paramInt, i, null, null, null, null, pointerByReference);
    if (j != 0)
      throw new Win32Exception(j); 
    int k = Advapi32.INSTANCE.GetSecurityDescriptorLength(pointerByReference.getValue());
    Memory memory = new Memory(k);
    Pointer pointer = pointerByReference.getValue();
    try {
      byte[] arrayOfByte = pointer.getByteArray(0L, k);
      memory.write(0L, arrayOfByte, 0, k);
      return memory;
    } finally {
      Kernel32Util.freeLocalMemory(pointer);
    } 
  }
  
  public static void setSecurityDescriptorForObject(String paramString, int paramInt, WinNT.SECURITY_DESCRIPTOR_RELATIVE paramSECURITY_DESCRIPTOR_RELATIVE, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    WinNT.PSID pSID1 = paramSECURITY_DESCRIPTOR_RELATIVE.getOwner();
    WinNT.PSID pSID2 = paramSECURITY_DESCRIPTOR_RELATIVE.getGroup();
    WinNT.ACL aCL1 = paramSECURITY_DESCRIPTOR_RELATIVE.getDiscretionaryACL();
    WinNT.ACL aCL2 = paramSECURITY_DESCRIPTOR_RELATIVE.getSystemACL();
    int i = 0;
    if (paramBoolean1) {
      if (pSID1 == null)
        throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain owner"); 
      if (!Advapi32.INSTANCE.IsValidSid(pSID1))
        throw new IllegalArgumentException("Owner PSID is invalid"); 
      i |= 0x1;
    } 
    if (paramBoolean2) {
      if (pSID2 == null)
        throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain group"); 
      if (!Advapi32.INSTANCE.IsValidSid(pSID2))
        throw new IllegalArgumentException("Group PSID is invalid"); 
      i |= 0x2;
    } 
    if (paramBoolean3) {
      if (aCL1 == null)
        throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain DACL"); 
      if (!Advapi32.INSTANCE.IsValidAcl(aCL1.getPointer()))
        throw new IllegalArgumentException("DACL is invalid"); 
      i |= 0x4;
    } 
    if (paramBoolean4) {
      if (aCL2 == null)
        throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain SACL"); 
      if (!Advapi32.INSTANCE.IsValidAcl(aCL2.getPointer()))
        throw new IllegalArgumentException("SACL is invalid"); 
      i |= 0x8;
    } 
    if (paramBoolean5)
      if ((paramSECURITY_DESCRIPTOR_RELATIVE.Control & 0x1000) != 0) {
        i |= Integer.MIN_VALUE;
      } else if ((paramSECURITY_DESCRIPTOR_RELATIVE.Control & 0x1000) == 0) {
        i |= 0x20000000;
      }  
    if (paramBoolean6)
      if ((paramSECURITY_DESCRIPTOR_RELATIVE.Control & 0x2000) != 0) {
        i |= 0x40000000;
      } else if ((paramSECURITY_DESCRIPTOR_RELATIVE.Control & 0x2000) == 0) {
        i |= 0x10000000;
      }  
    int j = Advapi32.INSTANCE.SetNamedSecurityInfo(paramString, paramInt, i, paramBoolean1 ? pSID1.getPointer() : null, paramBoolean2 ? pSID2.getPointer() : null, paramBoolean3 ? aCL1.getPointer() : null, paramBoolean4 ? aCL2.getPointer() : null);
    if (j != 0)
      throw new Win32Exception(j); 
  }
  
  public static boolean accessCheck(File paramFile, AccessCheckPermission paramAccessCheckPermission) {
    Memory memory = getSecurityDescriptorForFile(paramFile.getAbsolutePath().replace('/', '\\'));
    WinNT.HANDLEByReference hANDLEByReference1 = new WinNT.HANDLEByReference();
    WinNT.HANDLEByReference hANDLEByReference2 = new WinNT.HANDLEByReference();
    Win32Exception win32Exception = null;
    try {
      int i = 131086;
      WinNT.HANDLE hANDLE = Kernel32.INSTANCE.GetCurrentProcess();
      if (!Advapi32.INSTANCE.OpenProcessToken(hANDLE, i, hANDLEByReference1))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      if (!Advapi32.INSTANCE.DuplicateToken(hANDLEByReference1.getValue(), 2, hANDLEByReference2))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      WinNT.GENERIC_MAPPING gENERIC_MAPPING = new WinNT.GENERIC_MAPPING();
      gENERIC_MAPPING.genericRead = new WinDef.DWORD(1179785L);
      gENERIC_MAPPING.genericWrite = new WinDef.DWORD(1179926L);
      gENERIC_MAPPING.genericExecute = new WinDef.DWORD(1179808L);
      gENERIC_MAPPING.genericAll = new WinDef.DWORD(2032127L);
      WinDef.DWORDByReference dWORDByReference1 = new WinDef.DWORDByReference(new WinDef.DWORD(paramAccessCheckPermission.getCode()));
      Advapi32.INSTANCE.MapGenericMask(dWORDByReference1, gENERIC_MAPPING);
      WinNT.PRIVILEGE_SET pRIVILEGE_SET = new WinNT.PRIVILEGE_SET(1);
      pRIVILEGE_SET.PrivilegeCount = new WinDef.DWORD(0L);
      WinDef.DWORDByReference dWORDByReference2 = new WinDef.DWORDByReference(new WinDef.DWORD(pRIVILEGE_SET.size()));
      WinDef.DWORDByReference dWORDByReference3 = new WinDef.DWORDByReference();
      WinDef.BOOLByReference bOOLByReference = new WinDef.BOOLByReference();
      if (!Advapi32.INSTANCE.AccessCheck((Pointer)memory, hANDLEByReference2.getValue(), dWORDByReference1.getValue(), gENERIC_MAPPING, pRIVILEGE_SET, dWORDByReference2, dWORDByReference3, bOOLByReference))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return bOOLByReference.getValue().booleanValue();
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
      throw win32Exception;
    } finally {
      try {
        Kernel32Util.closeHandleRefs(new WinNT.HANDLEByReference[] { hANDLEByReference1, hANDLEByReference2 });
      } catch (Win32Exception win32Exception1) {
        if (win32Exception == null) {
          win32Exception = win32Exception1;
        } else {
          win32Exception.addSuppressedReflected((Throwable)win32Exception1);
        } 
      } 
      if (memory != null)
        memory.clear(); 
      if (win32Exception != null)
        throw win32Exception; 
    } 
  }
  
  public static WinNT.SECURITY_DESCRIPTOR_RELATIVE getFileSecurityDescriptor(File paramFile, boolean paramBoolean) {
    Memory memory = getSecurityDescriptorForObject(paramFile.getAbsolutePath().replaceAll("/", "\\"), 1, paramBoolean);
    return new WinNT.SECURITY_DESCRIPTOR_RELATIVE((Pointer)memory);
  }
  
  public static void setFileSecurityDescriptor(File paramFile, WinNT.SECURITY_DESCRIPTOR_RELATIVE paramSECURITY_DESCRIPTOR_RELATIVE, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6) {
    setSecurityDescriptorForObject(paramFile.getAbsolutePath().replaceAll("/", "\\"), 1, paramSECURITY_DESCRIPTOR_RELATIVE, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
  }
  
  public static void encryptFile(File paramFile) {
    String str = paramFile.getAbsolutePath();
    if (!Advapi32.INSTANCE.EncryptFile(str))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static void decryptFile(File paramFile) {
    String str = paramFile.getAbsolutePath();
    if (!Advapi32.INSTANCE.DecryptFile(str, new WinDef.DWORD(0L)))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static int fileEncryptionStatus(File paramFile) {
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
    String str = paramFile.getAbsolutePath();
    if (!Advapi32.INSTANCE.FileEncryptionStatus(str, dWORDByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return dWORDByReference.getValue().intValue();
  }
  
  public static void disableEncryption(File paramFile, boolean paramBoolean) {
    String str = paramFile.getAbsolutePath();
    if (!Advapi32.INSTANCE.EncryptionDisable(str, paramBoolean))
      throw new Win32Exception(Native.getLastError()); 
  }
  
  public static void backupEncryptedFile(File paramFile1, File paramFile2) {
    if (!paramFile2.isDirectory())
      throw new IllegalArgumentException("destDir must be a directory."); 
    WinDef.ULONG uLONG1 = new WinDef.ULONG(0L);
    WinDef.ULONG uLONG2 = new WinDef.ULONG(1L);
    if (paramFile1.isDirectory())
      uLONG2.setValue(3L); 
    String str1 = paramFile1.getAbsolutePath();
    PointerByReference pointerByReference = new PointerByReference();
    if (Advapi32.INSTANCE.OpenEncryptedFileRaw(str1, uLONG1, pointerByReference) != 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    WinBase.FE_EXPORT_FUNC fE_EXPORT_FUNC = new WinBase.FE_EXPORT_FUNC() {
        public WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONG param1ULONG) {
          byte[] arrayOfByte = param1Pointer1.getByteArray(0L, param1ULONG.intValue());
          try {
            outputStream.write(arrayOfByte);
          } catch (IOException iOException) {
            throw new RuntimeException(iOException);
          } 
          return new WinDef.DWORD(0L);
        }
      };
    if (Advapi32.INSTANCE.ReadEncryptedFileRaw(fE_EXPORT_FUNC, null, pointerByReference.getValue()) != 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    try {
      byteArrayOutputStream.close();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    Advapi32.INSTANCE.CloseEncryptedFileRaw(pointerByReference.getValue());
    String str2 = paramFile2.getAbsolutePath() + File.separator + paramFile1.getName();
    pointerByReference = new PointerByReference();
    if (Advapi32.INSTANCE.OpenEncryptedFileRaw(str2, uLONG2, pointerByReference) != 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    final IntByReference elementsReadWrapper = new IntByReference(0);
    WinBase.FE_IMPORT_FUNC fE_IMPORT_FUNC = new WinBase.FE_IMPORT_FUNC() {
        public WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONGByReference param1ULONGByReference) {
          int i = elementsReadWrapper.getValue();
          int j = outputStream.size() - i;
          int k = Math.min(j, param1ULONGByReference.getValue().intValue());
          param1Pointer1.write(0L, outputStream.toByteArray(), i, k);
          elementsReadWrapper.setValue(i + k);
          param1ULONGByReference.setValue(new WinDef.ULONG(k));
          return new WinDef.DWORD(0L);
        }
      };
    if (Advapi32.INSTANCE.WriteEncryptedFileRaw(fE_IMPORT_FUNC, null, pointerByReference.getValue()) != 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Advapi32.INSTANCE.CloseEncryptedFileRaw(pointerByReference.getValue());
  }
  
  public static boolean isCurrentProcessElevated() {
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    IntByReference intByReference = new IntByReference();
    if (Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 8, hANDLEByReference))
      try {
        WinNT.TOKEN_ELEVATION tOKEN_ELEVATION = new WinNT.TOKEN_ELEVATION();
        if (Advapi32.INSTANCE.GetTokenInformation(hANDLEByReference.getValue(), 20, tOKEN_ELEVATION, tOKEN_ELEVATION.size(), intByReference))
          return (tOKEN_ELEVATION.TokenIsElevated > 0); 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLEByReference.getValue());
      }  
    return false;
  }
  
  public static class Account {
    public String name;
    
    public String domain;
    
    public byte[] sid;
    
    public String sidString;
    
    public int accountType;
    
    public String fqn;
  }
  
  public static class InfoKey {
    public WinReg.HKEY hKey;
    
    public char[] lpClass = new char[260];
    
    public IntByReference lpcClass = new IntByReference(260);
    
    public IntByReference lpcSubKeys = new IntByReference();
    
    public IntByReference lpcMaxSubKeyLen = new IntByReference();
    
    public IntByReference lpcMaxClassLen = new IntByReference();
    
    public IntByReference lpcValues = new IntByReference();
    
    public IntByReference lpcMaxValueNameLen = new IntByReference();
    
    public IntByReference lpcMaxValueLen = new IntByReference();
    
    public IntByReference lpcbSecurityDescriptor = new IntByReference();
    
    public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();
    
    public InfoKey() {}
    
    public InfoKey(WinReg.HKEY param1HKEY, int param1Int) {
      this.hKey = param1HKEY;
      this.lpcbSecurityDescriptor = new IntByReference(param1Int);
    }
  }
  
  public static class EnumKey {
    public WinReg.HKEY hKey;
    
    public int dwIndex = 0;
    
    public char[] lpName = new char[255];
    
    public IntByReference lpcName = new IntByReference(255);
    
    public char[] lpClass = new char[255];
    
    public IntByReference lpcbClass = new IntByReference(255);
    
    public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();
    
    public EnumKey() {}
    
    public EnumKey(WinReg.HKEY param1HKEY, int param1Int) {
      this.hKey = param1HKEY;
      this.dwIndex = param1Int;
    }
  }
  
  public enum AccessCheckPermission {
    READ(-2147483648),
    WRITE(1073741824),
    EXECUTE(536870912);
    
    final int code;
    
    AccessCheckPermission(int param1Int1) {
      this.code = param1Int1;
    }
    
    public int getCode() {
      return this.code;
    }
  }
  
  public static class Privilege implements Closeable {
    private boolean currentlyImpersonating = false;
    
    private boolean privilegesEnabled = false;
    
    private final WinNT.LUID[] pLuids;
    
    public Privilege(String... param1VarArgs) throws IllegalArgumentException, Win32Exception {
      this.pLuids = new WinNT.LUID[param1VarArgs.length];
      byte b = 0;
      for (String str : param1VarArgs) {
        this.pLuids[b] = new WinNT.LUID();
        if (!Advapi32.INSTANCE.LookupPrivilegeValue(null, str, this.pLuids[b]))
          throw new IllegalArgumentException("Failed to find privilege \"" + param1VarArgs[b] + "\" - " + Kernel32.INSTANCE.GetLastError()); 
        b++;
      } 
    }
    
    public void close() {
      disable();
    }
    
    public Privilege enable() throws Win32Exception {
      if (this.privilegesEnabled)
        return this; 
      WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
      try {
        hANDLEByReference.setValue(getThreadToken());
        WinNT.TOKEN_PRIVILEGES tOKEN_PRIVILEGES = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
        for (byte b = 0; b < this.pLuids.length; b++)
          tOKEN_PRIVILEGES.Privileges[b] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[b], new WinDef.DWORD(2L)); 
        if (!Advapi32.INSTANCE.AdjustTokenPrivileges(hANDLEByReference.getValue(), false, tOKEN_PRIVILEGES, 0, null, null))
          throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
        this.privilegesEnabled = true;
      } catch (Win32Exception win32Exception) {
        if (this.currentlyImpersonating) {
          Advapi32.INSTANCE.SetThreadToken(null, null);
          this.currentlyImpersonating = false;
        } else if (this.privilegesEnabled) {
          WinNT.TOKEN_PRIVILEGES tOKEN_PRIVILEGES = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
          for (byte b = 0; b < this.pLuids.length; b++)
            tOKEN_PRIVILEGES.Privileges[b] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[b], new WinDef.DWORD(0L)); 
          Advapi32.INSTANCE.AdjustTokenPrivileges(hANDLEByReference.getValue(), false, tOKEN_PRIVILEGES, 0, null, null);
          this.privilegesEnabled = false;
        } 
        throw win32Exception;
      } finally {
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLEByReference.getValue()) && hANDLEByReference.getValue() != null) {
          Kernel32.INSTANCE.CloseHandle(hANDLEByReference.getValue());
          hANDLEByReference.setValue(null);
        } 
      } 
      return this;
    }
    
    public void disable() throws Win32Exception {
      WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
      try {
        hANDLEByReference.setValue(getThreadToken());
        if (this.currentlyImpersonating) {
          Advapi32.INSTANCE.SetThreadToken(null, null);
        } else if (this.privilegesEnabled) {
          WinNT.TOKEN_PRIVILEGES tOKEN_PRIVILEGES = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
          for (byte b = 0; b < this.pLuids.length; b++)
            tOKEN_PRIVILEGES.Privileges[b] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[b], new WinDef.DWORD(0L)); 
          Advapi32.INSTANCE.AdjustTokenPrivileges(hANDLEByReference.getValue(), false, tOKEN_PRIVILEGES, 0, null, null);
          this.privilegesEnabled = false;
        } 
      } finally {
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLEByReference.getValue()) && hANDLEByReference.getValue() != null) {
          Kernel32.INSTANCE.CloseHandle(hANDLEByReference.getValue());
          hANDLEByReference.setValue(null);
        } 
      } 
    }
    
    private WinNT.HANDLE getThreadToken() throws Win32Exception {
      WinNT.HANDLEByReference hANDLEByReference1 = new WinNT.HANDLEByReference();
      WinNT.HANDLEByReference hANDLEByReference2 = new WinNT.HANDLEByReference();
      try {
        if (!Advapi32.INSTANCE.OpenThreadToken(Kernel32.INSTANCE.GetCurrentThread(), 32, false, hANDLEByReference1)) {
          int i = Kernel32.INSTANCE.GetLastError();
          if (1008 != i)
            throw new Win32Exception(i); 
          if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 2, hANDLEByReference2))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
          if (!Advapi32.INSTANCE.DuplicateTokenEx(hANDLEByReference2.getValue(), 36, null, 2, 2, hANDLEByReference1))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
          if (!Advapi32.INSTANCE.SetThreadToken(null, hANDLEByReference1.getValue()))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
          this.currentlyImpersonating = true;
        } 
      } catch (Win32Exception win32Exception) {
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLEByReference1.getValue()) && hANDLEByReference1.getValue() != null) {
          Kernel32.INSTANCE.CloseHandle(hANDLEByReference1.getValue());
          hANDLEByReference1.setValue(null);
        } 
        throw win32Exception;
      } finally {
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLEByReference2.getValue()) && hANDLEByReference2.getValue() != null) {
          Kernel32.INSTANCE.CloseHandle(hANDLEByReference2.getValue());
          hANDLEByReference2.setValue(null);
        } 
      } 
      return hANDLEByReference1.getValue();
    }
  }
  
  public static class EventLogIterator implements Iterable<EventLogRecord>, Iterator<EventLogRecord> {
    private WinNT.HANDLE _h;
    
    private Memory _buffer = new Memory(65536L);
    
    private boolean _done = false;
    
    private int _dwRead = 0;
    
    private Pointer _pevlr = null;
    
    private int _flags;
    
    public EventLogIterator(String param1String) {
      this(null, param1String, 4);
    }
    
    public EventLogIterator(String param1String1, String param1String2, int param1Int) {
      this._flags = param1Int;
      this._h = Advapi32.INSTANCE.OpenEventLog(param1String1, param1String2);
      if (this._h == null)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    }
    
    private boolean read() {
      if (this._done || this._dwRead > 0)
        return false; 
      IntByReference intByReference1 = new IntByReference();
      IntByReference intByReference2 = new IntByReference();
      if (!Advapi32.INSTANCE.ReadEventLog(this._h, 0x1 | this._flags, 0, (Pointer)this._buffer, (int)this._buffer.size(), intByReference1, intByReference2)) {
        int i = Kernel32.INSTANCE.GetLastError();
        if (i == 122) {
          this._buffer = new Memory(intByReference2.getValue());
          if (!Advapi32.INSTANCE.ReadEventLog(this._h, 0x1 | this._flags, 0, (Pointer)this._buffer, (int)this._buffer.size(), intByReference1, intByReference2))
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
        } else {
          close();
          if (i != 38)
            throw new Win32Exception(i); 
          return false;
        } 
      } 
      this._dwRead = intByReference1.getValue();
      this._pevlr = (Pointer)this._buffer;
      return true;
    }
    
    public void close() {
      this._done = true;
      if (this._h != null) {
        if (!Advapi32.INSTANCE.CloseEventLog(this._h))
          throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
        this._h = null;
      } 
    }
    
    public Iterator<Advapi32Util.EventLogRecord> iterator() {
      return this;
    }
    
    public boolean hasNext() {
      read();
      return !this._done;
    }
    
    public Advapi32Util.EventLogRecord next() {
      read();
      Advapi32Util.EventLogRecord eventLogRecord = new Advapi32Util.EventLogRecord(this._pevlr);
      this._dwRead -= eventLogRecord.getLength();
      this._pevlr = this._pevlr.share(eventLogRecord.getLength());
      return eventLogRecord;
    }
    
    public void remove() {}
  }
  
  public static class EventLogRecord {
    private WinNT.EVENTLOGRECORD _record;
    
    private String _source;
    
    private byte[] _data;
    
    private String[] _strings;
    
    public WinNT.EVENTLOGRECORD getRecord() {
      return this._record;
    }
    
    public int getInstanceId() {
      return this._record.EventID.intValue();
    }
    
    @Deprecated
    public int getEventId() {
      return this._record.EventID.intValue();
    }
    
    public String getSource() {
      return this._source;
    }
    
    public int getStatusCode() {
      return this._record.EventID.intValue() & 0xFFFF;
    }
    
    public int getRecordNumber() {
      return this._record.RecordNumber.intValue();
    }
    
    public int getLength() {
      return this._record.Length.intValue();
    }
    
    public String[] getStrings() {
      return this._strings;
    }
    
    public Advapi32Util.EventLogType getType() {
      switch (this._record.EventType.intValue()) {
        case 0:
        case 4:
          return Advapi32Util.EventLogType.Informational;
        case 16:
          return Advapi32Util.EventLogType.AuditFailure;
        case 8:
          return Advapi32Util.EventLogType.AuditSuccess;
        case 1:
          return Advapi32Util.EventLogType.Error;
        case 2:
          return Advapi32Util.EventLogType.Warning;
      } 
      throw new RuntimeException("Invalid type: " + this._record.EventType.intValue());
    }
    
    public byte[] getData() {
      return this._data;
    }
    
    public EventLogRecord(Pointer param1Pointer) {
      this._record = new WinNT.EVENTLOGRECORD(param1Pointer);
      this._source = param1Pointer.getWideString(this._record.size());
      if (this._record.DataLength.intValue() > 0)
        this._data = param1Pointer.getByteArray(this._record.DataOffset.intValue(), this._record.DataLength.intValue()); 
      if (this._record.NumStrings.intValue() > 0) {
        ArrayList<String> arrayList = new ArrayList();
        int i = this._record.NumStrings.intValue();
        long l = this._record.StringOffset.intValue();
        while (i > 0) {
          String str = param1Pointer.getWideString(l);
          arrayList.add(str);
          l += (str.length() * Native.WCHAR_SIZE);
          l += Native.WCHAR_SIZE;
          i--;
        } 
        this._strings = arrayList.<String>toArray(new String[0]);
      } 
    }
  }
  
  public enum EventLogType {
    Error, Warning, Informational, AuditSuccess, AuditFailure;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Advapi32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */