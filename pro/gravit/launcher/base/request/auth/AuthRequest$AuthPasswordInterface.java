package pro.gravit.launcher.base.request.auth;

public interface AuthPasswordInterface {
  boolean check();
  
  default boolean isAllowSave() {
    return false;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\AuthRequest$AuthPasswordInterface.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */