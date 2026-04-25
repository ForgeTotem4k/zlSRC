package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IEnumIDList {
  public static final Guid.IID IID_IEnumIDList = new Guid.IID("{000214F2-0000-0000-C000-000000000046}");
  
  WinNT.HRESULT QueryInterface(Guid.REFIID paramREFIID, PointerByReference paramPointerByReference);
  
  int AddRef();
  
  int Release();
  
  WinNT.HRESULT Next(int paramInt, PointerByReference paramPointerByReference, IntByReference paramIntByReference);
  
  WinNT.HRESULT Skip(int paramInt);
  
  WinNT.HRESULT Reset();
  
  WinNT.HRESULT Clone(PointerByReference paramPointerByReference);
  
  public static class Converter {
    public static IEnumIDList PointerToIEnumIDList(PointerByReference param1PointerByReference) {
      final Pointer interfacePointer = param1PointerByReference.getValue();
      Pointer pointer2 = pointer1.getPointer(0L);
      final Pointer[] vTable = new Pointer[7];
      pointer2.read(0L, arrayOfPointer, 0, 7);
      return new IEnumIDList() {
          public WinNT.HRESULT QueryInterface(Guid.REFIID param2REFIID, PointerByReference param2PointerByReference) {
            Function function = Function.getFunction(vTable[0], 63);
            return new WinNT.HRESULT(function.invokeInt(new Object[] { this.val$interfacePointer, param2REFIID, param2PointerByReference }));
          }
          
          public int AddRef() {
            Function function = Function.getFunction(vTable[1], 63);
            return function.invokeInt(new Object[] { this.val$interfacePointer });
          }
          
          public int Release() {
            Function function = Function.getFunction(vTable[2], 63);
            return function.invokeInt(new Object[] { this.val$interfacePointer });
          }
          
          public WinNT.HRESULT Next(int param2Int, PointerByReference param2PointerByReference, IntByReference param2IntByReference) {
            Function function = Function.getFunction(vTable[3], 63);
            return new WinNT.HRESULT(function.invokeInt(new Object[] { this.val$interfacePointer, Integer.valueOf(param2Int), param2PointerByReference, param2IntByReference }));
          }
          
          public WinNT.HRESULT Skip(int param2Int) {
            Function function = Function.getFunction(vTable[4], 63);
            return new WinNT.HRESULT(function.invokeInt(new Object[] { this.val$interfacePointer, Integer.valueOf(param2Int) }));
          }
          
          public WinNT.HRESULT Reset() {
            Function function = Function.getFunction(vTable[5], 63);
            return new WinNT.HRESULT(function.invokeInt(new Object[] { this.val$interfacePointer }));
          }
          
          public WinNT.HRESULT Clone(PointerByReference param2PointerByReference) {
            Function function = Function.getFunction(vTable[6], 63);
            return new WinNT.HRESULT(function.invokeInt(new Object[] { this.val$interfacePointer, param2PointerByReference }));
          }
        };
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\IEnumIDList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */