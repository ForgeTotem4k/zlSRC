package io.sentry;

import io.sentry.transport.ITransport;
import io.sentry.transport.NoOpTransport;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class NoOpTransportFactory implements ITransportFactory {
  private static final NoOpTransportFactory instance = new NoOpTransportFactory();
  
  public static NoOpTransportFactory getInstance() {
    return instance;
  }
  
  @NotNull
  public ITransport create(@NotNull SentryOptions paramSentryOptions, @NotNull RequestDetails paramRequestDetails) {
    return (ITransport)NoOpTransport.getInstance();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpTransportFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */