package io.sentry;

import io.sentry.transport.RateLimiter;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SendCachedEnvelopeFireAndForgetIntegration implements Integration, IConnectionStatusProvider.IConnectionStatusObserver, Closeable {
  @NotNull
  private final SendFireAndForgetFactory factory;
  
  @Nullable
  private IConnectionStatusProvider connectionStatusProvider;
  
  @Nullable
  private IScopes scopes;
  
  @Nullable
  private SentryOptions options;
  
  @Nullable
  private SendFireAndForget sender;
  
  private final AtomicBoolean isInitialized = new AtomicBoolean(false);
  
  private final AtomicBoolean isClosed = new AtomicBoolean(false);
  
  public SendCachedEnvelopeFireAndForgetIntegration(@NotNull SendFireAndForgetFactory paramSendFireAndForgetFactory) {
    this.factory = (SendFireAndForgetFactory)Objects.requireNonNull(paramSendFireAndForgetFactory, "SendFireAndForgetFactory is required");
  }
  
  public void register(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "Scopes are required");
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required");
    String str = paramSentryOptions.getCacheDirPath();
    if (!this.factory.hasValidPath(str, paramSentryOptions.getLogger())) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "No cache dir path is defined in options.", new Object[0]);
      return;
    } 
    paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "SendCachedEventFireAndForgetIntegration installed.", new Object[0]);
    IntegrationUtils.addIntegrationToSdkVersion(getClass());
    sendCachedEnvelopes(paramIScopes, paramSentryOptions);
  }
  
  public void close() throws IOException {
    this.isClosed.set(true);
    if (this.connectionStatusProvider != null)
      this.connectionStatusProvider.removeConnectionStatusObserver(this); 
  }
  
  public void onConnectionStatusChanged(@NotNull IConnectionStatusProvider.ConnectionStatus paramConnectionStatus) {
    if (this.scopes != null && this.options != null)
      sendCachedEnvelopes(this.scopes, this.options); 
  }
  
  private synchronized void sendCachedEnvelopes(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    try {
      paramSentryOptions.getExecutorService().submit(() -> {
            try {
              if (this.isClosed.get()) {
                paramSentryOptions.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, not trying to send after closing.", new Object[0]);
                return;
              } 
              if (!this.isInitialized.getAndSet(true)) {
                this.connectionStatusProvider = paramSentryOptions.getConnectionStatusProvider();
                this.connectionStatusProvider.addConnectionStatusObserver(this);
                this.sender = this.factory.create(paramIScopes, paramSentryOptions);
              } 
              if (this.connectionStatusProvider != null && this.connectionStatusProvider.getConnectionStatus() == IConnectionStatusProvider.ConnectionStatus.DISCONNECTED) {
                paramSentryOptions.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, no connection.", new Object[0]);
                return;
              } 
              RateLimiter rateLimiter = paramIScopes.getRateLimiter();
              if (rateLimiter != null && rateLimiter.isActiveForCategory(DataCategory.All)) {
                paramSentryOptions.getLogger().log(SentryLevel.INFO, "SendCachedEnvelopeFireAndForgetIntegration, rate limiting active.", new Object[0]);
                return;
              } 
              if (this.sender == null) {
                paramSentryOptions.getLogger().log(SentryLevel.ERROR, "SendFireAndForget factory is null.", new Object[0]);
                return;
              } 
              this.sender.send();
            } catch (Throwable throwable) {
              paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed trying to send cached events.", throwable);
            } 
          });
    } catch (RejectedExecutionException rejectedExecutionException) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Cached events will not be sent. Did you call Sentry.close()?", rejectedExecutionException);
    } catch (Throwable throwable) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Cached events will not be sent", throwable);
    } 
  }
  
  public static interface SendFireAndForgetFactory {
    @Nullable
    SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes param1IScopes, @NotNull SentryOptions param1SentryOptions);
    
    default boolean hasValidPath(@Nullable String param1String, @NotNull ILogger param1ILogger) {
      if (param1String == null || param1String.isEmpty()) {
        param1ILogger.log(SentryLevel.INFO, "No cached dir path is defined in options.", new Object[0]);
        return false;
      } 
      return true;
    }
    
    @NotNull
    default SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget processDir(@NotNull DirectoryProcessor param1DirectoryProcessor, @NotNull String param1String, @NotNull ILogger param1ILogger) {
      File file = new File(param1String);
      return () -> {
          param1ILogger.log(SentryLevel.DEBUG, "Started processing cached files from %s", new Object[] { param1String });
          param1DirectoryProcessor.processDirectory(param1File);
          param1ILogger.log(SentryLevel.DEBUG, "Finished processing cached files from %s", new Object[] { param1String });
        };
    }
    
    static {
    
    }
  }
  
  public static interface SendFireAndForget {
    void send();
    
    static {
    
    }
  }
  
  public static interface SendFireAndForgetDirPath {
    @Nullable
    String getDirPath();
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SendCachedEnvelopeFireAndForgetIntegration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */