package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoOpClientReportRecorder implements IClientReportRecorder {
  public void recordLostEnvelope(@NotNull DiscardReason paramDiscardReason, @Nullable SentryEnvelope paramSentryEnvelope) {}
  
  public void recordLostEnvelopeItem(@NotNull DiscardReason paramDiscardReason, @Nullable SentryEnvelopeItem paramSentryEnvelopeItem) {}
  
  public void recordLostEvent(@NotNull DiscardReason paramDiscardReason, @NotNull DataCategory paramDataCategory) {}
  
  public void recordLostEvent(@NotNull DiscardReason paramDiscardReason, @NotNull DataCategory paramDataCategory, long paramLong) {}
  
  @NotNull
  public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    return paramSentryEnvelope;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\NoOpClientReportRecorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */