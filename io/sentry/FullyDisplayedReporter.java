package io.sentry;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class FullyDisplayedReporter {
  @NotNull
  private static final FullyDisplayedReporter instance = new FullyDisplayedReporter();
  
  @NotNull
  private final List<FullyDisplayedReporterListener> listeners = new CopyOnWriteArrayList<>();
  
  @NotNull
  public static FullyDisplayedReporter getInstance() {
    return instance;
  }
  
  public void registerFullyDrawnListener(@NotNull FullyDisplayedReporterListener paramFullyDisplayedReporterListener) {
    this.listeners.add(paramFullyDisplayedReporterListener);
  }
  
  public void reportFullyDrawn() {
    Iterator<FullyDisplayedReporterListener> iterator = this.listeners.iterator();
    this.listeners.clear();
    while (iterator.hasNext())
      ((FullyDisplayedReporterListener)iterator.next()).onFullyDrawn(); 
  }
  
  @Internal
  public static interface FullyDisplayedReporterListener {
    void onFullyDrawn();
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\FullyDisplayedReporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */