package oshi.hardware.common;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import oshi.hardware.LogicalVolumeGroup;

public class AbstractLogicalVolumeGroup implements LogicalVolumeGroup {
  private final String name;
  
  private final Map<String, Set<String>> lvMap;
  
  private final Set<String> pvSet;
  
  protected AbstractLogicalVolumeGroup(String paramString, Map<String, Set<String>> paramMap, Set<String> paramSet) {
    this.name = paramString;
    for (Map.Entry<String, Set<String>> entry : paramMap.entrySet())
      paramMap.put((String)entry.getKey(), Collections.unmodifiableSet((Set<? extends String>)entry.getValue())); 
    this.lvMap = Collections.unmodifiableMap(paramMap);
    this.pvSet = Collections.unmodifiableSet(paramSet);
  }
  
  public String getName() {
    return this.name;
  }
  
  public Map<String, Set<String>> getLogicalVolumes() {
    return this.lvMap;
  }
  
  public Set<String> getPhysicalVolumes() {
    return this.pvSet;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("Logical Volume Group: ");
    stringBuilder.append(this.name).append("\n |-- PVs: ");
    stringBuilder.append(this.pvSet.toString());
    for (Map.Entry<String, Set<String>> entry : this.lvMap.entrySet()) {
      stringBuilder.append("\n |-- LV: ").append((String)entry.getKey());
      Set set = (Set)entry.getValue();
      if (!set.isEmpty())
        stringBuilder.append(" --> ").append(set); 
    } 
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractLogicalVolumeGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */