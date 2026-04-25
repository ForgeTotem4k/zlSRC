package pro.gravit.launcher.core.hasher;

public class FindRecursiveResult {
  public final HashedDir parent;
  
  public final HashedEntry entry;
  
  public final String name;
  
  public FindRecursiveResult(HashedDir paramHashedDir, HashedEntry paramHashedEntry, String paramString) {
    this.parent = paramHashedDir;
    this.entry = paramHashedEntry;
    this.name = paramString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedDir$FindRecursiveResult.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */