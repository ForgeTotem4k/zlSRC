package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;
import java.util.List;

public class W32Service implements Closeable {
  Winsvc.SC_HANDLE _handle = null;
  
  public W32Service(Winsvc.SC_HANDLE paramSC_HANDLE) {
    this._handle = paramSC_HANDLE;
  }
  
  public void close() {
    if (this._handle != null) {
      if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      this._handle = null;
    } 
  }
  
  private void addShutdownPrivilegeToProcess() {
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    WinNT.LUID lUID = new WinNT.LUID();
    Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 32, hANDLEByReference);
    Advapi32.INSTANCE.LookupPrivilegeValue("", "SeShutdownPrivilege", lUID);
    WinNT.TOKEN_PRIVILEGES tOKEN_PRIVILEGES = new WinNT.TOKEN_PRIVILEGES(1);
    tOKEN_PRIVILEGES.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(lUID, new WinDef.DWORD(2L));
    Advapi32.INSTANCE.AdjustTokenPrivileges(hANDLEByReference.getValue(), false, tOKEN_PRIVILEGES, tOKEN_PRIVILEGES.size(), null, new IntByReference());
  }
  
  public void setFailureActions(List<Winsvc.SC_ACTION> paramList, int paramInt, String paramString1, String paramString2) {
    Winsvc.SERVICE_FAILURE_ACTIONS.ByReference byReference = new Winsvc.SERVICE_FAILURE_ACTIONS.ByReference();
    byReference.dwResetPeriod = paramInt;
    byReference.lpRebootMsg = paramString1;
    byReference.lpCommand = paramString2;
    byReference.cActions = paramList.size();
    byReference.lpsaActions = new Winsvc.SC_ACTION.ByReference();
    Winsvc.SC_ACTION[] arrayOfSC_ACTION = (Winsvc.SC_ACTION[])byReference.lpsaActions.toArray(paramList.size());
    boolean bool = false;
    byte b = 0;
    for (Winsvc.SC_ACTION sC_ACTION : paramList) {
      if (!bool && sC_ACTION.type == 2) {
        addShutdownPrivilegeToProcess();
        bool = true;
      } 
      (arrayOfSC_ACTION[b]).type = sC_ACTION.type;
      (arrayOfSC_ACTION[b]).delay = sC_ACTION.delay;
      b++;
    } 
    if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 2, byReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  private Pointer queryServiceConfig2(int paramInt) {
    IntByReference intByReference = new IntByReference();
    Advapi32.INSTANCE.QueryServiceConfig2(this._handle, paramInt, Pointer.NULL, 0, intByReference);
    Memory memory = new Memory(intByReference.getValue());
    if (!Advapi32.INSTANCE.QueryServiceConfig2(this._handle, paramInt, (Pointer)memory, intByReference.getValue(), new IntByReference()))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return (Pointer)memory;
  }
  
  public Winsvc.SERVICE_FAILURE_ACTIONS getFailureActions() {
    Pointer pointer = queryServiceConfig2(2);
    return new Winsvc.SERVICE_FAILURE_ACTIONS(pointer);
  }
  
  public void setFailureActionsFlag(boolean paramBoolean) {
    Winsvc.SERVICE_FAILURE_ACTIONS_FLAG sERVICE_FAILURE_ACTIONS_FLAG = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG();
    sERVICE_FAILURE_ACTIONS_FLAG.fFailureActionsOnNonCrashFailures = paramBoolean ? 1 : 0;
    if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 4, sERVICE_FAILURE_ACTIONS_FLAG))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
  }
  
  public boolean getFailureActionsFlag() {
    Pointer pointer = queryServiceConfig2(4);
    Winsvc.SERVICE_FAILURE_ACTIONS_FLAG sERVICE_FAILURE_ACTIONS_FLAG = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG(pointer);
    return (sERVICE_FAILURE_ACTIONS_FLAG.fFailureActionsOnNonCrashFailures != 0);
  }
  
  public Winsvc.SERVICE_STATUS_PROCESS queryStatus() {
    IntByReference intByReference = new IntByReference();
    Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, null, 0, intByReference);
    Winsvc.SERVICE_STATUS_PROCESS sERVICE_STATUS_PROCESS = new Winsvc.SERVICE_STATUS_PROCESS(intByReference.getValue());
    if (!Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, sERVICE_STATUS_PROCESS, sERVICE_STATUS_PROCESS.size(), intByReference))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    return sERVICE_STATUS_PROCESS;
  }
  
  public void startService() {
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState == 4)
      return; 
    if (!Advapi32.INSTANCE.StartService(this._handle, 0, null))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState != 4)
      throw new RuntimeException("Unable to start the service"); 
  }
  
  public void stopService() {
    stopService(30000L);
  }
  
  public void stopService(long paramLong) {
    long l = System.currentTimeMillis();
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState == 1)
      return; 
    Winsvc.SERVICE_STATUS sERVICE_STATUS = new Winsvc.SERVICE_STATUS();
    if (!Advapi32.INSTANCE.ControlService(this._handle, 1, sERVICE_STATUS))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    while (sERVICE_STATUS.dwCurrentState != 1) {
      long l1 = paramLong - System.currentTimeMillis() - l;
      if (l1 < 0L)
        throw new RuntimeException(String.format("Service stop exceeded timeout time of %d ms", new Object[] { Long.valueOf(paramLong) })); 
      long l2 = Math.min(sanitizeWaitTime(sERVICE_STATUS.dwWaitHint), l1);
      try {
        Thread.sleep(l2);
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } 
      if (!Advapi32.INSTANCE.QueryServiceStatus(this._handle, sERVICE_STATUS))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } 
  }
  
  public void continueService() {
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState == 4)
      return; 
    if (!Advapi32.INSTANCE.ControlService(this._handle, 3, new Winsvc.SERVICE_STATUS()))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState != 4)
      throw new RuntimeException("Unable to continue the service"); 
  }
  
  public void pauseService() {
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState == 7)
      return; 
    if (!Advapi32.INSTANCE.ControlService(this._handle, 2, new Winsvc.SERVICE_STATUS()))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    waitForNonPendingState();
    if ((queryStatus()).dwCurrentState != 7)
      throw new RuntimeException("Unable to pause the service"); 
  }
  
  int sanitizeWaitTime(int paramInt) {
    int i = paramInt / 10;
    if (i < 1000) {
      i = 1000;
    } else if (i > 10000) {
      i = 10000;
    } 
    return i;
  }
  
  public void waitForNonPendingState() {
    Winsvc.SERVICE_STATUS_PROCESS sERVICE_STATUS_PROCESS = queryStatus();
    int i = sERVICE_STATUS_PROCESS.dwCheckPoint;
    int j = Kernel32.INSTANCE.GetTickCount();
    while (isPendingState(sERVICE_STATUS_PROCESS.dwCurrentState)) {
      if (sERVICE_STATUS_PROCESS.dwCheckPoint != i) {
        i = sERVICE_STATUS_PROCESS.dwCheckPoint;
        j = Kernel32.INSTANCE.GetTickCount();
      } 
      if (Kernel32.INSTANCE.GetTickCount() - j > sERVICE_STATUS_PROCESS.dwWaitHint)
        throw new RuntimeException("Timeout waiting for service to change to a non-pending state."); 
      int k = sanitizeWaitTime(sERVICE_STATUS_PROCESS.dwWaitHint);
      try {
        Thread.sleep(k);
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } 
      sERVICE_STATUS_PROCESS = queryStatus();
    } 
  }
  
  private boolean isPendingState(int paramInt) {
    switch (paramInt) {
      case 2:
      case 3:
      case 5:
      case 6:
        return true;
    } 
    return false;
  }
  
  public Winsvc.SC_HANDLE getHandle() {
    return this._handle;
  }
  
  public Winsvc.ENUM_SERVICE_STATUS[] enumDependentServices(int paramInt) {
    IntByReference intByReference1 = new IntByReference(0);
    IntByReference intByReference2 = new IntByReference(0);
    Advapi32.INSTANCE.EnumDependentServices(this._handle, paramInt, Pointer.NULL, 0, intByReference1, intByReference2);
    int i = Kernel32.INSTANCE.GetLastError();
    if (i != 234)
      throw new Win32Exception(i); 
    Memory memory = new Memory(intByReference1.getValue());
    boolean bool = Advapi32.INSTANCE.EnumDependentServices(this._handle, paramInt, (Pointer)memory, (int)memory.size(), intByReference1, intByReference2);
    if (!bool)
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    if (intByReference2.getValue() == 0)
      return new Winsvc.ENUM_SERVICE_STATUS[0]; 
    Winsvc.ENUM_SERVICE_STATUS eNUM_SERVICE_STATUS = (Winsvc.ENUM_SERVICE_STATUS)Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS.class, (Pointer)memory);
    eNUM_SERVICE_STATUS.read();
    return (Winsvc.ENUM_SERVICE_STATUS[])eNUM_SERVICE_STATUS.toArray(intByReference2.getValue());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\W32Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */