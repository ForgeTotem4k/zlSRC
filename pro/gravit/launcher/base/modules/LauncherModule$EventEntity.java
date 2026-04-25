package pro.gravit.launcher.base.modules;

final class EventEntity<T extends LauncherModule.Event> {
  final Class<T> clazz;
  
  final LauncherModule.EventHandler<T> handler;
  
  private EventEntity(LauncherModule.EventHandler<T> paramEventHandler, Class<T> paramClass) {
    this.clazz = paramClass;
    this.handler = paramEventHandler;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModule$EventEntity.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */