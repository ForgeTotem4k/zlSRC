package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.common.AbstractLogicalVolumeGroup;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.linux.DevPath;

final class LinuxLogicalVolumeGroup extends AbstractLogicalVolumeGroup {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxLogicalVolumeGroup.class);
  
  private static final String BLOCK = "block";
  
  private static final String DM_UUID = "DM_UUID";
  
  private static final String DM_VG_NAME = "DM_VG_NAME";
  
  private static final String DM_LV_NAME = "DM_LV_NAME";
  
  LinuxLogicalVolumeGroup(String paramString, Map<String, Set<String>> paramMap, Set<String> paramSet) {
    super(paramString, paramMap, paramSet);
  }
  
  static List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    if (!LinuxOperatingSystem.HAS_UDEV) {
      LOG.warn("Logical Volume Group information requires libudev, which is not present.");
      return Collections.emptyList();
    } 
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    for (String str : ExecutingCommand.runNative("pvs -o vg_name,pv_name")) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
      if (arrayOfString.length == 2 && arrayOfString[1].startsWith(DevPath.DEV))
        ((Set<String>)hashMap2.computeIfAbsent(arrayOfString[0], paramString -> new HashSet())).add(arrayOfString[1]); 
    } 
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    try {
      Udev.UdevEnumerate udevEnumerate = udevContext.enumerateNew();
      try {
        udevEnumerate.addMatchSubsystem("block");
        udevEnumerate.scanDevices();
        for (Udev.UdevListEntry udevListEntry = udevEnumerate.getListEntry(); udevListEntry != null; udevListEntry = udevListEntry.getNext()) {
          String str = udevListEntry.getName();
          Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(str);
          if (udevDevice != null)
            try {
              String str1 = udevDevice.getDevnode();
              if (str1 != null && str1.startsWith(DevPath.DM)) {
                String str2 = udevDevice.getPropertyValue("DM_UUID");
                if (str2 != null && str2.startsWith("LVM-")) {
                  String str3 = udevDevice.getPropertyValue("DM_VG_NAME");
                  String str4 = udevDevice.getPropertyValue("DM_LV_NAME");
                  if (!Util.isBlank(str3) && !Util.isBlank(str4)) {
                    hashMap1.computeIfAbsent(str3, paramString -> new HashMap<>());
                    Map<String, Set<String>> map = (Map)hashMap1.get(str3);
                    hashMap2.computeIfAbsent(str3, paramString -> new HashSet());
                    Set<String> set = (Set)hashMap2.get(str3);
                    File file = new File(str + "/slaves");
                    File[] arrayOfFile = file.listFiles();
                    if (arrayOfFile != null)
                      for (File file1 : arrayOfFile) {
                        String str5 = file1.getName();
                        ((Set<String>)map.computeIfAbsent(str4, paramString -> new HashSet())).add(DevPath.DEV + str5);
                        set.add(DevPath.DEV + str5);
                      }  
                  } 
                } 
              } 
            } finally {
              udevDevice.unref();
            }  
        } 
      } finally {
        udevEnumerate.unref();
      } 
    } finally {
      udevContext.unref();
    } 
    return (List<LogicalVolumeGroup>)hashMap1.entrySet().stream().map(paramEntry -> new LinuxLogicalVolumeGroup((String)paramEntry.getKey(), (Map<String, Set<String>>)paramEntry.getValue(), (Set<String>)paramMap.get(paramEntry.getKey()))).collect(Collectors.toList());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxLogicalVolumeGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */