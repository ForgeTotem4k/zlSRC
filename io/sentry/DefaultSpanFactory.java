package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DefaultSpanFactory implements ISpanFactory {
  @NotNull
  public ITransaction createTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes, @NotNull TransactionOptions paramTransactionOptions, @Nullable TransactionPerformanceCollector paramTransactionPerformanceCollector) {
    return new SentryTracer(paramTransactionContext, paramIScopes, paramTransactionOptions, paramTransactionPerformanceCollector);
  }
  
  @NotNull
  public ISpan createSpan(@NotNull IScopes paramIScopes, @NotNull SpanOptions paramSpanOptions, @NotNull SpanContext paramSpanContext, @Nullable ISpan paramISpan) {
    return (paramISpan == null) ? NoOpSpan.getInstance() : paramISpan.startChild(paramSpanContext, paramSpanOptions);
  }
  
  @Nullable
  public ISpan retrieveCurrentSpan(IScopes paramIScopes) {
    return paramIScopes.getSpan();
  }
  
  @Nullable
  public ISpan retrieveCurrentSpan(IScope paramIScope) {
    return paramIScope.getSpan();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DefaultSpanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */