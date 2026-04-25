package com.sun.jna.platform.win32.COM;

public interface IPersistStream extends IPersist {
  boolean IsDirty();
  
  void Load(IStream paramIStream);
  
  void Save(IStream paramIStream);
  
  void GetSizeMax();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\IPersistStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */