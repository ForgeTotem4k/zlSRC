package io.sentry;

import io.sentry.transport.AsyncHttpTransport;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class AsyncHttpTransportFactory implements ITransportFactory {
  @NotNull
  public ITransport create(@NotNull SentryOptions paramSentryOptions, @NotNull RequestDetails paramRequestDetails) {
    Objects.requireNonNull(paramSentryOptions, "options is required");
    Objects.requireNonNull(paramRequestDetails, "requestDetails is required");
    return (ITransport)new AsyncHttpTransport(paramSentryOptions, new RateLimiter(paramSentryOptions), paramSentryOptions.getTransportGate(), paramRequestDetails);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\AsyncHttpTransportFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */