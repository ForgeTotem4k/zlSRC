package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class LazyEvaluator<T> {
  @Nullable
  private T value = null;
  
  @NotNull
  private final Evaluator<T> evaluator;
  
  public LazyEvaluator(@NotNull Evaluator<T> paramEvaluator) {
    this.evaluator = paramEvaluator;
  }
  
  @NotNull
  public synchronized T getValue() {
    if (this.value == null)
      this.value = this.evaluator.evaluate(); 
    return this.value;
  }
  
  public static interface Evaluator<T> {
    @NotNull
    T evaluate();
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\LazyEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */