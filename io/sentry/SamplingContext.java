package io.sentry;

import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SamplingContext {
  @NotNull
  private final TransactionContext transactionContext;
  
  @Nullable
  private final CustomSamplingContext customSamplingContext;
  
  public SamplingContext(@NotNull TransactionContext paramTransactionContext, @Nullable CustomSamplingContext paramCustomSamplingContext) {
    this.transactionContext = (TransactionContext)Objects.requireNonNull(paramTransactionContext, "transactionContexts is required");
    this.customSamplingContext = paramCustomSamplingContext;
  }
  
  @Nullable
  public CustomSamplingContext getCustomSamplingContext() {
    return this.customSamplingContext;
  }
  
  @NotNull
  public TransactionContext getTransactionContext() {
    return this.transactionContext;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SamplingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */