package io.sentry;

import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class OptionsContainer<T> {
  @NotNull
  private final Class<T> clazz;
  
  @NotNull
  public static <T> OptionsContainer<T> create(@NotNull Class<T> paramClass) {
    return new OptionsContainer<>(paramClass);
  }
  
  private OptionsContainer(@NotNull Class<T> paramClass) {
    this.clazz = paramClass;
  }
  
  @NotNull
  public T createInstance() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return this.clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\OptionsContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */