package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.VersionHelpers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.driver.windows.wmi.MSFTStorage;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.common.AbstractLogicalVolumeGroup;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

final class WindowsLogicalVolumeGroup extends AbstractLogicalVolumeGroup {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsLogicalVolumeGroup.class);
  
  private static final Pattern SP_OBJECT_ID = Pattern.compile(".*ObjectId=.*SP:(\\{.*\\}).*");
  
  private static final Pattern PD_OBJECT_ID = Pattern.compile(".*ObjectId=.*PD:(\\{.*\\}).*");
  
  private static final Pattern VD_OBJECT_ID = Pattern.compile(".*ObjectId=.*VD:(\\{.*\\})(\\{.*\\}).*");
  
  private static final boolean IS_WINDOWS8_OR_GREATER = VersionHelpers.IsWindows8OrGreater();
  
  WindowsLogicalVolumeGroup(String paramString, Map<String, Set<String>> paramMap, Set<String> paramSet) {
    super(paramString, paramMap, paramSet);
  }
  
  static List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    if (!IS_WINDOWS8_OR_GREATER)
      return Collections.emptyList(); 
    WmiQueryHandler wmiQueryHandler = Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance());
    boolean bool = false;
    try {
      bool = wmiQueryHandler.initCOM();
      WbemcliUtil.WmiResult wmiResult1 = MSFTStorage.queryStoragePools(wmiQueryHandler);
      int i = wmiResult1.getResultCount();
      if (i == 0)
        return (List)Collections.emptyList(); 
      HashMap<Object, Object> hashMap1 = new HashMap<>();
      WbemcliUtil.WmiResult wmiResult2 = MSFTStorage.queryVirtualDisks(wmiQueryHandler);
      i = wmiResult2.getResultCount();
      for (byte b1 = 0; b1 < i; b1++) {
        String str = WmiUtil.getString(wmiResult2, (Enum)MSFTStorage.VirtualDiskProperty.OBJECTID, b1);
        Matcher matcher = VD_OBJECT_ID.matcher(str);
        if (matcher.matches())
          str = matcher.group(2) + " " + matcher.group(1); 
        hashMap1.put(str, WmiUtil.getString(wmiResult2, (Enum)MSFTStorage.VirtualDiskProperty.FRIENDLYNAME, b1));
      } 
      HashMap<Object, Object> hashMap2 = new HashMap<>();
      WbemcliUtil.WmiResult wmiResult3 = MSFTStorage.queryPhysicalDisks(wmiQueryHandler);
      i = wmiResult3.getResultCount();
      for (byte b2 = 0; b2 < i; b2++) {
        String str = WmiUtil.getString(wmiResult3, (Enum)MSFTStorage.PhysicalDiskProperty.OBJECTID, b2);
        Matcher matcher = PD_OBJECT_ID.matcher(str);
        if (matcher.matches())
          str = matcher.group(1); 
        hashMap2.put(str, new Pair(WmiUtil.getString(wmiResult3, (Enum)MSFTStorage.PhysicalDiskProperty.FRIENDLYNAME, b2), WmiUtil.getString(wmiResult3, (Enum)MSFTStorage.PhysicalDiskProperty.PHYSICALLOCATION, b2)));
      } 
      HashMap<Object, Object> hashMap3 = new HashMap<>();
      WbemcliUtil.WmiResult wmiResult4 = MSFTStorage.queryStoragePoolPhysicalDisks(wmiQueryHandler);
      i = wmiResult4.getResultCount();
      for (byte b3 = 0; b3 < i; b3++) {
        String str1 = WmiUtil.getRefString(wmiResult4, (Enum)MSFTStorage.StoragePoolToPhysicalDiskProperty.STORAGEPOOL, b3);
        Matcher matcher = SP_OBJECT_ID.matcher(str1);
        if (matcher.matches())
          str1 = matcher.group(1); 
        String str2 = WmiUtil.getRefString(wmiResult4, (Enum)MSFTStorage.StoragePoolToPhysicalDiskProperty.PHYSICALDISK, b3);
        matcher = PD_OBJECT_ID.matcher(str2);
        if (matcher.matches())
          str2 = matcher.group(1); 
        hashMap3.put(str1 + " " + str2, str2);
      } 
      ArrayList<WindowsLogicalVolumeGroup> arrayList = new ArrayList();
      i = wmiResult1.getResultCount();
      for (byte b4 = 0; b4 < i; b4++) {
        String str1 = WmiUtil.getString(wmiResult1, (Enum)MSFTStorage.StoragePoolProperty.FRIENDLYNAME, b4);
        String str2 = WmiUtil.getString(wmiResult1, (Enum)MSFTStorage.StoragePoolProperty.OBJECTID, b4);
        Matcher matcher = SP_OBJECT_ID.matcher(str2);
        if (matcher.matches())
          str2 = matcher.group(1); 
        HashSet<String> hashSet = new HashSet();
        for (Map.Entry<Object, Object> entry : hashMap3.entrySet()) {
          if (((String)entry.getKey()).contains(str2)) {
            String str = (String)entry.getValue();
            Pair pair = (Pair)hashMap2.get(str);
            if (pair != null)
              hashSet.add((String)pair.getA() + " @ " + (String)pair.getB()); 
          } 
        } 
        HashMap<Object, Object> hashMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : hashMap1.entrySet()) {
          if (((String)entry.getKey()).contains(str2)) {
            String str = ParseUtil.whitespaces.split((CharSequence)entry.getKey())[0];
            hashMap.put((String)entry.getValue() + " " + str, hashSet);
          } 
        } 
        arrayList.add(new WindowsLogicalVolumeGroup(str1, (Map)hashMap, hashSet));
      } 
      return (List)arrayList;
    } catch (COMException cOMException) {
      LOG.warn("COM exception: {}", cOMException.getMessage());
      return (List)Collections.emptyList();
    } finally {
      if (bool)
        wmiQueryHandler.unInitCOM(); 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsLogicalVolumeGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */