package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class Pair<A, B> {
  @Nullable
  private final A first;
  
  @Nullable
  private final B second;
  
  public Pair(@Nullable A paramA, @Nullable B paramB) {
    this.first = paramA;
    this.second = paramB;
  }
  
  @Nullable
  public A getFirst() {
    return this.first;
  }
  
  @Nullable
  public B getSecond() {
    return this.second;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */