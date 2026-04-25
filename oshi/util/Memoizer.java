package oshi.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Memoizer {
  private static final Supplier<Long> DEFAULT_EXPIRATION_NANOS = memoize(Memoizer::queryExpirationConfig, TimeUnit.MINUTES.toNanos(1L));
  
  private static long queryExpirationConfig() {
    return TimeUnit.MILLISECONDS.toNanos(GlobalConfig.get("oshi.util.memoizer.expiration", 300));
  }
  
  public static long installedAppsExpiration() {
    return TimeUnit.MINUTES.toNanos(1L);
  }
  
  public static long defaultExpiration() {
    return ((Long)DEFAULT_EXPIRATION_NANOS.get()).longValue();
  }
  
  public static <T> Supplier<T> memoize(final Supplier<T> original, final long ttlNanos) {
    return new Supplier<T>() {
        private final Supplier<T> delegate = original;
        
        private volatile T value;
        
        private volatile long expirationNanos;
        
        public T get() {
          long l1 = this.expirationNanos;
          long l2 = System.nanoTime();
          if (l1 == 0L || (ttlNanos >= 0L && l2 - l1 >= 0L))
            synchronized (this) {
              if (l1 == this.expirationNanos) {
                T t = this.delegate.get();
                this.value = t;
                l1 = l2 + ttlNanos;
                this.expirationNanos = (l1 == 0L) ? 1L : l1;
                return t;
              } 
            }  
          return this.value;
        }
      };
  }
  
  public static <T> Supplier<T> memoize(Supplier<T> paramSupplier) {
    return memoize(paramSupplier, -1L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\Memoizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */