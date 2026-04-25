package io.sentry;

import io.sentry.transport.ITransport;
import org.jetbrains.annotations.NotNull;

public interface ITransportFactory {
  @NotNull
  ITransport create(@NotNull SentryOptions paramSentryOptions, @NotNull RequestDetails paramRequestDetails);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ITransportFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */