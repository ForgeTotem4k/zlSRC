package io.sentry;

import io.sentry.protocol.SentryStackFrame;
import io.sentry.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryStackTraceFactory {
  private static final int STACKTRACE_FRAME_LIMIT = 100;
  
  @NotNull
  private final SentryOptions options;
  
  public SentryStackTraceFactory(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
  }
  
  @Nullable
  public List<SentryStackFrame> getStackFrames(@Nullable StackTraceElement[] paramArrayOfStackTraceElement, boolean paramBoolean) {
    ArrayList<SentryStackFrame> arrayList = null;
    if (paramArrayOfStackTraceElement != null && paramArrayOfStackTraceElement.length > 0) {
      arrayList = new ArrayList();
      for (StackTraceElement stackTraceElement : paramArrayOfStackTraceElement) {
        if (stackTraceElement != null) {
          String str = stackTraceElement.getClassName();
          if (paramBoolean || !str.startsWith("io.sentry.") || str.startsWith("io.sentry.samples.") || str.startsWith("io.sentry.mobile.")) {
            SentryStackFrame sentryStackFrame = new SentryStackFrame();
            sentryStackFrame.setInApp(isInApp(str));
            sentryStackFrame.setModule(str);
            sentryStackFrame.setFunction(stackTraceElement.getMethodName());
            sentryStackFrame.setFilename(stackTraceElement.getFileName());
            if (stackTraceElement.getLineNumber() >= 0)
              sentryStackFrame.setLineno(Integer.valueOf(stackTraceElement.getLineNumber())); 
            sentryStackFrame.setNative(Boolean.valueOf(stackTraceElement.isNativeMethod()));
            arrayList.add(sentryStackFrame);
            if (arrayList.size() >= 100)
              break; 
          } 
        } 
      } 
      Collections.reverse(arrayList);
    } 
    return arrayList;
  }
  
  @Nullable
  public Boolean isInApp(@Nullable String paramString) {
    if (paramString == null || paramString.isEmpty())
      return Boolean.valueOf(true); 
    List<String> list1 = this.options.getInAppIncludes();
    for (String str : list1) {
      if (paramString.startsWith(str))
        return Boolean.valueOf(true); 
    } 
    List<String> list2 = this.options.getInAppExcludes();
    for (String str : list2) {
      if (paramString.startsWith(str))
        return Boolean.valueOf(false); 
    } 
    return null;
  }
  
  @NotNull
  List<SentryStackFrame> getInAppCallStack(@NotNull Throwable paramThrowable) {
    StackTraceElement[] arrayOfStackTraceElement = paramThrowable.getStackTrace();
    List<SentryStackFrame> list1 = getStackFrames(arrayOfStackTraceElement, false);
    if (list1 == null)
      return Collections.emptyList(); 
    List<SentryStackFrame> list2 = CollectionUtils.filterListEntries(list1, paramSentryStackFrame -> Boolean.TRUE.equals(paramSentryStackFrame.isInApp()));
    return !list2.isEmpty() ? list2 : CollectionUtils.filterListEntries(list1, paramSentryStackFrame -> {
          String str = paramSentryStackFrame.getModule();
          boolean bool = false;
          if (str != null)
            bool = (str.startsWith("sun.") || str.startsWith("java.") || str.startsWith("android.") || str.startsWith("com.android.")) ? true : false; 
          return !bool;
        });
  }
  
  @Internal
  @NotNull
  public List<SentryStackFrame> getInAppCallStack() {
    return getInAppCallStack(new Exception());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryStackTraceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */