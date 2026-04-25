package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;

public abstract class PdhUtil {
  private static final int CHAR_TO_BYTES = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
  
  private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
  
  private static final String ENGLISH_COUNTER_VALUE = "Counter";
  
  public static String PdhLookupPerfNameByIndex(String paramString, int paramInt) {
    WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
    int i = Pdh.INSTANCE.PdhLookupPerfNameByIndex(paramString, paramInt, null, dWORDByReference);
    Memory memory = null;
    if (i != -1073738819) {
      if (i != 0 && i != -2147481646)
        throw new PdhException(i); 
      if (dWORDByReference.getValue().intValue() < 1)
        return ""; 
      memory = new Memory((dWORDByReference.getValue().intValue() * CHAR_TO_BYTES));
      i = Pdh.INSTANCE.PdhLookupPerfNameByIndex(paramString, paramInt, (Pointer)memory, dWORDByReference);
    } else {
      int j;
      for (j = 32; j <= 1024; j *= 2) {
        dWORDByReference = new WinDef.DWORDByReference(new WinDef.DWORD(j));
        memory = new Memory((j * CHAR_TO_BYTES));
        i = Pdh.INSTANCE.PdhLookupPerfNameByIndex(paramString, paramInt, (Pointer)memory, dWORDByReference);
        if (i != -1073738819 && i != -1073738814)
          break; 
      } 
    } 
    if (i != 0)
      throw new PdhException(i); 
    return (CHAR_TO_BYTES == 1) ? memory.getString(0L) : memory.getWideString(0L);
  }
  
  public static int PdhLookupPerfIndexByEnglishName(String paramString) {
    String[] arrayOfString = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");
    for (byte b = 1; b < arrayOfString.length; b += 2) {
      if (arrayOfString[b].equals(paramString))
        try {
          return Integer.parseInt(arrayOfString[b - 1]);
        } catch (NumberFormatException numberFormatException) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public static PdhEnumObjectItems PdhEnumObjectItems(String paramString1, String paramString2, String paramString3, int paramInt) {
    ArrayList<String> arrayList1 = new ArrayList();
    ArrayList<String> arrayList2 = new ArrayList();
    WinDef.DWORDByReference dWORDByReference1 = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
    WinDef.DWORDByReference dWORDByReference2 = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
    int i = Pdh.INSTANCE.PdhEnumObjectItems(paramString1, paramString2, paramString3, null, dWORDByReference1, null, dWORDByReference2, paramInt, 0);
    if (i != 0 && i != -2147481646)
      throw new PdhException(i); 
    Memory memory1 = null;
    Memory memory2 = null;
    while (true) {
      if (dWORDByReference1.getValue().intValue() > 0)
        memory1 = new Memory((dWORDByReference1.getValue().intValue() * CHAR_TO_BYTES)); 
      if (dWORDByReference2.getValue().intValue() > 0)
        memory2 = new Memory((dWORDByReference2.getValue().intValue() * CHAR_TO_BYTES)); 
      i = Pdh.INSTANCE.PdhEnumObjectItems(paramString1, paramString2, paramString3, (Pointer)memory1, dWORDByReference1, (Pointer)memory2, dWORDByReference2, paramInt, 0);
      if (i == -2147481646) {
        if (memory1 != null) {
          long l = memory1.size() / CHAR_TO_BYTES;
          dWORDByReference1.setValue(new WinDef.DWORD(l + 1024L));
          memory1.close();
        } 
        if (memory2 != null) {
          long l = memory2.size() / CHAR_TO_BYTES;
          dWORDByReference2.setValue(new WinDef.DWORD(l + 1024L));
          memory2.close();
        } 
      } 
      if (i != -2147481646) {
        if (i != 0)
          throw new PdhException(i); 
        if (memory1 != null) {
          int j;
          for (j = 0; j < memory1.size(); j += (str.length() + 1) * CHAR_TO_BYTES) {
            String str = null;
            if (CHAR_TO_BYTES == 1) {
              str = memory1.getString(j);
            } else {
              str = memory1.getWideString(j);
            } 
            if (str.isEmpty())
              break; 
            arrayList1.add(str);
          } 
        } 
        if (memory2 != null) {
          int j;
          for (j = 0; j < memory2.size(); j += (str.length() + 1) * CHAR_TO_BYTES) {
            String str = null;
            if (CHAR_TO_BYTES == 1) {
              str = memory2.getString(j);
            } else {
              str = memory2.getWideString(j);
            } 
            if (str.isEmpty())
              break; 
            arrayList2.add(str);
          } 
        } 
        return new PdhEnumObjectItems(arrayList1, arrayList2);
      } 
    } 
  }
  
  public static final class PdhException extends RuntimeException {
    private final int errorCode;
    
    public PdhException(int param1Int) {
      super(String.format("Pdh call failed with error code 0x%08X", new Object[] { Integer.valueOf(param1Int) }));
      this.errorCode = param1Int;
    }
    
    public int getErrorCode() {
      return this.errorCode;
    }
  }
  
  public static class PdhEnumObjectItems {
    private final List<String> counters;
    
    private final List<String> instances;
    
    public PdhEnumObjectItems(List<String> param1List1, List<String> param1List2) {
      this.counters = copyAndEmptyListForNullList(param1List1);
      this.instances = copyAndEmptyListForNullList(param1List2);
    }
    
    public List<String> getCounters() {
      return this.counters;
    }
    
    public List<String> getInstances() {
      return this.instances;
    }
    
    private List<String> copyAndEmptyListForNullList(List<String> param1List) {
      return (param1List == null) ? new ArrayList<>() : new ArrayList<>(param1List);
    }
    
    public String toString() {
      return "PdhEnumObjectItems{counters=" + this.counters + ", instances=" + this.instances + '}';
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\PdhUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */