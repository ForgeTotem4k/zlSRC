package com.sun.jna.platform;

import com.sun.jna.platform.win32.W32FileMonitor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileMonitor {
  public static final int FILE_CREATED = 1;
  
  public static final int FILE_DELETED = 2;
  
  public static final int FILE_MODIFIED = 4;
  
  public static final int FILE_ACCESSED = 8;
  
  public static final int FILE_NAME_CHANGED_OLD = 16;
  
  public static final int FILE_NAME_CHANGED_NEW = 32;
  
  public static final int FILE_RENAMED = 48;
  
  public static final int FILE_SIZE_CHANGED = 64;
  
  public static final int FILE_ATTRIBUTES_CHANGED = 128;
  
  public static final int FILE_SECURITY_CHANGED = 256;
  
  public static final int FILE_ANY = 511;
  
  private final Map<File, Integer> watched = new HashMap<>();
  
  private List<FileListener> listeners = new ArrayList<>();
  
  protected abstract void watch(File paramFile, int paramInt, boolean paramBoolean) throws IOException;
  
  protected abstract void unwatch(File paramFile);
  
  public abstract void dispose();
  
  public void addWatch(File paramFile) throws IOException {
    addWatch(paramFile, 511);
  }
  
  public void addWatch(File paramFile, int paramInt) throws IOException {
    addWatch(paramFile, paramInt, paramFile.isDirectory());
  }
  
  public void addWatch(File paramFile, int paramInt, boolean paramBoolean) throws IOException {
    this.watched.put(paramFile, Integer.valueOf(paramInt));
    watch(paramFile, paramInt, paramBoolean);
  }
  
  public void removeWatch(File paramFile) {
    if (this.watched.remove(paramFile) != null)
      unwatch(paramFile); 
  }
  
  protected void notify(FileEvent paramFileEvent) {
    for (FileListener fileListener : this.listeners)
      fileListener.fileChanged(paramFileEvent); 
  }
  
  public synchronized void addFileListener(FileListener paramFileListener) {
    ArrayList<FileListener> arrayList = new ArrayList<>(this.listeners);
    arrayList.add(paramFileListener);
    this.listeners = arrayList;
  }
  
  public synchronized void removeFileListener(FileListener paramFileListener) {
    ArrayList<FileListener> arrayList = new ArrayList<>(this.listeners);
    arrayList.remove(paramFileListener);
    this.listeners = arrayList;
  }
  
  protected void finalize() {
    for (File file : this.watched.keySet())
      removeWatch(file); 
    dispose();
  }
  
  public static FileMonitor getInstance() {
    return Holder.INSTANCE;
  }
  
  public static interface FileListener {
    void fileChanged(FileMonitor.FileEvent param1FileEvent);
  }
  
  public class FileEvent extends EventObject {
    private final File file;
    
    private final int type;
    
    public FileEvent(File param1File, int param1Int) {
      super(FileMonitor.this);
      this.file = param1File;
      this.type = param1Int;
    }
    
    public File getFile() {
      return this.file;
    }
    
    public int getType() {
      return this.type;
    }
    
    public String toString() {
      return "FileEvent: " + this.file + ":" + this.type;
    }
  }
  
  private static class Holder {
    public static final FileMonitor INSTANCE;
    
    static {
      String str = System.getProperty("os.name");
      if (str.startsWith("Windows")) {
        INSTANCE = (FileMonitor)new W32FileMonitor();
      } else {
        throw new Error("FileMonitor not implemented for " + str);
      } 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\FileMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */