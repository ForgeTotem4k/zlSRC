package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;

public class W32ServiceManager implements Closeable {
  Winsvc.SC_HANDLE _handle = null;
  
  String _machineName = null;
  
  String _databaseName = null;
  
  public W32ServiceManager() {}
  
  public W32ServiceManager(int paramInt) {
    open(paramInt);
  }
  
  public W32ServiceManager(String paramString1, String paramString2) {
    this._machineName = paramString1;
    this._databaseName = paramString2;
  }
  
  public W32ServiceManager(String paramString1, String paramString2, int paramInt) {
    this._machineName = paramString1;
    this._databaseName = paramString2;
    open(paramInt);
  }
  
  public void open(int paramInt) {
    close();
    this._handle = Advapi32.INSTANCE.OpenSCManager(this._machineName, this._databaseName, paramInt);
    if (this._handle == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public void close() {
    if (this._handle != null) {
      if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      this._handle = null;
    } 
  }
  
  public W32Service openService(String paramString, int paramInt) {
    Winsvc.SC_HANDLE sC_HANDLE = Advapi32.INSTANCE.OpenService(this._handle, paramString, paramInt);
    if (sC_HANDLE == null)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return new W32Service(sC_HANDLE);
  }
  
  public Winsvc.SC_HANDLE getHandle() {
    return this._handle;
  }
  
  public Winsvc.ENUM_SERVICE_STATUS_PROCESS[] enumServicesStatusExProcess(int paramInt1, int paramInt2, String paramString) {
    IntByReference intByReference1 = new IntByReference(0);
    IntByReference intByReference2 = new IntByReference(0);
    IntByReference intByReference3 = new IntByReference(0);
    Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, paramInt1, paramInt2, Pointer.NULL, 0, intByReference1, intByReference2, intByReference3, paramString);
    int i = Kernel32.INSTANCE.GetLastError();
    if (i != 234)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference1.getValue());
    boolean bool = Advapi32.INSTANCE.EnumServicesStatusEx(this._handle, 0, paramInt1, paramInt2, (Pointer)memory, (int)memory.size(), intByReference1, intByReference2, intByReference3, paramString);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    if (intByReference2.getValue() == 0)
      return new Winsvc.ENUM_SERVICE_STATUS_PROCESS[0]; 
    Winsvc.ENUM_SERVICE_STATUS_PROCESS eNUM_SERVICE_STATUS_PROCESS = (Winsvc.ENUM_SERVICE_STATUS_PROCESS)Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS_PROCESS.class, (Pointer)memory);
    eNUM_SERVICE_STATUS_PROCESS.read();
    return (Winsvc.ENUM_SERVICE_STATUS_PROCESS[])eNUM_SERVICE_STATUS_PROCESS.toArray(intByReference2.getValue());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\W32ServiceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */