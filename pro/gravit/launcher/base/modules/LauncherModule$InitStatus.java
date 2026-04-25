package pro.gravit.launcher.base.modules;

public enum InitStatus {
  CREATED(false),
  PRE_INIT_WAIT(true),
  PRE_INIT(false),
  INIT_WAIT(true),
  INIT(false),
  FINISH(true);
  
  private final boolean isAvailable;
  
  InitStatus(boolean paramBoolean) {
    this.isAvailable = paramBoolean;
  }
  
  public boolean isAvailable() {
    return this.isAvailable;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModule$InitStatus.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */