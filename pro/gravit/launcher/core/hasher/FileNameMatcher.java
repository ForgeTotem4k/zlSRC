package pro.gravit.launcher.core.hasher;

import java.util.Collection;

public final class FileNameMatcher {
  private static final String[] NO_ENTRIES = new String[0];
  
  private final String[] update;
  
  private final String[] verify;
  
  private final String[] exclusions;
  
  public FileNameMatcher(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) {
    this.update = paramArrayOfString1;
    this.verify = paramArrayOfString2;
    this.exclusions = paramArrayOfString3;
  }
  
  private static boolean anyMatch(String[] paramArrayOfString, Collection<String> paramCollection) {
    String str = String.join("/", (Iterable)paramCollection);
    for (String str1 : paramArrayOfString) {
      if (str.startsWith(str1))
        return true; 
    } 
    return false;
  }
  
  public boolean shouldUpdate(Collection<String> paramCollection) {
    return ((anyMatch(this.update, paramCollection) || anyMatch(this.verify, paramCollection)) && !anyMatch(this.exclusions, paramCollection));
  }
  
  public boolean shouldVerify(Collection<String> paramCollection) {
    return (anyMatch(this.verify, paramCollection) && !anyMatch(this.exclusions, paramCollection));
  }
  
  public FileNameMatcher verifyOnly() {
    return new FileNameMatcher(NO_ENTRIES, this.verify, this.exclusions);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\FileNameMatcher.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */