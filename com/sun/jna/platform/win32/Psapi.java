package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Psapi extends StdCallLibrary {
  public static final Psapi INSTANCE = (Psapi)Native.load("psapi", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);
  
  int GetModuleFileNameExA(WinNT.HANDLE paramHANDLE1, WinNT.HANDLE paramHANDLE2, byte[] paramArrayOfbyte, int paramInt);
  
  int GetModuleFileNameExW(WinNT.HANDLE paramHANDLE1, WinNT.HANDLE paramHANDLE2, char[] paramArrayOfchar, int paramInt);
  
  int GetModuleFileNameEx(WinNT.HANDLE paramHANDLE1, WinNT.HANDLE paramHANDLE2, Pointer paramPointer, int paramInt);
  
  boolean EnumProcessModules(WinNT.HANDLE paramHANDLE, WinDef.HMODULE[] paramArrayOfHMODULE, int paramInt, IntByReference paramIntByReference);
  
  boolean GetModuleInformation(WinNT.HANDLE paramHANDLE, WinDef.HMODULE paramHMODULE, MODULEINFO paramMODULEINFO, int paramInt);
  
  int GetProcessImageFileName(WinNT.HANDLE paramHANDLE, char[] paramArrayOfchar, int paramInt);
  
  boolean GetPerformanceInfo(PERFORMANCE_INFORMATION paramPERFORMANCE_INFORMATION, int paramInt);
  
  boolean EnumProcesses(int[] paramArrayOfint, int paramInt, IntByReference paramIntByReference);
  
  boolean QueryWorkingSetEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, int paramInt);
  
  @FieldOrder({"VirtualAddress", "VirtualAttributes"})
  public static class PSAPI_WORKING_SET_EX_INFORMATION extends Structure {
    public Pointer VirtualAddress;
    
    public BaseTSD.ULONG_PTR VirtualAttributes;
    
    public boolean isValid() {
      return (getBitFieldValue(1, 0) == 1);
    }
    
    public int getShareCount() {
      return getBitFieldValue(3, 1);
    }
    
    public int getWin32Protection() {
      return getBitFieldValue(11, 4);
    }
    
    public boolean isShared() {
      return (getBitFieldValue(1, 15) == 1);
    }
    
    public int getNode() {
      return getBitFieldValue(6, 16);
    }
    
    public boolean isLocked() {
      return (getBitFieldValue(1, 22) == 1);
    }
    
    public boolean isLargePage() {
      return (getBitFieldValue(1, 23) == 1);
    }
    
    public boolean isBad() {
      return (getBitFieldValue(1, 25) == 1);
    }
    
    private int getBitFieldValue(int param1Int1, int param1Int2) {
      long l = 0L;
      for (byte b = 0; b < param1Int1; b++)
        l |= (1 << b); 
      return (int)(this.VirtualAttributes.longValue() >>> param1Int2 & l);
    }
  }
  
  @FieldOrder({"cb", "CommitTotal", "CommitLimit", "CommitPeak", "PhysicalTotal", "PhysicalAvailable", "SystemCache", "KernelTotal", "KernelPaged", "KernelNonpaged", "PageSize", "HandleCount", "ProcessCount", "ThreadCount"})
  public static class PERFORMANCE_INFORMATION extends Structure {
    public WinDef.DWORD cb;
    
    public BaseTSD.SIZE_T CommitTotal;
    
    public BaseTSD.SIZE_T CommitLimit;
    
    public BaseTSD.SIZE_T CommitPeak;
    
    public BaseTSD.SIZE_T PhysicalTotal;
    
    public BaseTSD.SIZE_T PhysicalAvailable;
    
    public BaseTSD.SIZE_T SystemCache;
    
    public BaseTSD.SIZE_T KernelTotal;
    
    public BaseTSD.SIZE_T KernelPaged;
    
    public BaseTSD.SIZE_T KernelNonpaged;
    
    public BaseTSD.SIZE_T PageSize;
    
    public WinDef.DWORD HandleCount;
    
    public WinDef.DWORD ProcessCount;
    
    public WinDef.DWORD ThreadCount;
  }
  
  @FieldOrder({"lpBaseOfDll", "SizeOfImage", "EntryPoint"})
  public static class MODULEINFO extends Structure {
    public Pointer EntryPoint;
    
    public Pointer lpBaseOfDll;
    
    public int SizeOfImage;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Psapi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */