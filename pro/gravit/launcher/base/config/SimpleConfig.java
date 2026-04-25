package pro.gravit.launcher.base.config;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.nio.file.Path;

public abstract class SimpleConfig<T> implements JsonConfigurableInterface<T> {
  protected final transient Path configPath;
  
  private final transient Class<T> type;
  
  protected SimpleConfig(Class<T> paramClass, Path paramPath) {
    this.type = paramClass;
    this.configPath = paramPath;
  }
  
  public T getConfig() {
    return (T)this;
  }
  
  public T getDefaultConfig() {
    try {
      return (T)MethodHandles.publicLookup().findConstructor(this.type, MethodType.methodType(void.class)).invokeWithArguments(new Object[0]);
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  public Path getPath() {
    return this.configPath;
  }
  
  public Type getType() {
    return this.type;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\config\SimpleConfig.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */