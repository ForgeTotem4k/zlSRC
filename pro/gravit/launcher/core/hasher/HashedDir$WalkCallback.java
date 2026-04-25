package pro.gravit.launcher.core.hasher;

@FunctionalInterface
public interface WalkCallback {
  HashedDir.WalkAction walked(String paramString1, String paramString2, HashedEntry paramHashedEntry);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedDir$WalkCallback.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */