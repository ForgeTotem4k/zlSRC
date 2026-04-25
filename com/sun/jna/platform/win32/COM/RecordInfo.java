package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class RecordInfo extends Unknown implements IRecordInfo {
  public RecordInfo() {}
  
  public RecordInfo(Pointer paramPointer) {
    super(paramPointer);
  }
  
  public WinNT.HRESULT RecordInit(WinDef.PVOID paramPVOID) {
    return null;
  }
  
  public WinNT.HRESULT RecordClear(WinDef.PVOID paramPVOID) {
    return null;
  }
  
  public WinNT.HRESULT RecordCopy(WinDef.PVOID paramPVOID1, WinDef.PVOID paramPVOID2) {
    return null;
  }
  
  public WinNT.HRESULT GetGuid(Guid.GUID paramGUID) {
    return null;
  }
  
  public WinNT.HRESULT GetName(WTypes.BSTR paramBSTR) {
    return null;
  }
  
  public WinNT.HRESULT GetSize(WinDef.ULONG paramULONG) {
    return null;
  }
  
  public WinNT.HRESULT GetTypeInfo(ITypeInfo paramITypeInfo) {
    return null;
  }
  
  public WinNT.HRESULT GetField(WinDef.PVOID paramPVOID, WString paramWString, Variant.VARIANT paramVARIANT) {
    return null;
  }
  
  public WinNT.HRESULT GetFieldNoCopy(WinDef.PVOID paramPVOID1, WString paramWString, Variant.VARIANT paramVARIANT, WinDef.PVOID paramPVOID2) {
    return null;
  }
  
  public WinNT.HRESULT PutField(WinDef.ULONG paramULONG, WinDef.PVOID paramPVOID, WString paramWString, Variant.VARIANT paramVARIANT) {
    return null;
  }
  
  public WinNT.HRESULT PutFieldNoCopy(WinDef.ULONG paramULONG, WinDef.PVOID paramPVOID, WString paramWString, Variant.VARIANT paramVARIANT) {
    return null;
  }
  
  public WinNT.HRESULT GetFieldNames(WinDef.ULONG paramULONG, WTypes.BSTR paramBSTR) {
    return null;
  }
  
  public WinDef.BOOL IsMatchingType(IRecordInfo paramIRecordInfo) {
    return null;
  }
  
  public WinDef.PVOID RecordCreate() {
    return null;
  }
  
  public WinNT.HRESULT RecordCreateCopy(WinDef.PVOID paramPVOID1, WinDef.PVOID paramPVOID2) {
    return null;
  }
  
  public WinNT.HRESULT RecordDestroy(WinDef.PVOID paramPVOID) {
    return null;
  }
  
  public static class ByReference extends RecordInfo implements Structure.ByReference {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\RecordInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */