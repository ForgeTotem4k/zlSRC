package io.sentry;

import org.jetbrains.annotations.Nullable;

interface UncaughtExceptionHandler {
  Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler();
  
  void setDefaultUncaughtExceptionHandler(@Nullable Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler);
  
  static {
  
  }
  
  public static final class Adapter implements UncaughtExceptionHandler {
    private static final Adapter INSTANCE = new Adapter();
    
    static UncaughtExceptionHandler getInstance() {
      return INSTANCE;
    }
    
    public Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
      return Thread.getDefaultUncaughtExceptionHandler();
    }
    
    public void setDefaultUncaughtExceptionHandler(@Nullable Thread.UncaughtExceptionHandler param1UncaughtExceptionHandler) {
      Thread.setDefaultUncaughtExceptionHandler(param1UncaughtExceptionHandler);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\UncaughtExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */