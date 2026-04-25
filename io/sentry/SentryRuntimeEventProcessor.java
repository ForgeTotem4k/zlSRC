package io.sentry;

import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SentryRuntimeEventProcessor implements EventProcessor {
  @Nullable
  private final String javaVersion;
  
  @Nullable
  private final String javaVendor;
  
  public SentryRuntimeEventProcessor(@Nullable String paramString1, @Nullable String paramString2) {
    this.javaVersion = paramString1;
    this.javaVendor = paramString2;
  }
  
  public SentryRuntimeEventProcessor() {
    this(System.getProperty("java.version"), System.getProperty("java.vendor"));
  }
  
  @NotNull
  public SentryEvent process(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return process(paramSentryEvent);
  }
  
  @NotNull
  public SentryTransaction process(@NotNull SentryTransaction paramSentryTransaction, @Nullable Hint paramHint) {
    return process(paramSentryTransaction);
  }
  
  @NotNull
  private <T extends SentryBaseEvent> T process(@NotNull T paramT) {
    if (paramT.getContexts().getRuntime() == null)
      paramT.getContexts().setRuntime(new SentryRuntime()); 
    SentryRuntime sentryRuntime = paramT.getContexts().getRuntime();
    if (sentryRuntime != null && sentryRuntime.getName() == null && sentryRuntime.getVersion() == null) {
      sentryRuntime.setName(this.javaVendor);
      sentryRuntime.setVersion(this.javaVersion);
    } 
    return paramT;
  }
  
  @Nullable
  public Long getOrder() {
    return Long.valueOf(2000L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryRuntimeEventProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */