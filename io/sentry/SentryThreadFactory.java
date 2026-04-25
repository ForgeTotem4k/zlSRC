package io.sentry;

import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryThreadFactory {
  @NotNull
  private final SentryStackTraceFactory sentryStackTraceFactory;
  
  @NotNull
  private final SentryOptions options;
  
  public SentryThreadFactory(@NotNull SentryStackTraceFactory paramSentryStackTraceFactory, @NotNull SentryOptions paramSentryOptions) {
    this.sentryStackTraceFactory = (SentryStackTraceFactory)Objects.requireNonNull(paramSentryStackTraceFactory, "The SentryStackTraceFactory is required.");
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "The SentryOptions is required");
  }
  
  @Nullable
  List<SentryThread> getCurrentThread() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    Thread thread = Thread.currentThread();
    hashMap.put(thread, thread.getStackTrace());
    return getCurrentThreads((Map)hashMap, null, false);
  }
  
  @Nullable
  List<SentryThread> getCurrentThreads(@Nullable List<Long> paramList, boolean paramBoolean) {
    return getCurrentThreads(Thread.getAllStackTraces(), paramList, paramBoolean);
  }
  
  @Nullable
  List<SentryThread> getCurrentThreads(@Nullable List<Long> paramList) {
    return getCurrentThreads(Thread.getAllStackTraces(), paramList, false);
  }
  
  @TestOnly
  @Nullable
  List<SentryThread> getCurrentThreads(@NotNull Map<Thread, StackTraceElement[]> paramMap, @Nullable List<Long> paramList, boolean paramBoolean) {
    ArrayList<SentryThread> arrayList = null;
    Thread thread = Thread.currentThread();
    if (!paramMap.isEmpty()) {
      arrayList = new ArrayList();
      if (!paramMap.containsKey(thread))
        paramMap.put(thread, thread.getStackTrace()); 
      for (Map.Entry<Thread, StackTraceElement> entry : paramMap.entrySet()) {
        Thread thread1 = (Thread)entry.getKey();
        boolean bool = ((thread1 == thread && !paramBoolean) || (paramList != null && paramList.contains(Long.valueOf(thread1.getId())) && !paramBoolean)) ? true : false;
        arrayList.add(getSentryThread(bool, (StackTraceElement[])entry.getValue(), (Thread)entry.getKey()));
      } 
    } 
    return arrayList;
  }
  
  @NotNull
  private SentryThread getSentryThread(boolean paramBoolean, @NotNull StackTraceElement[] paramArrayOfStackTraceElement, @NotNull Thread paramThread) {
    SentryThread sentryThread = new SentryThread();
    sentryThread.setName(paramThread.getName());
    sentryThread.setPriority(Integer.valueOf(paramThread.getPriority()));
    sentryThread.setId(Long.valueOf(paramThread.getId()));
    sentryThread.setDaemon(Boolean.valueOf(paramThread.isDaemon()));
    sentryThread.setState(paramThread.getState().name());
    sentryThread.setCrashed(Boolean.valueOf(paramBoolean));
    List<SentryStackFrame> list = this.sentryStackTraceFactory.getStackFrames(paramArrayOfStackTraceElement, false);
    if (this.options.isAttachStacktrace() && list != null && !list.isEmpty()) {
      SentryStackTrace sentryStackTrace = new SentryStackTrace(list);
      sentryStackTrace.setSnapshot(Boolean.valueOf(true));
      sentryThread.setStacktrace(sentryStackTrace);
    } 
    return sentryThread;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryThreadFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */