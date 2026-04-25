package oshi.hardware.platform.mac;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.common.AbstractLogicalVolumeGroup;
import oshi.util.ExecutingCommand;

final class MacLogicalVolumeGroup extends AbstractLogicalVolumeGroup {
  private static final String DISKUTIL_CS_LIST = "diskutil cs list";
  
  private static final String LOGICAL_VOLUME_GROUP = "Logical Volume Group";
  
  private static final String PHYSICAL_VOLUME = "Physical Volume";
  
  private static final String LOGICAL_VOLUME = "Logical Volume";
  
  MacLogicalVolumeGroup(String paramString, Map<String, Set<String>> paramMap, Set<String> paramSet) {
    super(paramString, paramMap, paramSet);
  }
  
  static List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    String str = null;
    boolean bool1 = false;
    boolean bool2 = false;
    for (String str1 : ExecutingCommand.runNative("diskutil cs list")) {
      if (str1.contains("Logical Volume Group")) {
        bool1 = true;
        continue;
      } 
      if (bool1) {
        int j = str1.indexOf("Name:");
        if (j >= 0) {
          str = str1.substring(j + 5).trim();
          bool1 = false;
        } 
        continue;
      } 
      if (str1.contains("Physical Volume")) {
        bool2 = true;
        continue;
      } 
      if (str1.contains("Logical Volume")) {
        bool2 = false;
        continue;
      } 
      int i = str1.indexOf("Disk:");
      if (i >= 0) {
        if (bool2) {
          ((Set<String>)hashMap2.computeIfAbsent(str, paramString -> new HashSet())).add(str1.substring(i + 5).trim());
          continue;
        } 
        ((Map)hashMap1.computeIfAbsent(str, paramString -> new HashMap<>())).put(str1.substring(i + 5).trim(), Collections.emptySet());
      } 
    } 
    return (List<LogicalVolumeGroup>)hashMap1.entrySet().stream().map(paramEntry -> new MacLogicalVolumeGroup((String)paramEntry.getKey(), (Map<String, Set<String>>)paramEntry.getValue(), (Set<String>)paramMap.get(paramEntry.getKey()))).collect(Collectors.toList());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacLogicalVolumeGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */