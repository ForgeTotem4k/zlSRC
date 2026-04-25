package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

public class WeakMemoryHolder {
  ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  
  IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap<>();
  
  public synchronized void put(Object paramObject, Memory paramMemory) {
    clean();
    WeakReference<Object> weakReference = new WeakReference(paramObject, this.referenceQueue);
    this.backingMap.put(weakReference, paramMemory);
  }
  
  public synchronized void clean() {
    for (Reference<?> reference = this.referenceQueue.poll(); reference != null; reference = this.referenceQueue.poll())
      this.backingMap.remove(reference); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\WeakMemoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */