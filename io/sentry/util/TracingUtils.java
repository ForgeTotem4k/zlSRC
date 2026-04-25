package io.sentry.util;

import io.sentry.Baggage;
import io.sentry.BaggageHeader;
import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.PropagationContext;
import io.sentry.SentryOptions;
import io.sentry.SentryTraceHeader;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TracingUtils {
  public static void startNewTrace(@NotNull IScopes paramIScopes) {
    paramIScopes.configureScope(paramIScope -> paramIScope.withPropagationContext(()));
  }
  
  @Nullable
  public static TracingHeaders traceIfAllowed(@NotNull IScopes paramIScopes, @NotNull String paramString, @Nullable List<String> paramList, @Nullable ISpan paramISpan) {
    SentryOptions sentryOptions = paramIScopes.getOptions();
    return (sentryOptions.isTraceSampling() && shouldAttachTracingHeaders(paramString, sentryOptions)) ? trace(paramIScopes, paramList, paramISpan) : null;
  }
  
  @Nullable
  public static TracingHeaders trace(@NotNull IScopes paramIScopes, @Nullable List<String> paramList, @Nullable ISpan paramISpan) {
    SentryOptions sentryOptions = paramIScopes.getOptions();
    if (paramISpan != null && !paramISpan.isNoOp())
      return new TracingHeaders(paramISpan.toSentryTrace(), paramISpan.toBaggageHeader(paramList)); 
    PropagationContextHolder propagationContextHolder = new PropagationContextHolder();
    paramIScopes.configureScope(paramIScope -> paramPropagationContextHolder.propagationContext = maybeUpdateBaggage(paramIScope, paramSentryOptions));
    if (propagationContextHolder.propagationContext != null) {
      PropagationContext propagationContext = propagationContextHolder.propagationContext;
      Baggage baggage = propagationContext.getBaggage();
      BaggageHeader baggageHeader = null;
      if (baggage != null)
        baggageHeader = BaggageHeader.fromBaggageAndOutgoingHeader(baggage, paramList); 
      return new TracingHeaders(new SentryTraceHeader(propagationContext.getTraceId(), propagationContext.getSpanId(), null), baggageHeader);
    } 
    return null;
  }
  
  @NotNull
  public static PropagationContext maybeUpdateBaggage(@NotNull IScope paramIScope, @NotNull SentryOptions paramSentryOptions) {
    return paramIScope.withPropagationContext(paramPropagationContext -> {
          Baggage baggage = paramPropagationContext.getBaggage();
          if (baggage == null) {
            baggage = new Baggage(paramSentryOptions.getLogger());
            paramPropagationContext.setBaggage(baggage);
          } 
          if (baggage.isMutable()) {
            baggage.setValuesFromScope(paramIScope, paramSentryOptions);
            baggage.freeze();
          } 
        });
  }
  
  private static boolean shouldAttachTracingHeaders(@NotNull String paramString, @NotNull SentryOptions paramSentryOptions) {
    return PropagationTargetsUtils.contain(paramSentryOptions.getTracePropagationTargets(), paramString);
  }
  
  static {
  
  }
  
  public static final class TracingHeaders {
    @NotNull
    private final SentryTraceHeader sentryTraceHeader;
    
    @Nullable
    private final BaggageHeader baggageHeader;
    
    public TracingHeaders(@NotNull SentryTraceHeader param1SentryTraceHeader, @Nullable BaggageHeader param1BaggageHeader) {
      this.sentryTraceHeader = param1SentryTraceHeader;
      this.baggageHeader = param1BaggageHeader;
    }
    
    @NotNull
    public SentryTraceHeader getSentryTraceHeader() {
      return this.sentryTraceHeader;
    }
    
    @Nullable
    public BaggageHeader getBaggageHeader() {
      return this.baggageHeader;
    }
  }
  
  private static final class PropagationContextHolder {
    @Nullable
    private PropagationContext propagationContext = null;
    
    private PropagationContextHolder() {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\TracingUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */