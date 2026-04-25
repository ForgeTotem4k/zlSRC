package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TransactionContext extends SpanContext {
  @NotNull
  public static final String DEFAULT_TRANSACTION_NAME = "<unlabeled transaction>";
  
  @NotNull
  private static final TransactionNameSource DEFAULT_NAME_SOURCE = TransactionNameSource.CUSTOM;
  
  @NotNull
  private static final String DEFAULT_OPERATION = "default";
  
  @NotNull
  private String name;
  
  @NotNull
  private TransactionNameSource transactionNameSource;
  
  @Nullable
  private TracesSamplingDecision parentSamplingDecision;
  
  private boolean isForNextAppStart = false;
  
  @Deprecated
  @NotNull
  public static TransactionContext fromSentryTrace(@NotNull String paramString1, @NotNull String paramString2, @NotNull SentryTraceHeader paramSentryTraceHeader) {
    Boolean bool = paramSentryTraceHeader.isSampled();
    TracesSamplingDecision tracesSamplingDecision = (bool == null) ? null : new TracesSamplingDecision(bool);
    TransactionContext transactionContext = new TransactionContext(paramSentryTraceHeader.getTraceId(), new SpanId(), paramSentryTraceHeader.getSpanId(), tracesSamplingDecision, null);
    transactionContext.setName(paramString1);
    transactionContext.setTransactionNameSource(TransactionNameSource.CUSTOM);
    transactionContext.setOperation(paramString2);
    return transactionContext;
  }
  
  @Internal
  public static TransactionContext fromPropagationContext(@NotNull PropagationContext paramPropagationContext) {
    Boolean bool = paramPropagationContext.isSampled();
    TracesSamplingDecision tracesSamplingDecision = (bool == null) ? null : new TracesSamplingDecision(bool);
    Baggage baggage = paramPropagationContext.getBaggage();
    if (baggage != null) {
      baggage.freeze();
      Double double_ = baggage.getSampleRateDouble();
      Boolean bool1 = Boolean.valueOf((bool != null) ? bool.booleanValue() : false);
      if (double_ != null) {
        tracesSamplingDecision = new TracesSamplingDecision(bool1, double_);
      } else {
        tracesSamplingDecision = new TracesSamplingDecision(bool1);
      } 
    } 
    return new TransactionContext(paramPropagationContext.getTraceId(), paramPropagationContext.getSpanId(), paramPropagationContext.getParentSpanId(), tracesSamplingDecision, baggage);
  }
  
  public TransactionContext(@NotNull String paramString1, @NotNull String paramString2) {
    this(paramString1, paramString2, (TracesSamplingDecision)null);
  }
  
  @Internal
  public TransactionContext(@NotNull String paramString1, @NotNull TransactionNameSource paramTransactionNameSource, @NotNull String paramString2) {
    this(paramString1, paramTransactionNameSource, paramString2, (TracesSamplingDecision)null);
  }
  
  public TransactionContext(@NotNull String paramString1, @NotNull String paramString2, @Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    this(paramString1, TransactionNameSource.CUSTOM, paramString2, paramTracesSamplingDecision);
  }
  
  @Internal
  public TransactionContext(@NotNull String paramString1, @NotNull TransactionNameSource paramTransactionNameSource, @NotNull String paramString2, @Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    super(paramString2);
    this.name = (String)Objects.requireNonNull(paramString1, "name is required");
    this.transactionNameSource = paramTransactionNameSource;
    setSamplingDecision(paramTracesSamplingDecision);
  }
  
  @Internal
  public TransactionContext(@NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId1, @Nullable SpanId paramSpanId2, @Nullable TracesSamplingDecision paramTracesSamplingDecision, @Nullable Baggage paramBaggage) {
    super(paramSentryId, paramSpanId1, "default", paramSpanId2, null);
    this.name = "<unlabeled transaction>";
    this.parentSamplingDecision = paramTracesSamplingDecision;
    this.transactionNameSource = DEFAULT_NAME_SOURCE;
    this.baggage = paramBaggage;
  }
  
  @NotNull
  public String getName() {
    return this.name;
  }
  
  @Nullable
  public Boolean getParentSampled() {
    return (this.parentSamplingDecision == null) ? null : this.parentSamplingDecision.getSampled();
  }
  
  @Nullable
  public TracesSamplingDecision getParentSamplingDecision() {
    return this.parentSamplingDecision;
  }
  
  public void setParentSampled(@Nullable Boolean paramBoolean) {
    if (paramBoolean == null) {
      this.parentSamplingDecision = null;
    } else {
      this.parentSamplingDecision = new TracesSamplingDecision(paramBoolean);
    } 
  }
  
  public void setParentSampled(@Nullable Boolean paramBoolean1, @Nullable Boolean paramBoolean2) {
    if (paramBoolean1 == null) {
      this.parentSamplingDecision = null;
    } else if (paramBoolean2 == null) {
      this.parentSamplingDecision = new TracesSamplingDecision(paramBoolean1);
    } else {
      this.parentSamplingDecision = new TracesSamplingDecision(paramBoolean1, null, paramBoolean2, null);
    } 
  }
  
  @NotNull
  public TransactionNameSource getTransactionNameSource() {
    return this.transactionNameSource;
  }
  
  public void setName(@NotNull String paramString) {
    this.name = (String)Objects.requireNonNull(paramString, "name is required");
  }
  
  public void setTransactionNameSource(@NotNull TransactionNameSource paramTransactionNameSource) {
    this.transactionNameSource = paramTransactionNameSource;
  }
  
  @Internal
  public void setForNextAppStart(boolean paramBoolean) {
    this.isForNextAppStart = paramBoolean;
  }
  
  public boolean isForNextAppStart() {
    return this.isForNextAppStart;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TransactionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */