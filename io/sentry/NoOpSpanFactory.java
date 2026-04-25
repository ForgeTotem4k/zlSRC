package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoOpSpanFactory implements ISpanFactory {
  private static final NoOpSpanFactory instance = new NoOpSpanFactory();
  
  public static NoOpSpanFactory getInstance() {
    return instance;
  }
  
  @NotNull
  public ITransaction createTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes, @NotNull TransactionOptions paramTransactionOptions, @Nullable TransactionPerformanceCollector paramTransactionPerformanceCollector) {
    return NoOpTransaction.getInstance();
  }
  
  @NotNull
  public ISpan createSpan(@NotNull IScopes paramIScopes, @NotNull SpanOptions paramSpanOptions, @NotNull SpanContext paramSpanContext, @Nullable ISpan paramISpan) {
    return NoOpSpan.getInstance();
  }
  
  @Nullable
  public ISpan retrieveCurrentSpan(IScopes paramIScopes) {
    return NoOpSpan.getInstance();
  }
  
  @Nullable
  public ISpan retrieveCurrentSpan(IScope paramIScope) {
    return NoOpSpan.getInstance();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpSpanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */