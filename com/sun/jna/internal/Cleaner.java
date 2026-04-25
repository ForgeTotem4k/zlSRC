package com.sun.jna.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cleaner {
  private static final Cleaner INSTANCE = new Cleaner();
  
  private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  
  private Thread cleanerThread;
  
  private CleanerRef firstCleanable;
  
  public static Cleaner getCleaner() {
    return INSTANCE;
  }
  
  public synchronized Cleanable register(Object paramObject, Runnable paramRunnable) {
    return add(new CleanerRef(this, paramObject, this.referenceQueue, paramRunnable));
  }
  
  private synchronized CleanerRef add(CleanerRef paramCleanerRef) {
    synchronized (this.referenceQueue) {
      if (this.firstCleanable == null) {
        this.firstCleanable = paramCleanerRef;
      } else {
        paramCleanerRef.setNext(this.firstCleanable);
        this.firstCleanable.setPrevious(paramCleanerRef);
        this.firstCleanable = paramCleanerRef;
      } 
      if (this.cleanerThread == null) {
        Logger.getLogger(Cleaner.class.getName()).log(Level.FINE, "Starting CleanerThread");
        this.cleanerThread = new CleanerThread();
        this.cleanerThread.start();
      } 
      return paramCleanerRef;
    } 
  }
  
  private synchronized boolean remove(CleanerRef paramCleanerRef) {
    synchronized (this.referenceQueue) {
      boolean bool = false;
      if (paramCleanerRef == this.firstCleanable) {
        this.firstCleanable = paramCleanerRef.getNext();
        bool = true;
      } 
      if (paramCleanerRef.getPrevious() != null)
        paramCleanerRef.getPrevious().setNext(paramCleanerRef.getNext()); 
      if (paramCleanerRef.getNext() != null)
        paramCleanerRef.getNext().setPrevious(paramCleanerRef.getPrevious()); 
      if (paramCleanerRef.getPrevious() != null || paramCleanerRef.getNext() != null)
        bool = true; 
      paramCleanerRef.setNext(null);
      paramCleanerRef.setPrevious(null);
      return bool;
    } 
  }
  
  private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
    private final Cleaner cleaner;
    
    private final Runnable cleanupTask;
    
    private CleanerRef previous;
    
    private CleanerRef next;
    
    public CleanerRef(Cleaner param1Cleaner, Object param1Object, ReferenceQueue<? super Object> param1ReferenceQueue, Runnable param1Runnable) {
      super(param1Object, param1ReferenceQueue);
      this.cleaner = param1Cleaner;
      this.cleanupTask = param1Runnable;
    }
    
    public void clean() {
      if (this.cleaner.remove(this))
        this.cleanupTask.run(); 
    }
    
    CleanerRef getPrevious() {
      return this.previous;
    }
    
    void setPrevious(CleanerRef param1CleanerRef) {
      this.previous = param1CleanerRef;
    }
    
    CleanerRef getNext() {
      return this.next;
    }
    
    void setNext(CleanerRef param1CleanerRef) {
      this.next = param1CleanerRef;
    }
  }
  
  private class CleanerThread extends Thread {
    private static final long CLEANER_LINGER_TIME = 30000L;
    
    public CleanerThread() {
      super("JNA Cleaner");
      setDaemon(true);
    }
    
    public void run() {
      while (true) {
        try {
          Reference reference = Cleaner.this.referenceQueue.remove(30000L);
          if (reference instanceof Cleaner.CleanerRef) {
            ((Cleaner.CleanerRef)reference).clean();
            continue;
          } 
          if (reference == null)
            synchronized (Cleaner.this.referenceQueue) {
              Logger logger = Logger.getLogger(Cleaner.class.getName());
              if (Cleaner.this.firstCleanable == null) {
                Cleaner.this.cleanerThread = null;
                logger.log(Level.FINE, "Shutting down CleanerThread");
                break;
              } 
              if (logger.isLoggable(Level.FINER)) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Cleaner.CleanerRef cleanerRef = Cleaner.this.firstCleanable; cleanerRef != null; cleanerRef = cleanerRef.next) {
                  if (stringBuilder.length() != 0)
                    stringBuilder.append(", "); 
                  stringBuilder.append(cleanerRef.cleanupTask.toString());
                } 
                logger.log(Level.FINER, "Registered Cleaners: {0}", stringBuilder.toString());
              } 
            }  
        } catch (InterruptedException interruptedException) {
          break;
        } catch (Exception exception) {
          Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, (String)null, exception);
        } 
      } 
    }
  }
  
  public static interface Cleanable {
    void clean();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\internal\Cleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */