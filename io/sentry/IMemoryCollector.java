package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public interface IMemoryCollector {
  @Nullable
  MemoryCollectionData collect();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IMemoryCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */