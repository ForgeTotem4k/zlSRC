package pro.gravit.launcher.base.profiles.optional;

import java.util.List;
import java.util.Objects;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class OptionalFile {
  @LauncherNetworkAPI
  public List<OptionalAction> actions;
  
  @LauncherNetworkAPI
  public boolean mark;
  
  @LauncherNetworkAPI
  public boolean visible = true;
  
  @LauncherNetworkAPI
  public String name;
  
  @LauncherNetworkAPI
  public String info;
  
  @LauncherNetworkAPI
  public List<OptionalTrigger> triggersList;
  
  @LauncherNetworkAPI
  public OptionalDepend[] dependenciesFile;
  
  @LauncherNetworkAPI
  public OptionalDepend[] conflictFile;
  
  @LauncherNetworkAPI
  public OptionalDepend[] groupFile;
  
  @LauncherNetworkAPI
  public transient OptionalFile[] dependencies;
  
  @LauncherNetworkAPI
  public transient OptionalFile[] conflict;
  
  @LauncherNetworkAPI
  public transient OptionalFile[] group;
  
  @LauncherNetworkAPI
  public int subTreeLevel = 1;
  
  @LauncherNetworkAPI
  public boolean isPreset;
  
  @LauncherNetworkAPI
  public boolean limited;
  
  @LauncherNetworkAPI
  public String category;
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    OptionalFile optionalFile = (OptionalFile)paramObject;
    return Objects.equals(this.name, optionalFile.name);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name });
  }
  
  public String getName() {
    return this.name;
  }
  
  public boolean isVisible() {
    return this.visible;
  }
  
  public boolean isMark() {
    return this.mark;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\OptionalFile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */