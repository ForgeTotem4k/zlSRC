package pro.gravit.utils;

@FunctionalInterface
public interface Hook<R> {
  boolean hook(R paramR) throws HookException;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\HookSet$Hook.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */