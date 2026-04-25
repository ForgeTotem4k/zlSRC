package pro.gravit.launcher.base.profiles.optional.actions;

import java.util.Map;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.utils.helper.LogHelper;

public class OptionalActionFile extends OptionalAction {
  public Map<String, String> files;
  
  public OptionalActionFile() {}
  
  public OptionalActionFile(Map<String, String> paramMap) {
    this.files = paramMap;
  }
  
  public void injectToHashedDir(HashedDir paramHashedDir) {
    if (this.files == null)
      return; 
    this.files.forEach((paramString1, paramString2) -> {
          HashedDir.FindRecursiveResult findRecursiveResult = paramHashedDir.findRecursive(paramString1);
          if (paramString2 != null && !paramString2.isEmpty()) {
            LogHelper.dev("Debug findRecursive: name %s, parent: ", new Object[] { findRecursiveResult.name, (findRecursiveResult.parent == null) ? "null" : "not null", (findRecursiveResult.entry == null) ? "null" : "not null" });
            HashedDir.FindRecursiveResult findRecursiveResult1 = paramHashedDir.findRecursive(paramString2);
            LogHelper.dev("Debug findRecursive: name %s, parent: ", new Object[] { findRecursiveResult1.name, (findRecursiveResult1.parent == null) ? "null" : "not null", (findRecursiveResult1.entry == null) ? "null" : "not null" });
            findRecursiveResult.parent.moveTo(findRecursiveResult.name, findRecursiveResult1.parent, findRecursiveResult1.name);
          } 
        });
  }
  
  public void disableInHashedDir(HashedDir paramHashedDir) {
    if (this.files == null)
      return; 
    this.files.forEach((paramString1, paramString2) -> {
          HashedDir.FindRecursiveResult findRecursiveResult = paramHashedDir.findRecursive(paramString1);
          findRecursiveResult.parent.remove(findRecursiveResult.name);
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\actions\OptionalActionFile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */