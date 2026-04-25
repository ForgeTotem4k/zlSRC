package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

@FieldOrder({"vtbl"})
public class DispatchListener extends Structure {
  public DispatchVTable.ByReference vtbl = constructVTable();
  
  public DispatchListener(IDispatchCallback paramIDispatchCallback) {
    initVTable(paramIDispatchCallback);
    write();
  }
  
  protected DispatchVTable.ByReference constructVTable() {
    return new DispatchVTable.ByReference();
  }
  
  protected void initVTable(final IDispatchCallback callback) {
    this.vtbl.QueryInterfaceCallback = new DispatchVTable.QueryInterfaceCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, Guid.REFIID param1REFIID, PointerByReference param1PointerByReference) {
          return callback.QueryInterface(param1REFIID, param1PointerByReference);
        }
      };
    this.vtbl.AddRefCallback = new DispatchVTable.AddRefCallback() {
        public int invoke(Pointer param1Pointer) {
          return callback.AddRef();
        }
      };
    this.vtbl.ReleaseCallback = new DispatchVTable.ReleaseCallback() {
        public int invoke(Pointer param1Pointer) {
          return callback.Release();
        }
      };
    this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, WinDef.UINTByReference param1UINTByReference) {
          return callback.GetTypeInfoCount(param1UINTByReference);
        }
      };
    this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, WinDef.UINT param1UINT, WinDef.LCID param1LCID, PointerByReference param1PointerByReference) {
          return callback.GetTypeInfo(param1UINT, param1LCID, param1PointerByReference);
        }
      };
    this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, Guid.REFIID param1REFIID, WString[] param1ArrayOfWString, int param1Int, WinDef.LCID param1LCID, OaIdl.DISPIDByReference param1DISPIDByReference) {
          return callback.GetIDsOfNames(param1REFIID, param1ArrayOfWString, param1Int, param1LCID, param1DISPIDByReference);
        }
      };
    this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, OaIdl.DISPID param1DISPID, Guid.REFIID param1REFIID, WinDef.LCID param1LCID, WinDef.WORD param1WORD, OleAuto.DISPPARAMS.ByReference param1ByReference, Variant.VARIANT.ByReference param1ByReference1, OaIdl.EXCEPINFO.ByReference param1ByReference2, IntByReference param1IntByReference) {
          return callback.Invoke(param1DISPID, param1REFIID, param1LCID, param1WORD, param1ByReference, param1ByReference1, param1ByReference2, param1IntByReference);
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\DispatchListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */