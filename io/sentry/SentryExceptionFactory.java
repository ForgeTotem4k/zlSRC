package io.sentry;

import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.Objects;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryExceptionFactory {
  @NotNull
  private final SentryStackTraceFactory sentryStackTraceFactory;
  
  public SentryExceptionFactory(@NotNull SentryStackTraceFactory paramSentryStackTraceFactory) {
    this.sentryStackTraceFactory = (SentryStackTraceFactory)Objects.requireNonNull(paramSentryStackTraceFactory, "The SentryStackTraceFactory is required.");
  }
  
  @NotNull
  public List<SentryException> getSentryExceptionsFromThread(@NotNull SentryThread paramSentryThread, @NotNull Mechanism paramMechanism, @NotNull Throwable paramThrowable) {
    SentryStackTrace sentryStackTrace = paramSentryThread.getStacktrace();
    if (sentryStackTrace == null)
      return new ArrayList<>(0); 
    ArrayList<SentryException> arrayList = new ArrayList(1);
    arrayList.add(getSentryException(paramThrowable, paramMechanism, paramSentryThread.getId(), sentryStackTrace.getFrames(), true));
    return arrayList;
  }
  
  @NotNull
  public List<SentryException> getSentryExceptions(@NotNull Throwable paramThrowable) {
    return getSentryExceptions(extractExceptionQueue(paramThrowable));
  }
  
  @NotNull
  private List<SentryException> getSentryExceptions(@NotNull Deque<SentryException> paramDeque) {
    return new ArrayList<>(paramDeque);
  }
  
  @NotNull
  private SentryException getSentryException(@NotNull Throwable paramThrowable, @Nullable Mechanism paramMechanism, @Nullable Long paramLong, @Nullable List<SentryStackFrame> paramList, boolean paramBoolean) {
    Package package_ = paramThrowable.getClass().getPackage();
    String str1 = paramThrowable.getClass().getName();
    SentryException sentryException = new SentryException();
    String str2 = paramThrowable.getMessage();
    String str3 = (package_ != null) ? str1.replace(package_.getName() + ".", "") : str1;
    String str4 = (package_ != null) ? package_.getName() : null;
    if (paramList != null && !paramList.isEmpty()) {
      SentryStackTrace sentryStackTrace = new SentryStackTrace(paramList);
      if (paramBoolean)
        sentryStackTrace.setSnapshot(Boolean.valueOf(true)); 
      sentryException.setStacktrace(sentryStackTrace);
    } 
    sentryException.setThreadId(paramLong);
    sentryException.setType(str3);
    sentryException.setMechanism(paramMechanism);
    sentryException.setModule(str4);
    sentryException.setValue(str2);
    return sentryException;
  }
  
  @TestOnly
  @NotNull
  Deque<SentryException> extractExceptionQueue(@NotNull Throwable paramThrowable) {
    return extractExceptionQueueInternal(paramThrowable, new AtomicInteger(-1), new HashSet<>(), new ArrayDeque<>());
  }
  
  Deque<SentryException> extractExceptionQueueInternal(@NotNull Throwable paramThrowable, @NotNull AtomicInteger paramAtomicInteger, @NotNull HashSet<Throwable> paramHashSet, @NotNull Deque<SentryException> paramDeque) {
    Throwable throwable = paramThrowable;
    int i;
    for (i = paramAtomicInteger.get(); throwable != null && paramHashSet.add(throwable); i = j) {
      Mechanism mechanism;
      Thread thread;
      boolean bool1 = false;
      if (throwable instanceof ExceptionMechanismException) {
        ExceptionMechanismException exceptionMechanismException = (ExceptionMechanismException)throwable;
        mechanism = exceptionMechanismException.getExceptionMechanism();
        throwable = exceptionMechanismException.getThrowable();
        thread = exceptionMechanismException.getThread();
        bool1 = exceptionMechanismException.isSnapshot();
      } else {
        mechanism = new Mechanism();
        thread = Thread.currentThread();
      } 
      boolean bool2 = Boolean.FALSE.equals(mechanism.isHandled());
      List<SentryStackFrame> list = this.sentryStackTraceFactory.getStackFrames(throwable.getStackTrace(), bool2);
      SentryException sentryException = getSentryException(throwable, mechanism, Long.valueOf(thread.getId()), list, bool1);
      paramDeque.addFirst(sentryException);
      if (mechanism.getType() == null)
        mechanism.setType("chained"); 
      if (paramAtomicInteger.get() >= 0)
        mechanism.setParentId(Integer.valueOf(i)); 
      int j = paramAtomicInteger.incrementAndGet();
      mechanism.setExceptionId(Integer.valueOf(j));
      Throwable[] arrayOfThrowable = throwable.getSuppressed();
      if (arrayOfThrowable != null && arrayOfThrowable.length > 0) {
        mechanism.setExceptionGroup(Boolean.valueOf(true));
        for (Throwable throwable1 : arrayOfThrowable)
          extractExceptionQueueInternal(throwable1, paramAtomicInteger, paramHashSet, paramDeque); 
      } 
      throwable = throwable.getCause();
    } 
    return paramDeque;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryExceptionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */