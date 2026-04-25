package io.sentry.transport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.jetbrains.annotations.NotNull;

public final class ReusableCountLatch {
  @NotNull
  private final Sync sync;
  
  public ReusableCountLatch(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("negative initial count '" + paramInt + "' is not allowed"); 
    this.sync = new Sync(paramInt);
  }
  
  public ReusableCountLatch() {
    this(0);
  }
  
  public int getCount() {
    return this.sync.getCount();
  }
  
  public void decrement() {
    this.sync.decrement();
  }
  
  public void increment() {
    this.sync.increment();
  }
  
  public void waitTillZero() throws InterruptedException {
    this.sync.acquireSharedInterruptibly(1);
  }
  
  public boolean waitTillZero(long paramLong, @NotNull TimeUnit paramTimeUnit) throws InterruptedException {
    return this.sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
  }
  
  private static final class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 5970133580157457018L;
    
    Sync(int param1Int) {
      setState(param1Int);
    }
    
    private int getCount() {
      return getState();
    }
    
    private void increment() {
      int i;
      int j;
      do {
        i = getState();
        j = i + 1;
      } while (!compareAndSetState(i, j));
    }
    
    private void decrement() {
      releaseShared(1);
    }
    
    public int tryAcquireShared(int param1Int) {
      return (getState() == 0) ? 1 : -1;
    }
    
    public boolean tryReleaseShared(int param1Int) {
      while (true) {
        int i = getState();
        if (i == 0)
          return false; 
        int j = i - 1;
        if (compareAndSetState(i, j))
          return (j == 0); 
      } 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\ReusableCountLatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */