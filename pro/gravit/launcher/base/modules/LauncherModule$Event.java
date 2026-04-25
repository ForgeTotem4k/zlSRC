package pro.gravit.launcher.base.modules;

public class Event {
  protected boolean cancel = false;
  
  public boolean isCancel() {
    return this.cancel;
  }
  
  public void cancel() {
    this.cancel = true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModule$Event.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */