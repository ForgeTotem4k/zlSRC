package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IEnumMoniker;
import com.sun.jna.platform.win32.COM.IRunningObjectTable;
import com.sun.jna.platform.win32.COM.Moniker;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.util.Iterator;

public class EnumMoniker implements Iterable<IDispatch> {
  ObjectFactory factory;
  
  IRunningObjectTable rawRot;
  
  IEnumMoniker raw;
  
  Moniker rawNext;
  
  protected EnumMoniker(IEnumMoniker paramIEnumMoniker, IRunningObjectTable paramIRunningObjectTable, ObjectFactory paramObjectFactory) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    this.rawRot = paramIRunningObjectTable;
    this.raw = paramIEnumMoniker;
    this.factory = paramObjectFactory;
    WinNT.HRESULT hRESULT = paramIEnumMoniker.Reset();
    COMUtils.checkRC(hRESULT);
    cacheNext();
  }
  
  protected void cacheNext() {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    PointerByReference pointerByReference = new PointerByReference();
    WinDef.ULONGByReference uLONGByReference = new WinDef.ULONGByReference();
    WinNT.HRESULT hRESULT = this.raw.Next(new WinDef.ULONG(1L), pointerByReference, uLONGByReference);
    if (WinNT.S_OK.equals(hRESULT) && uLONGByReference.getValue().intValue() > 0) {
      this.rawNext = new Moniker(pointerByReference.getValue());
    } else {
      if (!WinNT.S_FALSE.equals(hRESULT))
        COMUtils.checkRC(hRESULT); 
      this.rawNext = null;
    } 
  }
  
  public Iterator<IDispatch> iterator() {
    return new Iterator<IDispatch>() {
        public boolean hasNext() {
          return (null != EnumMoniker.this.rawNext);
        }
        
        public IDispatch next() {
          assert COMUtils.comIsInitialized() : "COM not initialized";
          Moniker moniker = EnumMoniker.this.rawNext;
          PointerByReference pointerByReference = new PointerByReference();
          WinNT.HRESULT hRESULT = EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), pointerByReference);
          COMUtils.checkRC(hRESULT);
          Dispatch dispatch = new Dispatch(pointerByReference.getValue());
          EnumMoniker.this.cacheNext();
          IDispatch iDispatch = EnumMoniker.this.factory.<IDispatch>createProxy(IDispatch.class, (IDispatch)dispatch);
          int i = dispatch.Release();
          return iDispatch;
        }
        
        public void remove() {
          throw new UnsupportedOperationException("remove");
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\EnumMoniker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */