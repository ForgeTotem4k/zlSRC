package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

@FieldOrder({"vtbl"})
public class UnknownListener extends Structure {
  public UnknownVTable.ByReference vtbl = constructVTable();
  
  public UnknownListener(IUnknownCallback paramIUnknownCallback) {
    initVTable(paramIUnknownCallback);
    write();
  }
  
  protected UnknownVTable.ByReference constructVTable() {
    return new UnknownVTable.ByReference();
  }
  
  protected void initVTable(final IUnknownCallback callback) {
    this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() {
        public WinNT.HRESULT invoke(Pointer param1Pointer, Guid.REFIID param1REFIID, PointerByReference param1PointerByReference) {
          return callback.QueryInterface(param1REFIID, param1PointerByReference);
        }
      };
    this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback() {
        public int invoke(Pointer param1Pointer) {
          return callback.AddRef();
        }
      };
    this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback() {
        public int invoke(Pointer param1Pointer) {
          return callback.Release();
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\UnknownListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */