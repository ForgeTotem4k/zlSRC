package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TracesSamplingDecision {
  @NotNull
  private final Boolean sampled;
  
  @Nullable
  private final Double sampleRate;
  
  @NotNull
  private final Boolean profileSampled;
  
  @Nullable
  private final Double profileSampleRate;
  
  public TracesSamplingDecision(@NotNull Boolean paramBoolean) {
    this(paramBoolean, null);
  }
  
  public TracesSamplingDecision(@NotNull Boolean paramBoolean, @Nullable Double paramDouble) {
    this(paramBoolean, paramDouble, Boolean.valueOf(false), null);
  }
  
  public TracesSamplingDecision(@NotNull Boolean paramBoolean1, @Nullable Double paramDouble1, @NotNull Boolean paramBoolean2, @Nullable Double paramDouble2) {
    this.sampled = paramBoolean1;
    this.sampleRate = paramDouble1;
    this.profileSampled = Boolean.valueOf((paramBoolean1.booleanValue() && paramBoolean2.booleanValue()));
    this.profileSampleRate = paramDouble2;
  }
  
  @NotNull
  public Boolean getSampled() {
    return this.sampled;
  }
  
  @Nullable
  public Double getSampleRate() {
    return this.sampleRate;
  }
  
  @NotNull
  public Boolean getProfileSampled() {
    return this.profileSampled;
  }
  
  @Nullable
  public Double getProfileSampleRate() {
    return this.profileSampleRate;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\TracesSamplingDecision.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */