package io.sentry.transport;

import io.sentry.Hint;
import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.util.Objects;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StdoutTransport implements ITransport {
  @NotNull
  private final ISerializer serializer;
  
  public StdoutTransport(@NotNull ISerializer paramISerializer) {
    this.serializer = (ISerializer)Objects.requireNonNull(paramISerializer, "Serializer is required");
  }
  
  public void send(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) throws IOException {
    Objects.requireNonNull(paramSentryEnvelope, "SentryEnvelope is required");
    try {
      this.serializer.serialize(paramSentryEnvelope, System.out);
    } catch (Throwable throwable) {}
  }
  
  public void flush(long paramLong) {
    System.out.println("Flushing");
  }
  
  @Nullable
  public RateLimiter getRateLimiter() {
    return null;
  }
  
  public void close() {}
  
  public void close(boolean paramBoolean) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\StdoutTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */