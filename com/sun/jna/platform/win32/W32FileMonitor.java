package com.sun.jna.platform.win32;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class W32FileMonitor extends FileMonitor {
  private static final Logger LOG = Logger.getLogger(W32FileMonitor.class.getName());
  
  private static final int BUFFER_SIZE = 4096;
  
  private Thread watcher;
  
  private WinNT.HANDLE port;
  
  private final Map<File, FileInfo> fileMap = new HashMap<>();
  
  private final Map<WinNT.HANDLE, FileInfo> handleMap = new HashMap<>();
  
  private boolean disposing = false;
  
  private static int watcherThreadID;
  
  private void handleChanges(FileInfo paramFileInfo) throws IOException {
    Kernel32 kernel32 = Kernel32.INSTANCE;
    WinNT.FILE_NOTIFY_INFORMATION fILE_NOTIFY_INFORMATION = paramFileInfo.info;
    fILE_NOTIFY_INFORMATION.read();
    while (true) {
      FileMonitor.FileEvent fileEvent = null;
      File file = new File(paramFileInfo.file, fILE_NOTIFY_INFORMATION.getFilename());
      switch (fILE_NOTIFY_INFORMATION.Action) {
        case 0:
          break;
        case 3:
          fileEvent = new FileMonitor.FileEvent(this, file, 4);
          break;
        case 1:
          fileEvent = new FileMonitor.FileEvent(this, file, 1);
          break;
        case 2:
          fileEvent = new FileMonitor.FileEvent(this, file, 2);
          break;
        case 4:
          fileEvent = new FileMonitor.FileEvent(this, file, 16);
          break;
        case 5:
          fileEvent = new FileMonitor.FileEvent(this, file, 32);
          break;
        default:
          LOG.log(Level.WARNING, "Unrecognized file action ''{0}''", Integer.valueOf(fILE_NOTIFY_INFORMATION.Action));
          break;
      } 
      if (fileEvent != null)
        notify(fileEvent); 
      fILE_NOTIFY_INFORMATION = fILE_NOTIFY_INFORMATION.next();
      if (fILE_NOTIFY_INFORMATION == null) {
        if (!paramFileInfo.file.exists()) {
          unwatch(paramFileInfo.file);
          return;
        } 
        if (!kernel32.ReadDirectoryChangesW(paramFileInfo.handle, paramFileInfo.info, paramFileInfo.info.size(), paramFileInfo.recursive, paramFileInfo.notifyMask, paramFileInfo.infoLength, paramFileInfo.overlapped, null) && !this.disposing) {
          int i = kernel32.GetLastError();
          throw new IOException("ReadDirectoryChangesW failed on " + paramFileInfo.file + ": '" + Kernel32Util.formatMessageFromLastErrorCode(i) + "' (" + i + ")");
        } 
        return;
      } 
    } 
  }
  
  private FileInfo waitForChange() {
    IntByReference intByReference = new IntByReference();
    BaseTSD.ULONG_PTRByReference uLONG_PTRByReference = new BaseTSD.ULONG_PTRByReference();
    PointerByReference pointerByReference = new PointerByReference();
    if (!Kernel32.INSTANCE.GetQueuedCompletionStatus(this.port, intByReference, uLONG_PTRByReference, pointerByReference, -1))
      return null; 
    synchronized (this) {
      return this.handleMap.get(new WinNT.HANDLE(uLONG_PTRByReference.getValue().toPointer()));
    } 
  }
  
  private int convertMask(int paramInt) {
    int i = 0;
    if ((paramInt & 0x1) != 0)
      i |= 0x40; 
    if ((paramInt & 0x2) != 0)
      i |= 0x3; 
    if ((paramInt & 0x4) != 0)
      i |= 0x10; 
    if ((paramInt & 0x30) != 0)
      i |= 0x3; 
    if ((paramInt & 0x40) != 0)
      i |= 0x8; 
    if ((paramInt & 0x8) != 0)
      i |= 0x20; 
    if ((paramInt & 0x80) != 0)
      i |= 0x4; 
    if ((paramInt & 0x100) != 0)
      i |= 0x100; 
    return i;
  }
  
  protected synchronized void watch(File paramFile, int paramInt, boolean paramBoolean) throws IOException {
    File file = paramFile;
    if (!file.isDirectory()) {
      paramBoolean = false;
      file = paramFile.getParentFile();
    } 
    while (file != null && !file.exists()) {
      paramBoolean = true;
      file = file.getParentFile();
    } 
    if (file == null)
      throw new FileNotFoundException("No ancestor found for " + paramFile); 
    Kernel32 kernel32 = Kernel32.INSTANCE;
    byte b = 7;
    int i = 1107296256;
    WinNT.HANDLE hANDLE = kernel32.CreateFile(paramFile.getAbsolutePath(), 1, b, null, 3, i, null);
    if (WinBase.INVALID_HANDLE_VALUE.equals(hANDLE))
      throw new IOException("Unable to open " + paramFile + " (" + kernel32.GetLastError() + ")"); 
    int j = convertMask(paramInt);
    FileInfo fileInfo = new FileInfo(paramFile, hANDLE, j, paramBoolean);
    this.fileMap.put(paramFile, fileInfo);
    this.handleMap.put(hANDLE, fileInfo);
    this.port = kernel32.CreateIoCompletionPort(hANDLE, this.port, hANDLE.getPointer(), 0);
    if (WinBase.INVALID_HANDLE_VALUE.equals(this.port))
      throw new IOException("Unable to create/use I/O Completion port for " + paramFile + " (" + kernel32.GetLastError() + ")"); 
    if (!kernel32.ReadDirectoryChangesW(hANDLE, fileInfo.info, fileInfo.info.size(), paramBoolean, j, fileInfo.infoLength, fileInfo.overlapped, null)) {
      int k = kernel32.GetLastError();
      throw new IOException("ReadDirectoryChangesW failed on " + fileInfo.file + ", handle " + hANDLE + ": '" + Kernel32Util.formatMessageFromLastErrorCode(k) + "' (" + k + ")");
    } 
    if (this.watcher == null) {
      this.watcher = new Thread("W32 File Monitor-" + watcherThreadID++) {
          public void run() {
            while (true) {
              W32FileMonitor.FileInfo fileInfo = W32FileMonitor.this.waitForChange();
              if (fileInfo == null) {
                synchronized (W32FileMonitor.this) {
                  if (W32FileMonitor.this.fileMap.isEmpty()) {
                    W32FileMonitor.this.watcher = null;
                    break;
                  } 
                } 
                continue;
              } 
              try {
                W32FileMonitor.this.handleChanges(fileInfo);
              } catch (IOException iOException) {
                iOException.printStackTrace();
              } 
            } 
          }
        };
      this.watcher.setDaemon(true);
      this.watcher.start();
    } 
  }
  
  protected synchronized void unwatch(File paramFile) {
    FileInfo fileInfo = this.fileMap.remove(paramFile);
    if (fileInfo != null) {
      this.handleMap.remove(fileInfo.handle);
      Kernel32 kernel32 = Kernel32.INSTANCE;
      kernel32.CloseHandle(fileInfo.handle);
    } 
  }
  
  public synchronized void dispose() {
    this.disposing = true;
    byte b = 0;
    Object[] arrayOfObject = this.fileMap.keySet().toArray();
    while (!this.fileMap.isEmpty())
      unwatch((File)arrayOfObject[b++]); 
    Kernel32 kernel32 = Kernel32.INSTANCE;
    kernel32.PostQueuedCompletionStatus(this.port, 0, null, null);
    kernel32.CloseHandle(this.port);
    this.port = null;
    this.watcher = null;
  }
  
  private class FileInfo {
    public final File file;
    
    public final WinNT.HANDLE handle;
    
    public final int notifyMask;
    
    public final boolean recursive;
    
    public final WinNT.FILE_NOTIFY_INFORMATION info = new WinNT.FILE_NOTIFY_INFORMATION(4096);
    
    public final IntByReference infoLength = new IntByReference();
    
    public final WinBase.OVERLAPPED overlapped = new WinBase.OVERLAPPED();
    
    public FileInfo(File param1File, WinNT.HANDLE param1HANDLE, int param1Int, boolean param1Boolean) {
      this.file = param1File;
      this.handle = param1HANDLE;
      this.notifyMask = param1Int;
      this.recursive = param1Boolean;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\W32FileMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */