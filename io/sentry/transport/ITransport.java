package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITransport extends Closeable {
  void send(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) throws IOException;
  
  default void send(@NotNull SentryEnvelope paramSentryEnvelope) throws IOException {
    send(paramSentryEnvelope, new Hint());
  }
  
  default boolean isHealthy() {
    return true;
  }
  
  void flush(long paramLong);
  
  @Nullable
  RateLimiter getRateLimiter();
  
  void close(boolean paramBoolean) throws IOException;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\ITransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */