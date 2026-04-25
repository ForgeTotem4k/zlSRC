package pro.gravit.launcher.gui.impl;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;

public class ContextHelper {
  private final AbstractVisualComponent pane;
  
  ContextHelper(AbstractVisualComponent paramAbstractVisualComponent) {
    this.pane = paramAbstractVisualComponent;
  }
  
  public static <T> CompletableFuture<T> runInFxThreadStatic(GuiExceptionCallback<T> paramGuiExceptionCallback) {
    CompletableFuture<T> completableFuture = new CompletableFuture();
    if (Platform.isFxApplicationThread()) {
      try {
        completableFuture.complete(paramGuiExceptionCallback.call());
      } catch (Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
      } 
    } else {
      Platform.runLater(() -> {
            try {
              paramCompletableFuture.complete(paramGuiExceptionCallback.call());
            } catch (Throwable throwable) {
              paramCompletableFuture.completeExceptionally(throwable);
            } 
          });
    } 
    return completableFuture;
  }
  
  public <T> T runCallback(GuiExceptionCallback<T> paramGuiExceptionCallback) {
    try {
      return paramGuiExceptionCallback.call();
    } catch (Throwable throwable) {
      errorHandling(throwable);
      return null;
    } 
  }
  
  public void runCallback(GuiExceptionRunnable paramGuiExceptionRunnable) {
    try {
      paramGuiExceptionRunnable.call();
    } catch (Throwable throwable) {
      errorHandling(throwable);
    } 
  }
  
  public static CompletableFuture<Void> runInFxThreadStatic(GuiExceptionRunnable paramGuiExceptionRunnable) {
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    if (Platform.isFxApplicationThread()) {
      try {
        paramGuiExceptionRunnable.call();
        completableFuture.complete(null);
      } catch (Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
      } 
    } else {
      Platform.runLater(() -> {
            try {
              paramGuiExceptionRunnable.call();
              paramCompletableFuture.complete(null);
            } catch (Throwable throwable) {
              paramCompletableFuture.completeExceptionally(throwable);
            } 
          });
    } 
    return completableFuture;
  }
  
  public final <T> CompletableFuture<T> runInFxThread(GuiExceptionCallback<T> paramGuiExceptionCallback) {
    return runInFxThreadStatic(paramGuiExceptionCallback).exceptionally(paramThrowable -> {
          errorHandling(paramThrowable);
          return null;
        });
  }
  
  public final CompletableFuture<Void> runInFxThread(GuiExceptionRunnable paramGuiExceptionRunnable) {
    return runInFxThreadStatic(paramGuiExceptionRunnable).exceptionally(paramThrowable -> {
          errorHandling(paramThrowable);
          return null;
        });
  }
  
  final void errorHandling(Throwable paramThrowable) {
    if (this.pane != null)
      this.pane.errorHandle(paramThrowable); 
  }
  
  public static interface GuiExceptionCallback<T> {
    T call() throws Throwable;
    
    static {
    
    }
  }
  
  public static interface GuiExceptionRunnable {
    void call() throws Throwable;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\ContextHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */