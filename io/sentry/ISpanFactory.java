package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public interface ISpanFactory {
  @NotNull
  ITransaction createTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes, @NotNull TransactionOptions paramTransactionOptions, @Nullable TransactionPerformanceCollector paramTransactionPerformanceCollector);
  
  @NotNull
  ISpan createSpan(@NotNull IScopes paramIScopes, @NotNull SpanOptions paramSpanOptions, @NotNull SpanContext paramSpanContext, @Nullable ISpan paramISpan);
  
  @Nullable
  ISpan retrieveCurrentSpan(IScopes paramIScopes);
  
  @Nullable
  ISpan retrieveCurrentSpan(IScope paramIScope);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ISpanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */