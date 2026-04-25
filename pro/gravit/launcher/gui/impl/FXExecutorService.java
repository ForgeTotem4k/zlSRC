package pro.gravit.launcher.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FXExecutorService implements ExecutorService {
  private final ContextHelper contextHelper;
  
  public FXExecutorService(ContextHelper paramContextHelper) {
    this.contextHelper = paramContextHelper;
  }
  
  public void shutdown() {}
  
  public List<Runnable> shutdownNow() {
    return new ArrayList<>(0);
  }
  
  public boolean isShutdown() {
    return false;
  }
  
  public boolean isTerminated() {
    return false;
  }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) {
    return false;
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable) {
    Objects.requireNonNull(paramCallable);
    return this.contextHelper.runInFxThread(paramCallable::call);
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT) {
    Objects.requireNonNull(paramRunnable);
    return this.contextHelper.runInFxThread(paramRunnable::run).thenApply(paramVoid -> paramObject);
  }
  
  public Future<?> submit(Runnable paramRunnable) {
    Objects.requireNonNull(paramRunnable);
    return this.contextHelper.runInFxThread(paramRunnable::run);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) {
    return null;
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) {
    return null;
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection) {
    return null;
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) {
    return null;
  }
  
  public void execute(Runnable paramRunnable) {
    Objects.requireNonNull(paramRunnable);
    this.contextHelper.runInFxThread(paramRunnable::run);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\FXExecutorService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */