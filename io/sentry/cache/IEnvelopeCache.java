package io.sentry.cache;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import org.jetbrains.annotations.NotNull;

public interface IEnvelopeCache extends Iterable<SentryEnvelope> {
  void store(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint);
  
  default void store(@NotNull SentryEnvelope paramSentryEnvelope) {
    store(paramSentryEnvelope, new Hint());
  }
  
  void discard(@NotNull SentryEnvelope paramSentryEnvelope);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\IEnvelopeCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */