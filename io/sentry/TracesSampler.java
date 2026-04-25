package io.sentry;

import io.sentry.util.Objects;
import java.security.SecureRandom;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class TracesSampler {
  @NotNull
  private static final Double DEFAULT_TRACES_SAMPLE_RATE = Double.valueOf(1.0D);
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final SecureRandom random;
  
  public TracesSampler(@NotNull SentryOptions paramSentryOptions) {
    this((SentryOptions)Objects.requireNonNull(paramSentryOptions, "options are required"), new SecureRandom());
  }
  
  @TestOnly
  TracesSampler(@NotNull SentryOptions paramSentryOptions, @NotNull SecureRandom paramSecureRandom) {
    this.options = paramSentryOptions;
    this.random = paramSecureRandom;
  }
  
  @NotNull
  public TracesSamplingDecision sample(@NotNull SamplingContext paramSamplingContext) {
    TracesSamplingDecision tracesSamplingDecision1 = paramSamplingContext.getTransactionContext().getSamplingDecision();
    if (tracesSamplingDecision1 != null)
      return tracesSamplingDecision1; 
    Double double_1 = null;
    if (this.options.getProfilesSampler() != null)
      try {
        double_1 = this.options.getProfilesSampler().sample(paramSamplingContext);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "Error in the 'ProfilesSamplerCallback' callback.", throwable);
      }  
    if (double_1 == null)
      double_1 = this.options.getProfilesSampleRate(); 
    Boolean bool1 = Boolean.valueOf((double_1 != null && sample(double_1)));
    if (this.options.getTracesSampler() != null) {
      Double double_ = null;
      try {
        double_ = this.options.getTracesSampler().sample(paramSamplingContext);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "Error in the 'TracesSamplerCallback' callback.", throwable);
      } 
      if (double_ != null)
        return new TracesSamplingDecision(Boolean.valueOf(sample(double_)), double_, bool1, double_1); 
    } 
    TracesSamplingDecision tracesSamplingDecision2 = paramSamplingContext.getTransactionContext().getParentSamplingDecision();
    if (tracesSamplingDecision2 != null)
      return tracesSamplingDecision2; 
    Double double_2 = this.options.getTracesSampleRate();
    Boolean bool2 = this.options.getEnableTracing();
    Double double_3 = Boolean.TRUE.equals(bool2) ? DEFAULT_TRACES_SAMPLE_RATE : null;
    Double double_4 = (double_2 == null) ? double_3 : double_2;
    Double double_5 = Double.valueOf(Math.pow(2.0D, this.options.getBackpressureMonitor().getDownsampleFactor()));
    Double double_6 = (double_4 == null) ? null : Double.valueOf(double_4.doubleValue() / double_5.doubleValue());
    return (double_6 != null) ? new TracesSamplingDecision(Boolean.valueOf(sample(double_6)), double_6, bool1, double_1) : new TracesSamplingDecision(Boolean.valueOf(false), null, Boolean.valueOf(false), null);
  }
  
  private boolean sample(@NotNull Double paramDouble) {
    return (paramDouble.doubleValue() >= this.random.nextDouble());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TracesSampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */