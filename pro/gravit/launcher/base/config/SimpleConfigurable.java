package pro.gravit.launcher.base.config;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;

public class SimpleConfigurable<T> extends JsonConfigurable<T> {
  private final Class<T> tClass;
  
  public T config;
  
  public SimpleConfigurable(Class<T> paramClass, Path paramPath) {
    super(paramClass, paramPath);
    this.tClass = paramClass;
  }
  
  public T getConfig() {
    return this.config;
  }
  
  public void setConfig(T paramT) {
    this.config = paramT;
  }
  
  public T getDefaultConfig() {
    try {
      return (T)MethodHandles.publicLookup().findConstructor(this.tClass, MethodType.methodType(void.class)).invokeWithArguments(new Object[0]);
    } catch (Throwable throwable) {
      return null;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\config\SimpleConfigurable.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */