package pro.gravit.utils;

@FunctionalInterface
public interface Hook<V, R> {
  boolean hook(V paramV, R paramR) throws HookException;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\BiHookSet$Hook.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */