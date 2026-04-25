package io.sentry;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class SentryWrapper {
  public static <U> Callable<U> wrapCallable(@NotNull Callable<U> paramCallable) {
    IScopes iScopes = Sentry.getCurrentScopes().forkedScopes("SentryWrapper.wrapCallable");
    return () -> {
        ISentryLifecycleToken iSentryLifecycleToken = paramIScopes.makeCurrent();
        try {
          Object object = paramCallable.call();
          if (iSentryLifecycleToken != null)
            iSentryLifecycleToken.close(); 
          return object;
        } catch (Throwable throwable) {
          if (iSentryLifecycleToken != null)
            try {
              iSentryLifecycleToken.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      };
  }
  
  public static <U> Supplier<U> wrapSupplier(@NotNull Supplier<U> paramSupplier) {
    IScopes iScopes = Sentry.forkedScopes("SentryWrapper.wrapSupplier");
    return () -> {
        ISentryLifecycleToken iSentryLifecycleToken = paramIScopes.makeCurrent();
        try {
          Object object = paramSupplier.get();
          if (iSentryLifecycleToken != null)
            iSentryLifecycleToken.close(); 
          return object;
        } catch (Throwable throwable) {
          if (iSentryLifecycleToken != null)
            try {
              iSentryLifecycleToken.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      };
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */