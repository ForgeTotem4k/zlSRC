package io.sentry;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpEnvelopeReader implements IEnvelopeReader {
  private static final NoOpEnvelopeReader instance = new NoOpEnvelopeReader();
  
  public static NoOpEnvelopeReader getInstance() {
    return instance;
  }
  
  @Nullable
  public SentryEnvelope read(@NotNull InputStream paramInputStream) throws IOException {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpEnvelopeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */