package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TransactionOptions extends SpanOptions {
  @Internal
  public static final long DEFAULT_DEADLINE_TIMEOUT_AUTO_TRANSACTION = 30000L;
  
  @Nullable
  private CustomSamplingContext customSamplingContext = null;
  
  private boolean bindToScope = false;
  
  private boolean isAppStartTransaction = false;
  
  private boolean waitForChildren = false;
  
  @Nullable
  private Long idleTimeout = null;
  
  @Nullable
  private Long deadlineTimeout = null;
  
  @Nullable
  private TransactionFinishedCallback transactionFinishedCallback = null;
  
  @Internal
  @Nullable
  private ISpanFactory spanFactory = null;
  
  @Nullable
  public CustomSamplingContext getCustomSamplingContext() {
    return this.customSamplingContext;
  }
  
  public void setCustomSamplingContext(@Nullable CustomSamplingContext paramCustomSamplingContext) {
    this.customSamplingContext = paramCustomSamplingContext;
  }
  
  public boolean isBindToScope() {
    return this.bindToScope;
  }
  
  public void setBindToScope(boolean paramBoolean) {
    this.bindToScope = paramBoolean;
  }
  
  public boolean isWaitForChildren() {
    return this.waitForChildren;
  }
  
  public void setWaitForChildren(boolean paramBoolean) {
    this.waitForChildren = paramBoolean;
  }
  
  @Nullable
  public Long getIdleTimeout() {
    return this.idleTimeout;
  }
  
  @Internal
  public void setDeadlineTimeout(@Nullable Long paramLong) {
    this.deadlineTimeout = paramLong;
  }
  
  @Internal
  @Nullable
  public Long getDeadlineTimeout() {
    return this.deadlineTimeout;
  }
  
  public void setIdleTimeout(@Nullable Long paramLong) {
    this.idleTimeout = paramLong;
  }
  
  @Nullable
  public TransactionFinishedCallback getTransactionFinishedCallback() {
    return this.transactionFinishedCallback;
  }
  
  public void setTransactionFinishedCallback(@Nullable TransactionFinishedCallback paramTransactionFinishedCallback) {
    this.transactionFinishedCallback = paramTransactionFinishedCallback;
  }
  
  @Internal
  public void setAppStartTransaction(boolean paramBoolean) {
    this.isAppStartTransaction = paramBoolean;
  }
  
  @Internal
  public boolean isAppStartTransaction() {
    return this.isAppStartTransaction;
  }
  
  @Internal
  @Nullable
  public ISpanFactory getSpanFactory() {
    return this.spanFactory;
  }
  
  @Internal
  public void setSpanFactory(@NotNull ISpanFactory paramISpanFactory) {
    this.spanFactory = paramISpanFactory;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TransactionOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */