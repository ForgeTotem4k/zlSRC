package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import io.sentry.cache.IEnvelopeCache;
import java.util.Collections;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public final class NoOpEnvelopeCache implements IEnvelopeCache {
  private static final NoOpEnvelopeCache instance = new NoOpEnvelopeCache();
  
  public static NoOpEnvelopeCache getInstance() {
    return instance;
  }
  
  public void store(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) {}
  
  public void discard(@NotNull SentryEnvelope paramSentryEnvelope) {}
  
  @NotNull
  public Iterator<SentryEnvelope> iterator() {
    return Collections.emptyIterator();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\NoOpEnvelopeCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */