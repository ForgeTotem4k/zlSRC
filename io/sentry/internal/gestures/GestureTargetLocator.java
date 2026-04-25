package io.sentry.internal.gestures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GestureTargetLocator {
  @Nullable
  UiElement locate(@NotNull Object paramObject, float paramFloat1, float paramFloat2, UiElement.Type paramType);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\gestures\GestureTargetLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */