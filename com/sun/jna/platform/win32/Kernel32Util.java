package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Kernel32Util implements WinDef {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  public static final String VOLUME_GUID_PATH_PREFIX = "\\\\?\\Volume{";
  
  public static final String VOLUME_GUID_PATH_SUFFIX = "}\\";
  
  public static String getComputerName() {
    char[] arrayOfChar = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
    IntByReference intByReference = new IntByReference(arrayOfChar.length);
    if (!Kernel32.INSTANCE.GetComputerName(arrayOfChar, intByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static void freeLocalMemory(Pointer paramPointer) {
    Pointer pointer = Kernel32.INSTANCE.LocalFree(paramPointer);
    if (pointer != null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static void freeGlobalMemory(Pointer paramPointer) {
    Pointer pointer = Kernel32.INSTANCE.GlobalFree(paramPointer);
    if (pointer != null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static void closeHandleRefs(WinNT.HANDLEByReference... paramVarArgs) {
    Win32Exception win32Exception = null;
    for (WinNT.HANDLEByReference hANDLEByReference : paramVarArgs) {
      try {
        closeHandleRef(hANDLEByReference);
      } catch (Win32Exception win32Exception1) {
        if (win32Exception == null) {
          win32Exception = win32Exception1;
        } else {
          win32Exception.addSuppressedReflected((Throwable)win32Exception1);
        } 
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
  }
  
  public static void closeHandleRef(WinNT.HANDLEByReference paramHANDLEByReference) {
    closeHandle((paramHANDLEByReference == null) ? null : paramHANDLEByReference.getValue());
  }
  
  public static void closeHandles(WinNT.HANDLE... paramVarArgs) {
    Win32Exception win32Exception = null;
    for (WinNT.HANDLE hANDLE : paramVarArgs) {
      try {
        closeHandle(hANDLE);
      } catch (Win32Exception win32Exception1) {
        if (win32Exception == null) {
          win32Exception = win32Exception1;
        } else {
          win32Exception.addSuppressedReflected((Throwable)win32Exception1);
        } 
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
  }
  
  public static void closeHandle(WinNT.HANDLE paramHANDLE) {
    if (paramHANDLE == null)
      return; 
    if (!Kernel32.INSTANCE.CloseHandle(paramHANDLE))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static String formatMessage(int paramInt) {
    return formatMessage(paramInt, 0, 0);
  }
  
  public static String formatMessage(int paramInt1, int paramInt2, int paramInt3) {
    PointerByReference pointerByReference = new PointerByReference();
    int i = Kernel32.INSTANCE.FormatMessage(4864, null, paramInt1, WinNT.LocaleMacros.MAKELANGID(paramInt2, paramInt3), pointerByReference, 0, null);
    if (i == 0)
      throw new LastErrorException(Native.getLastError()); 
    Pointer pointer = pointerByReference.getValue();
    try {
      String str = pointer.getWideString(0L);
      return str.trim();
    } finally {
      freeLocalMemory(pointer);
    } 
  }
  
  public static String formatMessage(WinNT.HRESULT paramHRESULT) {
    return formatMessage(paramHRESULT.intValue());
  }
  
  public static String formatMessage(WinNT.HRESULT paramHRESULT, int paramInt1, int paramInt2) {
    return formatMessage(paramHRESULT.intValue(), paramInt1, paramInt2);
  }
  
  public static String formatMessageFromLastErrorCode(int paramInt) {
    return formatMessage(W32Errors.HRESULT_FROM_WIN32(paramInt));
  }
  
  public static String formatMessageFromLastErrorCode(int paramInt1, int paramInt2, int paramInt3) {
    return formatMessage(W32Errors.HRESULT_FROM_WIN32(paramInt1), paramInt2, paramInt3);
  }
  
  public static String getLastErrorMessage() {
    return formatMessageFromLastErrorCode(Kernel32.INSTANCE.GetLastError());
  }
  
  public static String getLastErrorMessage(int paramInt1, int paramInt2) {
    return formatMessageFromLastErrorCode(Kernel32.INSTANCE.GetLastError(), paramInt1, paramInt2);
  }
  
  public static String getTempPath() {
    WinDef.DWORD dWORD = new WinDef.DWORD(260L);
    char[] arrayOfChar = new char[dWORD.intValue()];
    if (Kernel32.INSTANCE.GetTempPath(dWORD, arrayOfChar).intValue() == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static void deleteFile(String paramString) {
    if (!Kernel32.INSTANCE.DeleteFile(paramString))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static List<String> getLogicalDriveStrings() {
    WinDef.DWORD dWORD = Kernel32.INSTANCE.GetLogicalDriveStrings(new WinDef.DWORD(0L), null);
    if (dWORD.intValue() <= 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    char[] arrayOfChar = new char[dWORD.intValue()];
    dWORD = Kernel32.INSTANCE.GetLogicalDriveStrings(dWORD, arrayOfChar);
    int i = dWORD.intValue();
    if (i <= 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toStringList(arrayOfChar, 0, i);
  }
  
  public static int getFileAttributes(String paramString) {
    int i = Kernel32.INSTANCE.GetFileAttributes(paramString);
    if (i == -1)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return i;
  }
  
  public static int getFileType(String paramString) throws FileNotFoundException {
    File file = new File(paramString);
    if (!file.exists())
      throw new FileNotFoundException(paramString); 
    WinNT.HANDLE hANDLE = null;
    Win32Exception win32Exception = null;
    try {
      int j;
      hANDLE = Kernel32.INSTANCE.CreateFile(paramString, -2147483648, 1, new WinBase.SECURITY_ATTRIBUTES(), 3, 128, (new WinNT.HANDLEByReference()).getValue());
      if (WinBase.INVALID_HANDLE_VALUE.equals(hANDLE))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      int i = Kernel32.INSTANCE.GetFileType(hANDLE);
      switch (i) {
        case 0:
          j = Kernel32.INSTANCE.GetLastError();
          switch (j) {
            case 0:
              break;
          } 
          throw new Win32Exception(j);
      } 
      return i;
    } catch (Win32Exception win32Exception1) {
      throw win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static int getDriveType(String paramString) {
    return Kernel32.INSTANCE.GetDriveType(paramString);
  }
  
  public static String getEnvironmentVariable(String paramString) {
    int i = Kernel32.INSTANCE.GetEnvironmentVariable(paramString, null, 0);
    if (i == 0)
      return null; 
    if (i < 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    char[] arrayOfChar = new char[i];
    i = Kernel32.INSTANCE.GetEnvironmentVariable(paramString, arrayOfChar, arrayOfChar.length);
    if (i <= 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static Map<String, String> getEnvironmentVariables() {
    Pointer pointer = Kernel32.INSTANCE.GetEnvironmentStrings();
    if (pointer == null)
      throw new LastErrorException(Kernel32.INSTANCE.GetLastError()); 
    try {
      return getEnvironmentVariables(pointer, 0L);
    } finally {
      if (!Kernel32.INSTANCE.FreeEnvironmentStrings(pointer))
        throw new LastErrorException(Kernel32.INSTANCE.GetLastError()); 
    } 
  }
  
  public static Map<String, String> getEnvironmentVariables(Pointer paramPointer, long paramLong) {
    if (paramPointer == null)
      return null; 
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    boolean bool = isWideCharEnvironmentStringBlock(paramPointer, paramLong);
    long l1 = bool ? 2L : 1L;
    for (long l2 = paramLong;; l2 += (i + 1) * l1) {
      String str1 = readEnvironmentStringBlockEntry(paramPointer, l2, bool);
      int i = str1.length();
      if (i == 0)
        return (Map)treeMap; 
      int j = str1.indexOf('=');
      if (j < 0)
        throw new IllegalArgumentException("Missing variable value separator in " + str1); 
      String str2 = str1.substring(0, j);
      String str3 = str1.substring(j + 1);
      treeMap.put(str2, str3);
    } 
  }
  
  public static String readEnvironmentStringBlockEntry(Pointer paramPointer, long paramLong, boolean paramBoolean) {
    long l1 = findEnvironmentStringBlockEntryEnd(paramPointer, paramLong, paramBoolean);
    int i = (int)(l1 - paramLong);
    if (i == 0)
      return ""; 
    int j = paramBoolean ? (i / 2) : i;
    char[] arrayOfChar = new char[j];
    long l2 = paramLong;
    long l3 = paramBoolean ? 2L : 1L;
    ByteOrder byteOrder = ByteOrder.nativeOrder();
    byte b = 0;
    while (b < arrayOfChar.length) {
      byte b1 = paramPointer.getByte(l2);
      if (paramBoolean) {
        byte b2 = paramPointer.getByte(l2 + 1L);
        if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
          arrayOfChar[b] = (char)(b2 << 8 & 0xFF00 | b1 & 0xFF);
        } else {
          arrayOfChar[b] = (char)(b1 << 8 & 0xFF00 | b2 & 0xFF);
        } 
      } else {
        arrayOfChar[b] = (char)(b1 & 0xFF);
      } 
      b++;
      l2 += l3;
    } 
    return new String(arrayOfChar);
  }
  
  public static long findEnvironmentStringBlockEntryEnd(Pointer paramPointer, long paramLong, boolean paramBoolean) {
    long l1 = paramLong;
    long l2 = paramBoolean ? 2L : 1L;
    while (true) {
      byte b = paramPointer.getByte(l1);
      if (b == 0)
        return l1; 
      l1 += l2;
    } 
  }
  
  public static boolean isWideCharEnvironmentStringBlock(Pointer paramPointer, long paramLong) {
    byte b1 = paramPointer.getByte(paramLong);
    byte b2 = paramPointer.getByte(paramLong + 1L);
    ByteOrder byteOrder = ByteOrder.nativeOrder();
    return ByteOrder.LITTLE_ENDIAN.equals(byteOrder) ? isWideCharEnvironmentStringBlock(b2) : isWideCharEnvironmentStringBlock(b1);
  }
  
  private static boolean isWideCharEnvironmentStringBlock(byte paramByte) {
    return !(paramByte != 0);
  }
  
  public static final int getPrivateProfileInt(String paramString1, String paramString2, int paramInt, String paramString3) {
    return Kernel32.INSTANCE.GetPrivateProfileInt(paramString1, paramString2, paramInt, paramString3);
  }
  
  public static final String getPrivateProfileString(String paramString1, String paramString2, String paramString3, String paramString4) {
    char[] arrayOfChar = new char[1024];
    Kernel32.INSTANCE.GetPrivateProfileString(paramString1, paramString2, paramString3, arrayOfChar, new WinDef.DWORD(arrayOfChar.length), paramString4);
    return Native.toString(arrayOfChar);
  }
  
  public static final void writePrivateProfileString(String paramString1, String paramString2, String paramString3, String paramString4) {
    if (!Kernel32.INSTANCE.WritePrivateProfileString(paramString1, paramString2, paramString3, paramString4))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation() {
    int i = (new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION()).size();
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference(new WinDef.DWORD(i));
    while (true) {
      Memory memory = new Memory(dWORDByReference.getValue().intValue());
      if (!Kernel32.INSTANCE.GetLogicalProcessorInformation((Pointer)memory, dWORDByReference)) {
        int k = Kernel32.INSTANCE.GetLastError();
        if (k != 122)
          throw new Win32Exception(k); 
        continue;
      } 
      WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION sYSTEM_LOGICAL_PROCESSOR_INFORMATION = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION((Pointer)memory);
      int j = dWORDByReference.getValue().intValue() / i;
      return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[])sYSTEM_LOGICAL_PROCESSOR_INFORMATION.toArray((Structure[])new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[j]);
    } 
  }
  
  public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] getLogicalProcessorInformationEx(int paramInt) {
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference(new WinDef.DWORD(1L));
    while (true) {
      Memory memory = new Memory(dWORDByReference.getValue().intValue());
      if (!Kernel32.INSTANCE.GetLogicalProcessorInformationEx(paramInt, (Pointer)memory, dWORDByReference)) {
        int j = Kernel32.INSTANCE.GetLastError();
        if (j != 122)
          throw new Win32Exception(j); 
        continue;
      } 
      ArrayList<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX> arrayList = new ArrayList();
      int i;
      for (i = 0; i < dWORDByReference.getValue().intValue(); i += sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.size) {
        WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX = WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.fromPointer(memory.share(i));
        arrayList.add(sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX);
      } 
      return arrayList.<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX>toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[0]);
    } 
  }
  
  public static final String[] getPrivateProfileSection(String paramString1, String paramString2) {
    char[] arrayOfChar = new char[32768];
    if (Kernel32.INSTANCE.GetPrivateProfileSection(paramString1, arrayOfChar, new WinDef.DWORD(arrayOfChar.length), paramString2).intValue() == 0) {
      int i = Kernel32.INSTANCE.GetLastError();
      if (i == 0)
        return EMPTY_STRING_ARRAY; 
      throw new Win32Exception(i);
    } 
    return (new String(arrayOfChar)).split("\000");
  }
  
  public static final String[] getPrivateProfileSectionNames(String paramString) {
    char[] arrayOfChar = new char[65536];
    if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(arrayOfChar, new WinDef.DWORD(arrayOfChar.length), paramString).intValue() == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return (new String(arrayOfChar)).split("\000");
  }
  
  public static final void writePrivateProfileSection(String paramString1, String[] paramArrayOfString, String paramString2) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String str : paramArrayOfString)
      stringBuilder.append(str).append(false); 
    stringBuilder.append(false);
    if (!Kernel32.INSTANCE.WritePrivateProfileSection(paramString1, stringBuilder.toString(), paramString2))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static final List<String> queryDosDevice(String paramString, int paramInt) {
    char[] arrayOfChar = new char[paramInt];
    int i = Kernel32.INSTANCE.QueryDosDevice(paramString, arrayOfChar, arrayOfChar.length);
    if (i == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return Native.toStringList(arrayOfChar, 0, i);
  }
  
  public static final List<String> getVolumePathNamesForVolumeName(String paramString) {
    char[] arrayOfChar = new char[261];
    IntByReference intByReference = new IntByReference();
    if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(paramString, arrayOfChar, arrayOfChar.length, intByReference)) {
      int j = Kernel32.INSTANCE.GetLastError();
      if (j != 234)
        throw new Win32Exception(j); 
      int k = intByReference.getValue();
      arrayOfChar = new char[k];
      if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(paramString, arrayOfChar, arrayOfChar.length, intByReference))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } 
    int i = intByReference.getValue();
    return Native.toStringList(arrayOfChar, 0, i);
  }
  
  public static final String extractVolumeGUID(String paramString) {
    if (paramString == null || paramString.length() <= "\\\\?\\Volume{".length() + "}\\".length() || !paramString.startsWith("\\\\?\\Volume{") || !paramString.endsWith("}\\"))
      throw new IllegalArgumentException("Bad volume GUID path format: " + paramString); 
    return paramString.substring("\\\\?\\Volume{".length(), paramString.length() - "}\\".length());
  }
  
  public static final String QueryFullProcessImageName(int paramInt1, int paramInt2) {
    WinNT.HANDLE hANDLE = null;
    Win32Exception win32Exception = null;
    try {
      hANDLE = Kernel32.INSTANCE.OpenProcess(1040, false, paramInt1);
      if (hANDLE == null)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return QueryFullProcessImageName(hANDLE, paramInt2);
    } catch (Win32Exception win32Exception1) {
      throw win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static final String QueryFullProcessImageName(WinNT.HANDLE paramHANDLE, int paramInt) {
    char c = 'Ą';
    IntByReference intByReference = new IntByReference();
    while (true) {
      char[] arrayOfChar = new char[c];
      intByReference.setValue(c);
      if (Kernel32.INSTANCE.QueryFullProcessImageName(paramHANDLE, paramInt, arrayOfChar, intByReference))
        return new String(arrayOfChar, 0, intByReference.getValue()); 
      c += 'Ѐ';
      if (Kernel32.INSTANCE.GetLastError() != 122)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } 
  }
  
  public static byte[] getResource(String paramString1, String paramString2, String paramString3) {
    Win32Exception win32Exception;
    WinDef.HMODULE hMODULE = Kernel32.INSTANCE.LoadLibraryEx(paramString1, null, 2);
    if (hMODULE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Throwable throwable = null;
    Pointer pointer = null;
    int i = 0;
    byte[] arrayOfByte = null;
    try {
      Memory memory1;
      Memory memory2;
      Pointer pointer1 = null;
      try {
        pointer1 = new Pointer(Long.parseLong(paramString2));
      } catch (NumberFormatException numberFormatException) {
        memory1 = new Memory((Native.WCHAR_SIZE * (paramString2.length() + 1)));
        memory1.setWideString(0L, paramString2);
      } 
      Pointer pointer2 = null;
      try {
        pointer2 = new Pointer(Long.parseLong(paramString3));
      } catch (NumberFormatException numberFormatException) {
        memory2 = new Memory((Native.WCHAR_SIZE * (paramString3.length() + 1)));
        memory2.setWideString(0L, paramString3);
      } 
      WinDef.HRSRC hRSRC = Kernel32.INSTANCE.FindResource(hMODULE, (Pointer)memory2, (Pointer)memory1);
      if (hRSRC == null)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      WinNT.HANDLE hANDLE = Kernel32.INSTANCE.LoadResource(hMODULE, hRSRC);
      if (hANDLE == null)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      i = Kernel32.INSTANCE.SizeofResource(hMODULE, hRSRC);
      if (i == 0)
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      pointer = Kernel32.INSTANCE.LockResource(hANDLE);
      if (pointer == null)
        throw new IllegalStateException("LockResource returned null."); 
      arrayOfByte = pointer.getByteArray(0L, i);
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      if (hMODULE != null && !Kernel32.INSTANCE.FreeLibrary(hMODULE)) {
        Win32Exception win32Exception1 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
        if (win32Exception != null)
          win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
        throw win32Exception1;
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
    return arrayOfByte;
  }
  
  public static Map<String, List<String>> getResourceNames(String paramString) {
    Win32Exception win32Exception;
    WinDef.HMODULE hMODULE = Kernel32.INSTANCE.LoadLibraryEx(paramString, null, 2);
    if (hMODULE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    final ArrayList types = new ArrayList();
    final LinkedHashMap<Object, Object> result = new LinkedHashMap<>();
    WinBase.EnumResTypeProc enumResTypeProc = new WinBase.EnumResTypeProc() {
        public boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2) {
          if (Pointer.nativeValue(param1Pointer1) <= 65535L) {
            types.add(Pointer.nativeValue(param1Pointer1) + "");
          } else {
            types.add(param1Pointer1.getWideString(0L));
          } 
          return true;
        }
      };
    WinBase.EnumResNameProc enumResNameProc = new WinBase.EnumResNameProc() {
        public boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2, Pointer param1Pointer3) {
          String str = "";
          if (Pointer.nativeValue(param1Pointer1) <= 65535L) {
            str = Pointer.nativeValue(param1Pointer1) + "";
          } else {
            str = param1Pointer1.getWideString(0L);
          } 
          if (Pointer.nativeValue(param1Pointer2) < 65535L) {
            ((List<String>)result.get(str)).add(Pointer.nativeValue(param1Pointer2) + "");
          } else {
            ((List<String>)result.get(str)).add(param1Pointer2.getWideString(0L));
          } 
          return true;
        }
      };
    Throwable throwable = null;
    try {
      if (!Kernel32.INSTANCE.EnumResourceTypes(hMODULE, enumResTypeProc, null))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      for (String str : arrayList) {
        Memory memory;
        linkedHashMap.put(str, new ArrayList());
        Pointer pointer = null;
        try {
          pointer = new Pointer(Long.parseLong(str));
        } catch (NumberFormatException numberFormatException) {
          memory = new Memory((Native.WCHAR_SIZE * (str.length() + 1)));
          memory.setWideString(0L, str);
        } 
        boolean bool = Kernel32.INSTANCE.EnumResourceNames(hMODULE, (Pointer)memory, enumResNameProc, null);
        if (!bool)
          throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      } 
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      if (hMODULE != null && !Kernel32.INSTANCE.FreeLibrary(hMODULE)) {
        Win32Exception win32Exception1 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
        if (win32Exception != null)
          win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
        throw win32Exception1;
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
    return (Map)linkedHashMap;
  }
  
  public static List<Tlhelp32.MODULEENTRY32W> getModules(int paramInt) {
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD(paramInt));
    if (hANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Win32Exception win32Exception = null;
    try {
      Tlhelp32.MODULEENTRY32W mODULEENTRY32W1 = new Tlhelp32.MODULEENTRY32W();
      if (!Kernel32.INSTANCE.Module32FirstW(hANDLE, mODULEENTRY32W1))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      ArrayList<Tlhelp32.MODULEENTRY32W> arrayList = new ArrayList();
      arrayList.add(mODULEENTRY32W1);
      for (Tlhelp32.MODULEENTRY32W mODULEENTRY32W2 = new Tlhelp32.MODULEENTRY32W(); Kernel32.INSTANCE.Module32NextW(hANDLE, mODULEENTRY32W2); mODULEENTRY32W2 = new Tlhelp32.MODULEENTRY32W())
        arrayList.add(mODULEENTRY32W2); 
      int i = Kernel32.INSTANCE.GetLastError();
      if (i != 0 && i != 18)
        throw new Win32Exception(i); 
      return arrayList;
    } catch (Win32Exception win32Exception1) {
      throw win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static String expandEnvironmentStrings(String paramString) {
    Memory memory;
    if (paramString == null)
      return ""; 
    int i = Kernel32.INSTANCE.ExpandEnvironmentStrings(paramString, null, 0);
    if (i == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
      memory = new Memory((i * Native.WCHAR_SIZE));
    } else {
      memory = new Memory((i + 1));
    } 
    i = Kernel32.INSTANCE.ExpandEnvironmentStrings(paramString, (Pointer)memory, i);
    if (i == 0)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? memory.getWideString(0L) : memory.getString(0L);
  }
  
  public static WinDef.DWORD getCurrentProcessPriority() {
    WinDef.DWORD dWORD = Kernel32.INSTANCE.GetPriorityClass(Kernel32.INSTANCE.GetCurrentProcess());
    if (!isValidPriorityClass(dWORD))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return dWORD;
  }
  
  public static void setCurrentProcessPriority(WinDef.DWORD paramDWORD) {
    if (!isValidPriorityClass(paramDWORD))
      throw new IllegalArgumentException("The given priority value is invalid!"); 
    if (!Kernel32.INSTANCE.SetPriorityClass(Kernel32.INSTANCE.GetCurrentProcess(), paramDWORD))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static void setCurrentProcessBackgroundMode(boolean paramBoolean) {
    WinDef.DWORD dWORD = paramBoolean ? Kernel32.PROCESS_MODE_BACKGROUND_BEGIN : Kernel32.PROCESS_MODE_BACKGROUND_END;
    if (!Kernel32.INSTANCE.SetPriorityClass(Kernel32.INSTANCE.GetCurrentProcess(), dWORD))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static int getCurrentThreadPriority() {
    int i = Kernel32.INSTANCE.GetThreadPriority(Kernel32.INSTANCE.GetCurrentThread());
    if (!isValidThreadPriority(i))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return i;
  }
  
  public static void setCurrentThreadPriority(int paramInt) {
    if (!isValidThreadPriority(paramInt))
      throw new IllegalArgumentException("The given priority value is invalid!"); 
    if (!Kernel32.INSTANCE.SetThreadPriority(Kernel32.INSTANCE.GetCurrentThread(), paramInt))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static void setCurrentThreadBackgroundMode(boolean paramBoolean) {
    int i = paramBoolean ? 65536 : 131072;
    if (!Kernel32.INSTANCE.SetThreadPriority(Kernel32.INSTANCE.GetCurrentThread(), i))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public static WinDef.DWORD getProcessPriority(int paramInt) {
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, paramInt);
    if (hANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Win32Exception win32Exception = null;
    try {
      WinDef.DWORD dWORD = Kernel32.INSTANCE.GetPriorityClass(hANDLE);
      if (!isValidPriorityClass(dWORD))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return dWORD;
    } catch (Win32Exception win32Exception1) {
      throw win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static void setProcessPriority(int paramInt, WinDef.DWORD paramDWORD) {
    if (!isValidPriorityClass(paramDWORD))
      throw new IllegalArgumentException("The given priority value is invalid!"); 
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(512, false, paramInt);
    if (hANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Win32Exception win32Exception = null;
    try {
      if (!Kernel32.INSTANCE.SetPriorityClass(hANDLE, paramDWORD))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static int getThreadPriority(int paramInt) {
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenThread(64, false, paramInt);
    if (hANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Win32Exception win32Exception = null;
    try {
      int i = Kernel32.INSTANCE.GetThreadPriority(hANDLE);
      if (!isValidThreadPriority(i))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return i;
    } catch (Win32Exception win32Exception1) {
      throw win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static void setThreadPriority(int paramInt1, int paramInt2) {
    if (!isValidThreadPriority(paramInt2))
      throw new IllegalArgumentException("The given priority value is invalid!"); 
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenThread(32, false, paramInt1);
    if (hANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Win32Exception win32Exception = null;
    try {
      if (!Kernel32.INSTANCE.SetThreadPriority(hANDLE, paramInt2))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      cleanUp(hANDLE, win32Exception);
    } 
  }
  
  public static boolean isValidPriorityClass(WinDef.DWORD paramDWORD) {
    return (Kernel32.NORMAL_PRIORITY_CLASS.equals(paramDWORD) || Kernel32.IDLE_PRIORITY_CLASS.equals(paramDWORD) || Kernel32.HIGH_PRIORITY_CLASS.equals(paramDWORD) || Kernel32.REALTIME_PRIORITY_CLASS.equals(paramDWORD) || Kernel32.BELOW_NORMAL_PRIORITY_CLASS.equals(paramDWORD) || Kernel32.ABOVE_NORMAL_PRIORITY_CLASS.equals(paramDWORD));
  }
  
  public static boolean isValidThreadPriority(int paramInt) {
    switch (paramInt) {
      case -15:
      case -2:
      case -1:
      case 0:
      case 1:
      case 2:
      case 15:
        return true;
    } 
    return false;
  }
  
  private static void cleanUp(WinNT.HANDLE paramHANDLE, Win32Exception paramWin32Exception) {
    try {
      closeHandle(paramHANDLE);
    } catch (Win32Exception win32Exception) {
      if (paramWin32Exception == null) {
        paramWin32Exception = win32Exception;
      } else {
        paramWin32Exception.addSuppressedReflected((Throwable)win32Exception);
      } 
    } 
    if (paramWin32Exception != null)
      throw paramWin32Exception; 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Kernel32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */