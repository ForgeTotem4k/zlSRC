package io.sentry;

import io.sentry.exception.ExceptionMechanismException;
import io.sentry.hints.BlockingFlushHint;
import io.sentry.hints.EventDropReason;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.TransactionEnd;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.SentryId;
import io.sentry.util.HintUtils;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class UncaughtExceptionHandlerIntegration implements Integration, Thread.UncaughtExceptionHandler, Closeable {
  @Nullable
  private Thread.UncaughtExceptionHandler defaultExceptionHandler;
  
  @Nullable
  private IScopes scopes;
  
  @Nullable
  private SentryOptions options;
  
  private boolean registered = false;
  
  @NotNull
  private final UncaughtExceptionHandler threadAdapter;
  
  public UncaughtExceptionHandlerIntegration() {
    this(UncaughtExceptionHandler.Adapter.getInstance());
  }
  
  UncaughtExceptionHandlerIntegration(@NotNull UncaughtExceptionHandler paramUncaughtExceptionHandler) {
    this.threadAdapter = (UncaughtExceptionHandler)Objects.requireNonNull(paramUncaughtExceptionHandler, "threadAdapter is required.");
  }
  
  public final void register(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    if (this.registered) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Attempt to register a UncaughtExceptionHandlerIntegration twice.", new Object[0]);
      return;
    } 
    this.registered = true;
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "Scopes are required");
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required");
    this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration enabled: %s", new Object[] { Boolean.valueOf(this.options.isEnableUncaughtExceptionHandler()) });
    if (this.options.isEnableUncaughtExceptionHandler()) {
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.threadAdapter.getDefaultUncaughtExceptionHandler();
      if (uncaughtExceptionHandler != null) {
        this.options.getLogger().log(SentryLevel.DEBUG, "default UncaughtExceptionHandler class='" + uncaughtExceptionHandler.getClass().getName() + "'", new Object[0]);
        if (uncaughtExceptionHandler instanceof UncaughtExceptionHandlerIntegration) {
          UncaughtExceptionHandlerIntegration uncaughtExceptionHandlerIntegration = (UncaughtExceptionHandlerIntegration)uncaughtExceptionHandler;
          this.defaultExceptionHandler = uncaughtExceptionHandlerIntegration.defaultExceptionHandler;
        } else {
          this.defaultExceptionHandler = uncaughtExceptionHandler;
        } 
      } 
      this.threadAdapter.setDefaultUncaughtExceptionHandler(this);
      this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration installed.", new Object[0]);
      IntegrationUtils.addIntegrationToSdkVersion(getClass());
    } 
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
    if (this.options != null && this.scopes != null) {
      this.options.getLogger().log(SentryLevel.INFO, "Uncaught exception received.", new Object[0]);
      try {
        UncaughtExceptionHint uncaughtExceptionHint = new UncaughtExceptionHint(this.options.getFlushTimeoutMillis(), this.options.getLogger());
        Throwable throwable = getUnhandledThrowable(paramThread, paramThrowable);
        SentryEvent sentryEvent = new SentryEvent(throwable);
        sentryEvent.setLevel(SentryLevel.FATAL);
        ITransaction iTransaction = this.scopes.getTransaction();
        if (iTransaction == null && sentryEvent.getEventId() != null)
          uncaughtExceptionHint.setFlushable(sentryEvent.getEventId()); 
        Hint hint = HintUtils.createWithTypeCheckHint(uncaughtExceptionHint);
        SentryId sentryId = this.scopes.captureEvent(sentryEvent, hint);
        boolean bool = sentryId.equals(SentryId.EMPTY_ID);
        EventDropReason eventDropReason = HintUtils.getEventDropReason(hint);
        if ((!bool || EventDropReason.MULTITHREADED_DEDUPLICATION.equals(eventDropReason)) && !uncaughtExceptionHint.waitFlush())
          this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush event to disk before crashing. Event: %s", new Object[] { sentryEvent.getEventId() }); 
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "Error sending uncaught exception to Sentry.", throwable);
      } 
      if (this.defaultExceptionHandler != null) {
        this.options.getLogger().log(SentryLevel.INFO, "Invoking inner uncaught exception handler.", new Object[0]);
        this.defaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
      } else if (this.options.isPrintUncaughtStackTrace()) {
        paramThrowable.printStackTrace();
      } 
    } 
  }
  
  @TestOnly
  @NotNull
  static Throwable getUnhandledThrowable(@NotNull Thread paramThread, @NotNull Throwable paramThrowable) {
    Mechanism mechanism = new Mechanism();
    mechanism.setHandled(Boolean.valueOf(false));
    mechanism.setType("UncaughtExceptionHandler");
    return (Throwable)new ExceptionMechanismException(mechanism, paramThrowable, paramThread);
  }
  
  public void close() {
    if (this == this.threadAdapter.getDefaultUncaughtExceptionHandler()) {
      this.threadAdapter.setDefaultUncaughtExceptionHandler(this.defaultExceptionHandler);
      if (this.options != null)
        this.options.getLogger().log(SentryLevel.DEBUG, "UncaughtExceptionHandlerIntegration removed.", new Object[0]); 
    } 
  }
  
  @Internal
  public static class UncaughtExceptionHint extends BlockingFlushHint implements SessionEnd, TransactionEnd {
    private final AtomicReference<SentryId> flushableEventId = new AtomicReference<>();
    
    public UncaughtExceptionHint(long param1Long, @NotNull ILogger param1ILogger) {
      super(param1Long, param1ILogger);
    }
    
    public boolean isFlushable(@Nullable SentryId param1SentryId) {
      SentryId sentryId = this.flushableEventId.get();
      return (sentryId != null && sentryId.equals(param1SentryId));
    }
    
    public void setFlushable(@NotNull SentryId param1SentryId) {
      this.flushableEventId.set(param1SentryId);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\UncaughtExceptionHandlerIntegration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */