package io.sentry;

import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;

public final class ShutdownHookIntegration implements Integration, Closeable {
  @NotNull
  private final Runtime runtime;
  
  @Nullable
  private Thread thread;
  
  @TestOnly
  public ShutdownHookIntegration(@NotNull Runtime paramRuntime) {
    this.runtime = (Runtime)Objects.requireNonNull(paramRuntime, "Runtime is required");
  }
  
  public ShutdownHookIntegration() {
    this(Runtime.getRuntime());
  }
  
  public void register(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    Objects.requireNonNull(paramIScopes, "Scopes are required");
    Objects.requireNonNull(paramSentryOptions, "SentryOptions is required");
    if (paramSentryOptions.isEnableShutdownHook()) {
      this.thread = new Thread(() -> paramIScopes.flush(paramSentryOptions.getFlushTimeoutMillis()));
      handleShutdownInProgress(() -> {
            this.runtime.addShutdownHook(this.thread);
            paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "ShutdownHookIntegration installed.", new Object[0]);
            IntegrationUtils.addIntegrationToSdkVersion(getClass());
          });
    } else {
      paramSentryOptions.getLogger().log(SentryLevel.INFO, "enableShutdownHook is disabled.", new Object[0]);
    } 
  }
  
  public void close() throws IOException {
    if (this.thread != null)
      handleShutdownInProgress(() -> this.runtime.removeShutdownHook(this.thread)); 
  }
  
  private void handleShutdownInProgress(@NotNull Runnable paramRunnable) {
    try {
      paramRunnable.run();
    } catch (IllegalStateException illegalStateException) {
      String str = illegalStateException.getMessage();
      if (str == null || (!str.equals("Shutdown in progress") && !str.equals("VM already shutting down")))
        throw illegalStateException; 
    } 
  }
  
  @VisibleForTesting
  @Nullable
  Thread getHook() {
    return this.thread;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ShutdownHookIntegration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */