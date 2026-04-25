package pro.gravit.launcher.gui.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class OptionalListEntry {
  @LauncherNetworkAPI
  public List<ProfilesService.OptionalListEntryPair> enabled = new LinkedList<>();
  
  @LauncherNetworkAPI
  public String name;
  
  @LauncherNetworkAPI
  public UUID profileUUID;
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    OptionalListEntry optionalListEntry = (OptionalListEntry)paramObject;
    return (Objects.equals(this.profileUUID, optionalListEntry.profileUUID) && Objects.equals(this.name, optionalListEntry.name));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.profileUUID });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\ProfilesService$OptionalListEntry.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */