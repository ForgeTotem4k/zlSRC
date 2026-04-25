package pro.gravit.launcher.core.hasher;

public final class Diff {
  public final HashedDir mismatch;
  
  public final HashedDir extra;
  
  private Diff(HashedDir paramHashedDir1, HashedDir paramHashedDir2) {
    this.mismatch = paramHashedDir1;
    this.extra = paramHashedDir2;
  }
  
  public boolean isSame() {
    return (this.mismatch.isEmpty() && this.extra.isEmpty());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedDir$Diff.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */