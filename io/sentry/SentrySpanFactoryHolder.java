package io.sentry;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Experimental
@Internal
public final class SentrySpanFactoryHolder {
  private static ISpanFactory spanFactory = new DefaultSpanFactory();
  
  public static ISpanFactory getSpanFactory() {
    return spanFactory;
  }
  
  @Internal
  public static void setSpanFactory(@NotNull ISpanFactory paramISpanFactory) {
    spanFactory = paramISpanFactory;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentrySpanFactoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */