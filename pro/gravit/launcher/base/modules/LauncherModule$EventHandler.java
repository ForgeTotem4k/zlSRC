package pro.gravit.launcher.base.modules;

@FunctionalInterface
public interface EventHandler<T extends LauncherModule.Event> {
  void event(T paramT);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModule$EventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */