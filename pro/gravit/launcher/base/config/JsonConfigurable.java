package pro.gravit.launcher.base.config;

import java.lang.reflect.Type;
import java.nio.file.Path;

public abstract class JsonConfigurable<T> implements JsonConfigurableInterface<T> {
  protected final transient Path configPath;
  
  private final transient Type type;
  
  public JsonConfigurable(Type paramType, Path paramPath) {
    this.type = paramType;
    this.configPath = paramPath;
  }
  
  public Path getPath() {
    return this.configPath;
  }
  
  public Type getType() {
    return this.type;
  }
  
  public abstract T getConfig();
  
  public abstract void setConfig(T paramT);
  
  public abstract T getDefaultConfig();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\config\JsonConfigurable.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */