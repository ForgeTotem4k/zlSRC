package io.sentry;

import org.jetbrains.annotations.NotNull;

public interface IEnvelopeSender {
  void processEnvelopeFile(@NotNull String paramString, @NotNull Hint paramHint);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IEnvelopeSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */