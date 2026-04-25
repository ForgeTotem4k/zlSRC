package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoOpTransport implements ITransport {
  private static final NoOpTransport instance = new NoOpTransport();
  
  @NotNull
  public static NoOpTransport getInstance() {
    return instance;
  }
  
  public void send(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) throws IOException {}
  
  public void flush(long paramLong) {}
  
  @Nullable
  public RateLimiter getRateLimiter() {
    return null;
  }
  
  public void close() throws IOException {}
  
  public void close(boolean paramBoolean) throws IOException {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\NoOpTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */