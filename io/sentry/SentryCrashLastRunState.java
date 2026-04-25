package io.sentry;

import java.io.File;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryCrashLastRunState {
  private static final SentryCrashLastRunState INSTANCE = new SentryCrashLastRunState();
  
  private boolean readCrashedLastRun;
  
  @Nullable
  private Boolean crashedLastRun;
  
  @NotNull
  private final Object crashedLastRunLock = new Object();
  
  public static SentryCrashLastRunState getInstance() {
    return INSTANCE;
  }
  
  @Nullable
  public Boolean isCrashedLastRun(@Nullable String paramString, boolean paramBoolean) {
    synchronized (this.crashedLastRunLock) {
      if (this.readCrashedLastRun)
        return this.crashedLastRun; 
      if (paramString == null)
        return null; 
      this.readCrashedLastRun = true;
      File file1 = new File(paramString, "last_crash");
      File file2 = new File(paramString, ".sentry-native/last_crash");
      boolean bool = false;
      try {
        if (file1.exists()) {
          bool = true;
          file1.delete();
        } else if (file2.exists()) {
          bool = true;
          if (paramBoolean)
            file2.delete(); 
        } 
      } catch (Throwable throwable) {}
      this.crashedLastRun = Boolean.valueOf(bool);
    } 
    return this.crashedLastRun;
  }
  
  public void setCrashedLastRun(boolean paramBoolean) {
    synchronized (this.crashedLastRunLock) {
      if (!this.readCrashedLastRun) {
        this.crashedLastRun = Boolean.valueOf(paramBoolean);
        this.readCrashedLastRun = true;
      } 
    } 
  }
  
  @TestOnly
  public void reset() {
    synchronized (this.crashedLastRunLock) {
      this.readCrashedLastRun = false;
      this.crashedLastRun = null;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryCrashLastRunState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */