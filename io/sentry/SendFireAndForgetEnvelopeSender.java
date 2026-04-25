package io.sentry;

import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SendFireAndForgetEnvelopeSender implements SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetFactory {
  @NotNull
  private final SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath;
  
  public SendFireAndForgetEnvelopeSender(@NotNull SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath paramSendFireAndForgetDirPath) {
    this.sendFireAndForgetDirPath = (SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath)Objects.requireNonNull(paramSendFireAndForgetDirPath, "SendFireAndForgetDirPath is required");
  }
  
  @Nullable
  public SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    Objects.requireNonNull(paramIScopes, "Scopes are required");
    Objects.requireNonNull(paramSentryOptions, "SentryOptions is required");
    String str = this.sendFireAndForgetDirPath.getDirPath();
    if (str == null || !hasValidPath(str, paramSentryOptions.getLogger())) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "No cache dir path is defined in options.", new Object[0]);
      return null;
    } 
    EnvelopeSender envelopeSender = new EnvelopeSender(paramIScopes, paramSentryOptions.getSerializer(), paramSentryOptions.getLogger(), paramSentryOptions.getFlushTimeoutMillis(), paramSentryOptions.getMaxQueueSize());
    return processDir(envelopeSender, str, paramSentryOptions.getLogger());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SendFireAndForgetEnvelopeSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */