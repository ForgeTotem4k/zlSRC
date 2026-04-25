package pro.gravit.launcher.base.profiles.optional.actions;

import java.util.List;

public class OptionalActionClassPath extends OptionalAction {
  public List<String> args;
  
  public boolean useAltClasspath = false;
  
  public OptionalActionClassPath() {}
  
  public OptionalActionClassPath(List<String> paramList) {
    this.args = paramList;
  }
  
  public OptionalActionClassPath(List<String> paramList, boolean paramBoolean) {
    this.args = paramList;
    this.useAltClasspath = paramBoolean;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\actions\OptionalActionClassPath.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */