package io.sentry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NoOpSerializer implements ISerializer {
  private static final NoOpSerializer instance = new NoOpSerializer();
  
  public static NoOpSerializer getInstance() {
    return instance;
  }
  
  @Nullable
  public <T, R> T deserializeCollection(@NotNull Reader paramReader, @NotNull Class<T> paramClass, @Nullable JsonDeserializer<R> paramJsonDeserializer) {
    return null;
  }
  
  @Nullable
  public <T> T deserialize(@NotNull Reader paramReader, @NotNull Class<T> paramClass) {
    return null;
  }
  
  @Nullable
  public SentryEnvelope deserializeEnvelope(@NotNull InputStream paramInputStream) {
    return null;
  }
  
  public <T> void serialize(@NotNull T paramT, @NotNull Writer paramWriter) throws IOException {}
  
  public void serialize(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull OutputStream paramOutputStream) throws Exception {}
  
  @NotNull
  public String serialize(@NotNull Map<String, Object> paramMap) throws Exception {
    return "";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */