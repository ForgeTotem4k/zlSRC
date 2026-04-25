package io.sentry.exception;

import io.sentry.protocol.Mechanism;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class ExceptionMechanismException extends RuntimeException {
  private static final long serialVersionUID = 142345454265713915L;
  
  @NotNull
  private final Mechanism exceptionMechanism;
  
  @NotNull
  private final Throwable throwable;
  
  @NotNull
  private final Thread thread;
  
  private final boolean snapshot;
  
  public ExceptionMechanismException(@NotNull Mechanism paramMechanism, @NotNull Throwable paramThrowable, @NotNull Thread paramThread, boolean paramBoolean) {
    this.exceptionMechanism = (Mechanism)Objects.requireNonNull(paramMechanism, "Mechanism is required.");
    this.throwable = (Throwable)Objects.requireNonNull(paramThrowable, "Throwable is required.");
    this.thread = (Thread)Objects.requireNonNull(paramThread, "Thread is required.");
    this.snapshot = paramBoolean;
  }
  
  public ExceptionMechanismException(@NotNull Mechanism paramMechanism, @NotNull Throwable paramThrowable, @NotNull Thread paramThread) {
    this(paramMechanism, paramThrowable, paramThread, false);
  }
  
  @NotNull
  public Mechanism getExceptionMechanism() {
    return this.exceptionMechanism;
  }
  
  @NotNull
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  @NotNull
  public Thread getThread() {
    return this.thread;
  }
  
  public boolean isSnapshot() {
    return this.snapshot;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\exception\ExceptionMechanismException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */