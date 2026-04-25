package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.EnumMoniker;
import com.sun.jna.platform.win32.COM.IEnumMoniker;
import com.sun.jna.platform.win32.COM.IRunningObjectTable;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.List;

public class RunningObjectTable implements IRunningObjectTable {
  ObjectFactory factory;
  
  com.sun.jna.platform.win32.COM.RunningObjectTable raw;
  
  protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable paramRunningObjectTable, ObjectFactory paramObjectFactory) {
    this.raw = paramRunningObjectTable;
    this.factory = paramObjectFactory;
  }
  
  public Iterable<IDispatch> enumRunning() {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    PointerByReference pointerByReference = new PointerByReference();
    WinNT.HRESULT hRESULT = this.raw.EnumRunning(pointerByReference);
    COMUtils.checkRC(hRESULT);
    EnumMoniker enumMoniker = new EnumMoniker(pointerByReference.getValue());
    return new EnumMoniker((IEnumMoniker)enumMoniker, (IRunningObjectTable)this.raw, this.factory);
  }
  
  public <T> List<T> getActiveObjectsByInterface(Class<T> paramClass) {
    assert COMUtils.comIsInitialized() : "COM not initialized";
    ArrayList<T> arrayList = new ArrayList();
    for (IDispatch iDispatch : enumRunning()) {
      try {
        Object object = iDispatch.queryInterface((Class)paramClass);
        arrayList.add(object);
      } catch (COMException cOMException) {}
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\CO\\util\RunningObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */